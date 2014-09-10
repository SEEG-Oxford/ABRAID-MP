/* An AMD to bundle up knockout + the extensions we use so that they can all be required in one statement.
 * Note: Can't be an in-line define due to use of Squire in tests (based on requirejs multi-context).
 * Copyright (c) 2014 University of Oxford
 */
define([
    "knockout",
    "knockout.bootstrap",
    "knockout.validation",
    "knockout-postbox",
    "shared/app/KoCustomUtils",
    "shared/app/KoCustomRules",
    "shared/app/KoCustomBindings"
], function (ko) {
    "use strict";

    // Configure Knockout validation to use our standard validation template
    ko.validation.init({
        insertMessages: true,
        messageTemplate: "validation-template",
        messagesOnModified: true,
        registerExtenders: true,
        grouping: { deep: true, observable: true, live: true }
    });

    // Force postbox to publish every event, instead of checking whether value has changed against cache, when enforced
    // by skipSerialize boolean. Useful for large arrays (as with admin-units-to-be-reviewed event), but beware can
    // cause coupled referencing loop when used with knockout's syncWith.
    // https://github.com/rniemeyer/knockout-postbox/issues/10
    ko.postbox.serializer = function (object) {
        if (object && object.skipSerialize) { return {}; }
        return ko.toJSON(object);
    };

    // Bundle up all the knockout stuff
    return ko;
});