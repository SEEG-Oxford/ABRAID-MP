-- Add new healthmap subdiseases.
-- Copyright (c) 2015 University of Oxford
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'lnewcut', (SELECT id FROM disease_group WHERE abbreviation='nwcl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'loldcut', (SELECT id FROM disease_group WHERE abbreviation='owcl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'lcutnew', (SELECT id FROM disease_group WHERE abbreviation='nwcl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'lcutold', (SELECT id FROM disease_group WHERE abbreviation='owcl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'inewcut', (SELECT id FROM disease_group WHERE abbreviation='nwcl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'ioldcut', (SELECT id FROM disease_group WHERE abbreviation='owcl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'icutnew', (SELECT id FROM disease_group WHERE abbreviation='nwcl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'icutold', (SELECT id FROM disease_group WHERE abbreviation='owcl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'inew', (SELECT id FROM disease_group WHERE abbreviation='nwcl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'iold', (SELECT id FROM disease_group WHERE abbreviation='owcl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'ivisceral', (SELECT id FROM disease_group WHERE abbreviation='vl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'icut', (SELECT id FROM disease_group WHERE abbreviation='cl'));
INSERT INTO healthmap_subdisease (healthmap_disease_id, name, disease_group_id) VALUES ((SELECT id FROM healthmap_disease WHERE name='Leishmaniasis'), 'ivisc', (SELECT id FROM disease_group WHERE abbreviation='vl'));
