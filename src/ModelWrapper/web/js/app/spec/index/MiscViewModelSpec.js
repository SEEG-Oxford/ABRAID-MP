/* Tests for MiscViewModel.
 * Copyright (c) 2014 University of Oxford
 */
/*jshint nonew: false*/
define([
    "app/index/MiscViewModel",
    "ko",
    "underscore",
    "squire"
], function (MiscViewModel, ko, _, Squire) {
    "use strict";

    describe("The misc view model", function () {
        describe("composes three sub view models which", function () {
            it("are SingleFieldFormViewModel with the correct properties", function (done) {
                // Arrange
                var spy = jasmine.createSpy();

                // Squire.require is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                jasmine.Ajax.uninstall();
                var injector = new Squire();

                injector.mock("shared/app/SingleFieldFormViewModel", spy);

                injector.require(["app/index/MiscViewModel"], function (MiscViewModel) {
                    // Act
                    new MiscViewModel({
                        rPath: "expectedInitialRPath",
                        runDuration: "expectedDuration",
                        covariateDirectory: "expectedInitialCovariateDirectory"
                    }, "expectedBaseUrl");

                    // Assert
                    expect(spy).toHaveBeenCalledWith("expectedInitialRPath", { required : true },
                                                     false, false, "expectedBaseUrl", "misc/rpath");
                    expect(spy).toHaveBeenCalledWith("expectedDuration", { required : true, number: true, min: 1000 },
                                                     false, false, "expectedBaseUrl", "misc/runduration");
                    done();
                });
            });

            it("are assigned correctly", function () {
                // Act
                var vm = new MiscViewModel({
                    rPath: "expectedInitialRPath",
                    runDuration: "expectedInitialRunDuration"
                }, "expectedBaseUrl");

                // Assert
                expect(vm.RExecutableViewModel().value()).toBe("expectedInitialRPath");
                expect(vm.ModelDurationViewModel().value()).toBe("expectedInitialRunDuration");
            });
        });
    });
});
