-- Relax unique constraint on feed.name to be per provenance.
--
-- Copyright (c) 2014 University of Oxford

ALTER TABLE feed
    DROP CONSTRAINT uq_feed_name;

ALTER TABLE feed
    ADD CONSTRAINT uq_provenance_id_feed_name UNIQUE (provenance_id, name);

DROP INDEX ix_feed_provenance_id;
