-- Script: indexes.sql
--
-- Description: Creates indexes for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

CREATE INDEX ix_alert_feed_id ON alert (feed_id);
CREATE INDEX ix_disease_group_parent_id ON disease_group (parent_id);
CREATE INDEX ix_disease_occurrence_alert_id ON disease_occurrence (alert_id);
CREATE INDEX ix_disease_occurrence_disease_group_id ON disease_occurrence (disease_group_id);
CREATE INDEX ix_disease_occurrence_location_id ON disease_occurrence (location_id);
CREATE INDEX ix_disease_occurrence_review_disease_occurrence_id ON disease_occurrence_review (disease_occurrence_id);
CREATE INDEX ix_disease_occurrence_review_expert_id ON disease_occurrence_review (expert_id);
CREATE INDEX ix_expert_disease_group_disease_group_id ON expert_disease_group (disease_group_id);
CREATE INDEX ix_feed_provenance_id ON feed (provenance_id);
CREATE INDEX ix_healthmap_disease_disease_group_id ON healthmap_disease (disease_group_id);
