-- for using in DB
select * from areas;
select * from substations;
select * from residential_load_readings;

-- 1. Insert into substations
INSERT INTO substations (code, name, sub_profile_type, status, capacity_kw, current_load_kw)
VALUES ('SUB-1', 'Main Substation 1', 'GRID', 'ACTIVE', 5000.0, 350.02);

INSERT INTO areas (code, name, type, substation_id, current_load_kw, updated_at)
VALUES (
    'AREA-R1',
    'Residential Area 1',
    'RESIDENTIAL',
    (SELECT substation_id FROM substations WHERE code = 'SUB-1'),
    350.02,
    CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_load_readings_area_time
ON residential_load_readings(area_id, recorded_at);