/* An AMD defining and registering a set of custom knockout validation rules.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "underscore",
    "knockout",
    "knockout.validation"
], function (_, ko) {
    "use strict";

    // Adapted from:
    // https://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#are-same
    ko.validation.rules.areSame = {
        validator: function (val, otherField) {
            return ko.utils.recursiveUnwrap(val) === ko.utils.recursiveUnwrap(otherField);
        },
        message: "Password fields must match"
    };

    // Adapted from:
    // https://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#password-complexity
    ko.validation.rules.passwordComplexity = {
        validator: function (val) {
            var pattern = /(?=^[^\s]{6,128}$)((?=.*?\d)(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\d)(?=.*?[^\w\d\s])(?=.*?[a-z])|(?=.*?[^\w\d\s])(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\d)(?=.*?[A-Z])(?=.*?[^\w\d\s]))^.*/; /* jshint ignore:line */ // Line length
            return pattern.test("" + val + "");
        },
        message: "Password must be between 6 and 128 characters long and contain three of the following 4 items: upper case letter, lower case letter, a symbol, a number" /* jshint ignore:line */ // Line length
    };

    ko.validation.rules.digit.message = "Please enter a whole number";

    ko.validation.rules.isUniqueProperty = {
        validator: function (val, options) {
            var array = options.array;
            var id = ko.utils.unwrapObservable(options.id);
            var property = options.property;
            return ! _(array)
                .chain()
                .filter(function (o) { return o.id !== id; })
                .pluck(property)
                .contains(val)
                .value();
        },
        message: "Value must be unique"
    };
});