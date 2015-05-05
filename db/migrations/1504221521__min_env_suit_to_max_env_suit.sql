-- Fix name of min_env_suitability to max_env_suitability 
--
-- Copyright (c) 2015 University of Oxford
ALTER TABLE disease_group RENAME COLUMN min_env_suitability TO max_env_suitability;