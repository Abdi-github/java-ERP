// @ts-check
const { expect } = require('@playwright/test');

const BASE = 'http://localhost:8080';

/**
 * Login credentials for test users.
 * NOTE: All seed users have password "password" (set by V020 migration).
 */
const USERS = {
  admin:      { username: 'admin',      password: 'password', role: 'ADMIN' },
  manager:    { username: 'l.mueller',  password: 'password', role: 'MANAGER' },
  sales:      { username: 'a.bianchi',  password: 'password', role: 'SALES' },
  production: { username: 'm.keller',   password: 'password', role: 'PRODUCTION' },
  viewer:     { username: 'viewer',     password: 'password', role: 'VIEWER' },
};

/**
 * Login as the given user and wait for the dashboard.
 * @param {import('@playwright/test').Page} page
 * @param {'admin'|'manager'|'sales'|'production'|'viewer'} role
 */
async function loginAs(page, role = 'admin') {
  const user = USERS[role];
  await page.goto(`${BASE}/auth/login`);
  await page.waitForLoadState('networkidle');

  // Fill login form
  await page.fill('input[name="username"]', user.username);
  await page.fill('input[name="password"]', user.password);
  await page.click('button[type="submit"]');

  // Wait for redirect after login — defaultSuccessUrl is /app/dashboard
  // Use networkidle + check URL is no longer on /auth/login
  await page.waitForLoadState('networkidle', { timeout: 20_000 });
  const finalUrl = page.url();
  if (finalUrl.includes('/auth/login')) {
    // Login may have failed — throw with helpful message
    const body = await page.textContent('body').catch(() => '');
    throw new Error(`Login failed for user "${user.username}". URL: ${finalUrl}. Page contains: ${body.substring(0, 300)}`);
  }
  return user;
}

/**
 * Logout from the application.
 * Spring Security requires POST + CSRF for logout.
 * Uses page.evaluate to find and submit the logout form directly.
 * @param {import('@playwright/test').Page} page
 */
async function logout(page) {
  // Navigate to a page with the layout (sidebar/topbar)
  await page.goto(`${BASE}/app/dashboard`);
  await page.waitForLoadState('networkidle');

  // Try submitting any logout form on the page
  const submitted = await page.evaluate(() => {
    const forms = document.querySelectorAll('form');
    for (const form of forms) {
      const action = form.action || '';
      if (action.includes('/auth/logout') || action.includes('logout')) {
        form.submit();
        return true;
      }
    }
    return false;
  });

  if (submitted) {
    await page.waitForLoadState('networkidle');
    return;
  }

  // Fallback: find CSRF token and submit POST request manually
  const csrf = await page.evaluate(() => {
    const el = document.querySelector('input[name="_csrf"]');
    return el ? el.value : null;
  });

  if (csrf) {
    await page.request.post(`${BASE}/auth/logout`, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      data: `_csrf=${encodeURIComponent(csrf)}`,
    });
  }
  // Navigate to confirm logout
  await page.goto(`${BASE}/auth/login`);
  await page.waitForLoadState('networkidle');
}

module.exports = { loginAs, logout, USERS, BASE };






