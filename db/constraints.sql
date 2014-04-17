-- Script: constraints.sql
--
-- Description: Creates constraints for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford


-- Unique constraints
ALTER TABLE alert
    ADD CONSTRAINT uq_alert_healthmap_alert_id UNIQUE (healthmap_alert_id);

ALTER TABLE disease_occurrence_review
    ADD CONSTRAINT uq_disease_occurrence_review_expert_id_disease_occurrence_id UNIQUE (expert_id, disease_occurrence_id);

ALTER TABLE expert
    ADD CONSTRAINT uq_expert_email UNIQUE (email);

ALTER TABLE feed
    ADD CONSTRAINT uq_feed_name UNIQUE (name);

ALTER TABLE healthmap_disease
    ADD CONSTRAINT uq_healthmap_disease_name UNIQUE (name);

ALTER TABLE location
    ADD CONSTRAINT uq_location_geoname_id UNIQUE (geoname_id);

ALTER TABLE provenance
    ADD CONSTRAINT uq_provenance_name UNIQUE (name);


-- Primary keys
ALTER TABLE admin_unit ADD CONSTRAINT pk_admin_unit
    PRIMARY KEY (gaul_code);

ALTER TABLE admin_unit_global ADD CONSTRAINT pk_admin_unit_tailored_simplified_tropical
    PRIMARY KEY (gaul_code);

ALTER TABLE admin_unit_simplified_global ADD CONSTRAINT pk_admin_unit_simplified_global
    PRIMARY KEY (gaul_code);

ALTER TABLE admin_unit_simplified_tropical ADD CONSTRAINT pk_admin_unit_simplified_tropical
    PRIMARY KEY (gaul_code);

ALTER TABLE admin_unit_tropical ADD CONSTRAINT pk_admin_unit_tailored_tropical
    PRIMARY KEY (gaul_code);

ALTER TABLE alert ADD CONSTRAINT pk_alert
    PRIMARY KEY (id);

ALTER TABLE country ADD CONSTRAINT pk_country
    PRIMARY KEY (gaul_code);

ALTER TABLE disease_group ADD CONSTRAINT pk_disease
    PRIMARY KEY (id);

ALTER TABLE disease_occurrence ADD CONSTRAINT pk_disease_occurrence
    PRIMARY KEY (id);

ALTER TABLE disease_occurrence_review ADD CONSTRAINT pk_expert_review
    PRIMARY KEY (id);

ALTER TABLE expert ADD CONSTRAINT pk_expert
    PRIMARY KEY (id);

ALTER TABLE expert_validator_disease_group ADD CONSTRAINT pk_expert_validator_disease_group
    PRIMARY KEY (expert_id, validator_disease_group_id);

ALTER TABLE feed ADD CONSTRAINT pk_feed
    PRIMARY KEY (id);

ALTER TABLE geoname ADD CONSTRAINT pk_geoname
    PRIMARY KEY (id);

ALTER TABLE geonames_location_precision ADD CONSTRAINT pk_geonames_location_precision
    PRIMARY KEY (geonames_feature_code);

ALTER TABLE healthmap_country ADD CONSTRAINT pk_healthmap_country
    PRIMARY KEY (id);

ALTER TABLE healthmap_country_country ADD CONSTRAINT pk_healthmap_country_country
    PRIMARY KEY (healthmap_country_id, gaul_code);

ALTER TABLE healthmap_disease ADD CONSTRAINT pk_healthmap_disease
    PRIMARY KEY (id);

ALTER TABLE land_sea_border ADD CONSTRAINT pk_land_sea_border
    PRIMARY KEY (id);

ALTER TABLE location ADD CONSTRAINT pk_location
    PRIMARY KEY (id);

ALTER TABLE provenance ADD CONSTRAINT pk_provenance
    PRIMARY KEY (id);

ALTER TABLE validator_disease_group ADD CONSTRAINT pk_validator_disease_group
    PRIMARY KEY (id);


-- Foreign keys
ALTER TABLE alert ADD CONSTRAINT fk_alert_feed
    FOREIGN KEY (feed_id) REFERENCES feed (id);

ALTER TABLE disease_group ADD CONSTRAINT fk_disease_group_disease_group
    FOREIGN KEY (parent_id) REFERENCES disease_group (id);

ALTER TABLE disease_group ADD CONSTRAINT fk_disease_group_validator_disease_group
    FOREIGN KEY (validator_disease_group_id) REFERENCES validator_disease_group (id);

ALTER TABLE disease_occurrence ADD CONSTRAINT fk_disease_occurrence_alert
    FOREIGN KEY (alert_id) REFERENCES alert (id);

ALTER TABLE disease_occurrence ADD CONSTRAINT fk_disease_occurrence_disease
    FOREIGN KEY (disease_group_id) REFERENCES disease_group (id);

ALTER TABLE disease_occurrence ADD CONSTRAINT fk_disease_occurrence_location
    FOREIGN KEY (location_id) REFERENCES location (id);

ALTER TABLE disease_occurrence_review ADD CONSTRAINT fk_disease_occurence_review_expert
    FOREIGN KEY (expert_id) REFERENCES expert (id);

ALTER TABLE disease_occurrence_review ADD CONSTRAINT fk_disease_occurrence_review_disease_occurrence
    FOREIGN KEY (disease_occurrence_id) REFERENCES disease_occurrence (id);

ALTER TABLE expert_validator_disease_group ADD CONSTRAINT fk_expert_validator_disease_group_validator_disease_group
    FOREIGN KEY (validator_disease_group_id) REFERENCES validator_disease_group (id);

ALTER TABLE expert_validator_disease_group ADD CONSTRAINT fk_expert_validator_disease_group_expert
    FOREIGN KEY (expert_id) REFERENCES expert (id);

ALTER TABLE feed ADD CONSTRAINT fk_feed_provenance
    FOREIGN KEY (provenance_id) REFERENCES provenance (id);

ALTER TABLE healthmap_country_country ADD CONSTRAINT fk_healthmap_country_country_country
    FOREIGN KEY (gaul_code) REFERENCES country (gaul_code);

ALTER TABLE healthmap_country_country ADD CONSTRAINT fk_healthmap_country_country_healthmap_country
    FOREIGN KEY (healthmap_country_id) REFERENCES healthmap_country (id);

ALTER TABLE healthmap_disease ADD CONSTRAINT fk_healthmap_disease_disease_group
    FOREIGN KEY (disease_group_id) REFERENCES disease_group (id);

ALTER TABLE location ADD CONSTRAINT fk_location_admin_unit
    FOREIGN KEY (admin_unit_gaul_code) REFERENCES admin_unit (gaul_code);

ALTER TABLE location ADD CONSTRAINT fk_location_healthmap_country
    FOREIGN KEY (healthmap_country_id) REFERENCES healthmap_country (id);


-- Check constraints
ALTER TABLE disease_group ADD CONSTRAINT ck_disease_group_group_type
    CHECK (group_type IN ('CLUSTER', 'MICROCLUSTER', 'SINGLE'));

ALTER TABLE disease_occurrence_review ADD CONSTRAINT ck_disease_occurrence_review_response
    CHECK (response IN ('YES', 'NO', 'UNSURE'));

ALTER TABLE location ADD CONSTRAINT ck_location_precision
    CHECK (precision IN ('COUNTRY', 'ADMIN1', 'ADMIN2', 'PRECISE'));
