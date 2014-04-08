/* Configuration for require.js.
 * Copyright (c) 2014 University of Oxford
 */
/*global requirejs:false, define:false, baseUrl:false */
(function() {
    "use strict";

    requirejs.config({
        baseUrl: baseUrl + 'js/',
        paths: {
            /* Load external libs from cdn. */
            'jquery': 'https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.0/jquery',
            'bootstrap': 'https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.1.1/js/bootstrap',
            'knockout': "https://cdnjs.cloudflare.com/ajax/libs/knockout/3.1.0/knockout-debug",
            'knockout.validation': 'https://cdnjs.cloudflare.com/ajax/libs/knockout-validation/1.0.2/knockout.validation.min',
            'knockout.bootstrap': baseUrl + 'js/lib/knockout-bootstrap.min', // Move to cdn path when next version is published
            'domReady': 'https://cdnjs.cloudflare.com/ajax/libs/require-domReady/2.0.1/domReady'
        },
        shim: {
            /* Set bootstrap dependencies (just jQuery) */
            'bootstrap' : ['jquery'],
            'knockout.bootstrap' : [ 'knockout', 'bootstrap' ],
            'knockout.validation' : [ 'knockout' ]
        }
    });

    define("ko", ["knockout", "knockout.bootstrap", "knockout.validation", "app/knockout.validation.rules"], function(ko) {
        // Bundle up all the knockout stuff
        return ko;
    });
}());