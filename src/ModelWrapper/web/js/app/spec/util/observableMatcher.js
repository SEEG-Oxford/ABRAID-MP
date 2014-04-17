/* A custom Jasmine matcher for knockout observables.
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false*/
define(['ko'], function(ko) {
    "use strict";

    return function(util, customEqualityTesters) {
        return {
            compare: function(actual) {
                var result = {};

                result.pass = ko.isObservable(actual);
                if (!result.pass) {
                    result.message = "It's not a ko observable!.";
                }

                return result;
            }
        };
    };
});
