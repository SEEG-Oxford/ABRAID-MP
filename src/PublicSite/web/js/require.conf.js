/*
 * Configuration for require.js.
 * Copyright (c) 2014 University of Oxford
 */
/*global requirejs:false, baseUrl:false */
(function () {
    "use strict";

    requirejs.config({
        baseUrl: baseUrl + "js/",
        paths: {
            /* Load external libs from cdn. */
            "jquery": "https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.0/jquery",
            "bootstrap": "https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.0.3/js/bootstrap",
            "bootstrap.extensions": "https://cdnjs.cloudflare.com/ajax/libs/jasny-bootstrap/3.1.2/js/jasny-bootstrap",
            "knockout": "https://cdnjs.cloudflare.com/ajax/libs/knockout/3.1.0/knockout-debug",
            // Move to cdn path when next version is published (error template refreshing bug)
            "knockout.validation": baseUrl + "js/lib/knockout-validation.min",
            // Move to cdn path when next version is published (AMD support)
            "knockout.bootstrap": baseUrl + "js/lib/knockout-bootstrap.min",
            "knockout-postbox": baseUrl + "js/lib/knockout-postbox",
            "underscore": "https://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.6.0/underscore",
            "domReady": "https://cdnjs.cloudflare.com/ajax/libs/require-domReady/2.0.1/domReady",
            "leaflet": "https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.2/leaflet",
            "leaflet-markercluster": baseUrl + "js/lib/leaflet.markercluster",
            "leaflet-zoomslider": baseUrl + "js/lib/L.Control.Zoomslider",
            "flipclock": baseUrl + "js/lib/flipclock.min",
            "moment": "https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.6.0/moment.min"
        },
        shim: {
            /* Set bootstrap dependencies (just jQuery) */
            "bootstrap" : ["jquery"],
            "knockout.bootstrap" : [ "knockout", "bootstrap.extensions" ],
            "knockout.validation" : [ "knockout" ],
            "bootstrap.extensions" :  ["bootstrap" ],
            "leaflet-markercluster": [ "leaflet" ],
            "flipclock": ["jquery"]
        }
    });
}());
