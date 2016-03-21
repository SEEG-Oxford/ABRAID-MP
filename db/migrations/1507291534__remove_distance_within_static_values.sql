-- Remove redundant fake "distance within" column from extent classes
-- Copyright (c) 2015 University of Oxford
ALTER TABLE disease_extent_class DROP COLUMN distance_if_within_extent;