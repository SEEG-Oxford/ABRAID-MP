-- Script: indexes.sql
--
-- Description: Creates indexes for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

CREATE INDEX ix_admin_unit_disease_extent_class_global_gaul_code ON admin_unit_disease_extent_class (global_gaul_code);
CREATE INDEX ix_admin_unit_disease_extent_class_tropical_gaul_code ON admin_unit_disease_extent_class (tropical_gaul_code);
CREATE INDEX ix_admin_unit_disease_extent_class_disease_extent_class ON admin_unit_disease_extent_class (disease_extent_class);
CREATE INDEX ix_admin_unit_disease_extent_class_disease_group_id ON admin_unit_disease_extent_class (disease_group_id);
CREATE INDEX ix_admin_unit_review_global_gaul_code ON admin_unit_review (global_gaul_code);
CREATE INDEX ix_admin_unit_review_tropical_gaul_code ON admin_unit_review (tropical_gaul_code);
CREATE INDEX ix_admin_unit_review_disease_group_id ON admin_unit_review (disease_group_id);
CREATE INDEX ix_admin_unit_review_expert_id ON admin_unit_review (expert_id);
CREATE INDEX ix_admin_unit_review_response ON admin_unit_review (response);
CREATE INDEX ix_alert_feed_id ON alert (feed_id);
CREATE INDEX ix_disease_group_parent_id ON disease_group (parent_id);
CREATE INDEX ix_disease_group_validator_disease_group_id ON disease_group (validator_disease_group_id);
CREATE INDEX ix_disease_occurrence_alert_id ON disease_occurrence (alert_id);
CREATE INDEX ix_disease_occurrence_disease_group_id ON disease_occurrence (disease_group_id);
CREATE INDEX ix_disease_occurrence_location_id ON disease_occurrence (location_id);
CREATE INDEX ix_disease_occurrence_review_created_date ON disease_occurrence_review (created_date);
CREATE INDEX ix_disease_occurrence_review_disease_occurrence_id ON disease_occurrence_review (disease_occurrence_id);
CREATE INDEX ix_disease_occurrence_review_expert_id ON disease_occurrence_review (expert_id);
CREATE INDEX ix_expert_validator_disease_group_validator_disease_group_id ON expert_validator_disease_group (validator_disease_group_id);
CREATE INDEX ix_feed_provenance_id ON feed (provenance_id);
CREATE INDEX ix_healthmap_country_country_gaul_code ON healthmap_country_country (gaul_code);
CREATE INDEX ix_healthmap_disease_disease_group_id ON healthmap_disease (disease_group_id);
CREATE INDEX ix_location_admin_unit_global_gaul_code ON location (admin_unit_global_gaul_code);
CREATE INDEX ix_location_admin_unit_qc_gaul_code ON location (admin_unit_qc_gaul_code);
CREATE INDEX ix_location_admin_unit_tropical_gaul_code ON location (admin_unit_tropical_gaul_code);
CREATE INDEX ix_location_healthmap_country_id ON location (healthmap_country_id);
CREATE INDEX ix_model_run_disease_group_id ON model_run (disease_group_id);
