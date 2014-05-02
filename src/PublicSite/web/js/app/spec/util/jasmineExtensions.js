
define([
    "app/spec/util/observableMatcher",
    "app/spec/util/forceFailureMatcher"
], function (observableMatcher, forceFailureMatcher) {
    "use strict";

    beforeEach(function () {
        jasmine.addMatchers({
            toBeObservable: observableMatcher,
            toNotExecuteThisLine: forceFailureMatcher
        });
        jasmine.Ajax.install();
    });

    afterEach(function () {
        jasmine.Ajax.uninstall();
    });
});
