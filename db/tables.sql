-- Script: tables.sql
--
-- Description: Creates tables for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford


-- List of tables:
--
-- admin_unit_country:              Represents an admin 1's country, where the admin 1 is from admin_unit_global or admin_unit_tropical.
-- admin_unit_disease_extent_class: Represents the extent class (e.g. presence, absence) of a disease group across an administrative unit.
-- admin_unit_global:               Represents an admin 0/1 area. As admin_unit_tropical, except ten large countries have been divided into admin 1 areas, to use for global diseases.
-- admin_unit_review:               Represents an expert's response to the presence or absence of a disease group across an admin unit.
-- admin_unit_qc:                   Represents an admin 1/2 area for the purposes of QC (Quality Control).
--                                  Imported from the standard SEEG/GAUL admin 1 and admin 2 shapefiles, with smaller islands removed.
-- admin_unit_tropical:             Represents an admin 0/1 area. Tailored for ABRAID-MP by separating non-contiguous parts of countries, absorbing tiny countries, removing smaller
--                                  smaller islands etc. Eight large subtropical countries have been divided into admin 1 areas.
-- alert:                           Represents a report of a disease occurrence or occurrences, from a feed.
-- country:                         Represents a country as defined by SEEG. Imported from the standard SEEG/GAUL admin 0 shapefile, with smaller islands removed.
-- covariate_influence:             Contains information of the covariate used in a model run.
-- disease_group:                   Represents a group of diseases as defined by SEEG. This can be a disease cluster, disease microcluster, or a disease itself.
-- disease_occurrence:              Represents an occurrence of a disease group, in a location, as reported by an alert.
-- disease_occurrence_review:       Represents an expert's response on the validity of a disease occurrence point.
-- expert:                          Represents a user of the PublicSite.
-- expert_validator_disease_group:  Represents an expert's disease interest, in terms of a disease group used by the Data Validator.
-- feed:                            Represents a source of alerts.
-- geoname:                         Represents a GeoName.
-- geonames_location_precision:     Represents a mapping between a GeoNames feature code and a location precision.
-- healthmap_country:               Represents a country as defined by HealthMap.
-- healthmap_country_country:       Represents a mapping between HealthMap countries and SEEG countries.
-- healthmap_disease:               Represents a disease as defined by HealthMap.
-- land_sea_border:                 Represents a land-sea border to a 5km resolution as used by the model.
-- location:                        Represents the location of a disease occurrence.
-- model_run:                       Represents a run of the SEEG model.
-- provenance:                      Represents a provenance, i.e. the source of a group of feeds.
-- submodel_statistic:              Contains statistics of a model run.
-- validator_disease_group:         Represents a grouping of diseases for use by the Data Validator.


CREATE TABLE admin_unit_country (
    admin_unit_gaul_code integer NOT NULL,
    country_gaul_code integer NOT NULL
);

CREATE TABLE admin_unit_disease_extent_class (
    id serial NOT NULL,
    global_gaul_code integer,
    tropical_gaul_code integer,
    disease_group_id integer NOT NULL,
    disease_extent_class varchar(20) NOT NULL,
    occurrence_count integer NOT NULL,
    class_changed_date timestamp
);

CREATE TABLE admin_unit_global (
    gaul_code integer NOT NULL,
    level varchar(1) NOT NULL,
    name varchar(100) NOT NULL,
    pub_name varchar(100) NOT NULL,
    geom geometry(MULTIPOLYGON, 4326),
    simplified_geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE admin_unit_qc (
    gaul_code integer NOT NULL,
    level varchar(1) NOT NULL,
    name varchar(100) NOT NULL,
    centr_lon double precision NOT NULL,
    centr_lat double precision NOT NULL,
    area double precision NOT NULL,
    geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE admin_unit_review (
    id serial NOT NULL,
    expert_id integer NOT NULL,
    disease_group_id integer NOT NULL,
    global_gaul_code integer,
    tropical_gaul_code integer,
    response varchar(17),
    created_date timestamp NOT NULL DEFAULT statement_timestamp()
);

CREATE TABLE admin_unit_tropical (
    gaul_code integer NOT NULL,
    level varchar(1) NOT NULL,
    name varchar(100) NOT NULL,
    pub_name varchar(100) NOT NULL,
    geom geometry(MULTIPOLYGON, 4326),
    simplified_geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE alert (
    id serial NOT NULL,
    feed_id integer NOT NULL,
    title text,
    publication_date timestamp,
    url varchar(2000),
    summary text,
    healthmap_alert_id integer,
    created_date timestamp NOT NULL DEFAULT statement_timestamp()
);

CREATE TABLE country (
    gaul_code integer NOT NULL,
    name varchar(100) NOT NULL,
    for_min_data_spread boolean,
    geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE covariate_influence (
    id serial NOT NULL,
    model_run_id integer NOT NULL,
    covariate_name varchar(255) NOT NULL,
    covariate_display_name varchar(255),
    mean_influence double precision,
    upper_quantile double precision,
    lower_quantile double precision
);

CREATE TABLE disease_extent (
    disease_group_id integer NOT NULL,
    geom geometry(MULTIPOLYGON, 4326),
    min_validation_weighting double precision,
    min_occurrences_for_presence integer,
    min_occurrences_for_possible_presence integer,
    max_months_ago_for_higher_occurrence_score integer,
    lower_occurrence_score integer,
    higher_occurrence_score integer
);

CREATE TABLE disease_extent_class (
    name varchar(20) NOT NULL,
    weighting integer NOT NULL,
    distance_if_within_extent double precision
);

CREATE TABLE disease_group (
    id serial NOT NULL,
    parent_id integer,
    name varchar(100) NOT NULL,
    group_type varchar(15) NOT NULL,
    public_name varchar(100),
    short_name varchar(100),
    abbreviation varchar(10),
    is_global boolean,
    validator_disease_group_id integer,
    weighting double precision,
    last_model_run_prep_date timestamp,
    automatic_model_runs_start_date timestamp,
    min_new_locations_trigger integer,
    min_env_suitability double precision,
    min_distance_from_extent double precision,
    min_data_volume integer NOT NULL,
    min_distinct_countries integer,
    high_frequency_threshold integer,
    min_high_frequency_countries integer,
    occurs_in_africa boolean,
    use_machine_learning boolean NOT NULL,
    max_env_suitability_without_ml double precision,
    created_date timestamp NOT NULL DEFAULT statement_timestamp()
);

CREATE TABLE disease_occurrence (
    id serial NOT NULL,
    disease_group_id integer NOT NULL,
    location_id integer NOT NULL,
    alert_id integer NOT NULL,
    occurrence_date timestamp NOT NULL,
    env_suitability double precision,
    distance_from_extent double precision,
    expert_weighting double precision,
    machine_weighting double precision,
    validation_weighting double precision,
    final_weighting double precision,
    final_weighting_excl_spatial double precision,
    is_validated boolean,
    created_date timestamp NOT NULL DEFAULT statement_timestamp()
);

CREATE TABLE disease_occurrence_review (
    id serial NOT NULL,
    expert_id integer NOT NULL,
    disease_occurrence_id integer NOT NULL,
    response varchar(6) NOT NULL,
    created_date timestamp NOT NULL DEFAULT statement_timestamp()
);

CREATE TABLE expert (
    id serial NOT NULL,
    name varchar(1000) NOT NULL,
    email varchar(320) NOT NULL,
    hashed_password varchar(60) NOT NULL,
    job_title varchar(100) NOT NULL,
    institution varchar(100) NOT NULL,
    is_administrator boolean NOT NULL,
    is_seeg_member boolean NOT NULL,
    weighting double precision NOT NULL,
    visibility_requested boolean NOT NULL,
    visibility_approved boolean NOT NULL,
    created_date timestamp NOT NULL DEFAULT statement_timestamp(),
    updated_date timestamp NOT NULL DEFAULT statement_timestamp()
);

CREATE TABLE expert_validator_disease_group (
    expert_id integer NOT NULL,
    validator_disease_group_id integer NOT NULL,
    created_date timestamp NOT NULL DEFAULT statement_timestamp()
);

CREATE TABLE feed (
    id serial NOT NULL,
    provenance_id integer NOT NULL,
    name varchar(100) NOT NULL,
    weighting double precision NOT NULL,
    language varchar(4),
    healthmap_feed_id integer,
    created_date timestamp NOT NULL DEFAULT statement_timestamp()
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
    id integer NOT NULL,
    name varchar(100) NOT NULL,
    centroid_override geometry(POINT, 4326)
);

CREATE TABLE healthmap_country_country (
    healthmap_country_id integer NOT NULL,
    gaul_code integer NOT NULL
);

CREATE TABLE healthmap_disease (
    id integer NOT NULL,
    name varchar(100) NOT NULL,
    disease_group_id integer,
    created_date timestamp NOT NULL DEFAULT statement_timestamp()
);

CREATE TABLE land_sea_border (
    id integer NOT NULL,
    geom geometry(MULTIPOLYGON, 4326) NOT NULL
);

CREATE TABLE location (
    id serial NOT NULL,
    name varchar(1000),
    geom geometry(POINT, 4326) NOT NULL,
    precision varchar(10) NOT NULL,
    geoname_id integer,
    resolution_weighting double precision NOT NULL,
    created_date timestamp NOT NULL DEFAULT statement_timestamp(),
    healthmap_country_id integer,
    admin_unit_qc_gaul_code integer,
    admin_unit_global_gaul_code integer,
    admin_unit_tropical_gaul_code integer,
    country_gaul_code integer,
    has_passed_qc boolean NOT NULL,
    qc_message varchar(1000)
);

CREATE TABLE model_run (
    id serial NOT NULL,
    name varchar(300) NOT NULL,
    status varchar(15) NOT NULL,
    disease_group_id integer NOT NULL,
    request_date timestamp NOT NULL,
    response_date timestamp,
    output_text text,
    error_text text,
    mean_prediction_raster raster,
    prediction_uncertainty_raster raster,
    batch_end_date timestamp,
    batch_occurrence_count integer,
    batching_completed_date timestamp
);

CREATE TABLE provenance (
    id serial NOT NULL,
    name varchar(100) NOT NULL,
    default_feed_weighting double precision NOT NULL,
    created_date timestamp NOT NULL DEFAULT statement_timestamp(),
    last_retrieval_end_date timestamp
);

CREATE TABLE submodel_statistic (
    id serial NOT NULL,
    model_run_id integer NOT NULL,
    deviance double precision,
    root_mean_square_error double precision,
    kappa double precision,
    area_under_curve double precision,
    sensitivity double precision,
    specificity double precision,
    proportion_correctly_classified double precision,
    kappa_sd double precision,
    area_under_curve_sd double precision,
    sensitivity_sd double precision,
    specificity_sd double precision,
    proportion_correctly_classified_sd double precision,
    threshold double precision
);

CREATE TABLE validator_disease_group (
    id serial NOT NULL,
    name varchar(100) NOT NULL,
    created_date timestamp NOT NULL DEFAULT statement_timestamp()
);
