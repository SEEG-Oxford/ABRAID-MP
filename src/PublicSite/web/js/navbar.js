/**
 * JS file for the navbar header.
 * Copyright (c) 2014 University of Oxford
 */
/*global location:false*/
define(["jquery"], function ($) {
    "use strict";

    return function () {
        // Highlight the link for the current page
        $("ul.nav a").filter(function () {
            return this.href == location;
        }).parent().addClass("active");
    };
});
