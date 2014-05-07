/**
 * Bundle up all the knockout stuff
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false*/
define([
    "knockout",
    "knockout-postbox",
    "app/KoCustomBindings"
], function (ko) {
    "use strict";

    // Force postbox to publish every change - https://github.com/rniemeyer/knockout-postbox/issues/10
    ko.postbox.serializer = function () { return {}; };
    return ko;
});
