/* A suite of tests for the DiseaseGroupsListViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define(["app/DiseaseGroupsListViewModel"], function (DiseaseGroupsListViewModel) {
    "use strict";

    describe("The 'disease groups list' view model", function () {
        var dengue = { id: 87, name: "dengue" };
        var malarias = { id: 202, name: "malarias" };
        var initialData = [ dengue, malarias ];
        var vm = {};
        beforeEach(function () {
            vm = new DiseaseGroupsListViewModel("", initialData);
        });

        describe("holds the selected disease which", function () {
            it("is an observable", function () {
                expect(vm.selectedDisease).toBeObservable();
            });

            it("is initially 'disease occurrences'", function () {
                expect(vm.selectedDisease()).toBe(dengue);
            });
        });

        describe("holds the list of disease groups which", function () {
            it("is an observable", function () {
                expect(vm.diseases).toBeObservable();
            });

            it("has two groups", function () {
                expect(vm.diseases().length).toBe(2);
            });
        });
    });
});
