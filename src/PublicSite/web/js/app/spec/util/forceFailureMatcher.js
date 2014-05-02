/*
 * A custom Jasmine matcher to force failure, and return the specified failure message.
 * Copyright (c) 2014 University of Oxford
 */
define([], function () {
    "use strict";

    return function () {
        return {
            compare: function (actual, expected) {
                return {
                    pass: false,
                    message: expected
                };
            }
        };
    };
});
