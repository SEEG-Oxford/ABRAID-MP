/*
 * A set of custom Jasmine matchers.
 * Copyright (c) 2014 University of Oxford
 */
/* global baseUrl: false, commonBaseUrl:false */
define([
    (commonBaseUrl || baseUrl) + "js/shared/app/spec/util/observableMatcher.js",
    (commonBaseUrl || baseUrl) + "js/shared/app/spec/util/ruleMatcher.js",
    (commonBaseUrl || baseUrl) + "js/shared/app/spec/util/forceFailureMatcher.js"
], function (observableMatcher, ruleMatcher, forceFailureMatcher) {
    "use strict";

    beforeEach(function () {
        jasmine.addMatchers({
            toBeObservable: observableMatcher,
            toHaveValidationRule: ruleMatcher,
            toNotExecuteThisLine: forceFailureMatcher
        });
        jasmine.Ajax.install();
    });

    afterEach(function () {
        jasmine.Ajax.uninstall();
    });
});
