// @ts-check
const { test, expect } = require('@playwright/test');
const { loginAs, BASE } = require('./helpers/auth');

// ══════════════════════════════════════════════════════════
// 03 — DASHBOARD
// ══════════════════════════════════════════════════════════
test.describe('03 | Dashboard', () => {

  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'admin');
    await page.goto(`${BASE}/app/dashboard`);
    await page.waitForLoadState('networkidle');
  });

  test('Dashboard page loads without errors', async ({ page }) => {
    await expect(page).not.toHaveURL(/\/error/);
    const status = await page.evaluate(() => document.title);
    console.log(`  Page title: ${status}`);
    expect(page.url()).toContain('/app/dashboard');
    console.log('✅ Dashboard loaded successfully');
  });

  test('Dashboard has page title', async ({ page }) => {
    // H1 or visible heading
    const heading = page.locator('h1, h2, .page-title').first();
    await expect(heading).toBeVisible();
    const text = await heading.textContent();
    console.log(`✅ Dashboard heading: "${text.trim()}"`);
  });

  test('Dashboard stat cards or widgets are visible', async ({ page }) => {
    const cards = page.locator('.card, .stat-card');
    const count = await cards.count();
    console.log(`  Dashboard cards found: ${count}`);
    expect(count).toBeGreaterThan(0);
    console.log('✅ Dashboard widgets / cards present');
  });

  test('Dashboard navigation links are functional', async ({ page }) => {
    // Click a sidebar link from dashboard
    const dashLink = page.locator('.sidebar-link[data-path="/app/dashboard"]');
    await expect(dashLink).toBeVisible();
    await dashLink.click();
    await page.waitForLoadState('networkidle');
    await expect(page).toHaveURL(/\/app\/dashboard/);
    console.log('✅ Dashboard sidebar link works');
  });
});

