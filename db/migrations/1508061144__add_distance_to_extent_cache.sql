-- Add a table to cached distance to disease extent values
--
-- Copyright (c) 2015 University of Oxford
CREATE TABLE disease_to_extent_cache (
    disease_group_id integer NOT NULL 
        CONSTRAINT fk_disease_to_extent_cache_disease_group REFERENCES disease_group (id),
    location_id integer NOT NULL
        CONSTRAINT fk_disease_to_extent_cache_location REFERENCES location (id),
    distance double precision NOT NULL,
    CONSTRAINT pk_disease_to_extent_cache PRIMARY KEY (disease_group_id, location_id)
);

GRANT SELECT, INSERT, DELETE ON disease_to_extent_cache TO ${application_username};
