CREATE TYPE type_carburant AS ENUM ('DIESEL', 'ESSENCE', 'ELECTRIQUE', 'HYBRIDE', 'GPL');
CREATE TYPE statut_voiture AS ENUM ('EN_MISSION', 'DISPONIBLE', 'EN_MAINTENANCE', 'HORS_SERVICE');

create table if not exists voiture (
    equipment_id VARCHAR(150) constraint voiture_equipment_fk REFERENCES equipment(id),
    warehouse_id VARCHAR(150) constraint voiture_warehouse_fk REFERENCES warehouse(id),
    immatriculation VARCHAR(50),
    type_carburant type_carburant,
    marque VARCHAR(100),
    modele VARCHAR(100),
    annee INTEGER,
    couleur VARCHAR(50),
    kilometrage INTEGER,
    statut statut_voiture,
    PRIMARY KEY (equipment_id, warehouse_id)
);

SELECT add_audit_columns('voiture');
