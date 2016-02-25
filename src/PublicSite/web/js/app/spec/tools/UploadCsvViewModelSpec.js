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
            vm = new UploadCsvViewModel("baseUrl", [
                { id: 1, name: "dengue" },
                { id: 2, name: "not dengue" }
            ]);
        });

        describe("holds an 'isGoldStandard' field which", function () {
            it("is observable", function () {
                expect(vm.isGoldStandard).toBeObservable();
            });

            it("starts false", function () {
                expect(vm.isGoldStandard()).toBe(false);
            });
        });

        describe("holds an 'isBias' field which", function () {
            it("is observable", function () {
                expect(vm.isBias).toBeObservable();
            });

            it("starts false", function () {
                expect(vm.isBias()).toBe(false);
            });
        });

        describe("holds the selected disease group which", function () {
            it("is an observable", function () {
                expect(vm.selectedDiseaseGroup).toBeObservable();
            });

            it("is initially the first item in the disease groups list", function () {
                expect(vm.selectedDiseaseGroup()).toEqual({
                    id: 1,
                    name: "dengue"
                });
            });
        });

        describe("holds the list of disease groups which", function () {
            it("starts with the same disease groups as passed to the constructor", function () {
                expect(vm.diseaseGroups.length).toBe(2);
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

                injector.require(["app/tools/UploadCsvViewModel"],
                    function (UploadCsvViewModel) {
                        vm = new UploadCsvViewModel("baseUrl", [ { id: 1, name: "dengue" } ]);
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
                vm.isBias(true);
                vm.selectedDiseaseGroup(vm.diseaseGroups[1]);
                expect(vm.buildSubmissionData()).toEqual({
                    isGoldStandard: true,
                    isBias: true,
                    diseaseGroup: 2
                });
            });
        });
    });
});
