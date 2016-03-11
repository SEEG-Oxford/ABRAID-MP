-- Add agent_type and filter_bias_data_by_agent_type flags to disease_group
--
-- Copyright (c) 2016 University of Oxford

ALTER TABLE disease_group ADD COLUMN agent_type varchar(20);
ALTER TABLE disease_group ADD COLUMN filter_bias_data_by_agent_type boolean;

ALTER TABLE disease_group ADD CONSTRAINT ck_disease_group_agent_type
    CHECK (agent_type IN ('ALGA', 'BACTERIA', 'FUNGUS', 'PARASITE', 'PRION', 'VIRUS'));

-- Populate
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=34; -- Bartonellas
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=35; -- Bartonellosis - cat borne
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=43; -- Botulism
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=48; -- Brucellosis
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=52; -- Campylobacteriosis
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=64; -- Cholera
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=71; -- Clostridium difficile colitis
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=92; -- Diphtheria
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=132; -- Glanders
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=135; -- Gonococcal infection
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=187; -- Legionellosis
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=192; -- Leprosy
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=193; -- Leptospirosis
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=194; -- Listeriosis
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=199; -- Lyme disease
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=201; -- Lymphogranuloma venereum
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=211; -- Melioidosis
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=245; -- Ornithosis
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=266; -- Pertussis
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=271; -- Plague
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=284; -- Q fever
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=304; -- Rocky Mountain spotted fever
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=315; -- Scarlet fever
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=325; -- Shigellosis
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=340; -- Syphilis
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=343; -- Tetanus
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=351; -- Trachoma
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=361; -- Tuberculosis
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=362; -- Tularemia
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=364; -- Typhoid and enteric fever
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=367; -- Typhus - scrub (mite-borne)
UPDATE disease_group SET agent_type='BACTERIA' WHERE id=377; -- Vibrio parahaemolyticus infection
UPDATE disease_group SET agent_type='FUNGUS' WHERE id=72; -- Coccidioidomycosis
UPDATE disease_group SET agent_type='FUNGUS' WHERE id=80; -- Cryptococcosis
UPDATE disease_group SET agent_type='FUNGUS' WHERE id=152; -- Histoplasmosis
UPDATE disease_group SET agent_type='FUNGUS' WHERE id=254; -- Paracoccidioidomycosis
UPDATE disease_group SET agent_type='FUNGUS' WHERE id=331; -- Sporotrichosis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=22; -- Ascariasis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=26; -- Babesiosis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=41; -- Blastomycosis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=81; -- Cryptosporidosis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=84; -- Cysticercosis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=93; -- Diphyllobothriasis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=96; -- Dracunculiasis (Guinea worm)
UPDATE disease_group SET agent_type='PARASITE' WHERE id=109; -- Enterobiasis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=118; -- Fascioliasis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=119; -- Fasciolopsiasis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=131; -- Giardiasis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=133; -- Gnathostomiasis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=157; -- Hookworm
UPDATE disease_group SET agent_type='PARASITE' WHERE id=171; -- Isosporiasis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=202; -- Malarias
UPDATE disease_group SET agent_type='PARASITE' WHERE id=215; -- Metagonimiasis (see Heterophyids)
UPDATE disease_group SET agent_type='PARASITE' WHERE id=240; -- Onchocerciasis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=255; -- Paragonimiasis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=258; -- Pediculosis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=341; -- Taeniasis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=349; -- Toxocariasis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=350; -- Toxoplasmosis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=353; -- Trichinosis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=356; -- Trichuriasis
UPDATE disease_group SET agent_type='PARASITE' WHERE id=359; -- Trypanosomiasis - African
UPDATE disease_group SET agent_type='PARASITE' WHERE id=360; -- Trypanosomiasis- American
UPDATE disease_group SET agent_type='PRION' WHERE id=78; -- Creutzfeldt-Jakob disease
UPDATE disease_group SET agent_type='VIRUS' WHERE id=4; -- Adenovirus
UPDATE disease_group SET agent_type='VIRUS' WHERE id=42; -- Bolivian hemorrhagic fever (Machupo virus)
UPDATE disease_group SET agent_type='VIRUS' WHERE id=60; -- Chikungunya
UPDATE disease_group SET agent_type='VIRUS' WHERE id=74; -- Colorado tick fever
UPDATE disease_group SET agent_type='VIRUS' WHERE id=79; -- Crimean-Congo hemorrhagic fever
UPDATE disease_group SET agent_type='VIRUS' WHERE id=85; -- Cytomegalovirus infection (Human herpesvirus 5)
UPDATE disease_group SET agent_type='VIRUS' WHERE id=87; -- Dengue
UPDATE disease_group SET agent_type='VIRUS' WHERE id=97; -- Eastern equine encephalitis
UPDATE disease_group SET agent_type='VIRUS' WHERE id=98; -- Ebola
UPDATE disease_group SET agent_type='VIRUS' WHERE id=141; -- Hepatitis A
UPDATE disease_group SET agent_type='VIRUS' WHERE id=142; -- Hepatitis B
UPDATE disease_group SET agent_type='VIRUS' WHERE id=143; -- Hepatitis C
UPDATE disease_group SET agent_type='VIRUS' WHERE id=144; -- Hepatitis D
UPDATE disease_group SET agent_type='VIRUS' WHERE id=145; -- Hepatitis E
UPDATE disease_group SET agent_type='VIRUS' WHERE id=149; -- Herpes simplex infection
UPDATE disease_group SET agent_type='VIRUS' WHERE id=154; -- HIV/AIDS
UPDATE disease_group SET agent_type='VIRUS' WHERE id=159; -- Human papillomavirus
UPDATE disease_group SET agent_type='VIRUS' WHERE id=164; -- Infectious mononucleosis or EBV infection
UPDATE disease_group SET agent_type='VIRUS' WHERE id=173; -- Japanese encephalitis
UPDATE disease_group SET agent_type='VIRUS' WHERE id=186; -- Lassa fever
UPDATE disease_group SET agent_type='VIRUS' WHERE id=208; -- Marburg virus disease
UPDATE disease_group SET agent_type='VIRUS' WHERE id=220; -- Monkey pox
UPDATE disease_group SET agent_type='VIRUS' WHERE id=222; -- Murray Valley encephalitis
UPDATE disease_group SET agent_type='VIRUS' WHERE id=234; -- Nipah and Nipah-like virus disease
UPDATE disease_group SET agent_type='VIRUS' WHERE id=277; -- Poliomyelitis
UPDATE disease_group SET agent_type='VIRUS' WHERE id=278; -- Powassan
UPDATE disease_group SET agent_type='VIRUS' WHERE id=286; -- Rabies
UPDATE disease_group SET agent_type='VIRUS' WHERE id=290; -- Respiratory syncytial virus infection
UPDATE disease_group SET agent_type='VIRUS' WHERE id=302; -- Rift Valley fever
UPDATE disease_group SET agent_type='VIRUS' WHERE id=305; -- Roseola or human herpesvirus 6
UPDATE disease_group SET agent_type='VIRUS' WHERE id=307; -- Ross River virus
UPDATE disease_group SET agent_type='VIRUS' WHERE id=313; -- SARS
UPDATE disease_group SET agent_type='VIRUS' WHERE id=332; -- St. Louis encephalitis
UPDATE disease_group SET agent_type='VIRUS' WHERE id=374; -- Venezuelan equine encephalitis
UPDATE disease_group SET agent_type='VIRUS' WHERE id=386; -- West Nile fever
UPDATE disease_group SET agent_type='VIRUS' WHERE id=387; -- Western equine encephalitis
UPDATE disease_group SET agent_type='VIRUS' WHERE id=391; -- Yellow fever
UPDATE disease_group SET agent_type='VIRUS' WHERE id=393; -- Zika

-- Subdiseases, so ID not known in advance
UPDATE disease_group SET agent_type='PARASITE' WHERE abbreviation='nwcl';
UPDATE disease_group SET agent_type='PARASITE' WHERE abbreviation='owcl';

UPDATE disease_group SET filter_bias_data_by_agent_type=FALSE;

ALTER TABLE disease_group ALTER COLUMN filter_bias_data_by_agent_type SET NOT NULL;