/* Tests for MiscViewModel.
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false, describe:false, it:false, expect:false, beforeEach:false, afterEach:false, jasmine:false*/
define([ 'app/MiscViewModel', 'ko', 'underscore', 'app/spec/util/ruleMatcher', 'app/spec/util/observableMatcher' ], function(MiscViewModel, ko, _, ruleMatcher, observableMatcher) {
    "use strict";

    describe("The misc view model", function() {
        var addCustomMatchers = function() {
            jasmine.addMatchers({ toHaveValidationRule: ruleMatcher });
            jasmine.addMatchers({ toBeObservable: observableMatcher });
        };

        // TODO - Add tests
    });
});