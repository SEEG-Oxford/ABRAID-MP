/* A suite of tests for the DiseaseGroupSettingsViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/DiseaseGroupSettingsViewModel",
    "ko",
    "underscore"
], function (DiseaseGroupSettingsViewModel, ko, _) {
    "use strict";

    describe("The 'disease group settings' view model", function () {
        it("holds the expected properties of a disease group as observables", function () {
            var vm = new DiseaseGroupSettingsViewModel("", [], [], "");
            expect(vm.name).toBeObservable();
            expect(vm.publicName).toBeObservable();
            expect(vm.shortName).toBeObservable();
            expect(vm.abbreviation).toBeObservable();
            expect(vm.selectedType).toBeObservable();
            expect(vm.isGlobal).toBeObservable();
            expect(vm.selectedParentDiseaseGroup).toBeObservable();
            expect(vm.selectedValidatorDiseaseGroup).toBeObservable();
        });

        describe("holds the list of possible validator disease groups which", function () {
            it("takes the expected initial value", function () {
                //Arrange
                var validatorDiseaseGroups = [{ id: 1 }, { id: 2 }];
                //Act
                var vm = new DiseaseGroupSettingsViewModel("", [], validatorDiseaseGroups, "");
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
                var vm = new DiseaseGroupSettingsViewModel("", diseaseGroups, [], "");
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
                var vm = new DiseaseGroupSettingsViewModel("", diseaseGroups, [], "");
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
                var vm = new DiseaseGroupSettingsViewModel("", diseaseGroups, [], "");
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
            var eventName = "disease-group-selected";
            var vm = new DiseaseGroupSettingsViewModel("", diseaseGroups, validatorDiseaseGroups, eventName);

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
                expect(_.isEqual(vm.selectedParentDiseaseGroup(), diseaseGroup.parentDiseaseGroup)).toBe(true);
            });

            it("finds the correct validator disease group in the list of options", function () {
                expect(_.isEqual(vm.selectedValidatorDiseaseGroup(), diseaseGroup.validatorDiseaseGroup)).toBe(true);
            });
        });

        describe("holds the 'save changes' function which", function () {
            it("POSTs to the specified URL, with the correct parameters", function () {
                // Arrange
                var baseUrl = "base/";
                var eventName = "foo";

                var diseaseGroupId = 1;
                var name = "name";
                var groupType = "SINGLE";
                var parentId = 2;
                var parentDiseaseGroup = { id: parentId, groupType: "MICROCLUSTER" };
                var diseaseGroups = [parentDiseaseGroup];
                var validatorId = 3;
                var validatorDiseaseGroup = { id: validatorId };
                var validatorDiseaseGroups = [validatorDiseaseGroup];
                var diseaseGroup = {
                    id: 1,
                    name: name,
                    groupType: "SINGLE",
                    parentDiseaseGroup: parentDiseaseGroup,
                    validatorDiseaseGroup: validatorDiseaseGroup
                };

                var vm = new DiseaseGroupSettingsViewModel(baseUrl, diseaseGroups, validatorDiseaseGroups, eventName);
                var expectedUrl = baseUrl + "admin/diseasegroup/" + diseaseGroupId + "/settings";

                var expectedParams = "name=" + name + "&groupType=" + groupType +
                    "&parentDiseaseGroupId=" + parentId + "&validatorDiseaseGroupId=" + validatorId;

                // Act
                ko.postbox.publish(eventName, diseaseGroup);
                vm.save();

                // Assert
                expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                expect(jasmine.Ajax.requests.mostRecent().params).toBe(expectedParams);
                expect(jasmine.Ajax.requests.mostRecent().method).toBe("POST");
            });

            it("when unsuccessful, updates the 'notice' with an error", function () {
                // Arrange
                var vm = new DiseaseGroupSettingsViewModel("", [], [], "");
                var expectedNotice = { message: "Error saving", priority: "warning" };
                // Act
                vm.save();
                jasmine.Ajax.requests.mostRecent().response({ status: 500 });
                // Assert
                expect(_.isEqual(vm.notice(), expectedNotice)).toBeTruthy();
            });

            it("when successful, updates the 'notice' with a success", function () {
                // Arrange
                var vm = new DiseaseGroupSettingsViewModel("", [], [], "");
                var expectedNotice = { message: "Saved successfully", priority: "success" };
                // Act
                vm.save();
                jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                // Assert
                expect(_.isEqual(vm.notice(), expectedNotice)).toBeTruthy();
            });
        });
    });
});
