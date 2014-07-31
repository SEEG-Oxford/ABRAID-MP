/*
 * Configuration for require.js.
 * Copyright (c) 2014 University of Oxford
 */
/*global requirejs:false, baseUrl:false */
(function () {
    "use strict";
    
    var cdn = "https://cdnjs.cloudflare.com/ajax/libs/";
    requirejs.config({
        baseUrl: baseUrl + "js/",
        paths: {
            /* Load external libs from cdn. */
            "jquery": cdn + "jquery/2.1.0/jquery",
            "bootstrap": cdn + "/twitter-bootstrap/3.0.3/js/bootstrap",
            "bootstrap-datepicker": cdn + "bootstrap-datepicker/1.3.0/js/bootstrap-datepicker",
            "bootstrap.extensions": cdn + "jasny-bootstrap/3.1.2/js/jasny-bootstrap",
            "knockout": cdn + "knockout/3.1.0/knockout-debug",
            // Move to cdn path when next version is published (error template refreshing bug)
            "knockout.validation": baseUrl + "js/lib/knockout-validation.min",
            // Move to cdn path when next version is published (AMD support)
            "knockout.bootstrap": baseUrl + "js/lib/knockout-bootstrap.min",
            "knockout-postbox": baseUrl + "js/lib/knockout-postbox",
            "underscore": cdn + "libs/underscore.js/1.6.0/underscore",
            "domReady": cdn + "require-domReady/2.0.1/domReady",
            "leaflet": cdn + "leaflet/0.7.2/leaflet",
            "leaflet-markercluster": baseUrl + "js/lib/leaflet.markercluster",
            "leaflet-zoomslider": baseUrl + "js/lib/L.Control.Zoomslider",
            "flipclock": baseUrl + "js/lib/flipclock.min",
            "moment": cdn + "moment.js/2.6.0/moment.min"
        },
        shim: {
            /* Set bootstrap dependencies (just jQuery) */
            "bootstrap" : ["jquery"],
            "knockout.bootstrap" : [ "knockout", "bootstrap-datepicker", "bootstrap.extensions" ],
            "knockout.validation" : [ "knockout" ],
            "bootstrap-datepicker" :  [ "bootstrap" ],
            "bootstrap.extensions" :  [ "bootstrap" ],
            "leaflet-markercluster": [ "leaflet" ],
            "flipclock": ["jquery"]
        }
    });
}());
