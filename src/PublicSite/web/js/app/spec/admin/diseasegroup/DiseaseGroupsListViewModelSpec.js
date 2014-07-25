/* A suite of tests for the DiseaseGroupsListViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define(["" +
    "app/admin/diseasegroup/DiseaseGroupsListViewModel",
    "underscore"
], function (DiseaseGroupsListViewModel, _) {
    "use strict";

    describe("The 'disease groups list' view model", function () {
        var dengue = { id: 87, name: "dengue" };
        var malarias = { id: 202, name: "malarias" };
        var initialData = [ dengue, malarias ];

        describe("holds the selected disease group which", function () {
            it("is an observable", function () {
                var vm = new DiseaseGroupsListViewModel([], "");
                expect(vm.selectedDiseaseGroup).toBeObservable();
            });

            it("is initially the first item in the disease groups list", function () {
                var vm = new DiseaseGroupsListViewModel(initialData, "");
                expect(vm.selectedDiseaseGroup()).toBe(dengue);
            });
        });

        describe("holds the list of disease groups which", function () {
            it("is an observable", function () {
                var vm = new DiseaseGroupsListViewModel([], "");
                expect(vm.diseaseGroups).toBeObservable();
            });

            it("starts with the same disease groups as passed to the constructor", function () {
                var vm = new DiseaseGroupsListViewModel(initialData, "");
                expect(vm.diseaseGroups().length).toBe(2);
            });
        });

        it("holds the 'add' method which sets the selected disease group to an empty disease group", function () {
            // Arrange
            var vm = new DiseaseGroupsListViewModel([], "");
            var expectedDiseaseGroup = { name: "", groupType: "SINGLE" };
            // Act
            vm.add();
            // Assert
            expect(_.isEqual(vm.selectedDiseaseGroup(), expectedDiseaseGroup)).toBe(true);
        });
    });
});
