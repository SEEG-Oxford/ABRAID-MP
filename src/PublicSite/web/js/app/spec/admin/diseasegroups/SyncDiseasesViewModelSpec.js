/* A suite of tests for the SyncDiseasesViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "shared/app/BaseFormViewModel",
    "squire"
], function (BaseFormViewModel, Squire) {
    "use strict";

    describe("The 'sync diseases' view model", function () {
        var baseUrl = "baseUrl/";
        describe("has the behaviour of BaseFormViewModel", function () {
            var vm;
            var baseSpy;
            beforeEach(function (done) {
                if (vm === undefined) { // before first
                    // Squire is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                    jasmine.Ajax.uninstall();
                    var injector = new Squire();

                    baseSpy = jasmine.createSpy("baseSpy");
                    injector.mock("shared/app/BaseFormViewModel", baseSpy);

                    injector.require(["app/admin/diseasegroups/SyncDiseasesViewModel"],
                        function (SyncDiseasesViewModel) {
                            vm = new SyncDiseasesViewModel(baseUrl); // jshint ignore:line
                            jasmine.Ajax.install();
                            done();
                        }
                    );
                } else {
                    done();
                }
            });

            it("when created", function () {
                expect(baseSpy).toHaveBeenCalled();
            });

            it("it specifies the correct 'baseUrl'", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[2]).toBe(baseUrl);
            });

            it("it specifies the correct form url", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[3]).toBe("/admin/diseases/sync");
            });

            it("it specifies the correct content type option", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[0]).toBe(false); // don't send JSON
                expect(args[1]).toBe(false); // don't receive JSON
            });

            it("it specifies custom UI messages", function () {
                var args = baseSpy.calls.argsFor(0);
                expect(args[4].fail).toBe("Failed to synchronise the disease groups with all model wrapper instances.");
                expect(args[4].error).toBe("Failed to synchronise the disease groups with all model wrapper instances.");
                expect(args[4].success).toBe("Disease groups synchronised successfully with all model wrapper instances.");
            });
        });
    });
});
