-- Script: indexes.sql
-- 
-- Description: Creates indexes for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford

CREATE INDEX IX_Provenance_ProvenanceWeightId ON Provenance (ProvenanceWeightId);
CREATE INDEX IX_DiseaseOutbreak_ProvenanceWeightId ON DiseaseOutbreak (DiseaseId);
CREATE INDEX IX_DiseaseOutbreak_LocationId ON DiseaseOutbreak (LocationId);
CREATE INDEX IX_DiseaseOutbreak_ProvenanceId ON DiseaseOutbreak (ProvenanceId);
CREATE INDEX IX_Location_Country ON Location (Country);
CREATE INDEX IX_UserDisease_UserId_DiseaseId ON UserDisease (UserId, DiseaseId);
