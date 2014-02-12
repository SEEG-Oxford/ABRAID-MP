-- Script: constraints.sql
--
-- Description: Creates constraints for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford


-- Unique constraints
ALTER TABLE ProvenanceWeight
    ADD CONSTRAINT UQ_ProvenanceWeight_Name UNIQUE (Name);

ALTER TABLE Country
    ADD CONSTRAINT UQ_Country_Name UNIQUE (Name);

ALTER TABLE Disease
    ADD CONSTRAINT UQ_Disease_Name UNIQUE (Name);

    
-- Primary keys 
ALTER TABLE ProvenanceWeight ADD CONSTRAINT PK_ProvenanceWeight 
    PRIMARY KEY (Id);

ALTER TABLE Provenance ADD CONSTRAINT PK_Provenance 
    PRIMARY KEY (Id);

ALTER TABLE Country ADD CONSTRAINT PK_Country 
    PRIMARY KEY (Id);

ALTER TABLE Disease ADD CONSTRAINT PK_Disease 
    PRIMARY KEY (Id);

ALTER TABLE DiseaseOutbreak ADD CONSTRAINT PK_DiseaseOutbreak 
    PRIMARY KEY (Id);

ALTER TABLE Location ADD CONSTRAINT PK_Location 
    PRIMARY KEY (Id);
	
ALTER TABLE Expert ADD CONSTRAINT PK_Expert
	PRIMARY KEY (Id);

ALTER TABLE ExpertDisease ADD CONSTRAINT PK_ExpertDisease 
	PRIMARY KEY (ExpertId, DiseaseId);

    
-- Foreign keys
ALTER TABLE Provenance ADD CONSTRAINT FK_Provenance_ProvenanceWeight
    FOREIGN KEY (ProvenanceWeightId) REFERENCES ProvenanceWeight (Id);

ALTER TABLE DiseaseOutbreak ADD CONSTRAINT FK_DiseaseOutbreak_Disease 
    FOREIGN KEY (DiseaseId) REFERENCES Disease (Id);

ALTER TABLE DiseaseOutbreak ADD CONSTRAINT FK_DiseaseOutbreak_Location 
    FOREIGN KEY (LocationId) REFERENCES Location (Id);

ALTER TABLE DiseaseOutbreak ADD CONSTRAINT FK_DiseaseOutbreak_Provenance 
    FOREIGN KEY (ProvenanceId) REFERENCES Provenance (Id);

ALTER TABLE Location ADD CONSTRAINT FK_Location_Country 
    FOREIGN KEY (Country) REFERENCES Country (Id);
	
ALTER TABLE ExpertDisease ADD CONSTRAINT FK_ExpertDisease_Disease 
	FOREIGN KEY (DiseaseId) REFERENCES Disease (Id);

ALTER TABLE ExpertDisease ADD CONSTRAINT FK_ExpertDisease_Expert 
	FOREIGN KEY (ExpertId) REFERENCES Expert (Id);

