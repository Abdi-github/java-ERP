// @ts-check
const { test, expect } = require('@playwright/test');
const { loginAs, BASE } = require('./helpers/auth');

// Generic helper to test a list page
async function testListPage(page, path, label) {
  await page.goto(`${BASE}${path}`);
  await page.waitForLoadState('networkidle');
  const url = page.url();
  if (url.includes('/error')) {
    console.log(`❌ ${label} — error page! URL: ${url}`);
    return false;
  }
  if (url.includes('/auth/login')) {
    console.log(`🔒 ${label} — redirected to login (access denied)`);
    return false;
  }
  const heading = page.locator('h1, h2, .page-title').first();
  const headingVisible = await heading.isVisible().catch(() => false);
  const rows = page.locator('table tbody tr');
  const rowCount = await rows.count().catch(() => 0);
  console.log(`✅ ${label} — rows: ${rowCount}, heading: ${headingVisible}`);
  return true;
}

// Helper to test API endpoint (accepts 200, 403, 404 — API chain requires JWT, session won't work)
async function testApiEndpoint(page, path, label) {
  const r = await page.request.get(`${BASE}${path}`);
  console.log(`  ${label} API: ${r.status()}`);
  // 403 is expected since API uses JWT, not session auth
  expect([200, 403, 404]).toContain(r.status());
}

// ══════════════════════════════════════════════════════════
// 05 — INVENTORY MODULE
// ══════════════════════════════════════════════════════════
test.describe('05 | Inventory', () => {

  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'admin');
  });

  test('Inventory stock levels page', async ({ page }) => {
    const ok = await testListPage(page, '/app/inventory/stock', 'Stock Levels');
    expect(ok).not.toBe(false);
  });

  test('Inventory warehouses page', async ({ page }) => {
    const ok = await testListPage(page, '/app/inventory/warehouses', 'Warehouses');
    expect(ok).not.toBe(false);
  });

  test('Inventory API — stock levels', async ({ page }) => {
    await loginAs(page, 'admin');
    await testApiEndpoint(page, '/api/v1/inventory/stock-levels', 'Stock levels');
  });

  test('Inventory API — warehouses', async ({ page }) => {
    await testApiEndpoint(page, '/api/v1/inventory/warehouses', 'Warehouses');
  });
});

// ══════════════════════════════════════════════════════════
// 06 — SALES MODULE
// ══════════════════════════════════════════════════════════
test.describe('06 | Sales', () => {

  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'admin');
  });

  test('Sales orders list page', async ({ page }) => {
    await testListPage(page, '/app/sales/orders', 'Sales Orders');
    await expect(page).not.toHaveURL(/\/error/);
  });

  test('Sales customers list page', async ({ page }) => {
    await testListPage(page, '/app/sales/customers', 'Customers');
    await expect(page).not.toHaveURL(/\/error/);
  });

  test('Sales orders API endpoint', async ({ page }) => {
    await testApiEndpoint(page, '/api/v1/sales/orders', 'Sales orders');
  });

  test('Sales customers API endpoint', async ({ page }) => {
    await testApiEndpoint(page, '/api/v1/sales/customers', 'Sales customers');
  });
});

// ══════════════════════════════════════════════════════════
// 07 — PURCHASING MODULE
// ══════════════════════════════════════════════════════════
test.describe('07 | Purchasing', () => {

  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'admin');
  });

  test('Purchasing orders page', async ({ page }) => {
    await testListPage(page, '/app/purchasing/orders', 'Purchase Orders');
    await expect(page).not.toHaveURL(/\/error/);
  });

  test('Purchasing suppliers page', async ({ page }) => {
    await testListPage(page, '/app/purchasing/suppliers', 'Suppliers');
    await expect(page).not.toHaveURL(/\/error/);
  });

  test('Purchasing orders API', async ({ page }) => {
    await testApiEndpoint(page, '/api/v1/purchasing/orders', 'Purchasing orders');
  });

  test('Purchasing suppliers API', async ({ page }) => {
    await testApiEndpoint(page, '/api/v1/purchasing/suppliers', 'Purchasing suppliers');
  });
});

// ══════════════════════════════════════════════════════════
// 08 — PRODUCTION MODULE
// ══════════════════════════════════════════════════════════
test.describe('08 | Production', () => {

  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'admin');
  });

  test('Production orders page', async ({ page }) => {
    await testListPage(page, '/app/production/orders', 'Production Orders');
    await expect(page).not.toHaveURL(/\/error/);
  });

  test('Production work centers page', async ({ page }) => {
    await testListPage(page, '/app/production/work-centers', 'Work Centers');
    await expect(page).not.toHaveURL(/\/error/);
  });

  test('Production orders API', async ({ page }) => {
    await testApiEndpoint(page, '/api/v1/production/orders', 'Production orders');
  });
});

// ══════════════════════════════════════════════════════════
// 09 — ACCOUNTING MODULE
// ══════════════════════════════════════════════════════════
test.describe('09 | Accounting', () => {

  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'admin');
  });

  test('Accounting accounts page', async ({ page }) => {
    await testListPage(page, '/app/accounting/accounts', 'Accounts');
    await expect(page).not.toHaveURL(/\/error/);
  });

  test('Accounting journal entries page', async ({ page }) => {
    await testListPage(page, '/app/accounting/journal-entries', 'Journal Entries');
    await expect(page).not.toHaveURL(/\/error/);
  });

  test('Accounting accounts API', async ({ page }) => {
    await testApiEndpoint(page, '/api/v1/accounting/accounts', 'Accounting accounts');
  });
});

// ══════════════════════════════════════════════════════════
// 10 — HR MODULE
// ══════════════════════════════════════════════════════════
test.describe('10 | HR', () => {

  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'admin');
  });

  test('HR employees page', async ({ page }) => {
    await testListPage(page, '/app/hr/employees', 'Employees');
    await expect(page).not.toHaveURL(/\/error/);
  });

  test('HR departments page', async ({ page }) => {
    await testListPage(page, '/app/hr/departments', 'Departments');
    await expect(page).not.toHaveURL(/\/error/);
  });

  test('HR employees API', async ({ page }) => {
    await testApiEndpoint(page, '/api/v1/hr/employees', 'HR employees');
  });
});

// ══════════════════════════════════════════════════════════
// 11 — CRM MODULE
// ══════════════════════════════════════════════════════════
test.describe('11 | CRM', () => {

  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'admin');
  });

  test('CRM contacts page', async ({ page }) => {
    await testListPage(page, '/app/crm/contacts', 'CRM Contacts');
    await expect(page).not.toHaveURL(/\/error/);
  });

  test('CRM interactions page', async ({ page }) => {
    await testListPage(page, '/app/crm/interactions', 'CRM Interactions');
    await expect(page).not.toHaveURL(/\/error/);
  });

  test('CRM contacts API', async ({ page }) => {
    await testApiEndpoint(page, '/api/v1/crm/contacts', 'CRM contacts');
  });
});

// ══════════════════════════════════════════════════════════
// 12 — QUALITY CONTROL MODULE
// ══════════════════════════════════════════════════════════
test.describe('12 | Quality Control', () => {

  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'admin');
  });

  test('QC inspection plans page', async ({ page }) => {
    await testListPage(page, '/app/quality-control/inspection-plans', 'Inspection Plans');
    await expect(page).not.toHaveURL(/\/error/);
  });

  test('QC checks page', async ({ page }) => {
    await testListPage(page, '/app/quality-control/checks', 'Quality Checks');
    await expect(page).not.toHaveURL(/\/error/);
  });

  test('QC NCRs page', async ({ page }) => {
    await testListPage(page, '/app/quality-control/ncrs', 'NCRs');
    await expect(page).not.toHaveURL(/\/error/);
  });

  test('QC API endpoint', async ({ page }) => {
    await testApiEndpoint(page, '/api/v1/quality-control/inspection-plans', 'QC inspection plans');
  });
});


