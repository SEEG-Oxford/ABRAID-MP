/* A suite of tests for the DiseaseGroupsListViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/admin/diseasegroups/DiseaseGroupSetupViewModel",
    "ko"
], function (DiseaseGroupSetupViewModel, ko) {
    "use strict";

    describe("The 'disease group setup' view model", function () {
        var vm = {};
        var baseUrl = "";
        var selectedEventName = "selected";
        var savedEventName = "saved";
        beforeEach(function () {
            vm = new DiseaseGroupSetupViewModel(baseUrl, selectedEventName, savedEventName);
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

        describe("responds to the 'disease group saved' event by", function () {
            it("updating the expected parameters", function () {
                // Arrange
                var diseaseGroupId = 1;

                // Act
                ko.postbox.publish(selectedEventName, { id: diseaseGroupId });

                // Assert
                expect(vm.selectedDiseaseGroupId()).toBe(diseaseGroupId);
                expect(vm.working()).toBe(true);
                expect(vm.notices().length).toBe(0);
            });

            it("GETing from the expected URL", function () {
                // Arrange
                var id = 1;
                var diseaseGroup = { id: id };
                var expectedUrl = baseUrl + "admin/diseasegroups/" + id + "/modelruninformation";

                // Act
                ko.postbox.publish(selectedEventName, diseaseGroup);

                // Arrange
                expect(jasmine.Ajax.requests.mostRecent().url).toBe(expectedUrl);
                expect(jasmine.Ajax.requests.mostRecent().method).toBe("GET");
            });
        });
    });
});
