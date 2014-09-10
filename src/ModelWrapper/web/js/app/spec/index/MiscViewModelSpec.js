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

                injector.mock("app/index/SingleFieldFormViewModel", spy);

                injector.require(["app/index/MiscViewModel"], function (MiscViewModel) {
                    // Act
                    new MiscViewModel({
                        rPath: "expectedInitialRPath",
                        runDuration: "expectedInitialRunDuration",
                        covariateDirectory: "expectedInitialCovariateDirectory"
                    }, "expectedBaseUrl");

                    // Assert
                    expect(spy).toHaveBeenCalledWith("expectedBaseUrl", "misc/rpath",
                        "expectedInitialRPath", { required : true });
                    expect(spy).toHaveBeenCalledWith("expectedBaseUrl", "misc/runduration",
                        "expectedInitialRunDuration", { required : true, number: true, min: 1000 });
                    expect(spy).toHaveBeenCalledWith("expectedBaseUrl", "misc/covariatedirectory",
                        "expectedInitialCovariateDirectory", { required : true });
                    done();
                });
            });

            it("are assigned correctly", function () {
                // Act
                var vm = new MiscViewModel({
                    rPath: "expectedInitialRPath",
                    runDuration: "expectedInitialRunDuration",
                    covariateDirectory: "expectedInitialCovariateDirectory"
                }, "expectedBaseUrl");

                // Assert
                expect(vm.RExecutableViewModel().value()).toBe("expectedInitialRPath");
                expect(vm.ModelDurationViewModel().value()).toBe("expectedInitialRunDuration");
                expect(vm.CovariateDirectoryViewModel().value()).toBe("expectedInitialCovariateDirectory");
            });
        });
    });
});