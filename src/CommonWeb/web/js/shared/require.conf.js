/* Configuration for require.js.
 * Copyright (c) 2014 University of Oxford
 */
/*global requirejs:false, baseUrl:false, commonBaseUrl:false */
(function () {
    "use strict";

    var basePath;
    if (typeof commonBaseUrl === "undefined") {
        basePath = baseUrl + "js/shared/";
    } else {
        basePath = commonBaseUrl + "js/shared/";
    }

    var cdn = "https://cdnjs.cloudflare.com/ajax/libs/";
    requirejs.config({
        baseUrl: baseUrl + "js/",
        paths: {
            // CDN
            "jquery": cdn + "jquery/2.1.0/jquery",
            "bootstrap": cdn + "twitter-bootstrap/3.2.0/js/bootstrap",
            "bootstrap-datepicker": cdn + "bootstrap-datepicker/1.3.0/js/bootstrap-datepicker",
            "bootstrap.extensions": cdn + "jasny-bootstrap/3.1.3/js/jasny-bootstrap",
            "knockout": cdn + "knockout/3.1.0/knockout-debug",
            "underscore": cdn + "underscore.js/1.6.0/underscore",
            "moment": cdn + "moment.js/2.6.0/moment.min",
            "domReady": cdn + "require-domReady/2.0.1/domReady",
            "leaflet": cdn + "leaflet/0.7.2/leaflet",

            // Local
            "leaflet-markercluster": basePath + "lib/leaflet.markercluster",
            "leaflet-zoomslider": basePath + "lib/L.Control.Zoomslider",
            "flipclock": basePath + "lib/flipclock.min",
            // Move to cdn path when next version is published (error template refreshing bug)
            "knockout.validation": basePath + "lib/knockout-validation.min",
            // Move to cdn path when next version is published (AMD support)
            "knockout.bootstrap": basePath + "lib/knockout-bootstrap.min",
            "knockout-postbox": basePath + "lib/knockout-postbox",
            "jquery.iframe-transport": basePath + "lib/jquery.iframe-transport",

            // Shared - makes sure that shared files can load even when not working on deployed artifacts
            "ko": basePath + "ko",
            "L": basePath + "L",
            "shared/navbar": basePath + "navbar",
            "shared/app/BaseFormViewModel": basePath + "app/BaseFormViewModel",
            "shared/app/BaseFileFormViewModel": basePath + "app/BaseFileFormViewModel",
            "shared/app/BaseTableViewModel": basePath + "app/BaseTableViewModel",
            "shared/app/SingleFieldFormViewModel": basePath + "app/SingleFieldFormViewModel",
            "shared/app/KoCustomRules": basePath + "app/KoCustomRules",
            "shared/app/KoCustomBindings": basePath + "app/KoCustomBindings",
            "shared/app/KoCustomUtils": basePath + "app/KoCustomUtils",
            "squire": basePath + "app/spec/lib/squire"
        },
        shim: {
            "bootstrap" : ["jquery"],
            "knockout.bootstrap" : [ "knockout", "bootstrap-datepicker", "bootstrap.extensions" ],
            "knockout.validation" : [ "knockout" ],
            "bootstrap-datepicker" :  [ "bootstrap" ],
            "bootstrap.extensions" :  [ "bootstrap" ],
            "leaflet-markercluster": [ "leaflet" ],
            "flipclock": ["jquery"],
            "jquery.iframe-transport": ["jquery"]
        }
    });
}());
