/**
 * Bundle up all the knockout stuff
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false*/
define([
    "knockout",
    "knockout-postbox",
    "knockout.bootstrap",
    "knockout.validation",
    "app/KoCustomBindings"
], function (ko) {
    "use strict";

    /**
     * Force postbox to publish every event, instead of checking whether value has changed against cache, when enforced
     * by skipSerialize boolean. Useful for large arrays (as with admin-units-to-be-reviewed event), but beware coupled
     * referencing loop when used with knockout's syncWith.
     * https://github.com/rniemeyer/knockout-postbox/issues/10
     */
    ko.postbox.serializer = function (object) {
        if (object && object.skipSerialize) { return {}; }
        return ko.toJSON(object);
    };

    return ko;
});
