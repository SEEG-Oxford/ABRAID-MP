/* A suite of tests for the ModelRunParametersViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "underscore",
    "app/ModelRunParametersViewModel"
], function (ko, _, ModelRunParametersViewModel) {
    "use strict";

    describe("The 'model run parameters' view model", function () {
        it("holds the expected properties of a disease group as observables", function () {
            var vm = new ModelRunParametersViewModel("", "");
            expect(vm.minNewOccurrences).toBeObservable();
            expect(vm.minDataVolume).toBeObservable();
            expect(vm.minDistinctCountries).toBeObservable();
            expect(vm.minHighFrequencyCountries).toBeObservable();
            expect(vm.highFrequencyThreshold).toBeObservable();
            expect(vm.occursInAfrica).toBeObservable();
        });

        describe("holds the 'save changes' function which", function () {
            it("POSTs to the specified URL, with the correct parameters", function () {
                // Arrange
                var baseUrl = "base/";
                var eventName = "foo";

                var diseaseGroupId = 1;
                var minNewOccurences = 1;
                var diseaseGroup = {
                    id: diseaseGroupId,
                    minNewOccurrences: minNewOccurences
                };

                var vm = new ModelRunParametersViewModel(baseUrl, eventName);
                var expectedUrl = baseUrl + "admin/diseasegroup/" + diseaseGroupId + "/modelrunparameters";

                var expectedParams = "minNewOccurrences=" + minNewOccurences;

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
                var vm = new ModelRunParametersViewModel("", "");
                var expectedNotice = { message: "Error saving", priority: "warning" };
                // Act
                vm.save();
                jasmine.Ajax.requests.mostRecent().response({ status: 500 });
                // Assert
                expect(_.isEqual(vm.notice(), expectedNotice)).toBeTruthy();
            });

            it("when successful, updates the 'notice' with a success", function () {
                // Arrange
                var vm = new ModelRunParametersViewModel("", "");
                var expectedNotice = { message: "Saved successfully", priority: "success" };
                // Act
                vm.save();
                jasmine.Ajax.requests.mostRecent().response({ status: 204 });
                // Assert
                expect(_.isEqual(vm.notice(), expectedNotice)).toBeTruthy();
            });
        });

        it("holds the 'enable save button' parameter which detects when any values have changed", function () {
            // Arrange
            var eventName = "foo";
            var diseaseGroup = { minNewOccurrences: 1 };
            var vm = new ModelRunParametersViewModel("", eventName);
            ko.postbox.publish(eventName, diseaseGroup);
            expect(vm.enableSaveButton()).toBe(false);
            // Act
            vm.minNewOccurrences(2);
            // Assert
            expect(vm.enableSaveButton()).toBe(true);
        });
    });
});
