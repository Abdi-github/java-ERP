-- ============================================================
-- V022: Seed data for notifications and mail campaigns
-- ============================================================

-- ── Notifications ──────────────────────────────────────────
INSERT INTO notifications (id, recipient_user_id, recipient_email, template_code, channel, status, subject, body, reference_type, created_by, updated_by) VALUES
  (gen_random_uuid(), '60000001-0000-0000-0000-000000000001', 'admin@swiftapp.ch',
   'SYSTEM_WELCOME', 'IN_APP', 'READ',
   'Willkommen bei SwiftApp ERP',
   'Willkommen im SwiftApp ERP-System. Ihr Konto ist aktiv und einsatzbereit.',
   NULL, 'system', 'system'),

  (gen_random_uuid(), '60000001-0000-0000-0000-000000000001', 'admin@swiftapp.ch',
   'ORDER_CONFIRMED', 'IN_APP', 'SENT',
   'Bestellung SO-2026-0042 bestätigt',
   'Die Verkaufsbestellung SO-2026-0042 wurde vom Kunden bestätigt und ist bereit zur Bearbeitung.',
   'SALES_ORDER', 'system', 'system'),

  (gen_random_uuid(), '60000001-0000-0000-0000-000000000001', 'admin@swiftapp.ch',
   'LOW_STOCK_ALERT', 'IN_APP', 'SENT',
   'Lagerbestand kritisch: Saphirglas 40mm',
   'Der Lagerbestand von Saphirglas Flach 40mm (CRY-SAP40) ist unter den Mindestbestand gefallen. Aktuell: 12 Stück, Mindestbestand: 200 Stück.',
   'INVENTORY', 'system', 'system'),

  (gen_random_uuid(), '60000001-0000-0000-0000-000000000002', 'lukas.mueller@swiftapp.ch',
   'ORDER_SHIPPED', 'IN_APP', 'READ',
   'Lieferung SO-2026-0038 versandt',
   'Die Bestellung SO-2026-0038 wurde heute versandt. Tracking-Nummer: CH123456789.',
   'SALES_ORDER', 'system', 'system'),

  (gen_random_uuid(), '60000001-0000-0000-0000-000000000003', 'anna.bianchi@swiftapp.ch',
   'TASK_ASSIGNED', 'IN_APP', 'SENT',
   'Neue Aufgabe zugewiesen: Angebotsunterlagen Boutique Zürich',
   'Ihnen wurde eine neue Aufgabe zugewiesen: Angebotsunterlagen für die Boutique Zürich vorbereiten. Fälligkeitsdatum: 05.04.2026.',
   NULL, 'system', 'system'),

  (gen_random_uuid(), '60000001-0000-0000-0000-000000000004', 'markus.keller@swiftapp.ch',
   'PRODUCTION_ORDER_CREATED', 'IN_APP', 'SENT',
   'Neuer Produktionsauftrag: PO-2026-0015',
   'Ein neuer Produktionsauftrag PO-2026-0015 für 50 Stück SwiftApp Alpine 40 wurde erstellt und ist bereit zur Produktion.',
   'PRODUCTION_ORDER', 'system', 'system'),

  (gen_random_uuid(), '60000001-0000-0000-0000-000000000005', 'camille.dubois@swiftapp.ch',
   'INVOICE_DUE', 'IN_APP', 'SENT',
   'Rechnung RE-2026-0089 fällig in 3 Tagen',
   'Die Rechnung RE-2026-0089 über CHF 18''450.00 ist in 3 Tagen fällig (02.04.2026). Bitte stellen Sie die fristgerechte Zahlung sicher.',
   'INVOICE', 'system', 'system'),

  (gen_random_uuid(), '60000001-0000-0000-0000-000000000001', 'admin@swiftapp.ch',
   'SYSTEM_BACKUP', 'IN_APP', 'DISMISSED',
   'System-Backup erfolgreich abgeschlossen',
   'Das tägliche Datenbank-Backup wurde um 02:00 Uhr erfolgreich abgeschlossen. Backup-Grösse: 2.4 GB.',
   NULL, 'system', 'system');

-- ── Mail Campaigns ─────────────────────────────────────────
INSERT INTO mail_campaigns (id, name, description, template_code, locale, target_segment, status, total_recipients, sent_count, failed_count, scheduled_at, created_by, updated_by) VALUES
  (gen_random_uuid(),
   'Q1 2026 Produktneuheiten',
   'Ankündigung der neuen Alpine 40 Kollektion für alle registrierten Kunden.',
   'PRODUCT_NEWSLETTER',
   'de',
   'ALL_CUSTOMERS',
   'COMPLETED',
   142, 138, 4,
   '2026-01-15 09:00:00+01',
   'system', 'system'),

  (gen_random_uuid(),
   'VIP Kundeneinladung – Baselworld Preview',
   'Exklusive Einladung zur Baselworld Vorpremiere für Top-Kunden.',
   'VIP_INVITATION',
   'de',
   'VIP_CUSTOMERS',
   'COMPLETED',
   28, 28, 0,
   '2026-02-01 08:00:00+01',
   'system', 'system'),

  (gen_random_uuid(),
   'Frühling Kollektion 2026',
   'Frühjahrs-Newsletter mit der neuen Chronoswiss 42 Vert Kollektion.',
   'PRODUCT_NEWSLETTER',
   'fr',
   'ALL_CUSTOMERS',
   'QUEUED',
   200, 0, 0,
   '2026-04-01 10:00:00+02',
   'system', 'system'),

  (gen_random_uuid(),
   'Wartungserinnerung – Service-Aktion',
   'Erinnerung an den empfohlenen 5-Jahres-Service für Kunden mit älteren Modellen.',
   'SERVICE_REMINDER',
   'de',
   'ALL_CUSTOMERS',
   'DRAFT',
   0, 0, 0,
   NULL,
   'system', 'system');

