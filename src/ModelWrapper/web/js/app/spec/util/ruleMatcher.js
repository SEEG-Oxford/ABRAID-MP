/* A custom Jasmine matcher for knockout validation rules.
 * Copyright (c) 2014 University of Oxford
 */
define(["underscore"], function (_) {
    "use strict";

    return function (util, customEqualityTesters) {
        return {
            compare: function (actual, expected) {

                if (expected === undefined) {
                    expected = {};
                }
                if (expected.name === undefined) {
                    expected.name = "";
                }
                if (expected.params === undefined) {
                    expected.name = null;
                }

                var result = {};

                var matchingRules = _(actual.rules()).where({ rule: expected.name });
                if (matchingRules.length === 1) {
                    if (util.equals(matchingRules[0].params, expected.params, customEqualityTesters)) {
                        result.pass = true;
                    } else {
                        result.pass = false;
                        result.message = "Expected rule '" + expected.name + "' was present but the parameter was '" +
                            matchingRules[0].params + "' instead of the expected '" + expected.params + "'.";
                    }
                } else {
                    result.pass = false;
                    result.message = "Expected rule '" + expected.name +
                        "' be present but it was not in: [" + _(actual.rules()).pluck("rule") + "].";
                }

                return result;
            }
        };
    };
});
