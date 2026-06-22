INSERT INTO "equipment" (id, name, description, warehouse_id, floor_number, storage_number, is_damaged, is_lost, created_at, updated_at)
VALUES
('equipment1_id', 'Pelle mécanique', 'Pelle Caterpillar 320', 'warehouse1_id', 1, 10, false, false, NOW(), NOW()),
('equipment2_id', 'Bétonnière', 'Bétonnière électrique', 'warehouse1_id', 1, 15, false, false, NOW(), NOW()),
('equipment3_id', 'Climatisation', 'Unité extérieure', 'warehouse2_id', 2, 5, false, false, NOW(), NOW()),
('equipment4_id', 'Grue mobile', 'Grue Liebherr en route', 'warehouse_route_id', NULL, NULL, false, false, NOW(), NOW()),
('equipment5_id', 'Perceuse pro', 'Perceuse chez le vendeur', 'warehouse_at_seller_id', NULL, NULL, false, false, NOW(), NOW());
