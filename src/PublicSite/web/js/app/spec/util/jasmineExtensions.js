
define([
    "app/spec/util/observableMatcher",
    "app/spec/util/ruleMatcher",
    "app/spec/util/forceFailureMatcher"
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
