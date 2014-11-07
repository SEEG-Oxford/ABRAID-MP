/* An AMD defining some basic extensions to knockout.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "knockout"
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

    // Translates undefined, null and NaN to empty string (but not 0 or false)
    ko.utils.normaliseInput = function (s) {
        if (s === 0 || s === false) {
            return s;
        }
        return s || "";
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
});