-- Add more new healthmap subdiseases.
-- Copyright (c) 2015 University of Oxford
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'lnewvis', (SELECT id FROM disease_group WHERE abbreviation='nwvl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'lviscnew', (SELECT id FROM disease_group WHERE abbreviation='nwvl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'inewvisc', (SELECT id FROM disease_group WHERE abbreviation='nwvl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'iviscnew', (SELECT id FROM disease_group WHERE abbreviation='nwvl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'loldvisc', (SELECT id FROM disease_group WHERE abbreviation='owvl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'lviscold', (SELECT id FROM disease_group WHERE abbreviation='owvl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'ioldvisc', (SELECT id FROM disease_group WHERE abbreviation='owvl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'iviscold', (SELECT id FROM disease_group WHERE abbreviation='owvl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'viscnew', (SELECT id FROM disease_group WHERE abbreviation='nwvl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'viscold', (SELECT id FROM disease_group WHERE abbreviation='owvl'));
