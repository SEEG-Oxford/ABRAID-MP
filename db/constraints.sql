-- Script: constraints.sql
--
-- Description: Creates constraints for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford


-- Unique constraints
ALTER TABLE DiseaseGroup
    ADD CONSTRAINT UQ_DiseaseGroup_Name_GroupType UNIQUE (Name, GroupType);

ALTER TABLE Expert
    ADD CONSTRAINT UQ_Expert_Email UNIQUE (Email);

ALTER TABLE Feed
    ADD CONSTRAINT UQ_Feed_Name UNIQUE (Name);

ALTER TABLE HealthMapDisease
    ADD CONSTRAINT UQ_HealthMapDisease_Name UNIQUE (Name);

ALTER TABLE Provenance
    ADD CONSTRAINT UQ_Provenance_Name UNIQUE (Name);
    
   
-- Primary keys
ALTER TABLE Alert ADD CONSTRAINT PK_Alert
    PRIMARY KEY (Id);

ALTER TABLE Country ADD CONSTRAINT PK_Country
    PRIMARY KEY (Id);

ALTER TABLE DiseaseGroup ADD CONSTRAINT PK_Disease
    PRIMARY KEY (Id);

ALTER TABLE DiseaseOccurrence ADD CONSTRAINT PK_DiseaseOutbreak
    PRIMARY KEY (Id);

ALTER TABLE Expert ADD CONSTRAINT PK_Expert
    PRIMARY KEY (Id);

ALTER TABLE ExpertDiseaseGroup ADD CONSTRAINT PK_ExpertDiseaseGroup
    PRIMARY KEY (ExpertId, DiseaseGroupId);

ALTER TABLE Feed ADD CONSTRAINT PK_Feed
    PRIMARY KEY (Id);
	
ALTER TABLE HealthMapCountry ADD CONSTRAINT PK_HealthMapCountry
    PRIMARY KEY (Id);

ALTER TABLE HealthMapDisease ADD CONSTRAINT PK_HealthMapDisease
    PRIMARY KEY (Id);

ALTER TABLE Location ADD CONSTRAINT PK_Location
    PRIMARY KEY (Id);

ALTER TABLE Provenance ADD CONSTRAINT PK_Provenance
    PRIMARY KEY (Id);


-- Foreign keys
ALTER TABLE Alert ADD CONSTRAINT FK_Alert_Feed
    FOREIGN KEY (FeedId) REFERENCES Feed (Id);

ALTER TABLE DiseaseGroup ADD CONSTRAINT FK_DiseaseGroup_DiseaseGroup
    FOREIGN KEY (ParentId) REFERENCES DiseaseGroup (Id);

ALTER TABLE DiseaseOccurrence ADD CONSTRAINT FK_DiseaseOccurrence_Alert
    FOREIGN KEY (AlertId) REFERENCES Alert (Id);

ALTER TABLE DiseaseOccurrence ADD CONSTRAINT FK_DiseaseOccurrence_Disease
    FOREIGN KEY (DiseaseGroupId) REFERENCES DiseaseGroup (Id);

ALTER TABLE DiseaseOccurrence ADD CONSTRAINT FK_DiseaseOccurrence_Location
    FOREIGN KEY (LocationId) REFERENCES Location (Id);

ALTER TABLE ExpertDiseaseGroup ADD CONSTRAINT FK_ExpertDiseaseGroup_DiseaseGroup
    FOREIGN KEY (DiseaseGroupId) REFERENCES DiseaseGroup (Id);

ALTER TABLE ExpertDiseaseGroup ADD CONSTRAINT FK_ExpertDiseaseGroup_Expert
    FOREIGN KEY (ExpertId) REFERENCES Expert (Id);

ALTER TABLE Feed ADD CONSTRAINT FK_Feed_Provenance
    FOREIGN KEY (ProvenanceId) REFERENCES Provenance (Id);

ALTER TABLE HealthMapCountry ADD CONSTRAINT FK_HealthMapCountry_Country
    FOREIGN KEY (CountryId) REFERENCES Country (Id);

ALTER TABLE HealthMapDisease ADD CONSTRAINT FK_HealthMapDisease_DiseaseGroup
    FOREIGN KEY (DiseaseGroupId) REFERENCES DiseaseGroup (Id);

ALTER TABLE Location ADD CONSTRAINT FK_Location_Country
    FOREIGN KEY (CountryId) REFERENCES Country (Id);

    
-- Check constraints
ALTER TABLE DiseaseGroup ADD CONSTRAINT CK_DiseaseGroup_GroupType
    CHECK (GroupType IN ('CLUSTER', 'MICROCLUSTER', 'DISEASE'));

ALTER TABLE Location ADD CONSTRAINT CK_Location_Precision
    CHECK (Precision IN ('COUNTRY', 'ADMIN1', 'ADMIN2', 'PRECISE'));
