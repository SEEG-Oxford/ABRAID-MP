-- Script: tables.sql
--
-- Description: Creates tables for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford


-- List of tables:
--
-- admin_unit:                     Represents an admin 1/2 area. Imported from the standard SEEG/GAUL admin 1 and admin 2 shapefiles, with smaller islands removed.
-- admin_unit_global:              Represents an admin 0/1 area. As admin_unit_tropical, except ten large countries have been divided into admin 1 areas, to use for global diseases.
-- admin_unit_simplified_global:   Represents an admin 0/1 area. As admin_unit_global, except with simplified borders to improve rendering performance.
-- admin_unit_simplified_tropical: Represents an admin 0/1 area. As admin_unit_tropical, except with simplified borders to improve rendering performance.
-- admin_unit_tropical:            Represents an admin 0/1 area. Tailored for ABRAID-MP by separating non-contiguous parts of countries, absorbing tiny countries, removing smaller
--                                 smaller islands etc. Eight large subtropical countries have been divided into admin 1 areas.
-- alert:                          Represents a report of a disease occurrence or occurrences, from a feed.
-- country:                        Represents a country as defined by SEEG. Imported from the standard SEEG/GAUL admin 0 shapefile, with smaller islands removed.
-- disease_group:                  Represents a group of diseases as defined by SEEG. This can be a disease cluster, disease microcluster, or a disease itself.
-- disease_occurrence:             Represents an occurrence of a disease group, in a location, as reported by an alert.
-- disease_occurrence_review:      Represents an expert's response on the validity of a disease occurrence point.
-- expert:                         Represents a user of the PublicSite.
-- expert_disease_group:           Represents an expert's disease interest. These should be displayed to a user for review in the Data Validator.
-- feed:                           Represents a source of alerts.
-- geoname:                        Represents a GeoName.
-- geonames_location_precision:    Represents a mapping between a GeoNames feature code and a location precision.
-- healthmap_country:              Represents a country as defined by HealthMap.
-- healthmap_country_country:      Represents a mapping between HealthMap countries and SEEG countries.
-- healthmap_disease:              Represents a disease as defined by HealthMap.
-- location:                       Represents the location of a disease occurrence.
-- provenance:                     Represents a provenance, i.e. the source of a group of feeds.


CREATE TABLE admin_unit (
    gaul_code integer NOT NULL,
    parent_gaul_code integer NOT NULL,
    country_code varchar(3) NOT NULL,
    admin_level varchar(1) NOT NULL,
    name varchar(100) NOT NULL,
    centroid_latitude double precision NOT NULL,
    centroid_longitude double precision NOT NULL,
    area double precision NOT NULL,
    geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE admin_unit_global (
    gaul_code integer NOT NULL,
    parent_gaul_code integer NOT NULL,
    country_code varchar(3) NOT NULL,
    admin_level varchar(1) NOT NULL,
    name varchar(100) NOT NULL,
    public_name varchar(100) NOT NULL,
    geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE admin_unit_simplified_global (
    gaul_code integer NOT NULL,
    name varchar(100) NOT NULL,
    public_name varchar(100) NOT NULL,
    geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE admin_unit_simplified_tropical (
    gaul_code integer NOT NULL,
    name varchar(100) NOT NULL,
    public_name varchar(100) NOT NULL,
    geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE admin_unit_tropical (
    gaul_code integer NOT NULL,
    parent_gaul_code integer NOT NULL,
    country_code varchar(3) NOT NULL,
    admin_level varchar(1) NOT NULL,
    name varchar(100) NOT NULL,
    public_name varchar(100) NOT NULL,
    geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE alert (
    id serial NOT NULL,
    feed_id integer NOT NULL,
    title text,
    publication_date timestamp,
    url varchar(2000),
    summary text,
    healthmap_alert_id bigint,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE country (
    gaul_code integer NOT NULL,
    country_code varchar(3) NOT NULL,
    name varchar(100) NOT NULL,
    geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE disease_group (
    id serial NOT NULL,
    parent_id integer,
    name varchar(100) NOT NULL,
    group_type varchar(15) NOT NULL,
    public_name varchar(100),
    short_name varchar(100),
    abbreviation varchar(10),
    validator_set varchar(100),
    is_global boolean,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE disease_occurrence (
    id serial NOT NULL,
    disease_group_id integer NOT NULL,
    location_id integer NOT NULL,
    alert_id integer NOT NULL,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP,
    occurrence_start_date timestamp,
    validation_weighting double precision
);

CREATE TABLE disease_occurrence_review (
    id serial NOT NULL,
    expert_id integer NOT NULL,
    disease_occurrence_id integer NOT NULL,
    response varchar(6) NOT NULL,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE expert (
    id serial NOT NULL,
    name varchar(1000) NOT NULL,
    email varchar(320) NOT NULL,
    hashed_password varchar(60) NOT NULL,
    is_administrator boolean NOT NULL,
    weighting double precision,
    is_publicly_visible boolean,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE expert_disease_group (
    expert_id integer NOT NULL,
    disease_group_id integer NOT NULL
);

CREATE TABLE feed (
    id serial NOT NULL,
    provenance_id integer NOT NULL,
    name varchar(100) NOT NULL,
    weighting double precision NOT NULL,
    language varchar(4),
    healthmap_feed_id bigint,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE geoname (
    id integer NOT NULL,
    feature_code varchar(10) NOT NULL
);

CREATE TABLE geonames_location_precision (
    geonames_feature_code varchar(10) NOT NULL,
    location_precision varchar(10) NOT NULL
);

CREATE TABLE healthmap_country (
    id bigint NOT NULL,
    name varchar(100) NOT NULL
);

CREATE TABLE healthmap_country_country (
    healthmap_country_id bigint NOT NULL,
    gaul_code integer NOT NULL
);

CREATE TABLE healthmap_disease (
    id bigint NOT NULL,
    name varchar(100) NOT NULL,
    disease_group_id integer,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE location (
    id serial NOT NULL,
    name varchar(1000),
    geom geometry(POINT, 4326) NOT NULL,
    precision varchar(10) NOT NULL,
    geoname_id integer,
    resolution_weighting double precision,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP,
    healthmap_country_id bigint
);

CREATE TABLE provenance (
    id serial NOT NULL,
    name varchar(100) NOT NULL,
    default_feed_weighting double precision NOT NULL,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP,
    last_retrieval_end_date timestamp
);
