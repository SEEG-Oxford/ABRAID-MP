-- Allow healthmap_subdisease without parent healthmap_disease.
--
-- Copyright (c) 2015 University of Oxford
ALTER TABLE healthmap_subdisease ALTER COLUMN healthmap_disease_id DROP NOT NULL;