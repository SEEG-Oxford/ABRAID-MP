/**
 * JS file for the navbar header.
 * Copyright (c) 2014 University of Oxford
 */

// Highlight the link for the current page
$('ul.nav a').filter(function () {
    'use strict';

    return this.href == location;
}).parent().addClass('active');