-- Identify priority diseases in database.
--
-- Copyright (c) 2014 University of Oxford

ALTER TABLE disease_group ADD COLUMN is_priority_disease boolean;
UPDATE disease_group SET is_priority_disease=false;
UPDATE disease_group SET is_priority_disease=true where id in (select distinct disease_group_id from healthmap_disease where name in ('African Trypanosomiasis', 'Chagas', 'Crimean-Congo Hemorrhagic Fever', 'Japanese Encephalitis', 'West Nile Virus', 'Yellow Fever', 'Murray Valley encephalitis', 'Melioidosis', 'Cholera', 'Guinea Worm', 'Dracunculiasis', 'River Blindness', 'Onchocerciasis', 'Loiasis', 'Dengue', 'Scrub Typhus', 'Chikungunya', 'Polio', 'Ascariasis', 'Hookworm', 'Trichuriasis') union select distinct disease_group_id from healthmap_subdisease);
ALTER TABLE disease_group ALTER COLUMN is_priority_disease SET NOT NULL;
