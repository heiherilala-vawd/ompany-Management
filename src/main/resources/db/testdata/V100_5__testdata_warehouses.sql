INSERT INTO "warehouse" (id, name, description, job_id, created_at, updated_at)
VALUES
('warehouse1_id', 'Entrepôt Nord', 'Stockage matériaux lourds', 'job1_id', NOW(), NOW()),
('warehouse2_id', 'Entrepôt Sud', 'Stockage équipements', 'job2_id', NOW(), NOW()),
('warehouse_route_id', 'En route', 'Emplacement virtuel pour les équipements en déplacement', NULL, NOW(), NOW()),
('warehouse_at_seller_id', 'Chez le vendeur', 'Emplacement virtuel pour les équipements encore chez le vendeur', NULL, NOW(), NOW()),
('warehouse_unfindable_id', 'Introuvable', 'Emplacement virtuel pour les équipements introuvables', NULL, NOW(), NOW());
