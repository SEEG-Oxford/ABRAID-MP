-- Replace 'Uploaded' feed on 'Uploaded' provenance with new feeds on 'Manual gold standard dataset' provenance.
--
-- Copyright (c) 2014 University of Oxford

/* Create the two new provenances: MANUAL and MANUAL_GOLD_STANDARD */
INSERT INTO provenance (name, default_feed_weighting) VALUES ('Manual dataset', 1);
INSERT INTO provenance (name, default_feed_weighting) VALUES ('Manual gold standard dataset', 1);

/* Create the five new feeds from distinct alert titles, under the gold standard provenance */
INSERT INTO feed (name, provenance_id, weighting)
SELECT DISTINCT a.title, (SELECT id FROM provenance p WHERE p.name = 'Manual gold standard dataset'), 1
FROM alert a
JOIN feed f ON a.feed_id = f.id
WHERE f.name = 'Uploaded';

/* Move alerts from 'Uploaded' feed to their new feed created above */
UPDATE alert
SET feed_id = (SELECT id FROM feed WHERE feed.name = alert.title)
WHERE feed_id = (SELECT id FROM feed WHERE feed.name = 'Uploaded');

/* Clear alert titles */
UPDATE alert
SET title = NULL
WHERE id IN (SELECT a.id FROM feed f JOIN alert a ON a.feed_id = f.id WHERE f.name = a.title);

/* Remove old 'Uploaded' feed and provenance */
DELETE FROM feed
WHERE name = 'Uploaded';

DELETE FROM provenance
WHERE name = 'Uploaded';
