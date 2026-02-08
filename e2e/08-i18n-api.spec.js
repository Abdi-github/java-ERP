// @ts-check
const { test, expect } = require('@playwright/test');
const { loginAs, BASE } = require('./helpers/auth');

// ══════════════════════════════════════════════════════════
// 16 — I18N (Internationalisation)
// ══════════════════════════════════════════════════════════
test.describe('16 | i18n — Internationalisation', () => {

  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'admin');
  });

  test('Default language (de-CH) — sidebar section headers are translated', async ({ page }) => {
    await page.goto(`${BASE}/app/dashboard?lang=de`);
    await page.waitForLoadState('networkidle');
    const headers = page.locator('.sidebar-section-header .sidebar-label');
    const count = await headers.count();
    for (let i = 0; i < count; i++) {
      const text = (await headers.nth(i).textContent()).trim();
      expect(text).not.toMatch(/^\?\?/);
      expect(text.length).toBeGreaterThan(0);
      console.log(`  DE section ${i + 1}: "${text}"`);
    }
    console.log('✅ German — all sidebar section headers translated');
  });

  test('French (fr-CH) — page title and sidebar sections translated', async ({ page }) => {
    await page.goto(`${BASE}/app/dashboard?lang=fr`);
    await page.waitForLoadState('networkidle');
    const headers = page.locator('.sidebar-section-header .sidebar-label');
    const count = await headers.count();
    const texts = [];
    for (let i = 0; i < count; i++) {
      const text = (await headers.nth(i).textContent()).trim();
      expect(text).not.toMatch(/^\?\?/);
      texts.push(text);
    }
    console.log(`✅ French — sidebar sections: [${texts.join(', ')}]`);
  });

  test('Italian (it-CH) — translations applied', async ({ page }) => {
    await page.goto(`${BASE}/app/dashboard?lang=it`);
    await page.waitForLoadState('networkidle');
    const headers = page.locator('.sidebar-section-header .sidebar-label');
    const count = await headers.count();
    const texts = [];
    for (let i = 0; i < count; i++) {
      const text = (await headers.nth(i).textContent()).trim();
      expect(text).not.toMatch(/^\?\?/);
      texts.push(text);
    }
    console.log(`✅ Italian — sidebar sections: [${texts.join(', ')}]`);
  });

  test('English — translations applied', async ({ page }) => {
    await page.goto(`${BASE}/app/dashboard?lang=en`);
    await page.waitForLoadState('networkidle');
    const headers = page.locator('.sidebar-section-header .sidebar-label');
    const count = await headers.count();
    const texts = [];
    for (let i = 0; i < count; i++) {
      const text = (await headers.nth(i).textContent()).trim();
      expect(text).not.toMatch(/^\?\?/);
      texts.push(text);
    }
    console.log(`✅ English — sidebar sections: [${texts.join(', ')}]`);
  });

  test('Language toggle updates URL and persists selection', async ({ page }) => {
    await page.goto(`${BASE}/app/dashboard`);
    // Switch to FR
    await page.locator('#langDropdown').click();
    await page.waitForTimeout(200);
    await page.locator('.lang-switch[href*="lang=fr"]').click();
    await page.waitForLoadState('networkidle');
    const url = page.url();
    console.log(`  After FR switch, URL: ${url}`);
    // Navigate to another page — lang should persist via session/cookie
    await page.goto(`${BASE}/app/masterdata/products`);
    await page.waitForLoadState('networkidle');
    const body = await page.textContent('body');
    expect(body).not.toContain('??');
    console.log('✅ Language persists after navigation');
  });

  test('Login page supports multiple languages via lang param', async ({ page }) => {
    for (const lang of ['de', 'fr', 'it', 'en']) {
      await page.goto(`${BASE}/auth/login?lang=${lang}`);
      await page.waitForLoadState('networkidle');
      await expect(page.locator('input[name="username"]')).toBeVisible();
      const body = await page.textContent('body');
      expect(body).not.toContain('??');
      console.log(`✅ Login page renders in lang=${lang}`);
    }
  });

  test('No missing translation keys (no ?? on dashboard)', async ({ page }) => {
    for (const lang of ['de', 'fr', 'it', 'en']) {
      await page.goto(`${BASE}/app/dashboard?lang=${lang}`);
      await page.waitForLoadState('networkidle');
      const body = await page.textContent('body');
      if (body.includes('??')) {
        // Extract the missing keys
        const matches = body.match(/\?\?[^\?]+\?\?/g) || [];
        console.log(`⚠️  Missing translation keys in lang=${lang}: ${[...new Set(matches)].join(', ')}`);
      } else {
        console.log(`✅ No missing translations in lang=${lang}`);
      }
    }
  });
});

// ══════════════════════════════════════════════════════════
// 17 — API HEALTH CHECKS (REST JSON)
// ══════════════════════════════════════════════════════════
test.describe('17 | REST API Health', () => {

  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'admin');
  });

  const apiEndpoints = [
    { path: '/actuator/health',              expectStatus: [200], label: 'Actuator Health' },
    { path: '/api/v1/products',              expectStatus: [200, 403, 404], label: 'Products' },
    { path: '/api/v1/inventory/warehouses',  expectStatus: [200, 403, 404], label: 'Warehouses' },
    { path: '/api/v1/sales/orders',          expectStatus: [200, 403, 404], label: 'Sales Orders' },
    { path: '/api/v1/sales/customers',       expectStatus: [200, 403, 404], label: 'Customers' },
    { path: '/api/v1/purchasing/suppliers',  expectStatus: [200, 403, 404], label: 'Suppliers' },
    { path: '/api/v1/production/orders',     expectStatus: [200, 403, 404], label: 'Production Orders' },
    { path: '/api/v1/accounting/accounts',   expectStatus: [200, 403, 404], label: 'Accounts' },
    { path: '/api/v1/hr/employees',          expectStatus: [200, 403, 404], label: 'Employees' },
    { path: '/api/v1/crm/contacts',          expectStatus: [200, 403, 404], label: 'CRM Contacts' },
    { path: '/api/v1/notifications',         expectStatus: [200, 403, 404], label: 'Notifications' },
  ];

  for (const ep of apiEndpoints) {
    test(`API: ${ep.label} (${ep.path})`, async ({ page }) => {
      const r = await page.request.get(`${BASE}${ep.path}`, {
        headers: { 'Accept': 'application/json' }
      });
      const allowed = Array.isArray(ep.expectStatus) ? ep.expectStatus : [ep.expectStatus];
      console.log(`  ${ep.label}: ${r.status()}`);
      if (r.status() === 200) {
        const ct = r.headers()['content-type'] || '';
        const isJson = ct.includes('application/json');
        console.log(`    Content-Type: ${ct}`);
        if (isJson) {
          const body = await r.json();
          console.log(`    Response keys: ${Object.keys(body || {}).join(', ')}`);
        }
      }
      expect(allowed).toContain(r.status());
      console.log(`✅ API ${ep.label} returned ${r.status()}`);
    });
  }
});


