-- Replace 'Uploaded' feed on 'Uploaded' provenance with new feeds on 'Manual gold standard dataset' provenance.
--
-- Copyright (c) 2014 University of Oxford

/* Create the two new provenances: MANUAL and MANUAL_GOLD_STANDARD */
INSERT INTO provenance (name, default_feed_weighting) VALUES ('Manual dataset', 1);
INSERT INTO provenance (name, default_feed_weighting) VALUES ('Manual gold standard dataset', 1);

/* Create the five new feeds from distinct alert titles, under the gold standard provenance */
INSERT INTO feed (name, provenance_id, weighting)
SELECT distinct alert.title, (SELECT id FROM provenance WHERE provenance.name = 'Manual gold standard dataset'), 1
FROM alert
JOIN feed ON alert.feed_id = feed.id
WHERE feed.name = 'Uploaded';

/* Move alerts from 'Uploaded' feed to their new feed created above */
UPDATE alert
SET feed_id = (SELECT id FROM feed WHERE feed.name = alert.title)
WHERE feed_id = (SELECT id FROM feed WHERE feed.name = 'Uploaded');

/* Clear alert titles */
UPDATE alert
SET title = ''
WHERE id IN (SELECT alert.id FROM feed JOIN alert ON alert.feed_id = feed.id WHERE feed.name = alert.title);

/* Remove old 'Uploaded' feed and provenance */
DELETE FROM feed
WHERE name = 'Uploaded';

DELETE FROM provenance
WHERE name = 'Uploaded';
