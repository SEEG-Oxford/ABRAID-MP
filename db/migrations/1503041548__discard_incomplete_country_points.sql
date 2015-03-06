-- Discard country points that dont have all of the weightings that are needed going forward.
-- Copyright (c) 2014 University of Oxford
UPDATE disease_occurrence 
SET 
	status='DISCARDED_UNUSED',
	final_weighting=NULL,
	final_weighting_excl_spatial=NULL
WHERE 
	status='READY' AND
	location_id IN (SELECT id FROM location WHERE precision='COUNTRY') AND 
	disease_group_id IN (SELECT id FROM disease_group WHERE automatic_model_runs_start_date IS NOT NULL) AND 
	alert_id NOT IN (SELECT alert.id FROM alert JOIN feed ON feed.id=feed_id JOIN provenance ON provenance.id=provenance_id WHERE provenance.name='Manual gold standard dataset');