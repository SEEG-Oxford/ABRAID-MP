-- Wireup HM subdieases for leish.
--
-- Copyright (c) 2014 University of Oxford

-- Promote liesh to a cluster
UPDATE disease_group SET group_type='CLUSTER' WHERE abbreviation='leish';
-- Promote visc liesh to a micro-cluster
UPDATE disease_group SET group_type='MICROCLUSTER' WHERE abbreviation='vl';
-- Create micro-cluster for cutaneous liesh
INSERT INTO disease_group (name, group_type, public_name, short_name, abbreviation, is_global, validator_disease_group_id, weighting, min_new_locations_trigger, min_data_volume, min_distinct_countries, occurs_in_africa, use_machine_learning) VALUES ('Leishmaniasis - cutaneous/mucosal', 'MICROCLUSTER', 'cutaneous leishmaniasis', 'cutaneous leishmaniasis', 'cl', TRUE, 9, 1, 500, 1000, 15, TRUE, TRUE);
-- Create singles for ow/nw visc liesh
INSERT INTO disease_group (name, group_type, public_name, short_name, abbreviation, is_global, validator_disease_group_id, weighting, min_new_locations_trigger, min_data_volume, min_distinct_countries, occurs_in_africa, use_machine_learning) VALUES ('Leishmaniasis - visceral, New World', 'SINGLE', 'visceral leishmaniasis (New World)', 'visceral leishmaniasis (NW)', 'nwvl', FALSE, 9, 1, 500, 1000, 15, TRUE, TRUE);
INSERT INTO disease_group (name, group_type, public_name, short_name, abbreviation, is_global, validator_disease_group_id, weighting, min_new_locations_trigger, min_data_volume, min_distinct_countries, occurs_in_africa, use_machine_learning) VALUES ('Leishmaniasis - visceral, Old World', 'SINGLE', 'visceral leishmaniasis (Old World)', 'visceral leishmaniasis (OW)', 'owvl', FALSE, 9, 1, 500, 1000, 15, TRUE, TRUE);
-- Wire up parent diseases
UPDATE disease_group SET parent_id=(SELECT id FROM disease_group WHERE abbreviation='leish') WHERE abbreviation='vl';
UPDATE disease_group SET parent_id=(SELECT id FROM disease_group WHERE abbreviation='leish') WHERE abbreviation='cl';
UPDATE disease_group SET parent_id=(SELECT id FROM disease_group WHERE abbreviation='cl') WHERE abbreviation='nwcl';
UPDATE disease_group SET parent_id=(SELECT id FROM disease_group WHERE abbreviation='cl') WHERE abbreviation='owcl';
UPDATE disease_group SET parent_id=(SELECT id FROM disease_group WHERE abbreviation='vl') WHERE abbreviation='nwvl';
UPDATE disease_group SET parent_id=(SELECT id FROM disease_group WHERE abbreviation='vl') WHERE abbreviation='owvl';
-- Add new healthmap subdiesases
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'cut', (SELECT id FROM disease_group WHERE abbreviation='cl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'newcut', (SELECT id FROM disease_group WHERE abbreviation='nwcl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'oldcut', (SELECT id FROM disease_group WHERE abbreviation='owcl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'visc', (SELECT id FROM disease_group WHERE abbreviation='vl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'newvisc', (SELECT id FROM disease_group WHERE abbreviation='nwvl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'oldvisc', (SELECT id FROM disease_group WHERE abbreviation='owvl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'lcut', (SELECT id FROM disease_group WHERE abbreviation='cl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'lvisc', (SELECT id FROM disease_group WHERE abbreviation='vl'));
