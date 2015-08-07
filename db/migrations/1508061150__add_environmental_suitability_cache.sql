-- Add a table to cache environmental suitability values
--
-- Copyright (c) 2015 University of Oxford
CREATE TABLE environmental_suitability_cache (
    disease_group_id integer NOT NULL 
        CONSTRAINT fk_environmental_suitability_cache_disease_group REFERENCES disease_group (id),
    location_id integer NOT NULL
        CONSTRAINT fk_environmental_suitability_cache_location REFERENCES location (id),
    environmental_suitability double precision NOT NULL,
    CONSTRAINT pk_environmental_suitability_cache PRIMARY KEY (disease_group_id, location_id)
);

GRANT SELECT, INSERT, DELETE ON environmental_suitability_cache TO ${application_username};
