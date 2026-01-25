-- ===========================================================
-- V014 — Seed Translations: Swiss multilingual data
-- SwiftApp ERP — SwiftApp Horlogerie SA, Biel/Bienne
-- Locales: de (Swiss German default), fr (Swiss French),
--          it (Swiss Italian), en (English)
-- ===========================================================

-- ============================================================
-- 1. CATEGORY TRANSLATIONS
-- UUIDs from V012: a0000001-0000-0000-0000-0000000000xx
-- ============================================================
INSERT INTO category_translations (category_id, locale, name, description, created_by) VALUES
  -- Uhren / Watches (root)
  ('a0000001-0000-0000-0000-000000000001', 'de', 'Uhren',         'Fertige Uhren und Zeitmesser',                          'system'),
  ('a0000001-0000-0000-0000-000000000001', 'fr', 'Montres',       'Montres et instruments de mesure du temps finies',      'system'),
  ('a0000001-0000-0000-0000-000000000001', 'it', 'Orologi',       'Orologi e strumenti di misurazione del tempo finiti',   'system'),
  ('a0000001-0000-0000-0000-000000000001', 'en', 'Watches',       'Finished watches and timepieces',                       'system'),
  -- Uhrwerke / Movements
  ('a0000001-0000-0000-0000-000000000002', 'de', 'Uhrwerke',      'Mechanische und Quarz-Uhrwerke',                        'system'),
  ('a0000001-0000-0000-0000-000000000002', 'fr', 'Mouvements',    'Mouvements mécaniques et à quartz',                     'system'),
  ('a0000001-0000-0000-0000-000000000002', 'it', 'Movimenti',     'Movimenti meccanici e al quarzo',                       'system'),
  ('a0000001-0000-0000-0000-000000000002', 'en', 'Movements',     'Mechanical and quartz watch movements',                 'system'),
  -- Gehäuse / Cases
  ('a0000001-0000-0000-0000-000000000003', 'de', 'Gehäuse',       'Uhrengehäuse aus verschiedenen Materialien',            'system'),
  ('a0000001-0000-0000-0000-000000000003', 'fr', 'Boîtiers',      'Boîtiers de montre en différents matériaux',            'system'),
  ('a0000001-0000-0000-0000-000000000003', 'it', 'Casse',         'Casse per orologi in diversi materiali',                'system'),
  ('a0000001-0000-0000-0000-000000000003', 'en', 'Cases',         'Watch cases in various materials',                      'system'),
  -- Zifferblätter / Dials
  ('a0000001-0000-0000-0000-000000000004', 'de', 'Zifferblätter', 'Uhren-Zifferblätter',                                   'system'),
  ('a0000001-0000-0000-0000-000000000004', 'fr', 'Cadrans',       'Cadrans de montre',                                     'system'),
  ('a0000001-0000-0000-0000-000000000004', 'it', 'Quadranti',     'Quadranti per orologi',                                 'system'),
  ('a0000001-0000-0000-0000-000000000004', 'en', 'Dials',         'Watch dials',                                           'system'),
  -- Armbänder / Straps
  ('a0000001-0000-0000-0000-000000000005', 'de', 'Armbänder',     'Uhrenarmbänder und Metallarmbänder',                    'system'),
  ('a0000001-0000-0000-0000-000000000005', 'fr', 'Bracelets',     'Bracelets et bracelets métalliques',                    'system'),
  ('a0000001-0000-0000-0000-000000000005', 'it', 'Cinturini',     'Cinturini e bracciali metallici',                       'system'),
  ('a0000001-0000-0000-0000-000000000005', 'en', 'Straps',        'Watch straps and bracelets',                            'system'),
  -- Rohmaterialien / Raw Materials
  ('a0000001-0000-0000-0000-000000000006', 'de', 'Rohmaterialien','Rohmaterialien für die Uhrenherstellung',               'system'),
  ('a0000001-0000-0000-0000-000000000006', 'fr', 'Matières premières','Matières premières pour la fabrication horlogère',  'system'),
  ('a0000001-0000-0000-0000-000000000006', 'it', 'Materie prime', 'Materie prime per la fabbricazione di orologi',         'system'),
  ('a0000001-0000-0000-0000-000000000006', 'en', 'Raw Materials', 'Raw materials for watchmaking',                         'system'),
  -- Verpackung / Packaging
  ('a0000001-0000-0000-0000-000000000007', 'de', 'Verpackung',    'Verpackung und Etuis',                                  'system'),
  ('a0000001-0000-0000-0000-000000000007', 'fr', 'Emballage',     'Emballages et écrins',                                  'system'),
  ('a0000001-0000-0000-0000-000000000007', 'it', 'Imballaggio',   'Imballaggi e astucci',                                  'system'),
  ('a0000001-0000-0000-0000-000000000007', 'en', 'Packaging',     'Packaging and watch boxes',                             'system'),
  -- Sub: Automatik
  ('a0000001-0000-0000-0000-000000000010', 'de', 'Automatik',     'Automatikuhren mit Selbstaufzug',                       'system'),
  ('a0000001-0000-0000-0000-000000000010', 'fr', 'Automatique',   'Montres automatiques à remontage automatique',          'system'),
  ('a0000001-0000-0000-0000-000000000010', 'it', 'Automatico',    'Orologi automatici con carica automatica',              'system'),
  ('a0000001-0000-0000-0000-000000000010', 'en', 'Automatic',     'Self-winding automatic watches',                        'system'),
  -- Sub: Handaufzug
  ('a0000001-0000-0000-0000-000000000011', 'de', 'Handaufzug',    'Uhren mit manuellem Aufzug',                            'system'),
  ('a0000001-0000-0000-0000-000000000011', 'fr', 'Remontage manuel','Montres à remontage manuel',                          'system'),
  ('a0000001-0000-0000-0000-000000000011', 'it', 'Carica manuale','Orologi a carica manuale',                              'system'),
  ('a0000001-0000-0000-0000-000000000011', 'en', 'Manual Wind',   'Hand-wound manual watches',                             'system'),
  -- Sub: Chronographen
  ('a0000001-0000-0000-0000-000000000012', 'de', 'Chronographen', 'Stoppuhren mit Chronograph-Funktion',                   'system'),
  ('a0000001-0000-0000-0000-000000000012', 'fr', 'Chronographes', 'Montres chronographes',                                 'system'),
  ('a0000001-0000-0000-0000-000000000012', 'it', 'Cronografi',    'Orologi cronografo',                                    'system'),
  ('a0000001-0000-0000-0000-000000000012', 'en', 'Chronographs',  'Chronograph stopwatch watches',                         'system'),
  -- Sub: Komplikationen
  ('a0000001-0000-0000-0000-000000000013', 'de', 'Komplikationen','Uhren mit Grosskomplikationen',                         'system'),
  ('a0000001-0000-0000-0000-000000000013', 'fr', 'Complications', 'Montres à grandes complications',                       'system'),
  ('a0000001-0000-0000-0000-000000000013', 'it', 'Complicazioni', 'Orologi con grandi complicazioni',                      'system'),
  ('a0000001-0000-0000-0000-000000000013', 'en', 'Complications', 'Grand complication timepieces',                         'system'),
  -- Sub: Taucheruhren
  ('a0000001-0000-0000-0000-000000000014', 'de', 'Taucheruhren',  'Taucheruhren mit erhöhter Wasserdichtheit',             'system'),
  ('a0000001-0000-0000-0000-000000000014', 'fr', 'Montres de plongée','Montres de plongée haute étanchéité',               'system'),
  ('a0000001-0000-0000-0000-000000000014', 'it', 'Orologi subacquei','Orologi subacquei ad alta impermeabilità',           'system'),
  ('a0000001-0000-0000-0000-000000000014', 'en', 'Dive Watches',  'High water-resistance dive watches',                    'system'),
  -- Sub: Edelmetalle
  ('a0000001-0000-0000-0000-000000000060', 'de', 'Edelmetalle',   'Gold, Platin und andere Edelmetalle',                   'system'),
  ('a0000001-0000-0000-0000-000000000060', 'fr', 'Métaux précieux','Or, platine et autres métaux précieux',                'system'),
  ('a0000001-0000-0000-0000-000000000060', 'it', 'Metalli preziosi','Oro, platino e altri metalli preziosi',               'system'),
  ('a0000001-0000-0000-0000-000000000060', 'en', 'Precious Metals','Gold, platinum and other precious metals',             'system'),
  -- Sub: Edelsteine
  ('a0000001-0000-0000-0000-000000000061', 'de', 'Edelsteine',    'Diamanten und andere Edelsteine',                       'system'),
  ('a0000001-0000-0000-0000-000000000061', 'fr', 'Pierres précieuses','Diamants et autres pierres précieuses',             'system'),
  ('a0000001-0000-0000-0000-000000000061', 'it', 'Pietre preziose','Diamanti e altre pietre preziose',                     'system'),
  ('a0000001-0000-0000-0000-000000000061', 'en', 'Gemstones',     'Diamonds and other gemstones',                          'system'),
  -- Sub: Gläser
  ('a0000001-0000-0000-0000-000000000062', 'de', 'Saphirgläser',  'Doppelt entspiegelte Saphirgläser',                     'system'),
  ('a0000001-0000-0000-0000-000000000062', 'fr', 'Glaces saphir', 'Glaces saphir à double antireflet',                     'system'),
  ('a0000001-0000-0000-0000-000000000062', 'it', 'Vetri zaffiro', 'Vetri zaffiro a doppio antiriflesso',                   'system'),
  ('a0000001-0000-0000-0000-000000000062', 'en', 'Sapphire Crystals','Double anti-reflective sapphire crystals',           'system');

-- ============================================================
-- 2. UNIT OF MEASURE TRANSLATIONS
-- UUIDs from V012: b0000001-0000-0000-0000-0000000000xx
-- ============================================================
INSERT INTO uom_translations (uom_id, locale, name, description, created_by) VALUES
  -- PCS — Stück
  ('b0000001-0000-0000-0000-000000000001', 'de', 'Stück',      'Einzelstück',               'system'),
  ('b0000001-0000-0000-0000-000000000001', 'fr', 'Pièce',      'Pièce individuelle',        'system'),
  ('b0000001-0000-0000-0000-000000000001', 'it', 'Pezzo',      'Pezzo singolo',             'system'),
  ('b0000001-0000-0000-0000-000000000001', 'en', 'Piece',      'Individual piece',          'system'),
  -- SET
  ('b0000001-0000-0000-0000-000000000002', 'de', 'Set',        'Garnitur / Set',            'system'),
  ('b0000001-0000-0000-0000-000000000002', 'fr', 'Ensemble',   'Ensemble / Kit',            'system'),
  ('b0000001-0000-0000-0000-000000000002', 'it', 'Set',        'Set / Kit',                 'system'),
  ('b0000001-0000-0000-0000-000000000002', 'en', 'Set',        'Set / Kit',                 'system'),
  -- G — Gramm
  ('b0000001-0000-0000-0000-000000000003', 'de', 'Gramm',      'Gewicht in Gramm',          'system'),
  ('b0000001-0000-0000-0000-000000000003', 'fr', 'Gramme',     'Poids en grammes',          'system'),
  ('b0000001-0000-0000-0000-000000000003', 'it', 'Grammo',     'Peso in grammi',            'system'),
  ('b0000001-0000-0000-0000-000000000003', 'en', 'Gram',       'Weight in grams',           'system'),
  -- KG
  ('b0000001-0000-0000-0000-000000000004', 'de', 'Kilogramm',  'Gewicht in Kilogramm',      'system'),
  ('b0000001-0000-0000-0000-000000000004', 'fr', 'Kilogramme', 'Poids en kilogrammes',      'system'),
  ('b0000001-0000-0000-0000-000000000004', 'it', 'Chilogrammo','Peso in chilogrammi',       'system'),
  ('b0000001-0000-0000-0000-000000000004', 'en', 'Kilogram',   'Weight in kilograms',       'system'),
  -- M — Meter
  ('b0000001-0000-0000-0000-000000000005', 'de', 'Meter',      'Länge in Metern',           'system'),
  ('b0000001-0000-0000-0000-000000000005', 'fr', 'Mètre',      'Longueur en mètres',        'system'),
  ('b0000001-0000-0000-0000-000000000005', 'it', 'Metro',      'Lunghezza in metri',        'system'),
  ('b0000001-0000-0000-0000-000000000005', 'en', 'Meter',      'Length in meters',          'system'),
  -- MM — Millimeter
  ('b0000001-0000-0000-0000-000000000007', 'de', 'Millimeter', 'Länge in Millimetern',      'system'),
  ('b0000001-0000-0000-0000-000000000007', 'fr', 'Millimètre', 'Longueur en millimètres',   'system'),
  ('b0000001-0000-0000-0000-000000000007', 'it', 'Millimetro', 'Lunghezza in millimetri',   'system'),
  ('b0000001-0000-0000-0000-000000000007', 'en', 'Millimeter', 'Length in millimeters',     'system'),
  -- CT — Karat
  ('b0000001-0000-0000-0000-000000000009', 'de', 'Karat',      'Edelstein-Gewicht in Karat','system'),
  ('b0000001-0000-0000-0000-000000000009', 'fr', 'Carat',      'Poids des gemmes en carats','system'),
  ('b0000001-0000-0000-0000-000000000009', 'it', 'Carato',     'Peso delle gemme in carati','system'),
  ('b0000001-0000-0000-0000-000000000009', 'en', 'Carat',      'Gemstone weight in carats', 'system'),
  -- OZT — Feinunze
  ('b0000001-0000-0000-0000-000000000010', 'de', 'Feinunze',   'Feinunze für Edelmetalle',  'system'),
  ('b0000001-0000-0000-0000-000000000010', 'fr', 'Once Troy',  'Once troy pour métaux précieux','system'),
  ('b0000001-0000-0000-0000-000000000010', 'it', 'Oncia Troy', 'Oncia troy per metalli preziosi','system'),
  ('b0000001-0000-0000-0000-000000000010', 'en', 'Troy Ounce', 'Troy ounce for precious metals','system'),
  -- H — Stunde
  ('b0000001-0000-0000-0000-000000000011', 'de', 'Stunde',     'Zeiteinheit Stunde',        'system'),
  ('b0000001-0000-0000-0000-000000000011', 'fr', 'Heure',      'Unité de temps heure',      'system'),
  ('b0000001-0000-0000-0000-000000000011', 'it', 'Ora',        'Unità di tempo ora',        'system'),
  ('b0000001-0000-0000-0000-000000000011', 'en', 'Hour',       'Time unit hour',            'system'),
  -- BOX — Schachtel
  ('b0000001-0000-0000-0000-000000000012', 'de', 'Schachtel',  'Uhren-Schachtel / Etui',    'system'),
  ('b0000001-0000-0000-0000-000000000012', 'fr', 'Boîte',      'Boîte / Écrin montre',      'system'),
  ('b0000001-0000-0000-0000-000000000012', 'it', 'Scatola',    'Scatola / Astuccio orologio','system'),
  ('b0000001-0000-0000-0000-000000000012', 'en', 'Box',        'Watch box / case',          'system');

-- ============================================================
-- 3. PRODUCT TRANSLATIONS
-- UUIDs from V012: d0000001-0000-0000-0000-0000000000xx
-- ============================================================
INSERT INTO product_translations (product_id, locale, name, description, created_by) VALUES
  -- SW-ALP-40SS-BLK: SwiftApp Alpine 40 (Schwarz)
  ('d0000001-0000-0000-0000-000000000001', 'de', 'SwiftApp Alpine 40',
   'Automatik-Dreizeigeruhr, Edelstahl 40mm, schwarzes Sunburst-Zifferblatt, 100m wasserdicht', 'system'),
  ('d0000001-0000-0000-0000-000000000001', 'fr', 'SwiftApp Alpine 40',
   'Montre automatique trois aiguilles, acier 40mm, cadran sunburst noir, étanche 100m', 'system'),
  ('d0000001-0000-0000-0000-000000000001', 'it', 'SwiftApp Alpine 40',
   'Orologio automatico tre lancette, acciaio 40mm, quadrante sunburst nero, impermeabile 100m', 'system'),
  ('d0000001-0000-0000-0000-000000000001', 'en', 'SwiftApp Alpine 40',
   'Automatic three-hand watch, steel 40mm, black sunburst dial, water-resistant 100m', 'system'),

  -- SW-ALP-40SS-BLU: SwiftApp Alpine 40 Bleu
  ('d0000001-0000-0000-0000-000000000002', 'de', 'SwiftApp Alpine 40 Blau',
   'Automatik-Dreizeigeruhr, Edelstahl 40mm, blaues Fumé-Zifferblatt, 100m wasserdicht', 'system'),
  ('d0000001-0000-0000-0000-000000000002', 'fr', 'SwiftApp Alpine 40 Bleu',
   'Montre automatique trois aiguilles, acier 40mm, cadran fumé bleu, étanche 100m', 'system'),
  ('d0000001-0000-0000-0000-000000000002', 'it', 'SwiftApp Alpine 40 Blu',
   'Orologio automatico tre lancette, acciaio 40mm, quadrante fumé blu, impermeabile 100m', 'system'),
  ('d0000001-0000-0000-0000-000000000002', 'en', 'SwiftApp Alpine 40 Blue',
   'Automatic three-hand watch, steel 40mm, blue fumé dial, water-resistant 100m', 'system'),

  -- SW-ALP-40RG-WHT: Alpine 40 Or Rose
  ('d0000001-0000-0000-0000-000000000003', 'de', 'SwiftApp Alpine 40 Roségold',
   'Automatik-Dreizeigeruhr, 18K Roségold 40mm, weisses Lacquer-Zifferblatt, Krokodillederband', 'system'),
  ('d0000001-0000-0000-0000-000000000003', 'fr', 'SwiftApp Alpine 40 Or Rose',
   'Montre automatique trois aiguilles, or rose 18K 40mm, cadran laqué blanc, bracelet crocodile', 'system'),
  ('d0000001-0000-0000-0000-000000000003', 'it', 'SwiftApp Alpine 40 Oro Rosa',
   'Orologio automatico tre lancette, oro rosa 18K 40mm, quadrante laccato bianco, cinturino coccodrillo', 'system'),
  ('d0000001-0000-0000-0000-000000000003', 'en', 'SwiftApp Alpine 40 Rose Gold',
   'Automatic three-hand watch, 18K rose gold 40mm, white lacquered dial, crocodile strap', 'system'),

  -- SW-CHR-42SS-BLK: Chronoswiss 42
  ('d0000001-0000-0000-0000-000000000004', 'de', 'SwiftApp Chronoswiss 42',
   'Chronograph, Edelstahl 42mm, schwarzes Zifferblatt, Tachymeter-Lünette, 100m wasserdicht', 'system'),
  ('d0000001-0000-0000-0000-000000000004', 'fr', 'SwiftApp Chronoswiss 42',
   'Chronographe, acier 42mm, cadran noir, lunette tachymètre, étanche 100m', 'system'),
  ('d0000001-0000-0000-0000-000000000004', 'it', 'SwiftApp Chronoswiss 42',
   'Cronografo, acciaio 42mm, quadrante nero, lunetta tachimetro, impermeabile 100m', 'system'),
  ('d0000001-0000-0000-0000-000000000004', 'en', 'SwiftApp Chronoswiss 42',
   'Chronograph, steel 42mm, black dial, tachymeter bezel, water-resistant 100m', 'system'),

  -- SW-CHR-42SS-GRN: Chronoswiss 42 Vert
  ('d0000001-0000-0000-0000-000000000005', 'de', 'SwiftApp Chronoswiss 42 Grün',
   'Chronograph, Edelstahl 42mm, grünes Sunburst-Zifferblatt, Tachymeter-Lünette, 100m wasserdicht', 'system'),
  ('d0000001-0000-0000-0000-000000000005', 'fr', 'SwiftApp Chronoswiss 42 Vert',
   'Chronographe, acier 42mm, cadran sunburst vert, lunette tachymètre, étanche 100m', 'system'),
  ('d0000001-0000-0000-0000-000000000005', 'it', 'SwiftApp Chronoswiss 42 Verde',
   'Cronografo, acciaio 42mm, quadrante sunburst verde, lunetta tachimetro, impermeabile 100m', 'system'),
  ('d0000001-0000-0000-0000-000000000005', 'en', 'SwiftApp Chronoswiss 42 Green',
   'Chronograph, steel 42mm, green sunburst dial, tachymeter bezel, water-resistant 100m', 'system'),

  -- SW-DIV-44TI-BLU: Abyss 44
  ('d0000001-0000-0000-0000-000000000006', 'de', 'SwiftApp Abyss 44',
   'Taucheruhr, Titan 44mm, blaues Zifferblatt, Keramik-Drehlünette, 300m wasserdicht, Kautschukband', 'system'),
  ('d0000001-0000-0000-0000-000000000006', 'fr', 'SwiftApp Abyss 44',
   'Montre de plongée, titane 44mm, cadran bleu, lunette céramique tournante, étanche 300m, bracelet caoutchouc', 'system'),
  ('d0000001-0000-0000-0000-000000000006', 'it', 'SwiftApp Abyss 44',
   'Orologio subacqueo, titanio 44mm, quadrante blu, lunetta ceramica girevole, impermeabile 300m, cinturino gomma', 'system'),
  ('d0000001-0000-0000-0000-000000000006', 'en', 'SwiftApp Abyss 44',
   'Dive watch, titanium 44mm, blue dial, rotating ceramic bezel, water-resistant 300m, rubber strap', 'system'),

  -- SW-GMT-42SS-BLK: Voyager GMT
  ('d0000001-0000-0000-0000-000000000007', 'de', 'SwiftApp Voyager GMT',
   'GMT-Automatik, Edelstahl 42mm, schwarzes Zifferblatt, Bi-Color-Lünette, 24h-Anzeige, 100m wasserdicht', 'system'),
  ('d0000001-0000-0000-0000-000000000007', 'fr', 'SwiftApp Voyager GMT',
   'Automatique GMT, acier 42mm, cadran noir, lunette bicolore, affichage 24h, étanche 100m', 'system'),
  ('d0000001-0000-0000-0000-000000000007', 'it', 'SwiftApp Voyager GMT',
   'Automatico GMT, acciaio 42mm, quadrante nero, lunetta bicolore, display 24h, impermeabile 100m', 'system'),
  ('d0000001-0000-0000-0000-000000000007', 'en', 'SwiftApp Voyager GMT',
   'GMT automatic, steel 42mm, black dial, bi-colour bezel, 24h display, water-resistant 100m', 'system'),

  -- SW-DRS-40SS-WHT: Elegance 40
  ('d0000001-0000-0000-0000-000000000008', 'de', 'SwiftApp Elegance 40',
   'Handaufzug-Dressuhr, Edelstahl 40mm, weisses Zifferblatt, Krokodillederband, 30m wasserdicht', 'system'),
  ('d0000001-0000-0000-0000-000000000008', 'fr', 'SwiftApp Élégance 40',
   'Montre habillée à remontage manuel, acier 40mm, cadran blanc, bracelet crocodile, étanche 30m', 'system'),
  ('d0000001-0000-0000-0000-000000000008', 'it', 'SwiftApp Elegance 40',
   'Orologio elegante a carica manuale, acciaio 40mm, quadrante bianco, cinturino coccodrillo, impermeabile 30m', 'system'),
  ('d0000001-0000-0000-0000-000000000008', 'en', 'SwiftApp Elegance 40',
   'Hand-wound dress watch, steel 40mm, white dial, crocodile strap, water-resistant 30m', 'system'),

  -- SW-ALP-40SS-GRN: Alpine 40 Vert
  ('d0000001-0000-0000-0000-000000000009', 'de', 'SwiftApp Alpine 40 Grün',
   'Automatik-Dreizeigeruhr, Edelstahl 40mm, grünes Sunburst-Zifferblatt, 100m wasserdicht', 'system'),
  ('d0000001-0000-0000-0000-000000000009', 'fr', 'SwiftApp Alpine 40 Vert',
   'Montre automatique trois aiguilles, acier 40mm, cadran sunburst vert, étanche 100m', 'system'),
  ('d0000001-0000-0000-0000-000000000009', 'it', 'SwiftApp Alpine 40 Verde',
   'Orologio automatico tre lancette, acciaio 40mm, quadrante sunburst verde, impermeabile 100m', 'system'),
  ('d0000001-0000-0000-0000-000000000009', 'en', 'SwiftApp Alpine 40 Green',
   'Automatic three-hand watch, steel 40mm, green sunburst dial, water-resistant 100m', 'system'),

  -- SW-LTD-40RG-DIA: Limited Diamant
  ('d0000001-0000-0000-0000-000000000010', 'de', 'SwiftApp Alpine Limited Diamant',
   'Limited Edition Automatik, 18K Roségold 40mm, Diamantlünette (1.2ct), weisses Perlmutt, Nr. XX/50', 'system'),
  ('d0000001-0000-0000-0000-000000000010', 'fr', 'SwiftApp Alpine Édition Limitée Diamants',
   'Édition limitée automatique, or rose 18K 40mm, lunette diamants (1.2ct), cadran nacre blanc, N° XX/50', 'system'),
  ('d0000001-0000-0000-0000-000000000010', 'it', 'SwiftApp Alpine Edizione Limitata Diamanti',
   'Edizione limitata automatico, oro rosa 18K 40mm, lunetta diamanti (1.2ct), quadrante madreperla bianca, N° XX/50', 'system'),
  ('d0000001-0000-0000-0000-000000000010', 'en', 'SwiftApp Alpine Limited Edition Diamonds',
   'Limited edition automatic, 18K rose gold 40mm, diamond bezel (1.2ct), white mother-of-pearl dial, No. XX/50', 'system');

-- ============================================================
-- 4. MATERIAL TRANSLATIONS (key components only — movements, cases, dials)
-- UUIDs from V012: c0000001-0000-0000-0000-0000000000xx
-- ============================================================
INSERT INTO material_translations (material_id, locale, name, description, created_by) VALUES
  -- MVT-SA3135 Automatik-Kaliber
  ('c0000001-0000-0000-0000-000000000001', 'de', 'Kaliber SA-3135 Automatik',
   'Hauseigenes Automatik-Kaliber, 28''800 A/h, 48h Gangreserve, bidirektionaler Rotor', 'system'),
  ('c0000001-0000-0000-0000-000000000001', 'fr', 'Calibre SA-3135 Automatique',
   'Calibre automatique maison, 28''800 A/h, réserve de marche 48h, rotor bidirectionnel', 'system'),
  ('c0000001-0000-0000-0000-000000000001', 'it', 'Calibro SA-3135 Automatico',
   'Calibro automatico di manifattura, 28''800 A/h, riserva di carica 48h, rotore bidirezionale', 'system'),
  ('c0000001-0000-0000-0000-000000000001', 'en', 'Calibre SA-3135 Automatic',
   'In-house automatic calibre, 28,800 vph, 48h power reserve, bidirectional rotor', 'system'),

  -- MVT-SA7750 Chronograph-Kaliber
  ('c0000001-0000-0000-0000-000000000002', 'de', 'Kaliber SA-7750 Chronograph',
   'Valjoux-basiertes Chronograph-Kaliber, 28''800 A/h, Säulenrad-Steuerung', 'system'),
  ('c0000001-0000-0000-0000-000000000002', 'fr', 'Calibre SA-7750 Chronographe',
   'Calibre chronographe base Valjoux, 28''800 A/h, roue à colonnes', 'system'),
  ('c0000001-0000-0000-0000-000000000002', 'it', 'Calibro SA-7750 Cronografo',
   'Calibro cronografo base Valjoux, 28''800 A/h, ruota a colonne', 'system'),
  ('c0000001-0000-0000-0000-000000000002', 'en', 'Calibre SA-7750 Chronograph',
   'Valjoux-based chronograph calibre, 28,800 vph, column wheel control', 'system'),

  -- MVT-SA6498 Handaufzug
  ('c0000001-0000-0000-0000-000000000003', 'de', 'Kaliber SA-6498 Handaufzug',
   'Handaufzug-Kaliber, Unitas-Basis, 46h Gangreserve, Sichtboden', 'system'),
  ('c0000001-0000-0000-0000-000000000003', 'fr', 'Calibre SA-6498 Remontage Manuel',
   'Calibre remontage manuel, base Unitas, réserve de marche 46h, fond transparent', 'system'),
  ('c0000001-0000-0000-0000-000000000003', 'it', 'Calibro SA-6498 Carica Manuale',
   'Calibro a carica manuale, base Unitas, riserva di carica 46h, fondello trasparente', 'system'),
  ('c0000001-0000-0000-0000-000000000003', 'en', 'Calibre SA-6498 Manual Wind',
   'Manual-wind calibre, Unitas base, 46h power reserve, exhibition caseback', 'system'),

  -- CAS-SS40 Gehäuse Edelstahl 40mm
  ('c0000001-0000-0000-0000-000000000010', 'de', 'Gehäuse Edelstahl 40mm',
   'Oyster-Stil Edelstahlgehäuse 316L, 40mm Durchmesser, 100m wasserdicht, verschraubte Krone', 'system'),
  ('c0000001-0000-0000-0000-000000000010', 'fr', 'Boîtier Acier 40mm',
   'Boîtier style Oyster en acier 316L, diamètre 40mm, étanche 100m, couronne vissée', 'system'),
  ('c0000001-0000-0000-0000-000000000010', 'it', 'Cassa Acciaio 40mm',
   'Cassa stile Oyster in acciaio 316L, diametro 40mm, impermeabile 100m, corona avvitata', 'system'),
  ('c0000001-0000-0000-0000-000000000010', 'en', 'Steel Case 40mm',
   'Oyster-style 316L steel case, 40mm diameter, water-resistant 100m, screw-down crown', 'system'),

  -- CAS-TI44 Titan-Gehäuse
  ('c0000001-0000-0000-0000-000000000012', 'de', 'Gehäuse Titan 44mm',
   'Titan-Gehäuse Grade 5, 44mm, 300m wasserdicht, Keramik-Drehlünette', 'system'),
  ('c0000001-0000-0000-0000-000000000012', 'fr', 'Boîtier Titane 44mm',
   'Boîtier titane Grade 5, 44mm, étanche 300m, lunette tournante céramique', 'system'),
  ('c0000001-0000-0000-0000-000000000012', 'it', 'Cassa Titanio 44mm',
   'Cassa titanio Grade 5, 44mm, impermeabile 300m, lunetta girevole in ceramica', 'system'),
  ('c0000001-0000-0000-0000-000000000012', 'en', 'Titanium Case 44mm',
   'Grade 5 titanium case, 44mm, water-resistant 300m, rotating ceramic bezel', 'system'),

  -- DIA-BLK01 Schwarzes Zifferblatt
  ('c0000001-0000-0000-0000-000000000020', 'de', 'Zifferblatt Schwarz Sunburst',
   'Schwarzes Sunburst-Zifferblatt, Super-LumiNova applizierte Indizes, Strichindizes', 'system'),
  ('c0000001-0000-0000-0000-000000000020', 'fr', 'Cadran Noir Sunburst',
   'Cadran sunburst noir, index appliqués Super-LumiNova, index bâton', 'system'),
  ('c0000001-0000-0000-0000-000000000020', 'it', 'Quadrante Nero Sunburst',
   'Quadrante sunburst nero, indici applicati Super-LumiNova, indici a bastoncino', 'system'),
  ('c0000001-0000-0000-0000-000000000020', 'en', 'Black Sunburst Dial',
   'Black sunburst dial, Super-LumiNova applied indices, baton markers', 'system'),

  -- BRC-SS20 Oyster Armband
  ('c0000001-0000-0000-0000-000000000030', 'de', 'Armband Edelstahl Oyster 20mm',
   'Oyster-Armband 316L Edelstahl, 20mm Anstossbreite, Faltschliesse mit Sicherung', 'system'),
  ('c0000001-0000-0000-0000-000000000030', 'fr', 'Bracelet Acier Oyster 20mm',
   'Bracelet Oyster acier 316L, 20mm de largeur, fermoir déployant avec sécurité', 'system'),
  ('c0000001-0000-0000-0000-000000000030', 'it', 'Bracciale Acciaio Oyster 20mm',
   'Bracciale Oyster acciaio 316L, 20mm larghezza, chiusura deployante con sicurezza', 'system'),
  ('c0000001-0000-0000-0000-000000000030', 'en', 'Oyster Steel Bracelet 20mm',
   '316L Oyster steel bracelet, 20mm lug width, deployant clasp with safety', 'system'),

  -- STR-CROC20 Krokodillederband
  ('c0000001-0000-0000-0000-000000000032', 'de', 'Lederband Krokodil 20mm',
   'Alligatorlederband, handgenäht, braun patiniert, mit Dornschliesse', 'system'),
  ('c0000001-0000-0000-0000-000000000032', 'fr', 'Bracelet Crocodile 20mm',
   'Bracelet alligator cousu main, patiné brun, avec boucle ardillon', 'system'),
  ('c0000001-0000-0000-0000-000000000032', 'it', 'Cinturino Coccodrillo 20mm',
   'Cinturino coccodrillo cucito a mano, patinato marrone, con fibbia ardiglione', 'system'),
  ('c0000001-0000-0000-0000-000000000032', 'en', 'Crocodile Leather Strap 20mm',
   'Hand-stitched alligator leather strap, brown patina, with pin buckle', 'system');

-- ============================================================
-- 5. WAREHOUSE TRANSLATIONS
-- UUIDs from V012: e0000001-0000-0000-0000-0000000000xx
-- ============================================================
INSERT INTO warehouse_translations (warehouse_id, locale, name, description, created_by) VALUES
  -- WH-MAIN: Hauptlager Biel
  ('e0000001-0000-0000-0000-000000000001', 'de', 'Hauptlager Biel',
   'Zentrallager für Produktion und Versand, Rue de la Gare 42, Biel/Bienne', 'system'),
  ('e0000001-0000-0000-0000-000000000001', 'fr', 'Entrepôt Principal Bienne',
   'Entrepôt central pour la production et expédition, Rue de la Gare 42, Biel/Bienne', 'system'),
  ('e0000001-0000-0000-0000-000000000001', 'it', 'Magazzino Principale Biel',
   'Magazzino centrale per produzione e spedizione, Rue de la Gare 42, Biel/Bienne', 'system'),
  ('e0000001-0000-0000-0000-000000000001', 'en', 'Main Warehouse Biel',
   'Central warehouse for production and dispatch, Rue de la Gare 42, Biel/Bienne', 'system'),
  -- WH-COMP: Komponentenlager
  ('e0000001-0000-0000-0000-000000000002', 'de', 'Komponentenlager',
   'Lager für Uhrenkomponenten und Halbfabrikate, Gebäude B', 'system'),
  ('e0000001-0000-0000-0000-000000000002', 'fr', 'Stock Composants',
   'Stock de composants horlogers et demi-produits, Bâtiment B', 'system'),
  ('e0000001-0000-0000-0000-000000000002', 'it', 'Magazzino Componenti',
   'Magazzino per componenti orologieri e semilavorati, Edificio B', 'system'),
  ('e0000001-0000-0000-0000-000000000002', 'en', 'Component Warehouse',
   'Storage for watch components and semi-finished goods, Building B', 'system'),
  -- WH-RAW: Rohmateriallager
  ('e0000001-0000-0000-0000-000000000003', 'de', 'Rohmateriallager',
   'Tresor und Lager für Edelmetalle und Edelsteine', 'system'),
  ('e0000001-0000-0000-0000-000000000003', 'fr', 'Stock Matières Premières',
   'Coffre et stock de métaux précieux et pierres gemmes', 'system'),
  ('e0000001-0000-0000-0000-000000000003', 'it', 'Magazzino Materie Prime',
   'Caveau e magazzino per metalli preziosi e pietre gemme', 'system'),
  ('e0000001-0000-0000-0000-000000000003', 'en', 'Raw Materials Vault',
   'Vault and storage for precious metals and gemstones', 'system'),
  -- WH-FIN: Fertigwarenlager
  ('e0000001-0000-0000-0000-000000000004', 'de', 'Fertigwarenlager',
   'Lager für versandfertige Uhren, klimatisiert', 'system'),
  ('e0000001-0000-0000-0000-000000000004', 'fr', 'Stock Produits Finis',
   'Stock de montres prêtes à l''expédition, climatisé', 'system'),
  ('e0000001-0000-0000-0000-000000000004', 'it', 'Magazzino Prodotti Finiti',
   'Magazzino per orologi pronti alla spedizione, climatizzato', 'system'),
  ('e0000001-0000-0000-0000-000000000004', 'en', 'Finished Goods Warehouse',
   'Climate-controlled storage for shipment-ready watches', 'system'),
  -- WH-ZH: Showroom Zürich
  ('e0000001-0000-0000-0000-000000000005', 'de', 'Showroom Zürich',
   'Retail-Lager und Ausstellungsraum Bahnhofstrasse, Zürich', 'system'),
  ('e0000001-0000-0000-0000-000000000005', 'fr', 'Showroom Zurich',
   'Stock retail et salle d''exposition Bahnhofstrasse, Zurich', 'system'),
  ('e0000001-0000-0000-0000-000000000005', 'it', 'Showroom Zurigo',
   'Magazzino retail e sala espositiva Bahnhofstrasse, Zurigo', 'system'),
  ('e0000001-0000-0000-0000-000000000005', 'en', 'Zurich Showroom',
   'Retail stock and exhibition space Bahnhofstrasse, Zurich', 'system');

-- ============================================================
-- 6. WORK CENTER TRANSLATIONS
-- UUIDs from V012: 40000001-0000-0000-0000-0000000000xx
-- ============================================================
INSERT INTO work_center_translations (work_center_id, locale, name, description, created_by) VALUES
  -- WC-ASM01: Montage Linie 1
  ('40000001-0000-0000-0000-000000000001', 'de', 'Montagelinie 1',
   'Hauptmontagelinie für Automatik- und Handaufzuguhren, 12 Uhren/Tag', 'system'),
  ('40000001-0000-0000-0000-000000000001', 'fr', 'Ligne d''Assemblage 1',
   'Ligne d''assemblage principale, montres automatiques et remontage manuel, 12 montres/jour', 'system'),
  ('40000001-0000-0000-0000-000000000001', 'it', 'Linea di Montaggio 1',
   'Linea di montaggio principale, orologi automatici e a carica manuale, 12 orologi/giorno', 'system'),
  ('40000001-0000-0000-0000-000000000001', 'en', 'Assembly Line 1',
   'Main assembly line for automatic and manual-wind watches, 12 watches/day', 'system'),
  -- WC-ASM02: Montage Linie 2
  ('40000001-0000-0000-0000-000000000002', 'de', 'Montagelinie 2',
   'Zweite Montagelinie für Chronographen und Komplikationen, 8 Uhren/Tag', 'system'),
  ('40000001-0000-0000-0000-000000000002', 'fr', 'Ligne d''Assemblage 2',
   'Deuxième ligne d''assemblage, chronographes et complications, 8 montres/jour', 'system'),
  ('40000001-0000-0000-0000-000000000002', 'it', 'Linea di Montaggio 2',
   'Seconda linea di montaggio, cronografi e complicazioni, 8 orologi/giorno', 'system'),
  ('40000001-0000-0000-0000-000000000002', 'en', 'Assembly Line 2',
   'Second assembly line for chronographs and complications, 8 watches/day', 'system'),
  -- WC-CASE: Gehäusebau
  ('40000001-0000-0000-0000-000000000003', 'de', 'Gehäusefertigung',
   'CNC-Bearbeitung, Polieren und Satin-Finish für Uhrengehäuse', 'system'),
  ('40000001-0000-0000-0000-000000000003', 'fr', 'Fabrication Boîtiers',
   'Usinage CNC, polissage et finition satinée des boîtiers', 'system'),
  ('40000001-0000-0000-0000-000000000003', 'it', 'Lavorazione Casse',
   'Lavorazione CNC, lucidatura e finitura satinata delle casse', 'system'),
  ('40000001-0000-0000-0000-000000000003', 'en', 'Case Manufacturing',
   'CNC machining, polishing and satin finishing of watch cases', 'system'),
  -- WC-DIAL: Zifferblattwerkstatt
  ('40000001-0000-0000-0000-000000000004', 'de', 'Zifferblattwerkstatt',
   'Galvanik, Lackierung, Druck und Applikation von Zifferblättern', 'system'),
  ('40000001-0000-0000-0000-000000000004', 'fr', 'Atelier Cadrans',
   'Galvanoplastie, laquage, impression et application de cadrans', 'system'),
  ('40000001-0000-0000-0000-000000000004', 'it', 'Laboratorio Quadranti',
   'Galvanica, verniciatura, stampa e applicazione di quadranti', 'system'),
  ('40000001-0000-0000-0000-000000000004', 'en', 'Dial Workshop',
   'Electroplating, lacquering, printing and application of watch dials', 'system'),
  -- WC-QC: Qualitätskontrolle
  ('40000001-0000-0000-0000-000000000005', 'de', 'Qualitätskontrolle',
   'Gangkontrolle (COSC-Standard), Wasserdichtheitstest und Endkontrolle', 'system'),
  ('40000001-0000-0000-0000-000000000005', 'fr', 'Contrôle Qualité',
   'Contrôle de marche (norme COSC), test d''étanchéité et contrôle final', 'system'),
  ('40000001-0000-0000-0000-000000000005', 'it', 'Controllo Qualità',
   'Controllo di marcia (standard COSC), test impermeabilità e controllo finale', 'system'),
  ('40000001-0000-0000-0000-000000000005', 'en', 'Quality Control',
   'Rate testing (COSC standard), water-resistance test and final inspection', 'system');

-- ============================================================
-- 7. DEPARTMENT TRANSLATIONS
-- UUIDs from V012: 90000001-0000-0000-0000-0000000000xx
-- ============================================================
INSERT INTO department_translations (department_id, locale, name, description, created_by) VALUES
  -- DEP-GL: Geschäftsleitung
  ('90000001-0000-0000-0000-000000000001', 'de', 'Geschäftsleitung',
   'Direktion und Unternehmensführung', 'system'),
  ('90000001-0000-0000-0000-000000000001', 'fr', 'Direction générale',
   'Direction et gouvernance d''entreprise', 'system'),
  ('90000001-0000-0000-0000-000000000001', 'it', 'Direzione generale',
   'Direzione e governance aziendale', 'system'),
  ('90000001-0000-0000-0000-000000000001', 'en', 'Executive Management',
   'Corporate direction and governance', 'system'),
  -- DEP-PROD: Produktion
  ('90000001-0000-0000-0000-000000000002', 'de', 'Produktion',
   'Uhrenherstellung, Montage und Werkstätten', 'system'),
  ('90000001-0000-0000-0000-000000000002', 'fr', 'Production',
   'Fabrication horlogère, assemblage et ateliers', 'system'),
  ('90000001-0000-0000-0000-000000000002', 'it', 'Produzione',
   'Fabbricazione orologi, montaggio e laboratori', 'system'),
  ('90000001-0000-0000-0000-000000000002', 'en', 'Production',
   'Watch manufacturing, assembly and workshops', 'system'),
  -- DEP-QC: Qualitätssicherung
  ('90000001-0000-0000-0000-000000000003', 'de', 'Qualitätssicherung',
   'Qualitätskontrolle, Prüfung und Zertifizierung', 'system'),
  ('90000001-0000-0000-0000-000000000003', 'fr', 'Assurance qualité',
   'Contrôle qualité, tests et certification', 'system'),
  ('90000001-0000-0000-0000-000000000003', 'it', 'Assicurazione qualità',
   'Controllo qualità, collaudi e certificazione', 'system'),
  ('90000001-0000-0000-0000-000000000003', 'en', 'Quality Assurance',
   'Quality control, testing and certification', 'system'),
  -- DEP-SALES: Vertrieb
  ('90000001-0000-0000-0000-000000000004', 'de', 'Vertrieb',
   'Verkauf, Kundenbetreuung und Exportmanagement', 'system'),
  ('90000001-0000-0000-0000-000000000004', 'fr', 'Ventes',
   'Ventes, service client et gestion export', 'system'),
  ('90000001-0000-0000-0000-000000000004', 'it', 'Vendite',
   'Vendite, assistenza clienti e gestione export', 'system'),
  ('90000001-0000-0000-0000-000000000004', 'en', 'Sales',
   'Sales, customer service and export management', 'system'),
  -- DEP-PURCH: Einkauf
  ('90000001-0000-0000-0000-000000000005', 'de', 'Einkauf',
   'Beschaffung, Lieferantenmanagement und Rohstoffeinkauf', 'system'),
  ('90000001-0000-0000-0000-000000000005', 'fr', 'Achats',
   'Approvisionnement, gestion fournisseurs et achat matières', 'system'),
  ('90000001-0000-0000-0000-000000000005', 'it', 'Acquisti',
   'Approvvigionamento, gestione fornitori e acquisto materie prime', 'system'),
  ('90000001-0000-0000-0000-000000000005', 'en', 'Purchasing',
   'Procurement, supplier management and raw material sourcing', 'system'),
  -- DEP-FIN: Finanzen
  ('90000001-0000-0000-0000-000000000006', 'de', 'Finanzen & Buchhaltung',
   'Rechnungswesen, Controlling und Treasury', 'system'),
  ('90000001-0000-0000-0000-000000000006', 'fr', 'Finance & Comptabilité',
   'Comptabilité, contrôle de gestion et trésorerie', 'system'),
  ('90000001-0000-0000-0000-000000000006', 'it', 'Finanza & Contabilità',
   'Contabilità, controllo di gestione e tesoreria', 'system'),
  ('90000001-0000-0000-0000-000000000006', 'en', 'Finance & Accounting',
   'Accounting, controlling and treasury', 'system'),
  -- DEP-LOG: Logistik
  ('90000001-0000-0000-0000-000000000007', 'de', 'Logistik',
   'Lager, Versand, Wareneingang und Supply Chain', 'system'),
  ('90000001-0000-0000-0000-000000000007', 'fr', 'Logistique',
   'Entreposage, expédition, réception et chaîne d''approvisionnement', 'system'),
  ('90000001-0000-0000-0000-000000000007', 'it', 'Logistica',
   'Magazzino, spedizione, ricevimento merci e supply chain', 'system'),
  ('90000001-0000-0000-0000-000000000007', 'en', 'Logistics',
   'Warehousing, shipping, goods receipt and supply chain', 'system'),
  -- DEP-HR: Human Resources
  ('90000001-0000-0000-0000-000000000008', 'de', 'Human Resources',
   'Personalwesen, Lohnbuchhaltung und Personalentwicklung', 'system'),
  ('90000001-0000-0000-0000-000000000008', 'fr', 'Ressources humaines',
   'Gestion du personnel, paie et développement RH', 'system'),
  ('90000001-0000-0000-0000-000000000008', 'it', 'Risorse umane',
   'Gestione del personale, paghe e sviluppo HR', 'system'),
  ('90000001-0000-0000-0000-000000000008', 'en', 'Human Resources',
   'Personnel management, payroll and HR development', 'system'),
  -- DEP-IT: IT & Digitalisierung
  ('90000001-0000-0000-0000-000000000009', 'de', 'IT & Digitalisierung',
   'Informatik, ERP-Systeme und digitale Transformation', 'system'),
  ('90000001-0000-0000-0000-000000000009', 'fr', 'IT & Digitalisation',
   'Informatique, systèmes ERP et transformation digitale', 'system'),
  ('90000001-0000-0000-0000-000000000009', 'it', 'IT & Digitalizzazione',
   'Informatica, sistemi ERP e trasformazione digitale', 'system'),
  ('90000001-0000-0000-0000-000000000009', 'en', 'IT & Digitalisation',
   'Information technology, ERP systems and digital transformation', 'system'),
  -- DEP-MKT: Marketing
  ('90000001-0000-0000-0000-000000000010', 'de', 'Marketing',
   'Marketing, Kommunikation, Events und Messeauftritte', 'system'),
  ('90000001-0000-0000-0000-000000000010', 'fr', 'Marketing',
   'Marketing, communication, événements et salons horlogers', 'system'),
  ('90000001-0000-0000-0000-000000000010', 'it', 'Marketing',
   'Marketing, comunicazione, eventi e fiere orologiere', 'system'),
  ('90000001-0000-0000-0000-000000000010', 'en', 'Marketing',
   'Marketing, communications, events and watch fairs', 'system');

