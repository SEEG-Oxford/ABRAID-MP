-- Update model modes to reflect updated seegSDM modes.
-- Copyright (c) 2016 University of Oxford
UPDATE disease_group SET model_mode='Bhatt2013' WHERE model_mode='bhatt';
UPDATE disease_group SET model_mode='Shearer2016' WHERE model_mode='all_bias';