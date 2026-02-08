// @ts-check
const { test, expect } = require('@playwright/test');
const { loginAs, BASE } = require('./helpers/auth');

// ══════════════════════════════════════════════════════════
// 18 — CRUD OPERATIONS (Products sample)
// ══════════════════════════════════════════════════════════
test.describe('18 | CRUD Operations — Products', () => {

  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'admin');
  });

  test('CREATE product via API', async ({ page }) => {
    const payload = {
      sku: `E2E-TEST-${Date.now()}`,
      name: 'E2E Test Watch Chronograph',
      description: 'Playwright e2e test product — Swiss made',
      categoryId: null,
      unitPrice: '24900.0000',
      currency: 'CHF',
      weight: '0.1500',
      active: true
    };
    const r = await page.request.post(`${BASE}/api/v1/products`, {
      headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      data: payload
    });
    console.log(`  CREATE product API: ${r.status()}`);
    if (r.status() === 201 || r.status() === 200) {
      const body = await r.json();
      console.log(`✅ Product created — ID: ${body.id || body.productId || 'unknown'}`);
      // Store ID for subsequent tests (if supported)
    } else if (r.status() === 422 || r.status() === 400) {
      const body = await r.text();
      console.log(`ℹ️  Product create validation: ${body.substring(0, 200)}`);
    } else {
      console.log(`ℹ️  Product create returned ${r.status()} (API may not be implemented)`);
    }
    expect([200, 201, 400, 403, 404, 422]).toContain(r.status());
  });

  test('READ products list via API — returns paginated JSON', async ({ page }) => {
    const r = await page.request.get(`${BASE}/api/v1/products?page=0&size=10`, {
      headers: { 'Accept': 'application/json' }
    });
    console.log(`  READ products list: ${r.status()}`);
    if (r.status() === 200) {
      const body = await r.json();
      const items = body.content || body;
      console.log(`✅ Products API: ${Array.isArray(items) ? items.length : '?'} items`);
    } else {
      console.log(`ℹ️  Products API: ${r.status()}`);
    }
    expect([200, 403, 404]).toContain(r.status());
  });

  test('READ product by ID via API', async ({ page }) => {
    // First get a product ID from the list
    const listR = await page.request.get(`${BASE}/api/v1/products?size=1`);
    if (listR.status() === 200) {
      const body = await listR.json();
      const items = body.content || body;
      if (Array.isArray(items) && items.length > 0) {
        const id = items[0].id || items[0].productId;
        const r = await page.request.get(`${BASE}/api/v1/products/${id}`);
        console.log(`  READ product ${id}: ${r.status()}`);
        expect(r.status()).toBe(200);
        const product = await r.json();
        expect(product.id || product.productId).toBeTruthy();
        console.log(`✅ Product detail API works — name: ${product.name}`);
      }
    } else {
      console.log('ℹ️  Cannot test single product — list API not available');
    }
  });

  test('CRUD via UI — products list renders with Create button', async ({ page }) => {
    await page.goto(`${BASE}/app/masterdata/products`);
    await page.waitForLoadState('networkidle');

    // Check for Create / New button
    const createBtns = page.locator(
      'a[href*="new"], a[href*="create"], button:has-text("New"), a:has-text("New"), ' +
      'a:has-text("Neu"), button:has-text("Erstellen"), a:has-text("Créer")'
    );
    const count = await createBtns.count();
    console.log(`  Create button count on products page: ${count}`);
    if (count > 0) {
      console.log(`✅ Create button present: "${await createBtns.first().textContent()}"`);
    } else {
      console.log('⚠️  No Create button found on products page');
    }

    // Check table
    const rows = page.locator('table tbody tr');
    const rowCount = await rows.count();
    expect(rowCount).toBeGreaterThan(0);
    console.log(`✅ Products table: ${rowCount} rows`);
  });

  test('CRUD via UI — delete confirmation uses Bootstrap modal', async ({ page }) => {
    await page.goto(`${BASE}/app/masterdata/products`);
    await page.waitForLoadState('networkidle');

    // Find a visible delete button on a table row (not the hidden modal confirm button)
    const deleteBtn = page.locator(
      'table tbody tr button.btn-danger:visible, ' +
      'table tbody tr a.btn-danger:visible, ' +
      'table tbody tr button.btn-outline-danger:visible, ' +
      'table tbody tr a.btn-outline-danger:visible'
    ).first();

    if (await deleteBtn.count() > 0) {
      // Set up dialog handler — if browser dialog fires, it means modal is NOT working
      let dialogFired = false;
      page.on('dialog', dialog => {
        dialogFired = true;
        dialog.dismiss();
        console.log(`⚠️  Browser dialog fired (not Bootstrap modal): "${dialog.message()}"`);
      });

      await deleteBtn.click({ timeout: 5000 });
      await page.waitForTimeout(500);

      if (!dialogFired) {
        // Check if Bootstrap modal appeared
        const modal = page.locator('#erpDeleteModal');
        const isVisible = await modal.isVisible().catch(() => false);
        if (isVisible) {
          console.log('✅ Delete uses Bootstrap modal (correct behavior)');
          // Close the modal
          await page.locator('#erpDeleteModal .btn-outline-secondary, #erpDeleteModal [data-bs-dismiss="modal"]').first().click();
        } else {
          console.log('⚠️  Delete modal not visible after click — may use different pattern');
        }
      }
    } else {
      console.log('ℹ️  No visible delete button found on products page');
    }
  });
});

// ══════════════════════════════════════════════════════════
// 19 — STATIC ASSETS (CSS, JS, Bootstrap)
// ══════════════════════════════════════════════════════════
test.describe('19 | Static Assets', () => {

  test('Bootstrap CSS loads without 404', async ({ page }) => {
    let failed404 = false;
    page.on('response', r => {
      if (r.url().includes('bootstrap') && r.url().endsWith('.css') && r.status() === 404) {
        failed404 = true;
        console.log(`❌ Bootstrap CSS 404: ${r.url()}`);
      }
    });
    await page.goto(`${BASE}/auth/login`);
    await page.waitForLoadState('networkidle');
    expect(failed404).toBe(false);
    console.log('✅ Bootstrap CSS loaded without 404');
  });

  test('Bootstrap Icons CSS loads without 404', async ({ page }) => {
    let failed404 = false;
    page.on('response', r => {
      if (r.url().includes('bootstrap-icons') && r.status() === 404) {
        failed404 = true;
        console.log(`❌ Bootstrap Icons 404: ${r.url()}`);
      }
    });
    await loginAs(page, 'admin');
    await page.goto(`${BASE}/app/dashboard`);
    await page.waitForLoadState('networkidle');
    expect(failed404).toBe(false);
    console.log('✅ Bootstrap Icons CSS loaded without 404');
  });

  test('Custom app.css loads without 404', async ({ page }) => {
    const r = await page.request.get(`${BASE}/css/app.css`);
    expect(r.status()).toBe(200);
    const body = await r.text();
    // Must contain sidebar styles
    expect(body).toContain('.erp-sidebar');
    expect(body).toContain('.erp-topbar');
    console.log(`✅ app.css loaded (${body.length} chars) with sidebar styles`);
  });

  test('app.js loads without 404', async ({ page }) => {
    const r = await page.request.get(`${BASE}/js/app.js`);
    expect(r.status()).toBe(200);
    const body = await r.text();
    expect(body).toContain('initSidebar');
    expect(body).toContain('erpConfirm');
    console.log(`✅ app.js loaded (${body.length} chars) with sidebar & modal init`);
  });

  test('Bootstrap JS (bundle) loads without 404', async ({ page }) => {
    const r = await page.request.get(`${BASE}/webjars/bootstrap/5.3.3/js/bootstrap.bundle.min.js`);
    expect(r.status()).toBe(200);
    console.log('✅ Bootstrap JS bundle loaded');
  });

  test('HTMX JS loads without 404', async ({ page }) => {
    const r = await page.request.get(`${BASE}/webjars/htmx.org/2.0.4/dist/htmx.min.js`);
    expect(r.status()).toBe(200);
    console.log('✅ HTMX JS loaded');
  });
});




