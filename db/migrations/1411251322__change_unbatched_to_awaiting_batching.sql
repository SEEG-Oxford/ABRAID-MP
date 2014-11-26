-- In the disease_occurrence.status column, replace UNBATCHED with the more descriptive AWAITING_BATCHING
--
-- Copyright (c) 2014 University of Oxford

ALTER TABLE disease_occurrence DROP CONSTRAINT ck_disease_occurrence_status;

UPDATE disease_occurrence SET status = 'AWAITING_BATCHING' WHERE status = 'UNBATCHED';

ALTER TABLE disease_occurrence ADD CONSTRAINT ck_disease_occurrence_status
    CHECK (status IN ('AWAITING_BATCHING', 'DISCARDED_FAILED_QC', 'DISCARDED_UNREVIEWED', 'DISCARDED_UNUSED', 'IN_REVIEW', 'READY'));
