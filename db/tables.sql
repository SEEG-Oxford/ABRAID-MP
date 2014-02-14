-- Script: tables.sql
--
-- Description: Creates tables for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford


-- List of tables:
--
-- Alert: Represents a report of a disease occurrence or occurrences, from a feed.
-- Country: Represents a country.
-- DiseaseGroup: Represents a group of diseases. This can be a disease cluster, disease microcluster, or a disease itself.
-- DiseaseOccurrence: Represents an occurrence of a disease group, in a location, as reported by an alert.
-- Expert: Represents a user of the PublicSite.
-- ExpertDiseaseGroup: The disease interests of each expert. These should be displayed to a user for review in the Data Validator.
-- Feed: Represents a source of alerts.
-- HealthMapCountry: The list of countries from HealthMap.
-- HealthMapDisease: The list of diseases from HealthMap.
-- Location: Represents the location of a disease occurrence.
-- Provenance: The source of a group of feeds.


CREATE TABLE Alert (
    Id serial NOT NULL,
    FeedId integer NOT NULL,
    Title text,
    PublicationDate timestamp,
    Url varchar(2000),
    Summary text,
    HealthMapAlertId bigint,
    CreatedDate timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE Country (
    Id integer NOT NULL,
    Name varchar(100) NOT NULL
);

CREATE TABLE DiseaseGroup (
    Id serial NOT NULL,
    ParentId integer,
    Name varchar(100) NOT NULL,
    GroupType varchar(15) NOT NULL,
    CreatedDate double precision NOT NULL
);

CREATE TABLE DiseaseOccurrence (
    Id serial NOT NULL,
    DiseaseGroupId integer NOT NULL,
    LocationId integer NOT NULL,
    AlertId integer NOT NULL,
    CreatedDate timestamp NOT NULL DEFAULT LOCALTIMESTAMP,
    DiagnosticWeight double precision
);

CREATE TABLE Expert (
    Id serial NOT NULL,
    Name varchar(1000) NOT NULL,
    Email varchar(320) NOT NULL,
    HashedPassword varchar(60) NOT NULL,
    IsAdministrator boolean NOT NULL,
    Score double precision,
    IsPubliclyVisible boolean,
    CreatedDate timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE ExpertDiseaseGroup (
    ExpertId integer NOT NULL,
    DiseaseGroupId integer NOT NULL
);

CREATE TABLE Feed (
    Id serial NOT NULL,
    ProvenanceId integer NOT NULL,
    Name varchar(100) NOT NULL,
    Weight double precision NOT NULL,
    HealthMapFeedId bigint,
    CreatedDate timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE HealthMapCountry (
    Id bigint NOT NULL,
    Name varchar(100) NOT NULL,
    CountryId integer
);

CREATE TABLE HealthMapDisease (
    Id bigint NOT NULL,
    Name varchar(100) NOT NULL,
    IsOfInterest boolean NOT NULL,
    DiseaseGroupId integer,
    CreatedDate timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE Location (
    Id serial NOT NULL,
    Name varchar(1000) NOT NULL,
    Geom geometry NOT NULL,
    Precision varchar(10) NOT NULL,
    CountryId integer NOT NULL,
    Admin1 varchar(50),
    Admin2 varchar(50),
    GeoNamesId integer,
    CreatedDate timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE Provenance (
    Id serial NOT NULL,
    Name varchar(100) NOT NULL,
    DefaultFeedWeight double precision,
    CreatedDate timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);
