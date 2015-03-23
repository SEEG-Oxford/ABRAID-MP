-- Update validator disease groups.
--
-- Copyright (c) 2014 University of Oxford
UPDATE disease_group SET validator_disease_group_id=NULL WHERE validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='ascariasis');
DELETE FROM expert_validator_disease_group WHERE validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='ascariasis');
DELETE FROM validator_disease_group WHERE name='ascariasis';
UPDATE disease_group SET validator_disease_group_id=NULL WHERE validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='HIV');
DELETE FROM expert_validator_disease_group WHERE validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='HIV');
DELETE FROM validator_disease_group WHERE name='HIV';
UPDATE disease_group SET validator_disease_group_id=NULL WHERE validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='hookworm');
DELETE FROM expert_validator_disease_group WHERE validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='hookworm');
DELETE FROM validator_disease_group WHERE name='hookworm';
UPDATE disease_group SET validator_disease_group_id=NULL WHERE validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='lymphatic filariasis');
DELETE FROM expert_validator_disease_group WHERE validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='lymphatic filariasis');
DELETE FROM validator_disease_group WHERE name='lymphatic filariasis';
UPDATE disease_group SET validator_disease_group_id=NULL WHERE validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='schistosomiasis');
DELETE FROM expert_validator_disease_group WHERE validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='schistosomiasis');
DELETE FROM validator_disease_group WHERE name='schistosomiasis';
UPDATE disease_group SET validator_disease_group_id=NULL WHERE validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='trichuriasis');
DELETE FROM expert_validator_disease_group WHERE validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='trichuriasis');
DELETE FROM validator_disease_group WHERE name='trichuriasis';

INSERT INTO validator_disease_group (name) VALUES ('chikungunya');
UPDATE disease_group SET validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='chikungunya') WHERE id=60; -- name='Chikungunya'
INSERT INTO validator_disease_group (name) VALUES ('hantavirus');
UPDATE disease_group SET validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='hantavirus') WHERE id=49; -- originally named 'Bunyaviridae' this is now 'Hantavirus'
INSERT INTO validator_disease_group (name) VALUES ('West Nile virus');
UPDATE disease_group SET validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='West Nile virus') WHERE id=386; -- name='West Nile fever'
INSERT INTO validator_disease_group (name) VALUES ('leprosy');
UPDATE disease_group SET validator_disease_group_id=(SELECT id FROM validator_disease_group WHERE name='leprosy') WHERE id=192; -- name='Leprosy'

UPDATE validator_disease_group SET name='dracunculiasis' WHERE name='Guinea worm'; 