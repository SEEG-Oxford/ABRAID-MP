/* Google Universal Analytics tracking code with the ABRAID www.abraid.ox.ac.uk account ID,
 * expressed as an AMD for RequireJS compatibility.
 * https://gist.github.com/ismyrnow/6252718
 *
 * Copyright (c) 2014 University of Oxford
 */
/*global window:false*/
define(["require", "ko", "jquery", "domReady!"], function (require, ko, $) {
    "use strict";
    var noop = function () {};

    // Setup temporary Google Analytics objects.
    window.GoogleAnalyticsObject = "ga";
    window.ga = function () { (window.ga.q = window.ga.q || []).push(arguments); };
    window.ga.l = 1 * new Date();

    // Setup analytics account
    window.ga("create", "UA-366737-6", {
        "cookieDomain": "www.abraid.ox.ac.uk"
    });

    //If not in an iframe (avoids maps/help double counting)
    if (window.location === window.parent.location) {
        // Announce page view
        window.ga("send", "pageview");

        // Show cookie warning
        require(
            ["jquery.cookiecuttr"],
            function() {
                // loaded successfully
                $.cookieCuttr({
                    cookieAnalytics: true,
                    cookieAnalyticsMessage: "This site uses cookies to track usage and preferences.",
                    cookieAcceptButtonText: "OK",
                    cookieWhatAreLinkText: "?"
                });
            },
            noop
        );
    }

    // Asynchronously load Google Analytics, letting it take over our `window.ga`
    // object after it loads. This allows us to add events to `window.ga` even
    // before the library has fully loaded.
    require(["//www.google-analytics.com/analytics.js"], noop, noop);

    ko.postbox.subscribe("tracking-action-event", function (payload) {
        window.ga("send", "event", payload.category, payload.action, payload.label, payload.value);
    });
});