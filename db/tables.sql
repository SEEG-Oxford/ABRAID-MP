-- Script: tables.sql
--
-- Description: Creates tables for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford


-- List of tables:
--
-- Alert: Represents a report of a disease occurrence or occurrences, from a feed.
-- Country: Represents a country as defined by SEEG.
-- DiseaseGroup: Represents a group of diseases as defined by SEEG. This can be a disease cluster, disease microcluster, or a disease itself.
-- DiseaseOccurrence: Represents an occurrence of a disease group, in a location, as reported by an alert.
-- Expert: Represents a user of the PublicSite.
-- ExpertDiseaseGroup: Represents an expert's disease interest. These should be displayed to a user for review in the Data Validator.
-- ExpertReview: Represents an expert's response on the validity of a disease occurrence point.
-- Feed: Represents a source of alerts.
-- GeoNamesLocationPrecision: Represents a mapping between a GeoNames feature code and a location precision.
-- HealthMapCountry: Represents a country as defined by HealthMap.
-- HealthMapDisease: Represents a disease as defined by HealthMap.
-- Location: Represents the location of a disease occurrence.
-- Provenance: Represents a provenance, i.e. the source of a group of feeds.


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
    CreatedDate timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE DiseaseOccurrence (
    Id serial NOT NULL,
    DiseaseGroupId integer NOT NULL,
    LocationId integer NOT NULL,
    AlertId integer NOT NULL,
    CreatedDate timestamp NOT NULL DEFAULT LOCALTIMESTAMP,
    ValidationWeighting double precision,
    OccurrenceStartDate timestamp
);

CREATE TABLE DiseaseOccurrenceReview (
    Id serial NOT NULL,
    ExpertId integer NOT NULL,
    DiseaseOccurrenceId integer NOT NULL,
    Response varchar(6) NOT NULL,
    CreatedDate timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE Expert (
    Id serial NOT NULL,
    Name varchar(1000) NOT NULL,
    Email varchar(320) NOT NULL,
    HashedPassword varchar(60) NOT NULL,
    IsAdministrator boolean NOT NULL,
    Weighting double precision,
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
    Weighting double precision NOT NULL,
    HealthMapFeedId bigint,
    CreatedDate timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE GeoNamesLocationPrecision ( 
    GeoNamesFeatureCode varchar(50) NOT NULL,
    LocationPrecision varchar(50) NOT NULL
);

CREATE TABLE HealthMapCountry (
    Id bigint NOT NULL,
    Name varchar(100) NOT NULL,
    CountryId integer
);

CREATE TABLE HealthMapDisease (
    Id bigint NOT NULL,
    Name varchar(100) NOT NULL,
    DiseaseGroupId integer,
    CreatedDate timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE Location (
    Id serial NOT NULL,
    Name varchar(1000) NOT NULL,
    Geom geometry NOT NULL,
    Precision varchar(10) NOT NULL,
    CountryId integer NOT NULL,
    GeoNamesId integer,
    ResolutionWeighting double precision,
    CreatedDate timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE Provenance (
    Id serial NOT NULL,
    Name varchar(100) NOT NULL,
    DefaultFeedWeighting double precision,
    CreatedDate timestamp NOT NULL DEFAULT LOCALTIMESTAMP,
    LastRetrievedDate timestamp
);
