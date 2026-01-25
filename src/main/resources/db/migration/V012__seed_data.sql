-- ===========================================================
-- V012 — Seed Data: Realistic Swiss watch manufacturer data
-- SwiftApp ERP — Swiss Watch Manufacturing & Retail
-- Company: SwiftApp Horlogerie SA, Biel/Bienne, Switzerland
-- ===========================================================

-- ============================
-- 1. MASTERDATA — Categories
-- ============================
INSERT INTO categories (id, name, description, parent_id, created_by) VALUES
  -- Top-level categories
  ('a0000001-0000-0000-0000-000000000001', 'Uhren',                  'Fertige Uhren / Finished watches',                NULL, 'system'),
  ('a0000001-0000-0000-0000-000000000002', 'Uhrwerke',               'Mechanische und Quarz-Uhrwerke / Movements',      NULL, 'system'),
  ('a0000001-0000-0000-0000-000000000003', 'Gehäuse',                'Uhrengehäuse / Watch cases',                       NULL, 'system'),
  ('a0000001-0000-0000-0000-000000000004', 'Zifferblätter',          'Zifferblätter / Watch dials',                      NULL, 'system'),
  ('a0000001-0000-0000-0000-000000000005', 'Armbänder',              'Uhrenarmbänder / Watch straps & bracelets',        NULL, 'system'),
  ('a0000001-0000-0000-0000-000000000006', 'Rohmaterialien',         'Rohmaterialien / Raw materials',                   NULL, 'system'),
  ('a0000001-0000-0000-0000-000000000007', 'Verpackung',             'Verpackung & Etuis / Packaging & boxes',           NULL, 'system'),
  -- Sub-categories: Uhren
  ('a0000001-0000-0000-0000-000000000010', 'Automatik',              'Automatikuhren / Automatic watches',               'a0000001-0000-0000-0000-000000000001', 'system'),
  ('a0000001-0000-0000-0000-000000000011', 'Handaufzug',             'Handaufzuguhren / Manual-wind watches',            'a0000001-0000-0000-0000-000000000001', 'system'),
  ('a0000001-0000-0000-0000-000000000012', 'Chronographen',          'Chronographen / Chronographs',                     'a0000001-0000-0000-0000-000000000001', 'system'),
  ('a0000001-0000-0000-0000-000000000013', 'Komplikationen',         'Grosse Komplikationen / Grand complications',      'a0000001-0000-0000-0000-000000000001', 'system'),
  ('a0000001-0000-0000-0000-000000000014', 'Taucheruhren',           'Taucheruhren / Dive watches',                      'a0000001-0000-0000-0000-000000000001', 'system'),
  -- Sub-categories: Uhrwerke
  ('a0000001-0000-0000-0000-000000000020', 'Kaliber Automatik',      'Automatik-Kaliber / Automatic calibers',           'a0000001-0000-0000-0000-000000000002', 'system'),
  ('a0000001-0000-0000-0000-000000000021', 'Kaliber Handaufzug',     'Handaufzug-Kaliber / Manual-wind calibers',        'a0000001-0000-0000-0000-000000000002', 'system'),
  ('a0000001-0000-0000-0000-000000000022', 'Kaliber Chronograph',    'Chronographen-Kaliber / Chronograph calibers',     'a0000001-0000-0000-0000-000000000002', 'system'),
  -- Sub-categories: Rohmaterialien
  ('a0000001-0000-0000-0000-000000000060', 'Edelmetalle',            'Edelmetalle / Precious metals',                    'a0000001-0000-0000-0000-000000000006', 'system'),
  ('a0000001-0000-0000-0000-000000000061', 'Edelsteine',             'Edelsteine / Gemstones',                           'a0000001-0000-0000-0000-000000000006', 'system'),
  ('a0000001-0000-0000-0000-000000000062', 'Gläser',                 'Saphirgläser / Sapphire crystals',                 'a0000001-0000-0000-0000-000000000006', 'system');

-- ============================
-- 2. MASTERDATA — Units of Measure
-- ============================
INSERT INTO units_of_measure (id, code, name, description, created_by) VALUES
  ('b0000001-0000-0000-0000-000000000001', 'PCS',  'Stück',       'Einzelstück / Piece',              'system'),
  ('b0000001-0000-0000-0000-000000000002', 'SET',  'Set',         'Set / Kit',                        'system'),
  ('b0000001-0000-0000-0000-000000000003', 'G',    'Gramm',       'Gramm / Gram',                     'system'),
  ('b0000001-0000-0000-0000-000000000004', 'KG',   'Kilogramm',   'Kilogramm / Kilogram',             'system'),
  ('b0000001-0000-0000-0000-000000000005', 'M',    'Meter',       'Meter / Meter',                    'system'),
  ('b0000001-0000-0000-0000-000000000006', 'CM',   'Zentimeter',  'Zentimeter / Centimeter',          'system'),
  ('b0000001-0000-0000-0000-000000000007', 'MM',   'Millimeter',  'Millimeter / Millimeter',          'system'),
  ('b0000001-0000-0000-0000-000000000008', 'L',    'Liter',       'Liter / Liter',                    'system'),
  ('b0000001-0000-0000-0000-000000000009', 'CT',   'Karat',       'Karat / Carat (gemstones)',        'system'),
  ('b0000001-0000-0000-0000-000000000010', 'OZT',  'Feinunze',    'Feinunze / Troy ounce (metals)',   'system'),
  ('b0000001-0000-0000-0000-000000000011', 'H',    'Stunde',      'Stunde / Hour',                    'system'),
  ('b0000001-0000-0000-0000-000000000012', 'BOX',  'Schachtel',   'Schachtel / Box',                  'system');

-- ============================
-- 3. MASTERDATA — Materials (watch components & raw materials)
-- ============================
INSERT INTO materials (id, sku, name, description, category_id, unit_of_measure_id, unit_price, vat_rate, minimum_stock, created_by) VALUES
  -- Movements / Calibers
  ('c0000001-0000-0000-0000-000000000001', 'MVT-SA3135',  'Kaliber SA-3135 Automatik',       'Hauseigenes Automatik-Kaliber, 28''800 A/h, 48h Gangreserve',   'a0000001-0000-0000-0000-000000000020', 'b0000001-0000-0000-0000-000000000001', 1250.0000, 'STANDARD_8_1', 50, 'system'),
  ('c0000001-0000-0000-0000-000000000002', 'MVT-SA7750',  'Kaliber SA-7750 Chronograph',     'Valjoux-basiertes Chronograph-Kaliber, 28''800 A/h',            'a0000001-0000-0000-0000-000000000022', 'b0000001-0000-0000-0000-000000000001', 2100.0000, 'STANDARD_8_1', 30, 'system'),
  ('c0000001-0000-0000-0000-000000000003', 'MVT-SA6498',  'Kaliber SA-6498 Handaufzug',      'Handaufzug-Kaliber, Unitas-Basis, 46h Gangreserve',             'a0000001-0000-0000-0000-000000000021', 'b0000001-0000-0000-0000-000000000001',  850.0000, 'STANDARD_8_1', 25, 'system'),
  ('c0000001-0000-0000-0000-000000000004', 'MVT-SA3186',  'Kaliber SA-3186 GMT Automatik',   'GMT-Automatik-Kaliber mit 24h-Anzeige',                         'a0000001-0000-0000-0000-000000000020', 'b0000001-0000-0000-0000-000000000001', 1800.0000, 'STANDARD_8_1', 20, 'system'),
  -- Cases
  ('c0000001-0000-0000-0000-000000000010', 'CAS-SS40',    'Gehäuse Edelstahl 40mm',          'Oyster-Stil Edelstahlgehäuse 316L, 40mm, 100m wasserdicht',     'a0000001-0000-0000-0000-000000000003', 'b0000001-0000-0000-0000-000000000001',  380.0000, 'STANDARD_8_1', 100, 'system'),
  ('c0000001-0000-0000-0000-000000000011', 'CAS-SS42',    'Gehäuse Edelstahl 42mm',          'Sport-Gehäuse 316L, 42mm, 200m wasserdicht',                    'a0000001-0000-0000-0000-000000000003', 'b0000001-0000-0000-0000-000000000001',  420.0000, 'STANDARD_8_1',  80, 'system'),
  ('c0000001-0000-0000-0000-000000000012', 'CAS-TI44',    'Gehäuse Titan 44mm',              'Titan-Gehäuse Grade 5, 44mm, 300m wasserdicht',                 'a0000001-0000-0000-0000-000000000003', 'b0000001-0000-0000-0000-000000000001',  680.0000, 'STANDARD_8_1',  40, 'system'),
  ('c0000001-0000-0000-0000-000000000013', 'CAS-RG40',    'Gehäuse 18K Roségold 40mm',       '18K Roségold-Gehäuse, 40mm, poliert',                           'a0000001-0000-0000-0000-000000000003', 'b0000001-0000-0000-0000-000000000001', 4200.0000, 'STANDARD_8_1',  15, 'system'),
  -- Dials
  ('c0000001-0000-0000-0000-000000000020', 'DIA-BLK01',   'Zifferblatt Schwarz Sunburst',    'Schwarz Sunburst-Zifferblatt, applizierte Indizes',             'a0000001-0000-0000-0000-000000000004', 'b0000001-0000-0000-0000-000000000001',  120.0000, 'STANDARD_8_1', 150, 'system'),
  ('c0000001-0000-0000-0000-000000000021', 'DIA-BLU01',   'Zifferblatt Blau Fumé',           'Blaues Fumé-Zifferblatt, Sonnenschliff',                        'a0000001-0000-0000-0000-000000000004', 'b0000001-0000-0000-0000-000000000001',  140.0000, 'STANDARD_8_1', 120, 'system'),
  ('c0000001-0000-0000-0000-000000000022', 'DIA-WHT01',   'Zifferblatt Weiss Lacquer',       'Weisses Lacquer-Zifferblatt, gedruckte römische Ziffern',       'a0000001-0000-0000-0000-000000000004', 'b0000001-0000-0000-0000-000000000001',  110.0000, 'STANDARD_8_1', 100, 'system'),
  ('c0000001-0000-0000-0000-000000000023', 'DIA-GRN01',   'Zifferblatt Grün Sunburst',       'Grünes Sunburst-Zifferblatt, Super-LumiNova Indizes',           'a0000001-0000-0000-0000-000000000004', 'b0000001-0000-0000-0000-000000000001',  130.0000, 'STANDARD_8_1',  80, 'system'),
  -- Straps & Bracelets
  ('c0000001-0000-0000-0000-000000000030', 'BRC-SS20',    'Armband Edelstahl Oyster 20mm',   'Oyster-Armband 316L Edelstahl, Faltschliesse',                  'a0000001-0000-0000-0000-000000000005', 'b0000001-0000-0000-0000-000000000001',  280.0000, 'STANDARD_8_1', 120, 'system'),
  ('c0000001-0000-0000-0000-000000000031', 'BRC-SS22',    'Armband Edelstahl Jubilee 22mm',  'Jubilee-Armband 316L Edelstahl, Faltschliesse',                 'a0000001-0000-0000-0000-000000000005', 'b0000001-0000-0000-0000-000000000001',  320.0000, 'STANDARD_8_1',  80, 'system'),
  ('c0000001-0000-0000-0000-000000000032', 'STR-CROC20',  'Lederband Krokodil 20mm',         'Alligatorlederband, Handgenäht, mit Dornschliesse',             'a0000001-0000-0000-0000-000000000005', 'b0000001-0000-0000-0000-000000000001',  450.0000, 'STANDARD_8_1',  60, 'system'),
  ('c0000001-0000-0000-0000-000000000033', 'STR-RBR22',   'Kautschukband 22mm',              'FKM-Kautschukband, Tauchertauglich, Schnellwechsel',            'a0000001-0000-0000-0000-000000000005', 'b0000001-0000-0000-0000-000000000001',   85.0000, 'STANDARD_8_1', 100, 'system'),
  -- Sapphire crystals
  ('c0000001-0000-0000-0000-000000000040', 'CRY-SAP40',   'Saphirglas Flach 40mm',           'Doppelt entspiegeltes Saphirglas, 40mm',                        'a0000001-0000-0000-0000-000000000062', 'b0000001-0000-0000-0000-000000000001',   95.0000, 'STANDARD_8_1', 200, 'system'),
  ('c0000001-0000-0000-0000-000000000041', 'CRY-SAP42',   'Saphirglas Gewölbt 42mm',         'Doppelt entspiegeltes gewölbtes Saphirglas, 42mm',              'a0000001-0000-0000-0000-000000000062', 'b0000001-0000-0000-0000-000000000001',  110.0000, 'STANDARD_8_1', 150, 'system'),
  -- Raw materials
  ('c0000001-0000-0000-0000-000000000050', 'RAW-GOLD18',  '18K Gold (Roségold)',             '18 Karat Roségold, 750/1000 Feingehalt, pro Gramm',            'a0000001-0000-0000-0000-000000000060', 'b0000001-0000-0000-0000-000000000003',   72.5000, 'EXEMPT',     500, 'system'),
  ('c0000001-0000-0000-0000-000000000051', 'RAW-SS316L',  'Edelstahl 316L',                  'Chirurgischer Edelstahl 316L, pro Kilogramm',                  'a0000001-0000-0000-0000-000000000060', 'b0000001-0000-0000-0000-000000000004',    8.5000, 'STANDARD_8_1', 200, 'system'),
  ('c0000001-0000-0000-0000-000000000052', 'RAW-TITAN5',  'Titan Grade 5',                   'Titan Grade 5 (Ti-6Al-4V), pro Kilogramm',                     'a0000001-0000-0000-0000-000000000060', 'b0000001-0000-0000-0000-000000000004',   45.0000, 'STANDARD_8_1', 100, 'system'),
  ('c0000001-0000-0000-0000-000000000053', 'RAW-DIA',     'Diamanten VVS1',                  'Diamanten, VVS1 Reinheit, D Farbe, pro Karat',                 'a0000001-0000-0000-0000-000000000061', 'b0000001-0000-0000-0000-000000000009', 8500.0000, 'EXEMPT',      20, 'system'),
  ('c0000001-0000-0000-0000-000000000054', 'RAW-LUMI',    'Super-LumiNova C3',               'Super-LumiNova C3, Leuchtmasse, pro Gramm',                    'a0000001-0000-0000-0000-000000000006', 'b0000001-0000-0000-0000-000000000003',   35.0000, 'STANDARD_8_1',  50, 'system'),
  -- Packaging
  ('c0000001-0000-0000-0000-000000000060', 'PKG-BOX01',   'Uhrenbox Holz Standard',          'Hochglanz-Holzbox, Innenausstattung Alcantara',                'a0000001-0000-0000-0000-000000000007', 'b0000001-0000-0000-0000-000000000012',   65.0000, 'STANDARD_8_1', 200, 'system'),
  ('c0000001-0000-0000-0000-000000000061', 'PKG-BOX02',   'Uhrenbox Leder Premium',          'Lederbox mit Echtheitszertifikat-Fach, Reiseetui',             'a0000001-0000-0000-0000-000000000007', 'b0000001-0000-0000-0000-000000000012',  150.0000, 'STANDARD_8_1', 100, 'system'),
  -- Miscellaneous small parts
  ('c0000001-0000-0000-0000-000000000070', 'PRT-CROWN01', 'Krone Edelstahl verschraubt',     'Verschraubte Krone 316L, 6mm',                                 'a0000001-0000-0000-0000-000000000003', 'b0000001-0000-0000-0000-000000000001',   22.0000, 'STANDARD_8_1', 300, 'system'),
  ('c0000001-0000-0000-0000-000000000071', 'PRT-GASKET',  'Dichtungsring-Set',               'O-Ring Dichtungsset (Krone, Gehäuseboden, Glas)',               'a0000001-0000-0000-0000-000000000003', 'b0000001-0000-0000-0000-000000000002',    8.5000, 'STANDARD_8_1', 500, 'system'),
  ('c0000001-0000-0000-0000-000000000072', 'PRT-ROTOR',   'Rotor Wolfram',                   'Wolfram-Schwungmasse / Automatic rotor',                       'a0000001-0000-0000-0000-000000000002', 'b0000001-0000-0000-0000-000000000001',   75.0000, 'STANDARD_8_1', 100, 'system');

-- ============================
-- 4. MASTERDATA — Products (finished watches)
-- ============================
INSERT INTO products (id, sku, name, description, category_id, unit_price, list_price, vat_rate, created_by) VALUES
  ('d0000001-0000-0000-0000-000000000001', 'SW-ALP-40SS-BLK', 'SwiftApp Alpine 40',           'Automatik-Dreizeigeruhr, Edelstahl 40mm, Schwarzes Sunburst-Zifferblatt, 100m wasserdicht',                    'a0000001-0000-0000-0000-000000000010', 3250.0000, 3950.0000, 'STANDARD_8_1', 'system'),
  ('d0000001-0000-0000-0000-000000000002', 'SW-ALP-40SS-BLU', 'SwiftApp Alpine 40 Bleu',      'Automatik-Dreizeigeruhr, Edelstahl 40mm, Blaues Fumé-Zifferblatt, 100m wasserdicht',                          'a0000001-0000-0000-0000-000000000010', 3250.0000, 3950.0000, 'STANDARD_8_1', 'system'),
  ('d0000001-0000-0000-0000-000000000003', 'SW-ALP-40RG-WHT', 'SwiftApp Alpine 40 Or Rose',   'Automatik-Dreizeigeruhr, 18K Roségold 40mm, Weisses Lacquer-Zifferblatt, Krokodillederband',                  'a0000001-0000-0000-0000-000000000013', 12500.0000, 14900.0000, 'STANDARD_8_1', 'system'),
  ('d0000001-0000-0000-0000-000000000004', 'SW-CHR-42SS-BLK', 'SwiftApp Chronoswiss 42',      'Chronograph, Edelstahl 42mm, Schwarzes Zifferblatt, Tachymeter-Lünette, 100m wasserdicht',                    'a0000001-0000-0000-0000-000000000012', 5800.0000, 6950.0000, 'STANDARD_8_1', 'system'),
  ('d0000001-0000-0000-0000-000000000005', 'SW-CHR-42SS-GRN', 'SwiftApp Chronoswiss 42 Vert', 'Chronograph, Edelstahl 42mm, Grünes Sunburst-Zifferblatt, Tachymeter-Lünette, 100m wasserdicht',              'a0000001-0000-0000-0000-000000000012', 5800.0000, 6950.0000, 'STANDARD_8_1', 'system'),
  ('d0000001-0000-0000-0000-000000000006', 'SW-DIV-44TI-BLU', 'SwiftApp Abyss 44',            'Taucheruhr, Titan 44mm, Blaues Zifferblatt, Keramik-Drehlünette, 300m wasserdicht, Kautschukband',            'a0000001-0000-0000-0000-000000000014', 4950.0000, 5900.0000, 'STANDARD_8_1', 'system'),
  ('d0000001-0000-0000-0000-000000000007', 'SW-GMT-42SS-BLK', 'SwiftApp Voyager GMT',         'GMT-Automatik, Edelstahl 42mm, Schwarzes Zifferblatt, Bi-Color-Lünette, 24h-Anzeige, 100m wasserdicht',       'a0000001-0000-0000-0000-000000000010', 5200.0000, 6250.0000, 'STANDARD_8_1', 'system'),
  ('d0000001-0000-0000-0000-000000000008', 'SW-DRS-40SS-WHT', 'SwiftApp Elegance 40',         'Handaufzug-Dressuhr, Edelstahl 40mm, Weisses Zifferblatt, Krokodillederband, 30m wasserdicht',                'a0000001-0000-0000-0000-000000000011', 2850.0000, 3450.0000, 'STANDARD_8_1', 'system'),
  ('d0000001-0000-0000-0000-000000000009', 'SW-ALP-40SS-GRN', 'SwiftApp Alpine 40 Vert',      'Automatik-Dreizeigeruhr, Edelstahl 40mm, Grünes Sunburst-Zifferblatt, 100m wasserdicht',                      'a0000001-0000-0000-0000-000000000010', 3250.0000, 3950.0000, 'STANDARD_8_1', 'system'),
  ('d0000001-0000-0000-0000-000000000010', 'SW-LTD-40RG-DIA', 'SwiftApp Alpine Limited Diamant', 'Limited Edition Automatik, 18K Roségold 40mm, Diamantlünette (1.2ct), Weisses Perlmutt, Nr. XX/50',       'a0000001-0000-0000-0000-000000000013', 28500.0000, 34900.0000, 'STANDARD_8_1', 'system');

-- ============================
-- 5. MASTERDATA — Bill of Materials
-- ============================
-- Alpine 40 (SW-ALP-40SS-BLK)
INSERT INTO bill_of_materials (product_id, material_id, quantity, unit_of_measure_id, position, notes, created_by) VALUES
  ('d0000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000001', 1, 'b0000001-0000-0000-0000-000000000001', 1, 'Kaliber SA-3135', 'system'),
  ('d0000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000010', 1, 'b0000001-0000-0000-0000-000000000001', 2, 'Gehäuse SS 40mm', 'system'),
  ('d0000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000020', 1, 'b0000001-0000-0000-0000-000000000001', 3, 'Zifferblatt Schwarz', 'system'),
  ('d0000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000030', 1, 'b0000001-0000-0000-0000-000000000001', 4, 'Oyster-Armband', 'system'),
  ('d0000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000040', 1, 'b0000001-0000-0000-0000-000000000001', 5, 'Saphirglas 40mm', 'system'),
  ('d0000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000070', 1, 'b0000001-0000-0000-0000-000000000001', 6, 'Krone', 'system'),
  ('d0000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000071', 1, 'b0000001-0000-0000-0000-000000000002', 7, 'Dichtungsset', 'system'),
  ('d0000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000072', 1, 'b0000001-0000-0000-0000-000000000001', 8, 'Rotor', 'system'),
  ('d0000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000060', 1, 'b0000001-0000-0000-0000-000000000012', 9, 'Verpackung Standard', 'system');

-- Chronoswiss 42 (SW-CHR-42SS-BLK)
INSERT INTO bill_of_materials (product_id, material_id, quantity, unit_of_measure_id, position, notes, created_by) VALUES
  ('d0000001-0000-0000-0000-000000000004', 'c0000001-0000-0000-0000-000000000002', 1, 'b0000001-0000-0000-0000-000000000001', 1, 'Kaliber SA-7750 Chrono', 'system'),
  ('d0000001-0000-0000-0000-000000000004', 'c0000001-0000-0000-0000-000000000011', 1, 'b0000001-0000-0000-0000-000000000001', 2, 'Gehäuse SS 42mm', 'system'),
  ('d0000001-0000-0000-0000-000000000004', 'c0000001-0000-0000-0000-000000000020', 1, 'b0000001-0000-0000-0000-000000000001', 3, 'Zifferblatt Schwarz', 'system'),
  ('d0000001-0000-0000-0000-000000000004', 'c0000001-0000-0000-0000-000000000031', 1, 'b0000001-0000-0000-0000-000000000001', 4, 'Jubilee-Armband 22mm', 'system'),
  ('d0000001-0000-0000-0000-000000000004', 'c0000001-0000-0000-0000-000000000041', 1, 'b0000001-0000-0000-0000-000000000001', 5, 'Saphirglas gewölbt 42mm', 'system'),
  ('d0000001-0000-0000-0000-000000000004', 'c0000001-0000-0000-0000-000000000070', 2, 'b0000001-0000-0000-0000-000000000001', 6, 'Drücker (2x) + Krone', 'system'),
  ('d0000001-0000-0000-0000-000000000004', 'c0000001-0000-0000-0000-000000000071', 1, 'b0000001-0000-0000-0000-000000000002', 7, 'Dichtungsset', 'system'),
  ('d0000001-0000-0000-0000-000000000004', 'c0000001-0000-0000-0000-000000000072', 1, 'b0000001-0000-0000-0000-000000000001', 8, 'Rotor', 'system'),
  ('d0000001-0000-0000-0000-000000000004', 'c0000001-0000-0000-0000-000000000061', 1, 'b0000001-0000-0000-0000-000000000012', 9, 'Verpackung Premium', 'system');

-- Abyss 44 Dive (SW-DIV-44TI-BLU)
INSERT INTO bill_of_materials (product_id, material_id, quantity, unit_of_measure_id, position, notes, created_by) VALUES
  ('d0000001-0000-0000-0000-000000000006', 'c0000001-0000-0000-0000-000000000001', 1, 'b0000001-0000-0000-0000-000000000001', 1, 'Kaliber SA-3135', 'system'),
  ('d0000001-0000-0000-0000-000000000006', 'c0000001-0000-0000-0000-000000000012', 1, 'b0000001-0000-0000-0000-000000000001', 2, 'Gehäuse Titan 44mm', 'system'),
  ('d0000001-0000-0000-0000-000000000006', 'c0000001-0000-0000-0000-000000000021', 1, 'b0000001-0000-0000-0000-000000000001', 3, 'Zifferblatt Blau', 'system'),
  ('d0000001-0000-0000-0000-000000000006', 'c0000001-0000-0000-0000-000000000033', 1, 'b0000001-0000-0000-0000-000000000001', 4, 'Kautschukband 22mm', 'system'),
  ('d0000001-0000-0000-0000-000000000006', 'c0000001-0000-0000-0000-000000000041', 1, 'b0000001-0000-0000-0000-000000000001', 5, 'Saphirglas gewölbt 42mm', 'system'),
  ('d0000001-0000-0000-0000-000000000006', 'c0000001-0000-0000-0000-000000000070', 1, 'b0000001-0000-0000-0000-000000000001', 6, 'Krone verschraubt', 'system'),
  ('d0000001-0000-0000-0000-000000000006', 'c0000001-0000-0000-0000-000000000071', 2, 'b0000001-0000-0000-0000-000000000002', 7, 'Dichtungsset (doppelt für 300m)', 'system'),
  ('d0000001-0000-0000-0000-000000000006', 'c0000001-0000-0000-0000-000000000072', 1, 'b0000001-0000-0000-0000-000000000001', 8, 'Rotor', 'system'),
  ('d0000001-0000-0000-0000-000000000006', 'c0000001-0000-0000-0000-000000000061', 1, 'b0000001-0000-0000-0000-000000000012', 9, 'Verpackung Premium', 'system');

-- ============================
-- 6. INVENTORY — Warehouses
-- ============================
INSERT INTO warehouses (id, code, name, description, address, created_by) VALUES
  ('e0000001-0000-0000-0000-000000000001', 'WH-MAIN',  'Hauptlager Biel',           'Zentrallager, Produktion und Versand',                    'Rue de la Gare 42, 2502 Biel/Bienne',           'system'),
  ('e0000001-0000-0000-0000-000000000002', 'WH-COMP',  'Komponentenlager',          'Lager für Uhrenkomponenten und Halbfabrikate',            'Rue de la Gare 42, 2502 Biel/Bienne, Gebäude B', 'system'),
  ('e0000001-0000-0000-0000-000000000003', 'WH-RAW',   'Rohmateriallager',          'Tresor und Lager für Edelmetalle, Edelsteine',            'Rue de la Gare 42, 2502 Biel/Bienne, Tresor',    'system'),
  ('e0000001-0000-0000-0000-000000000004', 'WH-FIN',   'Fertigwarenlager',          'Lager für versandfertige Uhren',                          'Rue de la Gare 42, 2502 Biel/Bienne, Gebäude C', 'system'),
  ('e0000001-0000-0000-0000-000000000005', 'WH-ZH',    'Showroom Zürich',           'Retail-Lager und Ausstellungsraum Bahnhofstrasse Zürich', 'Bahnhofstrasse 15, 8001 Zürich',                  'system'),
  ('e0000001-0000-0000-0000-000000000006', 'WH-GE',    'Showroom Genf',             'Retail-Lager und Ausstellungsraum Genf',                  'Rue du Rhône 48, 1204 Genève',                    'system');

-- ============================
-- 7. INVENTORY — Stock Levels (materials in warehouses)
-- ============================
INSERT INTO stock_levels (item_id, item_type, warehouse_id, quantity_on_hand, quantity_reserved, created_by) VALUES
  -- Components in WH-COMP
  ('c0000001-0000-0000-0000-000000000001', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 85, 12, 'system'),
  ('c0000001-0000-0000-0000-000000000002', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 42, 8, 'system'),
  ('c0000001-0000-0000-0000-000000000003', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 30, 5, 'system'),
  ('c0000001-0000-0000-0000-000000000004', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 25, 3, 'system'),
  ('c0000001-0000-0000-0000-000000000010', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 130, 18, 'system'),
  ('c0000001-0000-0000-0000-000000000011', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 95, 10, 'system'),
  ('c0000001-0000-0000-0000-000000000012', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 48, 6, 'system'),
  ('c0000001-0000-0000-0000-000000000013', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 18, 2, 'system'),
  ('c0000001-0000-0000-0000-000000000020', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 180, 25, 'system'),
  ('c0000001-0000-0000-0000-000000000021', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 140, 15, 'system'),
  ('c0000001-0000-0000-0000-000000000022', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 110, 10, 'system'),
  ('c0000001-0000-0000-0000-000000000023', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 90, 8, 'system'),
  ('c0000001-0000-0000-0000-000000000030', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 150, 20, 'system'),
  ('c0000001-0000-0000-0000-000000000031', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 95, 12, 'system'),
  ('c0000001-0000-0000-0000-000000000032', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 70, 8, 'system'),
  ('c0000001-0000-0000-0000-000000000033', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 120, 10, 'system'),
  ('c0000001-0000-0000-0000-000000000040', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 220, 30, 'system'),
  ('c0000001-0000-0000-0000-000000000041', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 165, 20, 'system'),
  ('c0000001-0000-0000-0000-000000000070', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 350, 40, 'system'),
  ('c0000001-0000-0000-0000-000000000071', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 580, 50, 'system'),
  ('c0000001-0000-0000-0000-000000000072', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', 120, 15, 'system'),
  -- Raw materials in WH-RAW (vault)
  ('c0000001-0000-0000-0000-000000000050', 'MATERIAL', 'e0000001-0000-0000-0000-000000000003', 650, 80, 'system'),
  ('c0000001-0000-0000-0000-000000000051', 'MATERIAL', 'e0000001-0000-0000-0000-000000000003', 280, 30, 'system'),
  ('c0000001-0000-0000-0000-000000000052', 'MATERIAL', 'e0000001-0000-0000-0000-000000000003', 120, 15, 'system'),
  ('c0000001-0000-0000-0000-000000000053', 'MATERIAL', 'e0000001-0000-0000-0000-000000000003', 15.5, 2.4, 'system'),
  ('c0000001-0000-0000-0000-000000000054', 'MATERIAL', 'e0000001-0000-0000-0000-000000000003', 65, 8, 'system'),
  -- Packaging in WH-MAIN
  ('c0000001-0000-0000-0000-000000000060', 'MATERIAL', 'e0000001-0000-0000-0000-000000000001', 250, 30, 'system'),
  ('c0000001-0000-0000-0000-000000000061', 'MATERIAL', 'e0000001-0000-0000-0000-000000000001', 120, 15, 'system'),
  -- Finished products in WH-FIN
  ('d0000001-0000-0000-0000-000000000001', 'PRODUCT', 'e0000001-0000-0000-0000-000000000004', 24, 5, 'system'),
  ('d0000001-0000-0000-0000-000000000002', 'PRODUCT', 'e0000001-0000-0000-0000-000000000004', 18, 3, 'system'),
  ('d0000001-0000-0000-0000-000000000003', 'PRODUCT', 'e0000001-0000-0000-0000-000000000004', 6, 1, 'system'),
  ('d0000001-0000-0000-0000-000000000004', 'PRODUCT', 'e0000001-0000-0000-0000-000000000004', 15, 4, 'system'),
  ('d0000001-0000-0000-0000-000000000005', 'PRODUCT', 'e0000001-0000-0000-0000-000000000004', 12, 2, 'system'),
  ('d0000001-0000-0000-0000-000000000006', 'PRODUCT', 'e0000001-0000-0000-0000-000000000004', 10, 2, 'system'),
  ('d0000001-0000-0000-0000-000000000007', 'PRODUCT', 'e0000001-0000-0000-0000-000000000004', 8, 1, 'system'),
  ('d0000001-0000-0000-0000-000000000008', 'PRODUCT', 'e0000001-0000-0000-0000-000000000004', 14, 3, 'system'),
  ('d0000001-0000-0000-0000-000000000009', 'PRODUCT', 'e0000001-0000-0000-0000-000000000004', 20, 4, 'system'),
  ('d0000001-0000-0000-0000-000000000010', 'PRODUCT', 'e0000001-0000-0000-0000-000000000004', 3, 0, 'system'),
  -- Showroom stock (Zurich)
  ('d0000001-0000-0000-0000-000000000001', 'PRODUCT', 'e0000001-0000-0000-0000-000000000005', 4, 0, 'system'),
  ('d0000001-0000-0000-0000-000000000002', 'PRODUCT', 'e0000001-0000-0000-0000-000000000005', 3, 0, 'system'),
  ('d0000001-0000-0000-0000-000000000004', 'PRODUCT', 'e0000001-0000-0000-0000-000000000005', 2, 0, 'system'),
  ('d0000001-0000-0000-0000-000000000006', 'PRODUCT', 'e0000001-0000-0000-0000-000000000005', 2, 0, 'system'),
  ('d0000001-0000-0000-0000-000000000008', 'PRODUCT', 'e0000001-0000-0000-0000-000000000005', 3, 0, 'system'),
  -- Showroom stock (Geneva)
  ('d0000001-0000-0000-0000-000000000001', 'PRODUCT', 'e0000001-0000-0000-0000-000000000006', 3, 0, 'system'),
  ('d0000001-0000-0000-0000-000000000003', 'PRODUCT', 'e0000001-0000-0000-0000-000000000006', 2, 0, 'system'),
  ('d0000001-0000-0000-0000-000000000005', 'PRODUCT', 'e0000001-0000-0000-0000-000000000006', 2, 0, 'system'),
  ('d0000001-0000-0000-0000-000000000007', 'PRODUCT', 'e0000001-0000-0000-0000-000000000006', 2, 0, 'system'),
  ('d0000001-0000-0000-0000-000000000010', 'PRODUCT', 'e0000001-0000-0000-0000-000000000006', 1, 0, 'system');

-- ============================
-- 8. SALES — Customers
-- ============================
INSERT INTO customers (id, customer_number, company_name, first_name, last_name, email, phone, street, city, postal_code, canton, country, vat_number, payment_terms, credit_limit, notes, created_by) VALUES
  ('f0000001-0000-0000-0000-000000000001', 'KD-2024-001', 'Bucherer AG',                    'Thomas',   'Meier',     'thomas.meier@bucherer.ch',       '+41 41 369 70 00', 'Schwanenplatz 5',       'Luzern',      '6004', 'LU', 'CH', 'CHE-105.834.084 MWST', 30, 500000.0000, 'Grösster Schweizer Uhrenhändler, langjährige Partnerschaft', 'system'),
  ('f0000001-0000-0000-0000-000000000002', 'KD-2024-002', 'Watches of Switzerland Group',   'James',    'Harrison',  'j.harrison@watches-switzerland.com', '+44 20 7317 4600', '155 Regent Street',   'London',      'W1B 4AA', NULL, 'GB', 'GB 123 4567 89',       45, 750000.0000, 'UK flagship retailer, quarterly orders', 'system'),
  ('f0000001-0000-0000-0000-000000000003', 'KD-2024-003', 'Les Ambassadeurs SA',            'Marie',    'Dubois',    'marie.dubois@ambassadeurs.ch',   '+41 22 310 45 50', 'Quai du Général-Guisan 36', 'Genève', '1204', 'GE', 'CH', 'CHE-108.245.679 MWST', 30, 350000.0000, 'Premium multi-brand retailer, Westschweiz', 'system'),
  ('f0000001-0000-0000-0000-000000000004', 'KD-2024-004', 'Gübelin AG',                     'Stefan',   'Weber',     'stefan.weber@guebelin.com',      '+41 41 417 02 02', 'Schweizerhofquai 1',  'Luzern',      '6004', 'LU', 'CH', 'CHE-100.037.822 MWST', 30, 400000.0000, 'Traditionsjuwelier seit 1854', 'system'),
  ('f0000001-0000-0000-0000-000000000005', 'KD-2024-005', 'Juwelier Kurz AG',               'Andrea',   'Kurz',      'andrea.kurz@kurz.ch',            '+41 44 221 18 80', 'Bahnhofstrasse 34',   'Zürich',      '8001', 'ZH', 'CH', 'CHE-103.567.890 MWST', 30, 250000.0000, 'Familienbetrieb seit 1948, Zürich und Bern', 'system'),
  ('f0000001-0000-0000-0000-000000000006', 'KD-2024-006', NULL,                              'Akira',    'Tanaka',    'akira.tanaka@tanaka-corp.jp',    '+81 3 5544 8800',  '2-5-1 Ginza, Chuo-ku', 'Tokyo',     '104-0061', NULL, 'JP', NULL,                    60, 600000.0000, 'Japanese distributor, Asia-Pacific market', 'system'),
  ('f0000001-0000-0000-0000-000000000007', 'KD-2024-007', 'Wempe Juwelier GmbH',            'Klaus',    'Wempe',     'klaus.wempe@wempe.de',           '+49 40 334 48 0',  'Steinweg 3',          'Hamburg',     '20095', NULL, 'DE', 'DE 118 515 028',        45, 450000.0000, 'Deutscher Premium-Händler, alle Filialen', 'system'),
  ('f0000001-0000-0000-0000-000000000008', 'KD-2024-008', NULL,                              'Nikolai',  'Petrov',    'n.petrov@petrovcollection.com',  '+971 4 339 8888',  'DIFC, Gate Village 5', 'Dubai',     '507211', NULL, 'AE', NULL,                    30, 800000.0000, 'High-net-worth private collector, Middle East', 'system'),
  ('f0000001-0000-0000-0000-000000000009', 'KD-2024-009', 'Beyer Chronometrie AG',          'René',     'Beyer',     'rene.beyer@beyer-ch.com',        '+41 44 344 63 63', 'Bahnhofstrasse 31',   'Zürich',      '8001', 'ZH', 'CH', 'CHE-100.082.446 MWST', 30, 350000.0000, 'Ältestes Uhrengeschäft Europas, seit 1760', 'system'),
  ('f0000001-0000-0000-0000-000000000010', 'KD-2024-010', 'Chronext AG',                    'Philipp',  'Man',       'philipp.man@chronext.com',       '+41 41 500 30 30', 'Pilatusstrasse 14',   'Luzern',      '6003', 'LU', 'CH', 'CHE-440.155.283 MWST', 30, 200000.0000, 'Online-Plattform für Luxusuhren', 'system'),
  ('f0000001-0000-0000-0000-000000000011', 'KD-2024-011', NULL,                              'Sophie',   'Laurent',   'sophie.laurent@gmail.com',       '+41 79 345 67 89', 'Chemin de Roseneck 12', 'Montreux',  '1820', 'VD', 'CH', NULL,                    14, 50000.0000,  'VIP-Kundin, jährliche Sammlungserweiterung', 'system'),
  ('f0000001-0000-0000-0000-000000000012', 'KD-2024-012', 'Mondaine Watch Ltd',             'André',    'Bernheim',  'a.bernheim@mondaine.ch',         '+41 43 322 72 72', 'Joweid Zentrum 8',    'Rüti',        '8630', 'ZH', 'CH', 'CHE-110.568.401 MWST', 30, 150000.0000, 'B2B-Kooperation, Co-Branding-Projekte', 'system');

-- ============================
-- 9. SALES — Sales Orders
-- ============================
INSERT INTO sales_orders (id, order_number, customer_id, status, order_date, delivery_date, subtotal, vat_amount, total_amount, currency, notes, shipping_street, shipping_city, shipping_postal_code, shipping_canton, shipping_country, created_by) VALUES
  ('10000001-0000-0000-0000-000000000001', 'SO-2026-0001', 'f0000001-0000-0000-0000-000000000001', 'CONFIRMED', '2026-01-15', '2026-02-15', 39500.0000, 3199.5000, 42699.5000, 'CHF', 'Bucherer Q1 Bestellung',            'Schwanenplatz 5',       'Luzern',   '6004', 'LU', 'CH', 'system'),
  ('10000001-0000-0000-0000-000000000002', 'SO-2026-0002', 'f0000001-0000-0000-0000-000000000002', 'SHIPPED',   '2026-01-20', '2026-03-01', 83400.0000, 0.0000,    83400.0000, 'CHF', 'WoS UK Q1, Export tax-free',        '155 Regent Street',     'London',   'W1B 4AA', NULL, 'GB', 'system'),
  ('10000001-0000-0000-0000-000000000003', 'SO-2026-0003', 'f0000001-0000-0000-0000-000000000003', 'DELIVERED', '2026-02-01', '2026-02-20', 23700.0000, 1919.7000, 25619.7000, 'CHF', 'Les Ambassadeurs Genève Nachbestellung', 'Quai du Général-Guisan 36', 'Genève', '1204', 'GE', 'CH', 'system'),
  ('10000001-0000-0000-0000-000000000004', 'SO-2026-0004', 'f0000001-0000-0000-0000-000000000008', 'CONFIRMED', '2026-02-10', '2026-03-15', 57000.0000, 0.0000,    57000.0000, 'CHF', 'Petrov Collection, Dubai Delivery, Export', 'DIFC, Gate Village 5', 'Dubai',  '507211', NULL, 'AE', 'system'),
  ('10000001-0000-0000-0000-000000000005', 'SO-2026-0005', 'f0000001-0000-0000-0000-000000000005', 'DRAFT',     '2026-03-01', NULL,         15800.0000, 1279.8000, 17079.8000, 'CHF', 'Offerte Kurz AG, Frühlingssaison', 'Bahnhofstrasse 34',     'Zürich',   '8001', 'ZH', 'CH', 'system'),
  ('10000001-0000-0000-0000-000000000006', 'SO-2026-0006', 'f0000001-0000-0000-0000-000000000011', 'INVOICED',  '2026-02-14', '2026-02-28', 28500.0000, 2308.5000, 30808.5000, 'CHF', 'Valentine Spezialbestellung, Ltd Edition', 'Chemin de Roseneck 12', 'Montreux', '1820', 'VD', 'CH', 'system'),
  ('10000001-0000-0000-0000-000000000007', 'SO-2026-0007', 'f0000001-0000-0000-0000-000000000009', 'CONFIRMED', '2026-03-10', '2026-04-10', 31600.0000, 2559.6000, 34159.6000, 'CHF', 'Beyer Chronometrie, neue Kollektion', 'Bahnhofstrasse 31',   'Zürich',   '8001', 'ZH', 'CH', 'system'),
  ('10000001-0000-0000-0000-000000000008', 'SO-2026-0008', 'f0000001-0000-0000-0000-000000000006', 'SHIPPED',   '2026-03-15', '2026-04-30', 118000.0000, 0.0000,  118000.0000, 'CHF', 'Tanaka Corp, Japan H1 order, Export', '2-5-1 Ginza, Chuo-ku', 'Tokyo', '104-0061', NULL, 'JP', 'system');

-- ── Sales Order Lines ──
INSERT INTO sales_order_lines (sales_order_id, product_id, description, quantity, unit_price, discount_pct, vat_rate, line_total, position, created_by) VALUES
  -- SO-2026-0001 (Bucherer)
  ('10000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000001', 'Alpine 40 Schwarz', 5, 3950.0000, 0.00, 'STANDARD_8_1', 19750.0000, 1, 'system'),
  ('10000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000002', 'Alpine 40 Bleu',    5, 3950.0000, 0.00, 'STANDARD_8_1', 19750.0000, 2, 'system'),
  -- SO-2026-0002 (WoS UK)
  ('10000001-0000-0000-0000-000000000002', 'd0000001-0000-0000-0000-000000000004', 'Chronoswiss 42',    6, 6950.0000, 0.00, 'EXEMPT', 41700.0000, 1, 'system'),
  ('10000001-0000-0000-0000-000000000002', 'd0000001-0000-0000-0000-000000000006', 'Abyss 44',          4, 5900.0000, 0.00, 'EXEMPT', 23600.0000, 2, 'system'),
  ('10000001-0000-0000-0000-000000000002', 'd0000001-0000-0000-0000-000000000007', 'Voyager GMT',       3, 6250.0000, 2.50, 'EXEMPT', 18281.2500, 3, 'system'),
  -- SO-2026-0003 (Les Ambassadeurs)
  ('10000001-0000-0000-0000-000000000003', 'd0000001-0000-0000-0000-000000000001', 'Alpine 40 Schwarz', 3, 3950.0000, 0.00, 'STANDARD_8_1', 11850.0000, 1, 'system'),
  ('10000001-0000-0000-0000-000000000003', 'd0000001-0000-0000-0000-000000000002', 'Alpine 40 Bleu',    3, 3950.0000, 0.00, 'STANDARD_8_1', 11850.0000, 2, 'system'),
  -- SO-2026-0004 (Petrov)
  ('10000001-0000-0000-0000-000000000004', 'd0000001-0000-0000-0000-000000000010', 'Alpine Ltd Diamant', 2, 28500.0000, 0.00, 'EXEMPT', 57000.0000, 1, 'system'),
  -- SO-2026-0005 (Kurz – Draft)
  ('10000001-0000-0000-0000-000000000005', 'd0000001-0000-0000-0000-000000000001', 'Alpine 40 Schwarz', 2, 3950.0000, 0.00, 'STANDARD_8_1', 7900.0000, 1, 'system'),
  ('10000001-0000-0000-0000-000000000005', 'd0000001-0000-0000-0000-000000000009', 'Alpine 40 Vert',    2, 3950.0000, 0.00, 'STANDARD_8_1', 7900.0000, 2, 'system'),
  -- SO-2026-0006 (Sophie Laurent – Ltd Edition)
  ('10000001-0000-0000-0000-000000000006', 'd0000001-0000-0000-0000-000000000010', 'Alpine Ltd Diamant', 1, 28500.0000, 0.00, 'STANDARD_8_1', 28500.0000, 1, 'system'),
  -- SO-2026-0007 (Beyer)
  ('10000001-0000-0000-0000-000000000007', 'd0000001-0000-0000-0000-000000000004', 'Chronoswiss 42 BLK', 2, 6950.0000, 0.00, 'STANDARD_8_1', 13900.0000, 1, 'system'),
  ('10000001-0000-0000-0000-000000000007', 'd0000001-0000-0000-0000-000000000005', 'Chronoswiss 42 GRN', 2, 6950.0000, 0.00, 'STANDARD_8_1', 13900.0000, 2, 'system'),
  ('10000001-0000-0000-0000-000000000007', 'd0000001-0000-0000-0000-000000000009', 'Alpine 40 Vert',     1, 3950.0000, 3.80, 'STANDARD_8_1',  3799.9000, 3, 'system'),
  -- SO-2026-0008 (Tanaka Japan)
  ('10000001-0000-0000-0000-000000000008', 'd0000001-0000-0000-0000-000000000001', 'Alpine 40 BLK',     10, 3950.0000, 5.00, 'EXEMPT', 37525.0000, 1, 'system'),
  ('10000001-0000-0000-0000-000000000008', 'd0000001-0000-0000-0000-000000000004', 'Chronoswiss 42',     5, 6950.0000, 5.00, 'EXEMPT', 33012.5000, 2, 'system'),
  ('10000001-0000-0000-0000-000000000008', 'd0000001-0000-0000-0000-000000000006', 'Abyss 44',           5, 5900.0000, 5.00, 'EXEMPT', 28025.0000, 3, 'system'),
  ('10000001-0000-0000-0000-000000000008', 'd0000001-0000-0000-0000-000000000008', 'Elegance 40',        4, 3450.0000, 0.00, 'EXEMPT', 13800.0000, 4, 'system');

-- ============================
-- 10. PURCHASING — Suppliers
-- ============================
INSERT INTO suppliers (id, supplier_number, company_name, first_name, last_name, email, phone, street, city, postal_code, canton, country, vat_number, payment_terms, contact_person, website, notes, created_by) VALUES
  ('20000001-0000-0000-0000-000000000001', 'LF-2024-001', 'ETA SA (Swatch Group)',           'Marc',     'Schneider', 'marc.schneider@eta.ch',          '+41 32 343 93 11', 'Schild-Rust-Strasse 17', 'Grenchen',   '2540', 'SO', 'CH', 'CHE-101.403.021 MWST', 30, 'Marc Schneider', 'https://www.eta.ch',               'Hauptlieferant Uhrwerke, Swiss Made Kaliber', 'system'),
  ('20000001-0000-0000-0000-000000000002', 'LF-2024-002', 'Nivarox-FAR SA',                  'Sophie',   'Favre',     'sophie.favre@nivarox.ch',        '+41 32 722 48 00', 'Rue des Champs 6',       'Le Locle',   '2400', 'NE', 'CH', 'CHE-101.789.456 MWST', 30, 'Sophie Favre',   'https://www.nivarox.ch',           'Hemmungsbauteile, Spiralfedern', 'system'),
  ('20000001-0000-0000-0000-000000000003', 'LF-2024-003', 'Metalem SA',                      'Pierre',   'Leuba',     'pierre.leuba@metalem.ch',        '+41 32 889 69 69', 'Rue de lIndustrie 1',    'Biel/Bienne', '2504', 'BE', 'CH', 'CHE-105.678.234 MWST', 30, 'Pierre Leuba',   'https://www.metalem.ch',           'Gehäuse, CNC-Bearbeitung, Polieren', 'system'),
  ('20000001-0000-0000-0000-000000000004', 'LF-2024-004', 'Cadrans Design SA',               'Jean-Luc', 'Renaud',    'jl.renaud@cadransdesign.ch',     '+41 32 968 33 33', 'Route de Soleure 20',    'Biel/Bienne', '2504', 'BE', 'CH', 'CHE-107.890.567 MWST', 30, 'Jean-Luc Renaud','https://www.cadransdesign.ch',     'Zifferblätter, Galvanik, Druckverfahren', 'system'),
  ('20000001-0000-0000-0000-000000000005', 'LF-2024-005', 'Stettler Sapphire AG',            'Bruno',    'Stettler',  'bruno.stettler@stettlersapphire.ch', '+41 32 653 21 21', 'Industriestrasse 12', 'Lyss',       '3250', 'BE', 'CH', 'CHE-109.234.567 MWST', 30, 'Bruno Stettler', 'https://www.stettlersapphire.ch',  'Saphirgläser, Beschichtung AR, Formschliff', 'system'),
  ('20000001-0000-0000-0000-000000000006', 'LF-2024-006', 'Guenat SA (Camille Fournet)',     'Claire',   'Guenat',    'claire.guenat@guenat.ch',        '+41 32 926 04 40', 'Rue du Progrès 25',      'Bassecourt',  '2854', 'JU', 'CH', 'CHE-110.456.789 MWST', 30, 'Claire Guenat',  'https://www.camillefournet.com',   'Lederbänder, exotische Leder, Handnaht', 'system'),
  ('20000001-0000-0000-0000-000000000007', 'LF-2024-007', 'PAMP SA (MKS)',                   'Roberto',  'Santucci',  'roberto.santucci@pamp.com',      '+41 91 695 00 00', 'Via Dunant 6-8',         'Castel San Pietro', '6874', 'TI', 'CH', 'CHE-108.567.890 MWST', 14, 'Roberto Santucci','https://www.pamp.com',            'Edelmetalllieferant, Goldbarren, Affinerie', 'system'),
  ('20000001-0000-0000-0000-000000000008', 'LF-2024-008', 'Horotec SA',                      'Alain',    'Jaccard',   'alain.jaccard@horotec.ch',       '+41 32 924 00 70', 'Rue des Rangiers 12',    'Bassecourt',  '2854', 'JU', 'CH', 'CHE-111.789.012 MWST', 30, 'Alain Jaccard',  'https://www.horotec.ch',           'Werkzeuge, Verbrauchsmaterial, Uhrmacherbedarf', 'system'),
  ('20000001-0000-0000-0000-000000000009', 'LF-2024-009', 'Siegfried Bock GmbH',             'Helmut',   'Bock',      'helmut.bock@bock-diamonds.de',   '+49 7561 9820',    'Rotenbergstrasse 10',    'Leutkirch',   '88299', NULL, 'DE', 'DE 262 123 456',        45, 'Helmut Bock',    'https://www.bock-diamonds.de',     'Diamantschleifer, VVS1-Steine, melee', 'system'),
  ('20000001-0000-0000-0000-000000000010', 'LF-2024-010', 'Swiss Rubber Tech AG',            'Daniel',   'Zürcher',   'daniel.zuercher@swissrubber.ch', '+41 55 412 40 40', 'Industriestrasse 5',     'Lachen',      '8853', 'SZ', 'CH', 'CHE-112.345.678 MWST', 30, 'Daniel Zürcher', 'https://www.swissrubber.ch',       'FKM-Kautschukbänder, Dichtungen, Kunststoffteile', 'system');

-- ============================
-- 11. PURCHASING — Purchase Orders
-- ============================
INSERT INTO purchase_orders (id, order_number, supplier_id, status, order_date, expected_delivery_date, subtotal, vat_amount, total_amount, currency, notes, created_by) VALUES
  ('30000001-0000-0000-0000-000000000001', 'PO-2026-0001', '20000001-0000-0000-0000-000000000001', 'RECEIVED', '2026-01-05', '2026-01-20', 62500.0000, 5062.5000, 67562.5000, 'CHF', 'Q1 Kaliber SA-3135 Nachbestellung', 'system'),
  ('30000001-0000-0000-0000-000000000002', 'PO-2026-0002', '20000001-0000-0000-0000-000000000003', 'RECEIVED', '2026-01-10', '2026-02-01', 38000.0000, 3078.0000, 41078.0000, 'CHF', 'Gehäuse SS 40mm und 42mm',          'system'),
  ('30000001-0000-0000-0000-000000000003', 'PO-2026-0003', '20000001-0000-0000-0000-000000000004', 'RECEIVED', '2026-01-10', '2026-01-25', 14400.0000, 1166.4000, 15566.4000, 'CHF', 'Zifferblätter diverse Modelle',      'system'),
  ('30000001-0000-0000-0000-000000000004', 'PO-2026-0004', '20000001-0000-0000-0000-000000000007', 'CONFIRMED','2026-02-01', '2026-02-15', 47125.0000, 0.0000,    47125.0000, 'CHF', '18K Roségold, 650g, Edelmetall steuerbefreit', 'system'),
  ('30000001-0000-0000-0000-000000000005', 'PO-2026-0005', '20000001-0000-0000-0000-000000000005', 'ORDERED',  '2026-02-15', '2026-03-10', 9250.0000,  749.2500,  9999.2500,  'CHF', 'Saphirgläser 40mm + 42mm',          'system'),
  ('30000001-0000-0000-0000-000000000006', 'PO-2026-0006', '20000001-0000-0000-0000-000000000006', 'ORDERED',  '2026-02-20', '2026-03-15', 27000.0000, 2187.0000, 29187.0000, 'CHF', 'Krokodilleder 20mm (60 Stk)',        'system'),
  ('30000001-0000-0000-0000-000000000007', 'PO-2026-0007', '20000001-0000-0000-0000-000000000009', 'ORDERED',  '2026-03-01', '2026-04-01', 42500.0000, 0.0000,    42500.0000, 'CHF', 'Diamanten VVS1, 5ct, DE Import',     'system'),
  ('30000001-0000-0000-0000-000000000008', 'PO-2026-0008', '20000001-0000-0000-0000-000000000010', 'DRAFT',    '2026-03-20', NULL,         8500.0000,  688.5000,  9188.5000,  'CHF', 'FKM-Kautschukbänder 22mm Q2',       'system');

-- ── Purchase Order Lines ──
INSERT INTO purchase_order_lines (purchase_order_id, material_id, description, quantity, unit_price, discount_pct, vat_rate, line_total, position, created_by) VALUES
  -- PO-2026-0001 (ETA Movements)
  ('30000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000001', 'Kaliber SA-3135 Automatik', 50, 1250.0000, 0.00, 'STANDARD_8_1', 62500.0000, 1, 'system'),
  -- PO-2026-0002 (Metalem Cases)
  ('30000001-0000-0000-0000-000000000002', 'c0000001-0000-0000-0000-000000000010', 'Gehäuse SS 40mm', 50, 380.0000, 0.00, 'STANDARD_8_1', 19000.0000, 1, 'system'),
  ('30000001-0000-0000-0000-000000000002', 'c0000001-0000-0000-0000-000000000011', 'Gehäuse SS 42mm', 40, 420.0000, 5.00, 'STANDARD_8_1', 15960.0000, 2, 'system'),
  ('30000001-0000-0000-0000-000000000002', 'c0000001-0000-0000-0000-000000000070', 'Kronen verschraubt', 100, 22.0000, 0.00, 'STANDARD_8_1', 2200.0000,  3, 'system'),
  -- PO-2026-0003 (Cadrans Dials)
  ('30000001-0000-0000-0000-000000000003', 'c0000001-0000-0000-0000-000000000020', 'Schwarz Sunburst', 50, 120.0000, 0.00, 'STANDARD_8_1', 6000.0000, 1, 'system'),
  ('30000001-0000-0000-0000-000000000003', 'c0000001-0000-0000-0000-000000000021', 'Blau Fumé',        40, 140.0000, 0.00, 'STANDARD_8_1', 5600.0000, 2, 'system'),
  ('30000001-0000-0000-0000-000000000003', 'c0000001-0000-0000-0000-000000000023', 'Grün Sunburst',    20, 130.0000, 0.00, 'STANDARD_8_1', 2600.0000, 3, 'system'),
  -- PO-2026-0004 (PAMP Gold)
  ('30000001-0000-0000-0000-000000000004', 'c0000001-0000-0000-0000-000000000050', '18K Roségold', 650, 72.5000, 0.00, 'EXEMPT', 47125.0000, 1, 'system'),
  -- PO-2026-0005 (Stettler Sapphires)
  ('30000001-0000-0000-0000-000000000005', 'c0000001-0000-0000-0000-000000000040', 'Saphirglas flach 40mm', 50, 95.0000, 0.00, 'STANDARD_8_1', 4750.0000, 1, 'system'),
  ('30000001-0000-0000-0000-000000000005', 'c0000001-0000-0000-0000-000000000041', 'Saphirglas gewölbt 42mm', 50, 110.0000, 0.00, 'STANDARD_8_1', 5500.0000, 2, 'system'),  -- discount would change total but keeping simple
  -- PO-2026-0006 (Guenat Leather)
  ('30000001-0000-0000-0000-000000000006', 'c0000001-0000-0000-0000-000000000032', 'Krokodilleder 20mm', 60, 450.0000, 0.00, 'STANDARD_8_1', 27000.0000, 1, 'system'),
  -- PO-2026-0007 (Bock Diamonds)
  ('30000001-0000-0000-0000-000000000007', 'c0000001-0000-0000-0000-000000000053', 'Diamanten VVS1 D', 5, 8500.0000, 0.00, 'EXEMPT', 42500.0000, 1, 'system'),
  -- PO-2026-0008 (Swiss Rubber)
  ('30000001-0000-0000-0000-000000000008', 'c0000001-0000-0000-0000-000000000033', 'FKM-Kautschuk 22mm', 100, 85.0000, 0.00, 'STANDARD_8_1', 8500.0000, 1, 'system');

-- ============================
-- 12. PRODUCTION — Work Centers
-- ============================
INSERT INTO work_centers (id, code, name, description, capacity_per_day, cost_per_hour, created_by) VALUES
  ('40000001-0000-0000-0000-000000000001', 'WC-ASM01', 'Montage Linie 1',       'Hauptmontagelinie, Automatik und Handaufzug',          12, 185.0000, 'system'),
  ('40000001-0000-0000-0000-000000000002', 'WC-ASM02', 'Montage Linie 2',       'Zweite Montagelinie, Chronographen und Komplikationen', 8,  220.0000, 'system'),
  ('40000001-0000-0000-0000-000000000003', 'WC-CASE',  'Gehäusebau',            'CNC-Bearbeitung, Polieren, Satin-Finish',              20, 150.0000, 'system'),
  ('40000001-0000-0000-0000-000000000004', 'WC-DIAL',  'Zifferblattwerkstatt',  'Galvanik, Druck, Applikation',                         30, 140.0000, 'system'),
  ('40000001-0000-0000-0000-000000000005', 'WC-QC',    'Qualitätskontrolle',    'Gangkontrolle, Wasserdichtheit, Endkontrolle',         25, 160.0000, 'system'),
  ('40000001-0000-0000-0000-000000000006', 'WC-PACK',  'Verpackung & Versand',  'Endverpackung, Dokumentation, Versand',                40, 95.0000,  'system'),
  ('40000001-0000-0000-0000-000000000007', 'WC-ENGR',  'Gravur & Veredlung',    'Gravur, Rhodinieren, Vergolden',                       15, 175.0000, 'system');

-- ============================
-- 13. PRODUCTION — Production Orders
-- ============================
INSERT INTO production_orders (id, order_number, product_id, work_center_id, status, planned_quantity, completed_quantity, scrap_quantity, planned_start_date, planned_end_date, actual_start_date, actual_end_date, estimated_cost, actual_cost, priority, notes, created_by) VALUES
  ('50000001-0000-0000-0000-000000000001', 'PO-PRD-2026-001', 'd0000001-0000-0000-0000-000000000001', '40000001-0000-0000-0000-000000000001', 'COMPLETED', 30, 29, 1, '2026-01-06', '2026-01-17', '2026-01-06', '2026-01-18', 69000.0000, 71250.0000, 1, 'Alpine 40 BLK Q1 Charge, 1 Stk Ausschuss (Gehäusefehler)', 'system'),
  ('50000001-0000-0000-0000-000000000002', 'PO-PRD-2026-002', 'd0000001-0000-0000-0000-000000000002', '40000001-0000-0000-0000-000000000001', 'COMPLETED', 25, 25, 0, '2026-01-20', '2026-01-31', '2026-01-20', '2026-01-30', 57500.0000, 56800.0000, 1, 'Alpine 40 BLU Q1 Charge', 'system'),
  ('50000001-0000-0000-0000-000000000003', 'PO-PRD-2026-003', 'd0000001-0000-0000-0000-000000000004', '40000001-0000-0000-0000-000000000002', 'COMPLETED', 20, 20, 0, '2026-01-27', '2026-02-14', '2026-01-27', '2026-02-13', 92000.0000, 89500.0000, 2, 'Chronoswiss 42 BLK Q1 Charge', 'system'),
  ('50000001-0000-0000-0000-000000000004', 'PO-PRD-2026-004', 'd0000001-0000-0000-0000-000000000006', '40000001-0000-0000-0000-000000000002', 'IN_PROGRESS', 15, 8, 0, '2026-02-17', '2026-03-07', '2026-02-17', NULL, 66750.0000, 0.0000, 2, 'Abyss 44 Titan Taucheruhr', 'system'),
  ('50000001-0000-0000-0000-000000000005', 'PO-PRD-2026-005', 'd0000001-0000-0000-0000-000000000007', '40000001-0000-0000-0000-000000000001', 'IN_PROGRESS', 12, 4, 0, '2026-03-03', '2026-03-14', '2026-03-03', NULL, 55200.0000, 0.0000, 1, 'Voyager GMT Charge 1', 'system'),
  ('50000001-0000-0000-0000-000000000006', 'PO-PRD-2026-006', 'd0000001-0000-0000-0000-000000000010', '40000001-0000-0000-0000-000000000002', 'PLANNED', 5, 0, 0, '2026-04-01', '2026-04-30', NULL, NULL, 127500.0000, 0.0000, 3, 'Limited Edition Diamant, höchste Priorität, Einzelfertigung', 'system'),
  ('50000001-0000-0000-0000-000000000007', 'PO-PRD-2026-007', 'd0000001-0000-0000-0000-000000000009', '40000001-0000-0000-0000-000000000001', 'PLANNED', 30, 0, 0, '2026-03-17', '2026-03-28', NULL, NULL, 69000.0000, 0.0000, 1, 'Alpine 40 Vert Q1/Q2 Charge', 'system'),
  ('50000001-0000-0000-0000-000000000008', 'PO-PRD-2026-008', 'd0000001-0000-0000-0000-000000000008', '40000001-0000-0000-0000-000000000001', 'PLANNED', 20, 0, 0, '2026-03-31', '2026-04-11', NULL, NULL, 48000.0000, 0.0000, 1, 'Elegance 40 Dressuhr Charge', 'system');

-- ── Production Order Lines (material consumption) ──
INSERT INTO production_order_lines (production_order_id, material_id, description, planned_quantity, actual_quantity, unit_price, line_cost, position, created_by) VALUES
  -- PO-PRD-2026-001 (Alpine 40 BLK, qty 30)
  ('50000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000001', 'Kaliber SA-3135',     30, 30, 1250.0000, 37500.0000, 1, 'system'),
  ('50000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000010', 'Gehäuse SS 40mm',     30, 31, 380.0000,  11780.0000, 2, 'system'),
  ('50000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000020', 'Zifferblatt Schwarz', 30, 30, 120.0000,  3600.0000,  3, 'system'),
  ('50000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000030', 'Oyster-Armband',      30, 30, 280.0000,  8400.0000,  4, 'system'),
  ('50000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000040', 'Saphirglas 40mm',     30, 30, 95.0000,   2850.0000,  5, 'system'),
  -- PO-PRD-2026-003 (Chronoswiss 42, qty 20)
  ('50000001-0000-0000-0000-000000000003', 'c0000001-0000-0000-0000-000000000002', 'Kaliber SA-7750',     20, 20, 2100.0000, 42000.0000, 1, 'system'),
  ('50000001-0000-0000-0000-000000000003', 'c0000001-0000-0000-0000-000000000011', 'Gehäuse SS 42mm',     20, 20, 420.0000,  8400.0000,  2, 'system'),
  ('50000001-0000-0000-0000-000000000003', 'c0000001-0000-0000-0000-000000000020', 'Zifferblatt Schwarz', 20, 20, 120.0000,  2400.0000,  3, 'system'),
  ('50000001-0000-0000-0000-000000000003', 'c0000001-0000-0000-0000-000000000031', 'Jubilee-Armband',     20, 20, 320.0000,  6400.0000,  4, 'system'),
  ('50000001-0000-0000-0000-000000000003', 'c0000001-0000-0000-0000-000000000041', 'Saphirglas 42mm',     20, 20, 110.0000,  2200.0000,  5, 'system');

-- ============================
-- 14. AUTH — Users (password: "SwiftApp2026!")
-- BCrypt hash for "SwiftApp2026!" = $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- ============================
INSERT INTO users (id, username, email, password_hash, first_name, last_name, enabled, created_by) VALUES
  ('60000001-0000-0000-0000-000000000001', 'admin',         'admin@swiftapp.ch',              '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System',    'Administrator', true, 'system'),
  ('60000001-0000-0000-0000-000000000002', 'l.mueller',     'lukas.mueller@swiftapp.ch',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Lukas',     'Müller',        true, 'system'),
  ('60000001-0000-0000-0000-000000000003', 'a.bianchi',     'anna.bianchi@swiftapp.ch',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Anna',      'Bianchi',       true, 'system'),
  ('60000001-0000-0000-0000-000000000004', 'm.keller',      'markus.keller@swiftapp.ch',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Markus',    'Keller',        true, 'system'),
  ('60000001-0000-0000-0000-000000000005', 'c.dubois',      'camille.dubois@swiftapp.ch',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Camille',   'Dubois',        true, 'system'),
  ('60000001-0000-0000-0000-000000000006', 'p.rossi',       'paolo.rossi@swiftapp.ch',        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Paolo',     'Rossi',         true, 'system'),
  ('60000001-0000-0000-0000-000000000007', 's.favre',       'sylvie.favre@swiftapp.ch',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Sylvie',    'Favre',         true, 'system'),
  ('60000001-0000-0000-0000-000000000008', 'r.schneider',   'roger.schneider@swiftapp.ch',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Roger',     'Schneider',     true, 'system'),
  ('60000001-0000-0000-0000-000000000009', 'n.weber',       'nadia.weber@swiftapp.ch',        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nadia',     'Weber',         true, 'system'),
  ('60000001-0000-0000-0000-000000000010', 'viewer',        'viewer@swiftapp.ch',             '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Gast',      'Benutzer',      true, 'system');

-- ── User-Role Assignments ──
-- We need the role IDs from the V007 seed
INSERT INTO user_roles (user_id, role_id)
SELECT '60000001-0000-0000-0000-000000000001', id FROM roles WHERE name = 'ADMIN';
INSERT INTO user_roles (user_id, role_id)
SELECT '60000001-0000-0000-0000-000000000002', id FROM roles WHERE name = 'MANAGER';
INSERT INTO user_roles (user_id, role_id)
SELECT '60000001-0000-0000-0000-000000000003', id FROM roles WHERE name = 'SALES';
INSERT INTO user_roles (user_id, role_id)
SELECT '60000001-0000-0000-0000-000000000004', id FROM roles WHERE name = 'PRODUCTION';
INSERT INTO user_roles (user_id, role_id)
SELECT '60000001-0000-0000-0000-000000000005', id FROM roles WHERE name = 'ACCOUNTANT';
INSERT INTO user_roles (user_id, role_id)
SELECT '60000001-0000-0000-0000-000000000006', id FROM roles WHERE name = 'WAREHOUSE';
INSERT INTO user_roles (user_id, role_id)
SELECT '60000001-0000-0000-0000-000000000007', id FROM roles WHERE name = 'HR';
INSERT INTO user_roles (user_id, role_id)
SELECT '60000001-0000-0000-0000-000000000008', id FROM roles WHERE name = 'SALES';
INSERT INTO user_roles (user_id, role_id)
SELECT '60000001-0000-0000-0000-000000000008', id FROM roles WHERE name = 'MANAGER';
INSERT INTO user_roles (user_id, role_id)
SELECT '60000001-0000-0000-0000-000000000009', id FROM roles WHERE name = 'PRODUCTION';
INSERT INTO user_roles (user_id, role_id)
SELECT '60000001-0000-0000-0000-000000000009', id FROM roles WHERE name = 'WAREHOUSE';
INSERT INTO user_roles (user_id, role_id)
SELECT '60000001-0000-0000-0000-000000000010', id FROM roles WHERE name = 'VIEWER';

-- ============================
-- 15. ACCOUNTING — Chart of Accounts (Swiss KMU Kontenrahmen)
-- ============================
INSERT INTO accounts (id, account_number, name, description, account_type, parent_id, created_by) VALUES
  -- 1xxx Assets (Aktiven)
  ('70000001-0000-0000-0000-000000000001', '1000', 'Kasse',                         'Barkasse CHF',                                    'ASSET',     NULL, 'system'),
  ('70000001-0000-0000-0000-000000000002', '1020', 'Bankguthaben PostFinance',      'PostFinance Geschäftskonto',                       'ASSET',     NULL, 'system'),
  ('70000001-0000-0000-0000-000000000003', '1021', 'Bankguthaben UBS',              'UBS Geschäftskonto CHF',                           'ASSET',     NULL, 'system'),
  ('70000001-0000-0000-0000-000000000004', '1100', 'Forderungen aus Lieferungen',   'Debitoren / Accounts Receivable',                  'ASSET',     NULL, 'system'),
  ('70000001-0000-0000-0000-000000000005', '1109', 'Wertberichtigung Forderungen',  'Delkredere',                                      'ASSET',     NULL, 'system'),
  ('70000001-0000-0000-0000-000000000006', '1170', 'Vorsteuer MWST',               'Vorsteuer auf Materialaufwand',                    'ASSET',     NULL, 'system'),
  ('70000001-0000-0000-0000-000000000007', '1200', 'Warenvorräte',                 'Rohmaterialien und Halbfabrikate',                 'ASSET',     NULL, 'system'),
  ('70000001-0000-0000-0000-000000000008', '1210', 'Fertigfabrikate',              'Fertige Uhren im Lager',                           'ASSET',     NULL, 'system'),
  ('70000001-0000-0000-0000-000000000009', '1500', 'Maschinen und Apparate',       'CNC, Poliermaschinen, Prüfgeräte',                'ASSET',     NULL, 'system'),
  ('70000001-0000-0000-0000-000000000010', '1510', 'Mobiliar und Einrichtungen',   'Büro- und Werkstatteinrichtung',                  'ASSET',     NULL, 'system'),
  ('70000001-0000-0000-0000-000000000011', '1520', 'Fahrzeuge',                    'Firmenwagen',                                      'ASSET',     NULL, 'system'),
  -- 2xxx Liabilities (Passiven)
  ('70000001-0000-0000-0000-000000000020', '2000', 'Verbindlichkeiten L+L',        'Kreditoren / Accounts Payable',                    'LIABILITY', NULL, 'system'),
  ('70000001-0000-0000-0000-000000000021', '2200', 'Geschuldete MWST',             'Umsatzsteuer MWST',                                'LIABILITY', NULL, 'system'),
  ('70000001-0000-0000-0000-000000000022', '2201', 'MWST-Abrechnung',             'MWST-Verrechnungskonto',                           'LIABILITY', NULL, 'system'),
  ('70000001-0000-0000-0000-000000000023', '2270', 'Sozialversicherungen',         'AHV/IV/EO/ALV Beiträge',                           'LIABILITY', NULL, 'system'),
  ('70000001-0000-0000-0000-000000000024', '2300', 'Bankdarlehen',                 'Langfristige Bankverbindlichkeiten',               'LIABILITY', NULL, 'system'),
  -- 28xx Equity (Eigenkapital)
  ('70000001-0000-0000-0000-000000000030', '2800', 'Aktienkapital',                'Aktienkapital CHF 500''000',                        'EQUITY',    NULL, 'system'),
  ('70000001-0000-0000-0000-000000000031', '2900', 'Gesetzliche Gewinnreserve',    'Reserve nach OR Art. 671',                         'EQUITY',    NULL, 'system'),
  ('70000001-0000-0000-0000-000000000032', '2990', 'Gewinn-/Verlustvortrag',       'Ergebnisvortrag',                                  'EQUITY',    NULL, 'system'),
  -- 3xxx Revenue (Ertrag)
  ('70000001-0000-0000-0000-000000000040', '3000', 'Uhrenverkäufe Inland',         'Verkauf Uhren Schweiz (mit MWST)',                  'REVENUE',   NULL, 'system'),
  ('70000001-0000-0000-0000-000000000041', '3010', 'Uhrenverkäufe Export',         'Verkauf Uhren Export (steuerbefreit)',              'REVENUE',   NULL, 'system'),
  ('70000001-0000-0000-0000-000000000042', '3200', 'Sonstige Erträge',            'Service, Reparaturen, Ersatzteile',                'REVENUE',   NULL, 'system'),
  ('70000001-0000-0000-0000-000000000043', '3800', 'Erlösminderungen',            'Rabatte, Skonti, Rücksendungen',                   'REVENUE',   NULL, 'system'),
  -- 4xxx COGS (Materialaufwand)
  ('70000001-0000-0000-0000-000000000050', '4000', 'Materialaufwand Uhrwerke',    'Einkauf Kaliber und Werke',                        'EXPENSE',   NULL, 'system'),
  ('70000001-0000-0000-0000-000000000051', '4010', 'Materialaufwand Gehäuse',     'Einkauf Gehäuse und Komponenten',                  'EXPENSE',   NULL, 'system'),
  ('70000001-0000-0000-0000-000000000052', '4020', 'Materialaufwand Edelmetalle', 'Gold, Platin, Silber',                             'EXPENSE',   NULL, 'system'),
  ('70000001-0000-0000-0000-000000000053', '4030', 'Materialaufwand Edelsteine',  'Diamanten, Saphire, Rubine',                       'EXPENSE',   NULL, 'system'),
  ('70000001-0000-0000-0000-000000000054', '4090', 'Materialaufwand Diverses',    'Verpackung, Kleinmaterial',                        'EXPENSE',   NULL, 'system'),
  -- 5xxx Personnel (Personalaufwand)
  ('70000001-0000-0000-0000-000000000060', '5000', 'Löhne und Gehälter',          'Bruttolöhne',                                      'EXPENSE',   NULL, 'system'),
  ('70000001-0000-0000-0000-000000000061', '5700', 'Sozialversicherungsaufwand',  'Arbeitgeber-Anteile AHV/IV/EO/ALV/BVG',            'EXPENSE',   NULL, 'system'),
  -- 6xxx Other operating expenses
  ('70000001-0000-0000-0000-000000000070', '6000', 'Raumaufwand',                 'Miete, Nebenkosten, Unterhalt',                    'EXPENSE',   NULL, 'system'),
  ('70000001-0000-0000-0000-000000000071', '6100', 'Unterhalt und Reparaturen',   'Maschinen, Werkzeuge',                             'EXPENSE',   NULL, 'system'),
  ('70000001-0000-0000-0000-000000000072', '6200', 'Fahrzeugaufwand',             'Leasing, Treibstoff, Versicherung',                'EXPENSE',   NULL, 'system'),
  ('70000001-0000-0000-0000-000000000073', '6500', 'Verwaltungsaufwand',          'Büromaterial, IT, Telefon',                        'EXPENSE',   NULL, 'system'),
  ('70000001-0000-0000-0000-000000000074', '6600', 'Werbeaufwand',                'Marketing, Messen, PR',                            'EXPENSE',   NULL, 'system'),
  ('70000001-0000-0000-0000-000000000075', '6800', 'Abschreibungen',              'Abschreibungen auf Sachanlagen',                   'EXPENSE',   NULL, 'system'),
  ('70000001-0000-0000-0000-000000000076', '6900', 'Finanzaufwand',               'Bankspesen, Zinsen, Kursdifferenzen',              'EXPENSE',   NULL, 'system');

-- ============================
-- 16. ACCOUNTING — Journal Entries (sample transactions)
-- ============================
INSERT INTO journal_entries (id, entry_number, entry_date, description, posted, reference, created_by) VALUES
  ('80000001-0000-0000-0000-000000000001', 'JE-2026-0001', '2026-01-15', 'Rechnung Bucherer SO-2026-0001',                        true,  'SO-2026-0001', 'system'),
  ('80000001-0000-0000-0000-000000000002', 'JE-2026-0002', '2026-01-20', 'Rechnung Watches of Switzerland SO-2026-0002 (Export)',  true,  'SO-2026-0002', 'system'),
  ('80000001-0000-0000-0000-000000000003', 'JE-2026-0003', '2026-01-05', 'Eingangsrechnung ETA Kaliber PO-2026-0001',             true,  'PO-2026-0001', 'system'),
  ('80000001-0000-0000-0000-000000000004', 'JE-2026-0004', '2026-01-31', 'Lohnzahlung Januar 2026',                                true,  'PAYROLL-2026-01', 'system'),
  ('80000001-0000-0000-0000-000000000005', 'JE-2026-0005', '2026-02-01', 'Miete Februar Hauptstandort Biel',                       true,  'RENT-2026-02',    'system'),
  ('80000001-0000-0000-0000-000000000006', 'JE-2026-0006', '2026-02-14', 'Rechnung Sophie Laurent SO-2026-0006',                   true,  'SO-2026-0006', 'system'),
  ('80000001-0000-0000-0000-000000000007', 'JE-2026-0007', '2026-02-28', 'Zahlungseingang Bucherer',                               true,  'PAY-KD-2024-001', 'system'),
  ('80000001-0000-0000-0000-000000000008', 'JE-2026-0008', '2026-03-15', 'Rechnung Tanaka Corp SO-2026-0008 (Export)',             false, 'SO-2026-0008', 'system');

-- ── Journal Entry Lines ──
INSERT INTO journal_entry_lines (journal_entry_id, account_id, description, debit, credit, position, created_by) VALUES
  -- JE-2026-0001: Bucherer invoice (domestic, with VAT)
  ('80000001-0000-0000-0000-000000000001', '70000001-0000-0000-0000-000000000004', 'Forderung Bucherer AG',   42699.5000, 0.0000, 1, 'system'),
  ('80000001-0000-0000-0000-000000000001', '70000001-0000-0000-0000-000000000040', 'Uhrenverkauf Inland',     0.0000, 39500.0000, 2, 'system'),
  ('80000001-0000-0000-0000-000000000001', '70000001-0000-0000-0000-000000000021', 'MWST 8.1%',               0.0000,  3199.5000, 3, 'system'),
  -- JE-2026-0002: WoS UK export (no VAT)
  ('80000001-0000-0000-0000-000000000002', '70000001-0000-0000-0000-000000000004', 'Forderung WoS UK',        83400.0000, 0.0000, 1, 'system'),
  ('80000001-0000-0000-0000-000000000002', '70000001-0000-0000-0000-000000000041', 'Uhrenverkauf Export',      0.0000, 83400.0000, 2, 'system'),
  -- JE-2026-0003: ETA purchase (with input VAT)
  ('80000001-0000-0000-0000-000000000003', '70000001-0000-0000-0000-000000000050', 'Materialaufwand Kaliber', 62500.0000, 0.0000, 1, 'system'),
  ('80000001-0000-0000-0000-000000000003', '70000001-0000-0000-0000-000000000006', 'Vorsteuer 8.1%',           5062.5000, 0.0000, 2, 'system'),
  ('80000001-0000-0000-0000-000000000003', '70000001-0000-0000-0000-000000000020', 'Verbindlichkeit ETA SA',   0.0000, 67562.5000, 3, 'system'),
  -- JE-2026-0004: Payroll January
  ('80000001-0000-0000-0000-000000000004', '70000001-0000-0000-0000-000000000060', 'Löhne Januar',           245000.0000, 0.0000, 1, 'system'),
  ('80000001-0000-0000-0000-000000000004', '70000001-0000-0000-0000-000000000061', 'AHV/IV/EO AG-Anteil',     28000.0000, 0.0000, 2, 'system'),
  ('80000001-0000-0000-0000-000000000004', '70000001-0000-0000-0000-000000000023', 'Sozialvers. geschuldet',   0.0000, 56000.0000, 3, 'system'),
  ('80000001-0000-0000-0000-000000000004', '70000001-0000-0000-0000-000000000003', 'Auszahlung UBS',           0.0000, 217000.0000, 4, 'system'),
  -- JE-2026-0005: Rent February
  ('80000001-0000-0000-0000-000000000005', '70000001-0000-0000-0000-000000000070', 'Miete Biel Feb',          18500.0000, 0.0000, 1, 'system'),
  ('80000001-0000-0000-0000-000000000005', '70000001-0000-0000-0000-000000000003', 'Bankzahlung UBS',          0.0000, 18500.0000, 2, 'system'),
  -- JE-2026-0006: Sophie Laurent invoice
  ('80000001-0000-0000-0000-000000000006', '70000001-0000-0000-0000-000000000004', 'Ford. Sophie Laurent',    30808.5000, 0.0000, 1, 'system'),
  ('80000001-0000-0000-0000-000000000006', '70000001-0000-0000-0000-000000000040', 'Uhrenverkauf Inland',      0.0000, 28500.0000, 2, 'system'),
  ('80000001-0000-0000-0000-000000000006', '70000001-0000-0000-0000-000000000021', 'MWST 8.1%',                0.0000,  2308.5000, 3, 'system'),
  -- JE-2026-0007: Payment received from Bucherer
  ('80000001-0000-0000-0000-000000000007', '70000001-0000-0000-0000-000000000003', 'Zahlungseingang UBS',     42699.5000, 0.0000, 1, 'system'),
  ('80000001-0000-0000-0000-000000000007', '70000001-0000-0000-0000-000000000004', 'Forderung Bucherer',       0.0000, 42699.5000, 2, 'system'),
  -- JE-2026-0008: Tanaka export invoice (not yet posted)
  ('80000001-0000-0000-0000-000000000008', '70000001-0000-0000-0000-000000000004', 'Ford. Tanaka Corp',      118000.0000, 0.0000, 1, 'system'),
  ('80000001-0000-0000-0000-000000000008', '70000001-0000-0000-0000-000000000041', 'Uhrenverkauf Export',       0.0000, 118000.0000, 2, 'system');

-- ============================
-- 17. HR — Departments
-- ============================
INSERT INTO departments (id, code, name, description, created_by) VALUES
  ('90000001-0000-0000-0000-000000000001', 'DEP-GL',    'Geschäftsleitung',       'Direktion / Executive Management',             'system'),
  ('90000001-0000-0000-0000-000000000002', 'DEP-PROD',  'Produktion',             'Uhrenherstellung und Montage',                 'system'),
  ('90000001-0000-0000-0000-000000000003', 'DEP-QC',    'Qualitätssicherung',     'Qualitätskontrolle und Prüfung',               'system'),
  ('90000001-0000-0000-0000-000000000004', 'DEP-SALES', 'Vertrieb',               'Verkauf und Kundenbetreuung',                  'system'),
  ('90000001-0000-0000-0000-000000000005', 'DEP-PURCH', 'Einkauf',                'Beschaffung und Lieferantenmanagement',        'system'),
  ('90000001-0000-0000-0000-000000000006', 'DEP-FIN',   'Finanzen & Buchhaltung', 'Accounting, Controlling, Treasury',            'system'),
  ('90000001-0000-0000-0000-000000000007', 'DEP-LOG',   'Logistik',               'Lager, Versand, Wareneingang',                 'system'),
  ('90000001-0000-0000-0000-000000000008', 'DEP-HR',    'Human Resources',        'Personalwesen, Lohnbuchhaltung',               'system'),
  ('90000001-0000-0000-0000-000000000009', 'DEP-IT',    'IT & Digitalisierung',   'Informatik, ERP-Systeme',                      'system'),
  ('90000001-0000-0000-0000-000000000010', 'DEP-MKT',   'Marketing',              'Marketing, Kommunikation, Events',             'system');

-- ============================
-- 18. HR — Employees
-- ============================
INSERT INTO employees (id, employee_number, first_name, last_name, email, phone, hire_date, department_id, position, salary, created_by) VALUES
  ('a1000001-0000-0000-0000-000000000001', 'MA-001', 'Hans-Peter',  'Zürcher',   'hp.zuercher@swiftapp.ch',    '+41 79 100 00 01', '2015-03-01', '90000001-0000-0000-0000-000000000001', 'CEO / Geschäftsführer',              18500.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000002', 'MA-002', 'Lukas',       'Müller',    'lukas.mueller@swiftapp.ch',  '+41 79 100 00 02', '2016-06-15', '90000001-0000-0000-0000-000000000002', 'Produktionsleiter',                  14200.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000003', 'MA-003', 'Anna',        'Bianchi',   'anna.bianchi@swiftapp.ch',   '+41 79 100 00 03', '2017-01-10', '90000001-0000-0000-0000-000000000004', 'Vertriebsleiterin',                  13800.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000004', 'MA-004', 'Markus',      'Keller',    'markus.keller@swiftapp.ch',  '+41 79 100 00 04', '2018-04-01', '90000001-0000-0000-0000-000000000002', 'Uhrmacher / Master Watchmaker',      12500.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000005', 'MA-005', 'Camille',     'Dubois',    'camille.dubois@swiftapp.ch', '+41 79 100 00 05', '2019-08-01', '90000001-0000-0000-0000-000000000006', 'Leiterin Buchhaltung',               13000.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000006', 'MA-006', 'Paolo',       'Rossi',     'paolo.rossi@swiftapp.ch',    '+41 79 100 00 06', '2019-09-15', '90000001-0000-0000-0000-000000000007', 'Logistikleiter',                     11800.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000007', 'MA-007', 'Sylvie',      'Favre',     'sylvie.favre@swiftapp.ch',   '+41 79 100 00 07', '2020-01-06', '90000001-0000-0000-0000-000000000008', 'HR-Leiterin',                        12200.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000008', 'MA-008', 'Roger',       'Schneider', 'roger.schneider@swiftapp.ch','+41 79 100 00 08', '2017-11-01', '90000001-0000-0000-0000-000000000004', 'Key Account Manager',                11500.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000009', 'MA-009', 'Nadia',       'Weber',     'nadia.weber@swiftapp.ch',    '+41 79 100 00 09', '2020-03-15', '90000001-0000-0000-0000-000000000002', 'Uhrmacherin',                        10800.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000010', 'MA-010', 'Florian',     'Gerber',    'florian.gerber@swiftapp.ch', '+41 79 100 00 10', '2020-06-01', '90000001-0000-0000-0000-000000000003', 'Leiter Qualitätskontrolle',           12000.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000011', 'MA-011', 'Isabelle',    'Roth',      'isabelle.roth@swiftapp.ch',  '+41 79 100 00 11', '2021-01-11', '90000001-0000-0000-0000-000000000005', 'Einkaufsleiterin',                   11500.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000012', 'MA-012', 'Thierry',     'Bonnet',    'thierry.bonnet@swiftapp.ch', '+41 79 100 00 12', '2021-03-01', '90000001-0000-0000-0000-000000000002', 'Uhrmacher',                          10200.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000013', 'MA-013', 'Elena',       'Brunner',   'elena.brunner@swiftapp.ch',  '+41 79 100 00 13', '2021-07-15', '90000001-0000-0000-0000-000000000006', 'Buchhalterin',                        9800.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000014', 'MA-014', 'Benjamin',    'Hug',       'benjamin.hug@swiftapp.ch',   '+41 79 100 00 14', '2022-01-10', '90000001-0000-0000-0000-000000000009', 'IT-Leiter / ERP-Administrator',      13500.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000015', 'MA-015', 'Léa',         'Monnier',   'lea.monnier@swiftapp.ch',    '+41 79 100 00 15', '2022-04-01', '90000001-0000-0000-0000-000000000010', 'Marketing-Leiterin',                 11800.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000016', 'MA-016', 'Marco',       'Pellegrini','marco.pellegrini@swiftapp.ch','+41 79 100 00 16','2022-09-01', '90000001-0000-0000-0000-000000000002', 'Uhrmacher-Lehrling',                  4800.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000017', 'MA-017', 'Sandra',      'Wyss',      'sandra.wyss@swiftapp.ch',    '+41 79 100 00 17', '2023-01-15', '90000001-0000-0000-0000-000000000003', 'QC-Technikerin',                      9500.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000018', 'MA-018', 'David',       'Steiner',   'david.steiner@swiftapp.ch',  '+41 79 100 00 18', '2023-06-01', '90000001-0000-0000-0000-000000000007', 'Lagerist',                            8200.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000019', 'MA-019', 'Valentina',   'Caruso',    'valentina.caruso@swiftapp.ch','+41 79 100 00 19','2024-01-08', '90000001-0000-0000-0000-000000000004', 'Verkaufsberaterin',                   8800.0000, 'system'),
  ('a1000001-0000-0000-0000-000000000020', 'MA-020', 'Thomas',      'Ammann',    'thomas.ammann@swiftapp.ch',  '+41 79 100 00 20', '2024-03-01', '90000001-0000-0000-0000-000000000002', 'CNC-Operator',                        9200.0000, 'system');

-- Update department managers
UPDATE departments SET manager_id = 'a1000001-0000-0000-0000-000000000001' WHERE code = 'DEP-GL';
UPDATE departments SET manager_id = 'a1000001-0000-0000-0000-000000000002' WHERE code = 'DEP-PROD';
UPDATE departments SET manager_id = 'a1000001-0000-0000-0000-000000000010' WHERE code = 'DEP-QC';
UPDATE departments SET manager_id = 'a1000001-0000-0000-0000-000000000003' WHERE code = 'DEP-SALES';
UPDATE departments SET manager_id = 'a1000001-0000-0000-0000-000000000011' WHERE code = 'DEP-PURCH';
UPDATE departments SET manager_id = 'a1000001-0000-0000-0000-000000000005' WHERE code = 'DEP-FIN';
UPDATE departments SET manager_id = 'a1000001-0000-0000-0000-000000000006' WHERE code = 'DEP-LOG';
UPDATE departments SET manager_id = 'a1000001-0000-0000-0000-000000000007' WHERE code = 'DEP-HR';
UPDATE departments SET manager_id = 'a1000001-0000-0000-0000-000000000014' WHERE code = 'DEP-IT';
UPDATE departments SET manager_id = 'a1000001-0000-0000-0000-000000000015' WHERE code = 'DEP-MKT';

-- ============================
-- 19. CRM — Contacts
-- ============================
INSERT INTO contacts (id, first_name, last_name, email, phone, company, position, customer_id, notes, created_by) VALUES
  ('b1000001-0000-0000-0000-000000000001', 'Thomas',   'Meier',      'thomas.meier@bucherer.ch',            '+41 41 369 70 01', 'Bucherer AG',              'Head of Purchasing',           'f0000001-0000-0000-0000-000000000001', 'Hauptansprechpartner Einkauf, seit 2020',   'system'),
  ('b1000001-0000-0000-0000-000000000002', 'Barbara',  'Frei',       'barbara.frei@bucherer.ch',            '+41 41 369 70 02', 'Bucherer AG',              'Category Manager Watches',     'f0000001-0000-0000-0000-000000000001', 'Zuständig für neue Marken im Sortiment',    'system'),
  ('b1000001-0000-0000-0000-000000000003', 'James',    'Harrison',   'j.harrison@watches-switzerland.com',  '+44 20 7317 4601', 'Watches of Switzerland',   'Buying Director',              'f0000001-0000-0000-0000-000000000002', 'UK market lead, quarterly meetings',        'system'),
  ('b1000001-0000-0000-0000-000000000004', 'Marie',    'Dubois',     'marie.dubois@ambassadeurs.ch',        '+41 22 310 45 51', 'Les Ambassadeurs SA',      'Directrice Commerciale',       'f0000001-0000-0000-0000-000000000003', 'Entscheidungsträgerin Westschweiz',         'system'),
  ('b1000001-0000-0000-0000-000000000005', 'Stefan',   'Weber',      'stefan.weber@guebelin.com',           '+41 41 417 02 03', 'Gübelin AG',               'Einkaufsleiter',               'f0000001-0000-0000-0000-000000000004', 'Langjährige Beziehung seit 2018',           'system'),
  ('b1000001-0000-0000-0000-000000000006', 'Akira',    'Tanaka',     'akira.tanaka@tanaka-corp.jp',         '+81 3 5544 8801',  'Tanaka Corporation',       'CEO',                          'f0000001-0000-0000-0000-000000000006', 'Direktkontakt, spricht Englisch',           'system'),
  ('b1000001-0000-0000-0000-000000000007', 'Yuki',     'Sato',       'yuki.sato@tanaka-corp.jp',            '+81 3 5544 8802',  'Tanaka Corporation',       'Import Manager',               'f0000001-0000-0000-0000-000000000006', 'Zuständig für Zollabwicklung und Logistik', 'system'),
  ('b1000001-0000-0000-0000-000000000008', 'Nikolai',  'Petrov',     'n.petrov@petrovcollection.com',       '+971 4 339 8889',  NULL,                       'Private Collector',            'f0000001-0000-0000-0000-000000000008', 'VIP-Kunde, nur auf Einladung',              'system'),
  ('b1000001-0000-0000-0000-000000000009', 'René',     'Beyer',      'rene.beyer@beyer-ch.com',             '+41 44 344 63 64', 'Beyer Chronometrie AG',    'Geschäftsführer',              'f0000001-0000-0000-0000-000000000009', 'Persönlicher Kontakt mit CEO',              'system'),
  ('b1000001-0000-0000-0000-000000000010', 'Sophie',   'Laurent',    'sophie.laurent@gmail.com',            '+41 79 345 67 89', NULL,                       'Sammlerin',                    'f0000001-0000-0000-0000-000000000011', 'VIP-Privatkundin, jährliche Events',        'system');

-- ============================
-- 20. CRM — Interactions
-- ============================
INSERT INTO interactions (id, contact_id, interaction_type, subject, description, interaction_date, follow_up_date, created_by) VALUES
  ('c1000001-0000-0000-0000-000000000001', 'b1000001-0000-0000-0000-000000000001', 'MEETING',  'Jahresgespräch 2026 Bucherer',          'Besprechung Jahresplanung, Sortimentsstrategie, neue Modelle Q2. Bucherer plant 15% mehr Abnahme.',   '2026-01-08 10:00:00+01', '2026-04-01', 'system'),
  ('c1000001-0000-0000-0000-000000000002', 'b1000001-0000-0000-0000-000000000003', 'CALL',     'UK Market Q1 Update',                   'James confirmed Q1 order for 13 pieces. Discussed Brexit customs issues - resolved with new HMRC code.',  '2026-01-15 14:30:00+01', '2026-04-15', 'system'),
  ('c1000001-0000-0000-0000-000000000003', 'b1000001-0000-0000-0000-000000000004', 'EMAIL',    'Neue Kollektion Präsentation',          'Einladung zur Vorstellung der Frühling/Sommer Kollektion in Genf am 15. Februar.',                      '2026-01-20 09:00:00+01', '2026-02-15', 'system'),
  ('c1000001-0000-0000-0000-000000000004', 'b1000001-0000-0000-0000-000000000006', 'MEETING',  'Tokyo Visit — Japan Expansion',         'Met with Tanaka-san in Tokyo. Discussed Japan distribution strategy, 20% growth target for 2026.',       '2026-02-05 09:00:00+09', '2026-05-01', 'system'),
  ('c1000001-0000-0000-0000-000000000005', 'b1000001-0000-0000-0000-000000000008', 'MEETING',  'Dubai Watch Week — Petrov Meeting',      'Private viewing of Limited Edition Diamant at Dubai Watch Week. Petrov ordered 2 pieces immediately.',   '2026-02-10 18:00:00+04', '2026-06-01', 'system'),
  ('c1000001-0000-0000-0000-000000000006', 'b1000001-0000-0000-0000-000000000010', 'CALL',     'Valentine Spezialbestellung',            'Sophie Laurent rief an wegen Limited Edition Diamant als Valentinsgeschenk. Sofortige Bestellung.',       '2026-02-12 11:00:00+01', NULL,         'system'),
  ('c1000001-0000-0000-0000-000000000007', 'b1000001-0000-0000-0000-000000000009', 'MEETING',  'Beyer Chronometrie neue Kollektion',    'Vorstellung neue Modelle bei Beyer, Bahnhofstrasse. Interesse an Chronoswiss 42 Vert und Voyager GMT.',  '2026-03-05 15:00:00+01', '2026-04-05', 'system'),
  ('c1000001-0000-0000-0000-000000000008', 'b1000001-0000-0000-0000-000000000005', 'NOTE',     'Gübelin Feedback Alpine 40',            'Herr Weber berichtet exzellente Kundenzufriedenheit mit Alpine 40 Kollektion. Nachbestellung geplant.',  '2026-03-12 08:30:00+01', '2026-04-15', 'system'),
  ('c1000001-0000-0000-0000-000000000009', 'b1000001-0000-0000-0000-000000000002', 'EMAIL',    'Bucherer Schaufenster-Kampagne',        'Abstimmung Schaufenster-Deko für Bucherer Luzern, SwiftApp Alpine als Hauptprodukt, Start Mai.',         '2026-03-18 16:00:00+01', '2026-05-01', 'system'),
  ('c1000001-0000-0000-0000-000000000010', 'b1000001-0000-0000-0000-000000000007', 'EMAIL',    'Japan Shipment Customs',                'Yuki confirmed customs clearance for SO-2026-0008. Expected delivery to Ginza store by April 30.',       '2026-03-20 10:00:00+09', '2026-04-30', 'system');

-- ============================
-- 21. QUALITY CONTROL — Inspection Plans
-- ============================
INSERT INTO inspection_plans (id, plan_number, name, description, product_id, material_id, created_by) VALUES
  ('d1000001-0000-0000-0000-000000000001', 'IP-001', 'Ganggenauigkeit Automatik',    'Prüfung Gangabweichung: ±4 Sek/Tag in 5 Lagen, COSC-Standard',                          'd0000001-0000-0000-0000-000000000001', NULL, 'system'),
  ('d1000001-0000-0000-0000-000000000002', 'IP-002', 'Ganggenauigkeit Chronograph',  'Prüfung Chronograph: Gangreserve, Start/Stop/Reset, alle Totalizer',                     'd0000001-0000-0000-0000-000000000004', NULL, 'system'),
  ('d1000001-0000-0000-0000-000000000003', 'IP-003', 'Wasserdichtheit 100m',         'Drucktest 10 ATM, Kondenswassertest',                                                    'd0000001-0000-0000-0000-000000000001', NULL, 'system'),
  ('d1000001-0000-0000-0000-000000000004', 'IP-004', 'Wasserdichtheit 300m',         'Drucktest 30 ATM, He-Ventil-Prüfung, Sättigungstauchtest',                                'd0000001-0000-0000-0000-000000000006', NULL, 'system'),
  ('d1000001-0000-0000-0000-000000000005', 'IP-005', 'Optische Endkontrolle',        'Visuelle Inspektion: Zifferblatt, Zeiger, Lünette, Armband, Gravur, Leuchtmasse unter UV', NULL, NULL, 'system'),
  ('d1000001-0000-0000-0000-000000000006', 'IP-006', 'Wareneingangsprüfung Kaliber', 'Eingangskontrolle zugekaufte Kaliber: Ganggenauigkeit, optisch, Magnetisierung',          NULL, 'c0000001-0000-0000-0000-000000000001', 'system'),
  ('d1000001-0000-0000-0000-000000000007', 'IP-007', 'Wareneingangsprüfung Gehäuse', 'Eingangskontrolle: Masse, Oberfläche, Gewindegänge, Passform',                            NULL, 'c0000001-0000-0000-0000-000000000010', 'system');

-- ============================
-- 22. QUALITY CONTROL — Quality Checks
-- ============================
INSERT INTO quality_checks (id, check_number, inspection_plan_id, production_order_id, checked_by, check_date, result, notes, created_by) VALUES
  ('e1000001-0000-0000-0000-000000000001', 'QC-2026-0001', 'd1000001-0000-0000-0000-000000000001', '50000001-0000-0000-0000-000000000001', 'Florian Gerber', '2026-01-18', 'PASS',        '29/30 Stk bestanden, Gangabweichung ∅ +2.3 Sek/Tag, 1 Stk Gehäusefehler → NCR',                'system'),
  ('e1000001-0000-0000-0000-000000000002', 'QC-2026-0002', 'd1000001-0000-0000-0000-000000000003', '50000001-0000-0000-0000-000000000001', 'Sandra Wyss',    '2026-01-18', 'PASS',        'Alle 29 Stk wasserdicht 10 ATM, kein Kondensattest positiv',                                    'system'),
  ('e1000001-0000-0000-0000-000000000003', 'QC-2026-0003', 'd1000001-0000-0000-0000-000000000005', '50000001-0000-0000-0000-000000000001', 'Florian Gerber', '2026-01-19', 'PASS',        'Visuelle Endkontrolle bestanden. 1 Stk kleinere Kratzspuren am Armband → nachpoliert',          'system'),
  ('e1000001-0000-0000-0000-000000000004', 'QC-2026-0004', 'd1000001-0000-0000-0000-000000000002', '50000001-0000-0000-0000-000000000003', 'Florian Gerber', '2026-02-13', 'PASS',        'Chronograph alle 20 Stk bestanden, Start/Stop/Reset einwandfrei, Totalizer korrekt',           'system'),
  ('e1000001-0000-0000-0000-000000000005', 'QC-2026-0005', 'd1000001-0000-0000-0000-000000000001', '50000001-0000-0000-0000-000000000002', 'Sandra Wyss',    '2026-01-30', 'PASS',        'Alpine 40 Bleu: 25/25 bestanden, ∅ +1.8 Sek/Tag, ausgezeichnet',                               'system'),
  ('e1000001-0000-0000-0000-000000000006', 'QC-2026-0006', 'd1000001-0000-0000-0000-000000000006', NULL,                                   'Sandra Wyss',    '2026-01-22', 'CONDITIONAL','Wareneingang ETA 50 Kaliber: 48 i.O., 2 Stk erhöhte Gangabweichung > ±6 Sek → Rücksprache ETA', 'system'),
  ('e1000001-0000-0000-0000-000000000007', 'QC-2026-0007', 'd1000001-0000-0000-0000-000000000007', NULL,                                   'Sandra Wyss',    '2026-02-03', 'PASS',        'Wareneingang Gehäuse SS 40mm (50 Stk) + 42mm (40 Stk): alle i.O.',                             'system'),
  ('e1000001-0000-0000-0000-000000000008', 'QC-2026-0008', 'd1000001-0000-0000-0000-000000000004', '50000001-0000-0000-0000-000000000004', 'Florian Gerber', '2026-03-10', 'PASS',        'Abyss 44 Titan: 8/8 Stk 30 ATM bestanden, He-Ventil einwandfrei',                              'system');

-- ============================
-- 23. QUALITY CONTROL — Non-Conformance Reports
-- ============================
INSERT INTO non_conformance_reports (id, ncr_number, quality_check_id, severity, description, corrective_action, status, closed_at, created_by) VALUES
  ('f1000001-0000-0000-0000-000000000001', 'NCR-2026-0001', 'e1000001-0000-0000-0000-000000000001', 'MINOR',  'Alpine 40 BLK PO-PRD-2026-001: 1 Stk Gehäuse mit feinem Haarriss an der Lünettendichtfläche, nicht reparabel.',
   'Gehäuse als Ausschuss verbucht. Lieferant Metalem SA informiert (Reklamation LF-RK-2026-001). Zusätzliches Gehäuse aus Bestand entnommen.',
   'CLOSED', '2026-01-25 14:30:00+01', 'system'),

  ('f1000001-0000-0000-0000-000000000002', 'NCR-2026-0002', 'e1000001-0000-0000-0000-000000000006', 'MAJOR',  'Wareneingangskontrolle ETA Kaliber SA-3135: 2 von 50 Stück mit Gangabweichung > ±6 Sek/Tag (COSC-Grenze ±4).',
   'Betroffene Kaliber separiert und an ETA SA retourniert (RMA-2026-001). Ersatzlieferung angefordert. Eingangskontrolle ab sofort 100% statt Stichprobe.',
   'IN_PROGRESS', NULL, 'system'),

  ('f1000001-0000-0000-0000-000000000003', 'NCR-2026-0003', 'e1000001-0000-0000-0000-000000000003', 'MINOR',  'Alpine 40 BLK Endkontrolle: 1 Stk leichte Oberflächenkratzer am Oyster-Armband, Glied 3.',
   'Armband nachpoliert, erneute Prüfung bestanden. Kein systematischer Fehler erkannt.',
   'CLOSED', '2026-01-20 11:00:00+01', 'system');

-- ============================
-- 24. INVENTORY — Stock Movements (sample history)
-- ============================
INSERT INTO stock_movements (reference_number, movement_type, item_id, item_type, source_warehouse_id, target_warehouse_id, quantity, movement_date, reason, source_document_type, source_document_id, created_by) VALUES
  -- Goods receipt from PO-2026-0001 (ETA Movements)
  ('SM-2026-0001', 'GOODS_RECEIPT',  'c0000001-0000-0000-0000-000000000001', 'MATERIAL', NULL, 'e0000001-0000-0000-0000-000000000002', 50, '2026-01-20 08:30:00+01', 'Wareneingang ETA Kaliber SA-3135',       'PURCHASE_ORDER', '30000001-0000-0000-0000-000000000001', 'system'),
  -- Goods receipt from PO-2026-0002 (Metalem Cases)
  ('SM-2026-0002', 'GOODS_RECEIPT',  'c0000001-0000-0000-0000-000000000010', 'MATERIAL', NULL, 'e0000001-0000-0000-0000-000000000002', 50, '2026-02-01 09:00:00+01', 'Wareneingang Gehäuse SS 40mm',           'PURCHASE_ORDER', '30000001-0000-0000-0000-000000000002', 'system'),
  ('SM-2026-0003', 'GOODS_RECEIPT',  'c0000001-0000-0000-0000-000000000011', 'MATERIAL', NULL, 'e0000001-0000-0000-0000-000000000002', 40, '2026-02-01 09:00:00+01', 'Wareneingang Gehäuse SS 42mm',           'PURCHASE_ORDER', '30000001-0000-0000-0000-000000000002', 'system'),
  -- Production issue (materials to production)
  ('SM-2026-0004', 'PRODUCTION_ISSUE','c0000001-0000-0000-0000-000000000001', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', NULL, 30, '2026-01-06 07:00:00+01', 'Materialentnahme Alpine 40 BLK Prod.',   'PRODUCTION_ORDER','50000001-0000-0000-0000-000000000001', 'system'),
  ('SM-2026-0005', 'PRODUCTION_ISSUE','c0000001-0000-0000-0000-000000000010', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', NULL, 31, '2026-01-06 07:00:00+01', 'Gehäuse SS 40mm für Produktion (1 Ers.)', 'PRODUCTION_ORDER','50000001-0000-0000-0000-000000000001', 'system'),
  -- Production receipt (finished goods)
  ('SM-2026-0006', 'PRODUCTION_RECEIPT','d0000001-0000-0000-0000-000000000001', 'PRODUCT', NULL, 'e0000001-0000-0000-0000-000000000004', 29, '2026-01-18 16:00:00+01', 'Fertigstellung Alpine 40 BLK Charge',    'PRODUCTION_ORDER','50000001-0000-0000-0000-000000000001', 'system'),
  ('SM-2026-0007', 'PRODUCTION_RECEIPT','d0000001-0000-0000-0000-000000000002', 'PRODUCT', NULL, 'e0000001-0000-0000-0000-000000000004', 25, '2026-01-30 16:00:00+01', 'Fertigstellung Alpine 40 BLU Charge',    'PRODUCTION_ORDER','50000001-0000-0000-0000-000000000002', 'system'),
  ('SM-2026-0008', 'PRODUCTION_RECEIPT','d0000001-0000-0000-0000-000000000004', 'PRODUCT', NULL, 'e0000001-0000-0000-0000-000000000004', 20, '2026-02-13 16:00:00+01', 'Fertigstellung Chronoswiss 42 Charge',   'PRODUCTION_ORDER','50000001-0000-0000-0000-000000000003', 'system'),
  -- Shipments to customers
  ('SM-2026-0009', 'SHIPMENT',       'd0000001-0000-0000-0000-000000000001', 'PRODUCT', 'e0000001-0000-0000-0000-000000000004', NULL, 5, '2026-02-14 10:00:00+01', 'Lieferung Bucherer Alpine 40 BLK',       'SALES_ORDER',    '10000001-0000-0000-0000-000000000001', 'system'),
  ('SM-2026-0010', 'SHIPMENT',       'd0000001-0000-0000-0000-000000000002', 'PRODUCT', 'e0000001-0000-0000-0000-000000000004', NULL, 5, '2026-02-14 10:00:00+01', 'Lieferung Bucherer Alpine 40 BLU',       'SALES_ORDER',    '10000001-0000-0000-0000-000000000001', 'system'),
  -- Inter-warehouse transfer (to showrooms)
  ('SM-2026-0011', 'TRANSFER',       'd0000001-0000-0000-0000-000000000001', 'PRODUCT', 'e0000001-0000-0000-0000-000000000004', 'e0000001-0000-0000-0000-000000000005', 4, '2026-02-20 08:00:00+01', 'Showroom-Auffüllung Zürich',          NULL, NULL, 'system'),
  ('SM-2026-0012', 'TRANSFER',       'd0000001-0000-0000-0000-000000000001', 'PRODUCT', 'e0000001-0000-0000-0000-000000000004', 'e0000001-0000-0000-0000-000000000006', 3, '2026-02-20 08:00:00+01', 'Showroom-Auffüllung Genf',            NULL, NULL, 'system'),
  ('SM-2026-0013', 'TRANSFER',       'd0000001-0000-0000-0000-000000000003', 'PRODUCT', 'e0000001-0000-0000-0000-000000000004', 'e0000001-0000-0000-0000-000000000006', 2, '2026-02-20 08:00:00+01', 'Alpine Or Rose → Showroom Genf',      NULL, NULL, 'system'),
  -- Scrap
  ('SM-2026-0014', 'SCRAP',          'c0000001-0000-0000-0000-000000000010', 'MATERIAL', 'e0000001-0000-0000-0000-000000000002', NULL, 1, '2026-01-18 17:00:00+01', 'Gehäuse Ausschuss (NCR-2026-0001)',    'PRODUCTION_ORDER','50000001-0000-0000-0000-000000000001', 'system');

