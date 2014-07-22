/* A suite of tests for the DiseaseGroupsListViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define(["app/admin/diseasegroup/DiseaseGroupSetupViewModel"], function (DiseaseGroupSetupViewModel) {
    "use strict";

    describe("The 'disease group setup' view model", function () {
        var vm = {};
        beforeEach(function () {
            vm = new DiseaseGroupSetupViewModel("", "disease-group-selected");
        });

        describe("holds the last model run text which", function () {
            it("is an observable", function () {
                expect(vm.lastModelRunText).toBeObservable();
            });
        });

        describe("holds the disease occurrences text which", function () {
            it("is an observable", function () {
                expect(vm.lastModelRunText).toBeObservable();
            });
        });

        describe("holds whether or not the model can be run which", function () {
            it("is an observable", function () {
                expect(vm.canRunModel).toBeObservable();
            });
        });

        describe("holds whether or not the client is awaiting server response which", function () {
            it("is an observable", function () {
                expect(vm.working).toBeObservable();
            });
        });
    });
});
