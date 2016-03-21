-- Remove min_occurrences_for_presence & min_occurrences_for_possible_presence from disease_extent.
--
-- Copyright (c) 2015 University of Oxford
ALTER TABLE disease_extent DROP COLUMN min_occurrences_for_presence;
ALTER TABLE disease_extent DROP COLUMN min_occurrences_for_possible_presence;