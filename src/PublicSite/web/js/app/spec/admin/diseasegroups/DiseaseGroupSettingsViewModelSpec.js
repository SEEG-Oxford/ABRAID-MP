/* A suite of tests for the DiseaseGroupSettingsViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/admin/diseasegroups/DiseaseGroupSettingsViewModel",
    "ko"
], function (DiseaseGroupSettingsViewModel, ko) {
    "use strict";

    describe("The 'disease group settings' view model", function () {
        describe("holds the expected properties of a disease group", function () {
            var diseaseGroups = [ { id: 1 }];
            var vm = new DiseaseGroupSettingsViewModel(diseaseGroups, [], "");
            it("as observables", function () {
                expect(vm.id).toBeObservable();
                expect(vm.name).toBeObservable();
                expect(vm.publicName).toBeObservable();
                expect(vm.shortName).toBeObservable();
                expect(vm.abbreviation).toBeObservable();
                expect(vm.selectedType).toBeObservable();
                expect(vm.isGlobal).toBeObservable();
                expect(vm.selectedParentDiseaseGroup).toBeObservable();
                expect(vm.selectedValidatorDiseaseGroup).toBeObservable();
            });

            it("with appropriate validation rules", function () {
                expect(vm.name).toHaveValidationRule({ name: "required", params: true });
                expect(vm.name).toHaveValidationRule({ name: "isUniqueProperty",
                    params: { array: diseaseGroups, property: "name", id: vm.id }});
                expect(vm.publicName).toHaveValidationRule({ name: "isUniqueProperty",
                    params: { array: diseaseGroups, property: "publicName", id: vm.id }});
                expect(vm.shortName).toHaveValidationRule({ name: "isUniqueProperty",
                    params: { array: diseaseGroups, property: "shortName", id: vm.id }});
                expect(vm.abbreviation).toHaveValidationRule({ name: "isUniqueProperty",
                    params: { array: diseaseGroups, property: "abbreviation", id: vm.id }});
            });
        });

        describe("holds the list of possible validator disease groups which", function () {
            it("takes the expected initial value", function () {
                //Arrange
                var validatorDiseaseGroups = [{ id: 1 }, { id: 2 }];
                //Act
                var vm = new DiseaseGroupSettingsViewModel([], validatorDiseaseGroups, "");
                //Assert
                expect(vm.validatorDiseaseGroups).toBe(validatorDiseaseGroups);
            });
        });

        describe("holds the list of possible parent disease groups which", function () {
            it("contains the list of microclusters when the selected disease group is a single disease", function () {
                // Arrange
                var microclusterDiseaseGroup1 = { groupType: "MICROCLUSTER" };
                var microclusterDiseaseGroup2 = { groupType: "MICROCLUSTER" };
                var diseaseGroups = [microclusterDiseaseGroup1, microclusterDiseaseGroup2];
                var vm = new DiseaseGroupSettingsViewModel(diseaseGroups, [], "");
                // Act
                vm.selectedType("SINGLE");
                // Assert
                expect(vm.parentDiseaseGroups()).toContain(microclusterDiseaseGroup1);
                expect(vm.parentDiseaseGroups()).toContain(microclusterDiseaseGroup2);
            });

            it("contains the list of clusters when the selected disease group is a microcluster", function () {
                // Arrange
                var clusterDiseaseGroup1 = { groupType: "CLUSTER" };
                var clusterDiseaseGroup2 = { groupType: "CLUSTER" };
                var diseaseGroups = [clusterDiseaseGroup1, clusterDiseaseGroup2];
                // Act
                var vm = new DiseaseGroupSettingsViewModel(diseaseGroups, [], "");
                vm.selectedType("MICROCLUSTER");
                // Assert
                expect(vm.parentDiseaseGroups()).toContain(clusterDiseaseGroup1);
                expect(vm.parentDiseaseGroups()).toContain(clusterDiseaseGroup2);
            });

            it("is an empty list when the selected disease group is a cluster", function () {
                // Arrange
                var microclusterDiseaseGroup = { groupType: "MICROCLUSTER" };
                var clusterDiseaseGroup = { groupType: "CLUSTER" };
                var diseaseGroups = [microclusterDiseaseGroup, clusterDiseaseGroup];
                // Act
                var vm = new DiseaseGroupSettingsViewModel(diseaseGroups, [], "");
                vm.selectedType("CLUSTER");
                // Assert
                expect(vm.parentDiseaseGroups()).toBeNull();
            });
        });

        describe("subscribes to the specified event and, when fired,", function () {
            // Arrange
            var parentDiseaseGroup = {id: 1, name: "Parent", groupType: "MICROCLUSTER" };
            var diseaseGroups = [parentDiseaseGroup];
            var validatorDiseaseGroup = { id: 2, name: "Validator"};
            var validatorDiseaseGroups = [validatorDiseaseGroup];
            var diseaseGroup = {
                name: "Ascariasis",
                publicName: "ascariasis",
                shortName: "ascariasis",
                abbreviation: "ascar",
                groupType: "SINGLE",
                isGlobal: false,
                parentDiseaseGroup: parentDiseaseGroup,
                validatorDiseaseGroup: validatorDiseaseGroup
            };
            var eventName = "event";
            var vm = new DiseaseGroupSettingsViewModel(diseaseGroups, validatorDiseaseGroups, eventName);

            // Act
            ko.postbox.publish(eventName, diseaseGroup);

            // Assert
            it("updates the disease group property fields", function () {
                expect(vm.name()).toBe(diseaseGroup.name);
                expect(vm.publicName()).toBe(diseaseGroup.publicName);
                expect(vm.shortName()).toBe(diseaseGroup.shortName);
                expect(vm.abbreviation()).toBe(diseaseGroup.abbreviation);
                expect(vm.selectedType()).toBe(diseaseGroup.groupType);
                expect(vm.isGlobal()).toBe(diseaseGroup.isGlobal);
            });

            it("finds the correct parent disease group in the list of options", function () {
                expect(vm.selectedParentDiseaseGroup()).toEqual(diseaseGroup.parentDiseaseGroup);
            });

            it("finds the correct validator disease group in the list of options", function () {
                expect(vm.selectedValidatorDiseaseGroup()).toEqual(diseaseGroup.validatorDiseaseGroup);
            });
        });
    });
});
