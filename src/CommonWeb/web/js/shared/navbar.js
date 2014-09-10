/**
 * JS file for the navbar header.
 * Copyright (c) 2014 University of Oxford
 */
/*global window:false*/
// Although bootstrap.js is not used within the function below, it is needed to generate drop-down menus in the navbar
define(["jquery", "bootstrap", "domReady!"], function ($) {
    "use strict";

    var cleanUrl = function (url) {
        var out = url.toLowerCase();

        // ignore params
        if (out.indexOf("?") !== -1) {
            out = url.substring(0, out.indexOf("?"));
        }

        // ignore anchors
        if (out.indexOf("#") !== -1) {
            out = url.substring(0, out.indexOf("#"));
        }

        // ignore http basic auth (unlikely)
        if (out.indexOf("@") !== -1) {
            out = url.substring(out.indexOf("@"), out.length);
        }

        // ignore protocol
        if (out.match(/^.*?:\/\/.*/g)) {
            out = out.replace(/^.*?:\/\//g, "");
        }

        // ignore www
        if (out.match(/^www\..*/g)) {
            out = out.replace(/^www\./g, "");
        }

        return out;
    };

    var currentURL = cleanUrl(window.location.href);
    $("ul.nav a").filter(function () {
        return $(this).attr("href") !== "#" && cleanUrl(this.href) === currentURL;
    }).parent().addClass("active");
});
