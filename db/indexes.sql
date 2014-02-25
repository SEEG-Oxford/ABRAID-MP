-- Script: indexes.sql
-- 
-- Description: Creates indexes for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

CREATE INDEX IX_Alert_FeedId ON Alert (FeedId);
CREATE INDEX IX_DiseaseGroup_ParentId ON DiseaseGroup (ParentId);
CREATE INDEX IX_DiseaseOccurrence_AlertId ON DiseaseOccurrence (AlertId);
CREATE INDEX IX_DiseaseOccurrence_DiseaseGroupId ON DiseaseOccurrence (DiseaseGroupId);
CREATE INDEX IX_DiseaseOccurrence_LocationId ON DiseaseOccurrence (LocationId);
CREATE INDEX IX_ExpertDiseaseGroup_DiseaseGroupId ON ExpertDiseaseGroup (DiseaseGroupId);
CREATE INDEX IX_Feed_ProvenanceId ON Feed (ProvenanceId);
CREATE INDEX IX_HealthMapCountry_CountryId ON HealthMapCountry (CountryId);
CREATE INDEX IX_HealthMapDisease_DiseaseGroupId ON HealthMapDisease (DiseaseGroupId);
CREATE INDEX IX_Location_CountryId ON Location (CountryId);
