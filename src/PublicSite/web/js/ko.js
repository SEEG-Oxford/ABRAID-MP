/* Bundle up all the knockout stuff
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false*/
define([
    "knockout",
    "knockout-postbox",
    "knockout.bootstrap",
    "knockout.validation",
    "app/KoCustomBindings",
    "app/KoCustomRules"
], function (ko) {
    "use strict";

    // A utility function to assist in extracting values from knockout value accessors.
    ko.utils.recursiveUnwrap = function (func) {
        if (typeof func !== "function") {
            return func;
        }
        return ko.utils.recursiveUnwrap(func());
    };

    // Same as recursiveUnwrap but accesses observables and computed observables without creating a ko dependency.
    ko.utils.recursivePeek = function (func) {
        if (typeof func !== "function") {
            return func;
        }

        if (ko.isObservable(func)) {
            return ko.utils.recursivePeek(func.peek());
        }

        return ko.utils.recursivePeek(func());
    };

    // Extend binding contexts to add a helper method "find" for reading the value of a field/observable from the
    // local binding context's data, or work up though the parent contexts if not present on the local context.
    ko.bindingContext.prototype.find = function (field) {
        var context = this;

        while (context !== undefined && context.$data !== undefined) {
            if (context.$data[field] !== undefined) {
                return ko.utils.recursiveUnwrap(context.$data[field]);
            } else {
                context = context.$parentContext;
            }
        }

        throw new Error(field + " field not found on context or any parent context");
    };

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

    return ko;
});
