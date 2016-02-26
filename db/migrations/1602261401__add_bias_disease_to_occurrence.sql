-- Add bias disease field to disease group
--
-- Copyright (c) 2016 University of Oxford
ALTER TABLE disease_occurrence ADD COLUMN bias_disease_group_id integer 
        CONSTRAINT fk_disease_occurrence_bias_disease_group_id REFERENCES disease_group (id);

ALTER TABLE disease_occurrence DROP  CONSTRAINT ck_disease_occurrence_status;
ALTER TABLE disease_occurrence ADD CONSTRAINT ck_disease_occurrence_status
    CHECK (status IN ('AWAITING_BATCHING', 'DISCARDED_FAILED_QC', 'DISCARDED_UNREVIEWED', 'DISCARDED_UNUSED', 'IN_REVIEW', 'READY', 'BIAS'));

ALTER TABLE disease_occurrence ADD CONSTRAINT ck_disease_occurrence_bias_disease_group_id
    CHECK ((status<>'BIAS' AND bias_disease_group_id IS NULL) OR (status IN ('BIAS', 'DISCARDED_FAILED_QC') AND bias_disease_group_id IS NOT NULL));

GRANT DELETE ON disease_occurrence TO ${application_username};