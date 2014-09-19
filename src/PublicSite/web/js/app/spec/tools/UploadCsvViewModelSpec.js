/* Tests for UploadCsvViewModel.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/tools/UploadCsvViewModel",
    "ko",
    "squire"
], function (UploadCsvViewModel, ko, Squire) {
    "use strict";

    describe("The 'upload CSV' view model", function () {
        var vm;

        beforeEach(function () {
            vm = new UploadCsvViewModel("baseUrl");
        });

        describe("holds an 'isGoldStandard' field which", function () {
            it("is observable", function () {
                expect(vm.isGoldStandard).toBeObservable();
            });

            it("starts false", function () {
                expect(vm.isGoldStandard()).toBe(false);
            });
        });

        describe("is an extension of BaseFileFormViewModel, with", function () {
            it("the correct constructor arguments", function (done) {
                jasmine.Ajax.uninstall();
                var injector = new Squire();
                var baseSpy = jasmine.createSpy("baseSpy").and.callFake(function () {
                    this.file = function () { return undefined; };
                });
                injector.mock("shared/app/BaseFileFormViewModel", baseSpy);

                injector.require(["app/tools/UploadCSVViewModel"],
                    function (UploadCSVViewModel) {
                        vm = new UploadCSVViewModel("baseUrl");
                        expect(baseSpy.calls.argsFor(0)[0]).toBe("baseUrl");
                        expect(baseSpy.calls.argsFor(0)[1]).toBe("tools/uploadcsv/upload");
                        expect(baseSpy.calls.argsFor(0)[2].success).toBe(
                            "CSV file submitted. The results of the upload will be e-mailed to you.");
                        done();
                    }
                );
            });

            it("a custom buildSubmissionData function, which builds the correct data", function () {
                vm.isGoldStandard(true);
                expect(vm.buildSubmissionData()).toEqual({
                    isGoldStandard: true
                });
            });
        });
    });
});