-- Script: tables.sql
--
-- Description: Creates tables and constraints for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford


-- Represents the weight of a provenance (e.g. Low, High)
CREATE TABLE ProvenanceWeight ( 
    Id serial NOT NULL,
    Name varchar(30) NOT NULL
);

-- Represents the source of the information concerning a disease outbreak
CREATE TABLE Provenance ( 
    Id serial NOT NULL,
    Name varchar(100) NOT NULL,
    ProvenanceWeightId integer,
    HealthMapFeedId integer
);

-- Represents a country
CREATE TABLE Country ( 
    Id varchar(3) NOT NULL,
    Name varchar(1000) NOT NULL
);

-- Represents a disease
CREATE TABLE Disease ( 
    Id serial NOT NULL,
    Name varchar(50) NOT NULL,
    HealthMapDiseaseId integer
);

-- Represents a disease outbreak, e.g. an alert from HealthMap
CREATE TABLE DiseaseOutbreak ( 
    Id serial NOT NULL,
    DiseaseId integer NOT NULL,
    LocationId integer NOT NULL,
    ProvenanceId integer NOT NULL,
    Title varchar(4000),
    PublicationDate date,
    OutbreakStartDate date
);

-- Represents a location
CREATE TABLE Location ( 
    Id serial NOT NULL,
    Point geometry,
    PlaceName varchar(1000),
    Admin1 varchar(50),
    Country varchar(3)
);
