/* A suite of tests for the DiseaseGroupsListViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define(["app/admin/diseasegroup/DiseaseGroupsListViewModel"], function (DiseaseGroupsListViewModel) {
    "use strict";

    describe("The 'disease groups list' view model", function () {
        var dengue = { id: 87, name: "dengue" };
        var malarias = { id: 202, name: "malarias" };
        var initialData = [ dengue, malarias ];
        var vm = new DiseaseGroupsListViewModel(initialData, "disease-group-selected");

        describe("holds the selected disease group which", function () {
            it("is an observable", function () {
                expect(vm.selectedDiseaseGroup).toBeObservable();
            });

            it("is initially the first item in the disease groups list", function () {
                expect(vm.selectedDiseaseGroup()).toBe(dengue);
            });
        });

        describe("holds the list of disease groups which", function () {
            it("is an observable", function () {
                expect(vm.diseaseGroups).toBeObservable();
            });

            it("start with the same disease groups as passed to the constructor", function () {
                expect(vm.diseaseGroups().length).toBe(2);
            });
        });
    });
});
