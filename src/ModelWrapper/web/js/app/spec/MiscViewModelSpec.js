/* Tests for MiscViewModel.
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false, describe:false, it:false, expect:false, beforeEach:false, afterEach:false, jasmine:false*/
define([ 'app/MiscViewModel', 'ko', 'underscore', 'app/spec/util/ruleMatcher', 'app/spec/util/observableMatcher', 'app/spec/util/squire' ], function(MiscViewModel, ko, _, ruleMatcher, observableMatcher, Squire) {
    "use strict";

    describe("The misc view model", function() {
        var addCustomMatchers = function() {
            jasmine.addMatchers({ toHaveValidationRule: ruleMatcher });
            jasmine.addMatchers({ toBeObservable: observableMatcher });
        };

        describe("composes two a sub view models which", function() {
            beforeEach(addCustomMatchers);

            it("are SingleFieldFormViewModel with the correct properties", function (done) {
                // Arrange
                var spy = jasmine.createSpy();
                var injector = new Squire();
                injector.mock("app/SingleFieldFormViewModel", spy);

                injector.require(['app/MiscViewModel'], function (MiscViewModel) {
                    // Act
                    var vm = new MiscViewModel({rPath: "expectedInitialRPath", runDuration: "expectedInitialRunDuration"}, "expectedBaseUrl");

                    // Assert
                    expect(spy).toHaveBeenCalledWith('expectedBaseUrl', 'misc/rpath', 'expectedInitialRPath', { required : true });
                    expect(spy).toHaveBeenCalledWith('expectedBaseUrl', 'misc/runduration', 'expectedInitialRunDuration', { required : true, number: true  });
                    done();
                });
            });

            it("are assigned correctly", function () {

                // Act
                var vm = new MiscViewModel({rPath: "expectedInitialRPath", runDuration: "expectedInitialRunDuration"}, "expectedBaseUrl");

                // Assert
                expect(vm.RExecutableViewModel().value()).toBe("expectedInitialRPath");
                expect(vm.ModelDurationViewModel().value()).toBe("expectedInitialRunDuration");
            });
        });
    });
});