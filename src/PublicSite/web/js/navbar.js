/**
 * JS file for the navbar header.
 * Copyright (c) 2014 University of Oxford
 */
/*global window:false*/
// Although bootstrap.js is not used within the function below, it is needed to generate drop-down menus in the navbar
define(["jquery", "bootstrap"], function ($) {
    "use strict";

    return function () {
        // Highlight the link for the current page
        $("ul.nav a").filter(function () {
            return this.href.toLowerCase() === window.location.href.toLowerCase();
        }).parent().addClass("active");
    };
});
