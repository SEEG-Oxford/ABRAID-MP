/* An AMD defining and registering a set of custom knockout validation rules.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "knockout",
    "knockout.validation"
], function (ko) {
    "use strict";

    // Adapted from:
    // https://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#are-same
    ko.validation.rules.areSame = {
        getValue: function (o) {
            return (typeof o === "function" ? o() : o);
        },
        validator: function (val, otherField) {
            return val === this.getValue(otherField);
        },
        message: "Password fields must match"
    };

    // Adapted from:
    // https://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#password-complexity
    ko.validation.rules.passwordComplexity = {
        validator: function (val) {
            var pattern = "(?=^[^\s]{6,128}$)((?=.*?\d)(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\d)(?=.*?[^\w\d\s])(?=.*?[a-z])|(?=.*?[^\w\d\s])(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\d)(?=.*?[A-Z])(?=.*?[^\w\d\s]))^.*"; /* jshint ignore:line */ // Line length
            return (new RegExp(pattern)).test("" + val + "");
        },
        message: "Password must be between 6 and 128 characters long and contain three of the following 4 items: upper case letter, lower case letter, a symbol, a number" /* jshint ignore:line */ // Line length
    };
});