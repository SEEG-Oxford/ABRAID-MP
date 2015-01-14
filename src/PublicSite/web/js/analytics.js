/* Google Universal Analytics tracking code with the the ABRAID www.abraid.ox.ac.uk account ID,
 * expressed as an AMD for RequireJS compatibility.
 * https://gist.github.com/ismyrnow/6252718
 *
 * Copyright (c) 2014 University of Oxford
 */
/*global window:false*/
define(["require"], function (require) {
    "use strict";

    // Setup temporary Google Analytics objects.
    window.GoogleAnalyticsObject = "ga";
    window.ga = function () { (window.ga.q = window.ga.q || []).push(arguments); };
    window.ga.l = 1 * new Date();

    // Immediately add a pageview event to the queue.
    window.ga("create", "UA-366737-6", {
        "cookieDomain": "none"
    });

    if (window.location === window.parent.location) {
        // Announce page load, if not in an iframe (avoids maps/help double counting)
        window.ga("send", "pageview");
    }

    // Asynchronously load Google Analytics, letting it take over our `window.ga`
    // object after it loads. This allows us to add events to `window.ga` even
    // before the library has fully loaded.
    require(["//www.google-analytics.com/analytics.js"]);
});