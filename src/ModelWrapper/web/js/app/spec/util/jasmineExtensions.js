/* Add custom matchers and jasmine ajax to basic jasmine setup, to avoid setting it up in each spec.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/spec/util/observableMatcher",
    "app/spec/util/ruleMatcher"
], function (observableMatcher, ruleMatcher) {
    "use strict";

    beforeEach(function () {
        jasmine.addMatchers({
            toBeObservable: observableMatcher,
            toHaveValidationRule: ruleMatcher
        });
        jasmine.Ajax.install();
    });

    afterEach(function () {
        jasmine.Ajax.uninstall();
    });
});