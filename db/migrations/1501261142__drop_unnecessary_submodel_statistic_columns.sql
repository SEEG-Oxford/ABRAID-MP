-- Drop unnecessary submodel statistic columns.
--
-- Copyright (c) 2015 University of Oxford

ALTER TABLE submodel_statistic DROP COLUMN deviance;
ALTER TABLE submodel_statistic DROP COLUMN root_mean_square_error;
ALTER TABLE submodel_statistic DROP COLUMN threshold;