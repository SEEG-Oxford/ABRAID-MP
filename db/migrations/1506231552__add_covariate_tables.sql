-- Add tabels to store covariate configuration
--
-- Copyright (c) 2014 University of Oxford

CREATE TABLE covariate_file (
    id serial CONSTRAINT pk_covariate_file PRIMARY KEY,
    name varchar(70),
    file varchar(80) NOT NULL
        CONSTRAINT uq_covariate_file_file UNIQUE,
    hide boolean NOT NULL,
    info varchar(200)
);

GRANT SELECT, INSERT, UPDATE ON covariate_file TO ${application_username};
GRANT SELECT, UPDATE ON covariate_file_id_seq TO ${application_username};

CREATE TABLE disease_group_covariate_file (
    disease_group_id integer NOT NULL
        CONSTRAINT fk_disease_group_covariate_file_disease_group REFERENCES disease_group (id),
    covariate_file_id integer NOT NULL
        CONSTRAINT fk_disease_group_covariate_file_covariate_file REFERENCES covariate_file (id),
    CONSTRAINT pk_disease_group_covariate_file PRIMARY KEY (disease_group_id, covariate_file_id)
);

GRANT SELECT, INSERT, DELETE ON disease_group_covariate_file TO ${application_username};

-- Add default covariates
INSERT INTO covariate_file (name, file, hide) VALUES 
	('EC JRC Urban Accessability', 'access.tif', FALSE),
	('Prevalence of Duffy negativity (%)', 'duffy_neg.tif', FALSE),
	('G-Econ relative poverty', 'gecon.tif', FALSE),
	('MODIS Elevation', 'mod_dem.tif', FALSE),
	('WorldClim monthly precipitation (mean)', 'prec57a0.tif', FALSE),
	('WorldClim monthly precipitation (1st amplitude)', 'prec57a1.tif', FALSE),
	('WorldClim monthly precipitation (2nd amplitude)', 'prec57a2.tif', FALSE),
	('WorldClim monthly precipitation (minimum)', 'prec57mn.tif', FALSE),
	('WorldClim monthly precipitation (maximum)', 'prec57mx.tif', FALSE),
	('WorldClim monthly precipitation (1st phase)', 'prec57p1.tif', FALSE),
	('WorldClim monthly precipitation (2nd phase)', 'prec57p2.tif', FALSE),
	('Temperature suitability index (Malaria Pf)', 'tempaucpf.tif', FALSE),
	('Temperature suitability index (Malaria Pv)', 'tempaucpv.tif', FALSE),
	('Temperature suitability index (Dengue)', 'tempsuit.tif', FALSE),
	('GRUMP peri-urban surface', 'upr_p.tif', FALSE),
	('GRUMP urban surface', 'upr_u.tif', FALSE),
	('AVHRR Land Surface Temperature (mean)', 'wd0107a0.tif', FALSE),
	('AVHRR Land Surface Temperature (minimum)', 'wd0107mn.tif', FALSE),
	('AVHRR Land Surface Temperature (maximum)', 'wd0107mx.tif', FALSE),
	('AVHRR Normalized Difference Vegetation Index (mean)', 'wd0114a0.tif', FALSE),
	('AVHRR Normalized Difference Vegetation Index (minimum)', 'wd0114mn.tif', FALSE),
	('AVHRR Normalized Difference Vegetation Index (maximum)', 'wd0114mx.tif', FALSE);

INSERT INTO disease_group_covariate_file (disease_group_id, covariate_file_id) VALUES
	((SELECT id FROM disease_group WHERE name='Chikungunya'), (SELECT id FROM covariate_file WHERE file='access.tif')),
	((SELECT id FROM disease_group WHERE name='Cholera'), (SELECT id FROM covariate_file WHERE file='access.tif')),
	((SELECT id FROM disease_group WHERE name='Dengue'), (SELECT id FROM covariate_file WHERE file='access.tif')),
	((SELECT id FROM disease_group WHERE name='P. knowlesi'), (SELECT id FROM covariate_file WHERE file='access.tif')),
	((SELECT id FROM disease_group WHERE name='Poliomyelitis'), (SELECT id FROM covariate_file WHERE file='access.tif')),
	((SELECT id FROM disease_group WHERE name='P. vivax'), (SELECT id FROM covariate_file WHERE file='duffy_neg.tif')),
	((SELECT id FROM disease_group WHERE name='Chikungunya'), (SELECT id FROM covariate_file WHERE file='gecon.tif')),
	((SELECT id FROM disease_group WHERE name='Cholera'), (SELECT id FROM covariate_file WHERE file='gecon.tif')),
	((SELECT id FROM disease_group WHERE name='Dengue'), (SELECT id FROM covariate_file WHERE file='gecon.tif')),
	((SELECT id FROM disease_group WHERE name='P. knowlesi'), (SELECT id FROM covariate_file WHERE file='gecon.tif')),
	((SELECT id FROM disease_group WHERE name='Poliomyelitis'), (SELECT id FROM covariate_file WHERE file='gecon.tif')),
	((SELECT id FROM disease_group WHERE name='Cholera'), (SELECT id FROM covariate_file WHERE file='mod_dem.tif')),
	((SELECT id FROM disease_group WHERE name='Cholera'), (SELECT id FROM covariate_file WHERE file='prec57a0.tif')),
	((SELECT id FROM disease_group WHERE name='P. falciparum'), (SELECT id FROM covariate_file WHERE file='prec57a0.tif')),
	((SELECT id FROM disease_group WHERE name='P. falciparum'), (SELECT id FROM covariate_file WHERE file='prec57a1.tif')),
	((SELECT id FROM disease_group WHERE name='P. falciparum'), (SELECT id FROM covariate_file WHERE file='prec57a2.tif')),
	((SELECT id FROM disease_group WHERE name='Chikungunya'), (SELECT id FROM covariate_file WHERE file='prec57mn.tif')),
	((SELECT id FROM disease_group WHERE name='Dengue'), (SELECT id FROM covariate_file WHERE file='prec57mn.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniases'), (SELECT id FROM covariate_file WHERE file='prec57mn.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - cutaneous/mucosal, New World'), (SELECT id FROM covariate_file WHERE file='prec57mn.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - cutaneous/mucosal, Old World'), (SELECT id FROM covariate_file WHERE file='prec57mn.tif')),
	((SELECT id FROM disease_group WHERE name='P. knowlesi'), (SELECT id FROM covariate_file WHERE file='prec57mn.tif')),
	((SELECT id FROM disease_group WHERE name='Poliomyelitis'), (SELECT id FROM covariate_file WHERE file='prec57mn.tif')),
	((SELECT id FROM disease_group WHERE name='Chikungunya'), (SELECT id FROM covariate_file WHERE file='prec57mx.tif')),
	((SELECT id FROM disease_group WHERE name='Dengue'), (SELECT id FROM covariate_file WHERE file='prec57mx.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniases'), (SELECT id FROM covariate_file WHERE file='prec57mx.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - cutaneous/mucosal, New World'), (SELECT id FROM covariate_file WHERE file='prec57mx.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - visceral'), (SELECT id FROM covariate_file WHERE file='prec57mx.tif')),
	((SELECT id FROM disease_group WHERE name='P. falciparum'), (SELECT id FROM covariate_file WHERE file='prec57mx.tif')),
	((SELECT id FROM disease_group WHERE name='P. knowlesi'), (SELECT id FROM covariate_file WHERE file='prec57mx.tif')),
	((SELECT id FROM disease_group WHERE name='Poliomyelitis'), (SELECT id FROM covariate_file WHERE file='prec57mx.tif')),
	((SELECT id FROM disease_group WHERE name='P. falciparum'), (SELECT id FROM covariate_file WHERE file='prec57p1.tif')),
	((SELECT id FROM disease_group WHERE name='P. falciparum'), (SELECT id FROM covariate_file WHERE file='prec57p2.tif')),
	((SELECT id FROM disease_group WHERE name='P. falciparum'), (SELECT id FROM covariate_file WHERE file='tempaucpf.tif')),
	((SELECT id FROM disease_group WHERE name='P. knowlesi'), (SELECT id FROM covariate_file WHERE file='tempaucpf.tif')),
	((SELECT id FROM disease_group WHERE name='Malarias'), (SELECT id FROM covariate_file WHERE file='tempaucpv.tif')),
	((SELECT id FROM disease_group WHERE name='P. knowlesi'), (SELECT id FROM covariate_file WHERE file='tempaucpv.tif')),
	((SELECT id FROM disease_group WHERE name='P. vivax'), (SELECT id FROM covariate_file WHERE file='tempaucpv.tif')),
	((SELECT id FROM disease_group WHERE name='Chikungunya'), (SELECT id FROM covariate_file WHERE file='tempsuit.tif')),
	((SELECT id FROM disease_group WHERE name='Dengue'), (SELECT id FROM covariate_file WHERE file='tempsuit.tif')),
	((SELECT id FROM disease_group WHERE name='Chikungunya'), (SELECT id FROM covariate_file WHERE file='upr_p.tif')),
	((SELECT id FROM disease_group WHERE name='Cholera'), (SELECT id FROM covariate_file WHERE file='upr_p.tif')),
	((SELECT id FROM disease_group WHERE name='Dengue'), (SELECT id FROM covariate_file WHERE file='upr_p.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniases'), (SELECT id FROM covariate_file WHERE file='upr_p.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - cutaneous/mucosal, New World'), (SELECT id FROM covariate_file WHERE file='upr_p.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - cutaneous/mucosal, Old World'), (SELECT id FROM covariate_file WHERE file='upr_p.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - visceral'), (SELECT id FROM covariate_file WHERE file='upr_p.tif')),
	((SELECT id FROM disease_group WHERE name='Malarias'), (SELECT id FROM covariate_file WHERE file='upr_p.tif')),
	((SELECT id FROM disease_group WHERE name='P. falciparum'), (SELECT id FROM covariate_file WHERE file='upr_p.tif')),
	((SELECT id FROM disease_group WHERE name='P. knowlesi'), (SELECT id FROM covariate_file WHERE file='upr_p.tif')),
	((SELECT id FROM disease_group WHERE name='P. vivax'), (SELECT id FROM covariate_file WHERE file='upr_p.tif')),
	((SELECT id FROM disease_group WHERE name='Poliomyelitis'), (SELECT id FROM covariate_file WHERE file='upr_p.tif')),
	((SELECT id FROM disease_group WHERE name='Chikungunya'), (SELECT id FROM covariate_file WHERE file='upr_u.tif')),
	((SELECT id FROM disease_group WHERE name='Cholera'), (SELECT id FROM covariate_file WHERE file='upr_u.tif')),
	((SELECT id FROM disease_group WHERE name='Dengue'), (SELECT id FROM covariate_file WHERE file='upr_u.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniases'), (SELECT id FROM covariate_file WHERE file='upr_u.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - cutaneous/mucosal, Old World'), (SELECT id FROM covariate_file WHERE file='upr_u.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - visceral'), (SELECT id FROM covariate_file WHERE file='upr_u.tif')),
	((SELECT id FROM disease_group WHERE name='Malarias'), (SELECT id FROM covariate_file WHERE file='upr_u.tif')),
	((SELECT id FROM disease_group WHERE name='P. falciparum'), (SELECT id FROM covariate_file WHERE file='upr_u.tif')),
	((SELECT id FROM disease_group WHERE name='P. knowlesi'), (SELECT id FROM covariate_file WHERE file='upr_u.tif')),
	((SELECT id FROM disease_group WHERE name='P. vivax'), (SELECT id FROM covariate_file WHERE file='upr_u.tif')),
	((SELECT id FROM disease_group WHERE name='Poliomyelitis'), (SELECT id FROM covariate_file WHERE file='upr_u.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniases'), (SELECT id FROM covariate_file WHERE file='wd0107a0.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - visceral'), (SELECT id FROM covariate_file WHERE file='wd0107a0.tif')),
	((SELECT id FROM disease_group WHERE name='Poliomyelitis'), (SELECT id FROM covariate_file WHERE file='wd0107a0.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniases'), (SELECT id FROM covariate_file WHERE file='wd0107mn.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - cutaneous/mucosal, New World'), (SELECT id FROM covariate_file WHERE file='wd0107mn.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - cutaneous/mucosal, Old World'), (SELECT id FROM covariate_file WHERE file='wd0107mn.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - visceral'), (SELECT id FROM covariate_file WHERE file='wd0107mn.tif')),
	((SELECT id FROM disease_group WHERE name='P. knowlesi'), (SELECT id FROM covariate_file WHERE file='wd0107mn.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniases'), (SELECT id FROM covariate_file WHERE file='wd0107mx.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - cutaneous/mucosal, New World'), (SELECT id FROM covariate_file WHERE file='wd0107mx.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - visceral'), (SELECT id FROM covariate_file WHERE file='wd0107mx.tif')),
	((SELECT id FROM disease_group WHERE name='P. knowlesi'), (SELECT id FROM covariate_file WHERE file='wd0107mx.tif')),
	((SELECT id FROM disease_group WHERE name='Chikungunya'), (SELECT id FROM covariate_file WHERE file='wd0114a0.tif')),
	((SELECT id FROM disease_group WHERE name='Dengue'), (SELECT id FROM covariate_file WHERE file='wd0114a0.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniases'), (SELECT id FROM covariate_file WHERE file='wd0114a0.tif')),
	((SELECT id FROM disease_group WHERE name='Malarias'), (SELECT id FROM covariate_file WHERE file='wd0114a0.tif')),
	((SELECT id FROM disease_group WHERE name='P. falciparum'), (SELECT id FROM covariate_file WHERE file='wd0114a0.tif')),
	((SELECT id FROM disease_group WHERE name='P. knowlesi'), (SELECT id FROM covariate_file WHERE file='wd0114a0.tif')),
	((SELECT id FROM disease_group WHERE name='P. vivax'), (SELECT id FROM covariate_file WHERE file='wd0114a0.tif')),
	((SELECT id FROM disease_group WHERE name='Poliomyelitis'), (SELECT id FROM covariate_file WHERE file='wd0114a0.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - visceral'), (SELECT id FROM covariate_file WHERE file='wd0114mn.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniases'), (SELECT id FROM covariate_file WHERE file='wd0114mx.tif')),
	((SELECT id FROM disease_group WHERE name='Leishmaniasis - visceral'), (SELECT id FROM covariate_file WHERE file='wd0114mx.tif'));

-- Intergrate existing covariate influence tables
ALTER TABLE covariate_influence ADD COLUMN covariate_file_id integer
	CONSTRAINT fk_covariate_influence_covariate_file REFERENCES covariate_file (id);

UPDATE covariate_influence
	SET covariate_file_id = covariate_file.id 
	FROM covariate_file
	WHERE CONCAT('/var/lib/abraid/modelwrapper/covariates/', covariate_file.file)=covariate_influence.covariate_file_path;

ALTER TABLE covariate_influence ALTER COLUMN covariate_file_id SET NOT NULL;
ALTER TABLE covariate_influence DROP COLUMN covariate_file_path;
ALTER TABLE covariate_influence DROP COLUMN covariate_display_name;

ALTER TABLE effect_curve_covariate_influence ADD COLUMN covariate_file_id integer
	CONSTRAINT fk_effect_curve_covariate_influence_covariate_file REFERENCES covariate_file (id);

UPDATE effect_curve_covariate_influence
	SET covariate_file_id = covariate_file.id 
	FROM covariate_file
	WHERE CONCAT('/var/lib/abraid/modelwrapper/covariates/', covariate_file.file)=effect_curve_covariate_influence.covariate_file_path;

ALTER TABLE effect_curve_covariate_influence ALTER COLUMN covariate_file_id SET NOT NULL;
ALTER TABLE effect_curve_covariate_influence DROP COLUMN covariate_file_path;
ALTER TABLE effect_curve_covariate_influence DROP COLUMN covariate_display_name;