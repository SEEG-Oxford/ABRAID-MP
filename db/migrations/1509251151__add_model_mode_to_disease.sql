-- Add model model field to disease group
--
-- Copyright (c) 2014 University of Oxford
ALTER TABLE disease_group ADD COLUMN model_mode varchar(50);
UPDATE disease_group SET model_mode='bhatt';