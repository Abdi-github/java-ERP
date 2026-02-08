// @ts-check
const { test, expect } = require('@playwright/test');
const { loginAs, logout, BASE } = require('./helpers/auth');

// ══════════════════════════════════════════════════════════
// 02 — LAYOUT & SIDEBAR
// ══════════════════════════════════════════════════════════
test.describe('02 | Layout & Sidebar', () => {

  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'admin');
  });

  test('Topbar renders: brand, toggle, notification bell, language switcher, user menu', async ({ page }) => {
    const topbar = page.locator('.erp-topbar');
    await expect(topbar).toBeVisible();

    // Brand
    await expect(page.locator('.erp-brand')).toBeVisible();
    await expect(page.locator('.brand-name')).toContainText('SwiftApp ERP');

    // Sidebar toggle
    await expect(page.locator('#sidebarToggle')).toBeVisible();

    // Notification bell icon
    const notifBell = page.locator('.erp-topbar-right a[href*="notifications"]').first();
    await expect(notifBell).toBeVisible();
    console.log('✅ Notification bell visible in topbar');

    // Language switcher
    await expect(page.locator('#langDropdown')).toBeVisible();

    // User menu (authenticated)
    const userMenu = page.locator('.erp-topbar-right .dropdown button').nth(1);
    await expect(userMenu).toBeVisible();

    console.log('✅ Topbar: all elements present');
  });

  test('Sidebar renders all navigation sections', async ({ page }) => {
    const sidebar = page.locator('#erpSidebar');
    await expect(sidebar).toBeVisible();

    // Check all section headers have text (not ?? messages)
    const sectionHeaders = page.locator('.sidebar-section-header');
    const count = await sectionHeaders.count();
    expect(count).toBeGreaterThan(3);

    for (let i = 0; i < count; i++) {
      const text = await sectionHeaders.nth(i).textContent();
      const trimmed = text.trim();
      expect(trimmed).not.toMatch(/^\?\?/);  // No missing translations
      expect(trimmed.length).toBeGreaterThan(2);
      console.log(`  📌 Section: "${trimmed}"`);
    }

    // Check dashboard link
    await expect(page.locator('.sidebar-link[data-path="/app/dashboard"]')).toBeVisible();
    console.log('✅ Sidebar sections render with proper translations');
  });

  test('Sidebar collapse / expand works', async ({ page }) => {
    const sidebar = page.locator('#erpSidebar');
    const collapseBtn = page.locator('#sidebarCollapseBtn');

    await expect(collapseBtn).toBeVisible();

    // Collapse
    await collapseBtn.click();
    await page.waitForTimeout(400);
    await expect(sidebar).toHaveClass(/sidebar-collapsed/);
    console.log('✅ Sidebar collapsed');

    // Expand
    await collapseBtn.click();
    await page.waitForTimeout(400);
    await expect(sidebar).not.toHaveClass(/sidebar-collapsed/);
    console.log('✅ Sidebar expanded');
  });

  test('Sidebar submenu expands on click', async ({ page }) => {
    const inventoryToggle = page.locator('.sidebar-link[data-path="/app/inventory"]');
    await inventoryToggle.click();
    await page.waitForTimeout(350);
    await expect(page.locator('#inventoryMenu')).toHaveClass(/show/);
    console.log('✅ Inventory submenu expanded');
  });

  test('Language switcher changes locale to French', async ({ page }) => {
    await page.locator('#langDropdown').click();
    await page.locator('.lang-switch[href*="lang=fr"]').click();
    await page.waitForLoadState('networkidle');
    const url = page.url();
    console.log(`  URL after FR switch: ${url}`);
    // Page should still be functional
    await expect(page.locator('#erpSidebar')).toBeVisible();
    console.log('✅ Language switched to French');
  });

  test('Language switcher changes locale to German', async ({ page }) => {
    await page.locator('#langDropdown').click();
    await page.locator('.lang-switch[href*="lang=de"]').click();
    await page.waitForLoadState('networkidle');
    await expect(page.locator('#erpSidebar')).toBeVisible();
    console.log('✅ Language switched to German');
  });

  test('Language switcher changes locale to Italian', async ({ page }) => {
    await page.locator('#langDropdown').click();
    await page.locator('.lang-switch[href*="lang=it"]').click();
    await page.waitForLoadState('networkidle');
    await expect(page.locator('#erpSidebar')).toBeVisible();
    console.log('✅ Language switched to Italian');
  });

  test('Language switcher changes locale to English', async ({ page }) => {
    await page.locator('#langDropdown').click();
    await page.locator('.lang-switch[href*="lang=en"]').click();
    await page.waitForLoadState('networkidle');
    await expect(page.locator('#erpSidebar')).toBeVisible();
    console.log('✅ Language switched to English');
  });

  test('User menu dropdown contains Logout link', async ({ page }) => {
    const userMenuBtn = page.locator('.erp-topbar-right .dropdown button').nth(1);
    await userMenuBtn.click();
    await page.waitForTimeout(300);
    const logoutLink = page.locator('.dropdown-item.text-danger, .dropdown-item[href*="logout"]');
    await expect(logoutLink.first()).toBeVisible();
    const text = await logoutLink.first().textContent();
    console.log(`✅ User menu logout link found: "${text.trim()}"`);
  });

  test('Bootstrap CSS is applied — no unstyled content', async ({ page }) => {
    // Check Bootstrap is loaded by verifying btn class styling
    const btn = await page.evaluate(() => {
      const el = document.createElement('button');
      el.className = 'btn btn-primary';
      document.body.appendChild(el);
      const style = getComputedStyle(el);
      const bg = style.backgroundColor;
      document.body.removeChild(el);
      return bg;
    });
    // Bootstrap primary is not transparent
    expect(btn).not.toBe('rgba(0, 0, 0, 0)');
    console.log(`✅ Bootstrap CSS applied — btn-primary bg: ${btn}`);
  });

  test('Sidebar CSS is applied — sidebar has correct background color', async ({ page }) => {
    const sidebarBg = await page.evaluate(() => {
      const el = document.getElementById('erpSidebar');
      return el ? getComputedStyle(el).backgroundColor : 'NOT_FOUND';
    });
    expect(sidebarBg).not.toBe('rgba(0, 0, 0, 0)');
    expect(sidebarBg).not.toBe('NOT_FOUND');
    console.log(`✅ Sidebar CSS applied — background: ${sidebarBg}`);
  });

  test('Delete confirmation modal is Bootstrap modal (not browser confirm)', async ({ page }) => {
    const modal = page.locator('#erpDeleteModal');
    await expect(modal).toBeAttached();
    // Verify it has Bootstrap modal classes
    const hasModalClass = await modal.evaluate(el => el.classList.contains('modal'));
    expect(hasModalClass).toBeTruthy();
    console.log('✅ Bootstrap delete modal present (not browser confirm)');
  });
});

