/**
 * SwiftApp ERP — Custom JavaScript
 *
 * - Sidebar toggle (desktop collapse / mobile overlay) with localStorage
 * - Active nav link detection + submenu auto-expand
 * - Global delete confirmation modal (replaces browser confirm() dialogs)
 * - HTMX confirm intercept
 * - Swiss CHF number formatting
 * - Language switcher path preservation
 * - Notification badge polling
 */

/* ══════════════════════════════════════════════════════════
   SWISS NUMBER / CHF FORMATTING
   ══════════════════════════════════════════════════════════ */
const chfFormatter = new Intl.NumberFormat('de-CH', {
    style: 'currency',
    currency: 'CHF',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
});

function formatCHF(amount) {
    return chfFormatter.format(amount);
}

function applyChfFormat(root) {
    (root || document).querySelectorAll('[data-chf]').forEach(el => {
        const value = parseFloat(el.getAttribute('data-chf'));
        if (!isNaN(value)) el.textContent = formatCHF(value);
    });
}

/* ══════════════════════════════════════════════════════════
   GLOBAL DELETE CONFIRMATION MODAL
   Replaces all browser confirm() dialogs with a Bootstrap
   modal so the UI stays consistent and professional.
   ══════════════════════════════════════════════════════════ */
(function initDeleteModal() {
    const modalEl   = document.getElementById('erpDeleteModal');
    const msgEl     = document.getElementById('erpDeleteModalMessage');
    const confirmEl = document.getElementById('erpDeleteModalConfirm');

    if (!modalEl) return;

    let _bsModal  = null;
    let _callback = null;

    function getBsModal() {
        if (!_bsModal) _bsModal = new bootstrap.Modal(modalEl);
        return _bsModal;
    }

    /**
     * Show the delete confirmation modal.
     * @param {string}   message   - Body text to display
     * @param {Function} onConfirm - Called when user clicks "Delete"
     */
    window.erpConfirm = function(message, onConfirm) {
        if (msgEl) msgEl.textContent = message || 'Are you sure? This action cannot be undone.';
        _callback = onConfirm;
        getBsModal().show();
    };

    /* Confirm button handler */
    confirmEl?.addEventListener('click', () => {
        getBsModal().hide();
        if (typeof _callback === 'function') {
            _callback();
            _callback = null;
        }
    });

    /* Clean up callback when modal is dismissed without confirming */
    modalEl.addEventListener('hidden.bs.modal', () => {
        _callback = null;
    });

    /* ── Intercept all forms with onsubmit="return confirm('...')" ──
       We detect these at DOMContentLoaded, strip the inline handler,
       and wire up our modal instead. form.submit() skips onsubmit so
       the action proceeds cleanly after confirmation.                  */
    function interceptConfirmForms() {
        document.querySelectorAll('form[onsubmit]').forEach(form => {
            const attr = form.getAttribute('onsubmit') || '';
            if (!attr.includes('confirm(')) return;

            // Extract the message string from confirm('message')
            const match = attr.match(/confirm\(\s*['"](.+?)['"]\s*\)/);
            const message = match ? match[1] : 'Are you sure? This action cannot be undone.';

            // Remove the inline handler (so it won't block our listener)
            form.removeAttribute('onsubmit');

            form.addEventListener('submit', e => {
                e.preventDefault();
                window.erpConfirm(message, () => {
                    // Programmatic submit — does NOT re-fire the submit event
                    form.submit();
                });
            });
        });
    }

    document.addEventListener('DOMContentLoaded', interceptConfirmForms);
    // Also run immediately in case DOM is already ready
    if (document.readyState !== 'loading') interceptConfirmForms();
})();

/* ══════════════════════════════════════════════════════════
   SIDEBAR
   ══════════════════════════════════════════════════════════ */
(function initSidebar() {
    const STORAGE_KEY = 'erp_sidebar_collapsed';
    const MOBILE_BP   = 768; // px

    const sidebar      = document.getElementById('erpSidebar');
    const main         = document.getElementById('erpMain');
    const topbarToggle = document.getElementById('sidebarToggle');
    const collapseBtn  = document.getElementById('sidebarCollapseBtn');
    const backdrop     = document.getElementById('sidebarBackdrop');

    if (!sidebar) return;

    const isMobile = () => window.innerWidth < MOBILE_BP;

    function setCollapsed(collapsed) {
        if (isMobile()) return;
        sidebar.classList.toggle('sidebar-collapsed', collapsed);
        main?.classList.toggle('sidebar-collapsed', collapsed);
        localStorage.setItem(STORAGE_KEY, collapsed ? '1' : '0');
    }

    function toggleCollapse() {
        setCollapsed(!sidebar.classList.contains('sidebar-collapsed'));
    }

    function openMobileSidebar() {
        sidebar.classList.add('mobile-open');
        backdrop?.classList.add('active');
        document.body.style.overflow = 'hidden';
    }

    function closeMobileSidebar() {
        sidebar.classList.remove('mobile-open');
        backdrop?.classList.remove('active');
        document.body.style.overflow = '';
    }

    /* Restore persisted desktop collapse state */
    if (!isMobile() && localStorage.getItem(STORAGE_KEY) === '1') {
        sidebar.classList.add('sidebar-collapsed');
        main?.classList.add('sidebar-collapsed');
    }

    topbarToggle?.addEventListener('click', () => {
        if (isMobile()) {
            sidebar.classList.contains('mobile-open') ? closeMobileSidebar() : openMobileSidebar();
        } else {
            toggleCollapse();
        }
    });

    collapseBtn?.addEventListener('click', toggleCollapse);

    backdrop?.addEventListener('click', closeMobileSidebar);

    window.addEventListener('resize', () => {
        if (!isMobile()) closeMobileSidebar();
    }, { passive: true });

    document.addEventListener('keydown', e => {
        if (e.key === 'Escape' && isMobile()) closeMobileSidebar();
    });

    /* ── Active link detection ──────────────────────────── */
    (function activateCurrentNav() {
        const path = window.location.pathname;

        // Highlight best-matching sidebar-link (top-level)
        let bestLink = null;
        let bestLen  = 0;
        document.querySelectorAll('.sidebar-link[data-path]').forEach(link => {
            const lp = link.getAttribute('data-path');
            if (lp && (path === lp || (lp.length > 1 && path.startsWith(lp)))) {
                if (lp.length > bestLen) { bestLink = link; bestLen = lp.length; }
            }
        });
        if (bestLink) {
            bestLink.classList.add('active');
            const sub = bestLink.closest('.sidebar-submenu');
            if (sub) {
                sub.classList.add('show');
                document.querySelector(`[href="#${sub.id}"]`)?.setAttribute('aria-expanded', 'true');
            }
        }

        // Highlight matching sub-links
        document.querySelectorAll('.sidebar-sublink[data-path]').forEach(link => {
            const lp = link.getAttribute('data-path');
            if (lp && (path === lp || (lp.length > 1 && path.startsWith(lp)))) {
                link.classList.add('active');
                const sub = link.closest('.sidebar-submenu');
                if (sub) {
                    sub.classList.add('show');
                    document.querySelector(`[href="#${sub.id}"]`)?.setAttribute('aria-expanded', 'true');
                }
            }
        });
    })();
})();

/* ══════════════════════════════════════════════════════════
   LANGUAGE SWITCHER — preserve current path
   ══════════════════════════════════════════════════════════ */
document.addEventListener('DOMContentLoaded', () => {
    applyChfFormat();

    document.querySelectorAll('.lang-switch').forEach(link => {
        link.addEventListener('click', e => {
            e.preventDefault();
            const lang = new URL(link.href, window.location.origin).searchParams.get('lang');
            const url  = new URL(window.location.href);
            url.searchParams.set('lang', lang);
            window.location.href = url.toString();
        });
    });
});

/* ══════════════════════════════════════════════════════════
   HTMX HOOKS
   ══════════════════════════════════════════════════════════ */
document.addEventListener('htmx:afterSwap', e => {
    applyChfFormat(e.detail.target);
});

/* Replace HTMX's hx-confirm with our Bootstrap modal */
document.addEventListener('htmx:confirm', e => {
    e.preventDefault();
    const message = e.detail.question || 'Are you sure? This action cannot be undone.';
    window.erpConfirm(message, () => e.detail.issueRequest(true));
});

/* ══════════════════════════════════════════════════════════
   NOTIFICATION BADGE — poll unread count every 30 s
   ══════════════════════════════════════════════════════════ */
(function pollNotifications() {
    const badge = document.getElementById('notif-badge');
    if (!badge) return;

    function updateBadge() {
        fetch('/api/v1/notifications/unread-count')
            .then(r => r.ok ? r.json() : null)
            .then(data => {
                if (data?.count !== undefined) {
                    badge.textContent = data.count > 99 ? '99+' : data.count;
                    badge.style.display = data.count > 0 ? '' : 'none';
                }
            })
            .catch(() => { /* ignore polling errors */ });
    }

    setInterval(updateBadge, 30_000);
})();

/* ══════════════════════════════════════════════════════════
   DATATABLES — Auto-initialise all tables with data-dt
   ══════════════════════════════════════════════════════════ */
(function initDataTables() {
    if (typeof DataTable === 'undefined') return;

    function _initAll() {
        document.querySelectorAll('table[data-dt]').forEach(function (table) {
            if (DataTable.isDataTable(table)) return; // skip if already initialised
        // Read per-table config from data attributes
        const pageLength = parseInt(table.dataset.dtPageLength) || 25;
        const searching  = table.dataset.dtSearching !== 'false';
        const ordering   = table.dataset.dtOrdering  !== 'false';
        const paging     = table.dataset.dtPaging     !== 'false';

        // Collect disabled-sort columns (comma-separated column indices)
        const noSortCols = (table.dataset.dtNoSort || '')
            .split(',').filter(Boolean).map(i => ({ orderable: false, targets: parseInt(i) }));

        new DataTable(table, {
            pageLength,
            searching,
            ordering,
            paging,
            language: {
                search:         'Suchen:',
                lengthMenu:     '_MENU_ Einträge anzeigen',
                info:           '_START_ – _END_ von _TOTAL_ Einträgen',
                infoEmpty:      '0 – 0 von 0 Einträgen',
                infoFiltered:   '(gefiltert aus _MAX_ Einträgen)',
                paginate: {
                    first:    '«',
                    last:     '»',
                    next:     '›',
                    previous: '‹',
                },
                emptyTable:     'Keine Daten vorhanden',
                zeroRecords:    'Keine passenden Einträge gefunden',
                loadingRecords: 'Wird geladen…',
                processing:     'Bitte warten…',
            },
            columnDefs: noSortCols,
            responsive: true,
            stateSave:  false,
        });
    });
    }

    // Run when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', _initAll);
    } else {
        _initAll();
    }
})();

