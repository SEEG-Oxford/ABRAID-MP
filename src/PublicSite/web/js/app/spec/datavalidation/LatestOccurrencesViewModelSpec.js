/* A suite of tests for the LatestOccurrencesViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/datavalidation/LatestOccurrencesViewModel",
    "ko"
], function (LatestOccurrencesViewModel, ko) {
    "use strict";

    describe("The 'latest occurrences' view model", function () {

        describe("holds the count which", function () {
            var vm = new LatestOccurrencesViewModel();
            it("is an observable", function () {
                expect(vm.count).toBeObservable();
            });

            it("takes the expected initial value", function () {
                expect(vm.count()).toBe(0);
            });

            it("sets its value when the 'admin-unit-selected' event is fired", function () {
                // Arrange
                var expectedCount = 3;
                var data = { count: expectedCount };
                // Act
                ko.postbox.publish("admin-unit-selected", data);
                // Assert
                expect(vm.count()).toBe(expectedCount);
            });
        });

        describe("holds the list of occurrences which", function () {
            it("is an observable", function () {
                var vm = new LatestOccurrencesViewModel();
                expect(vm.occurrences).toBeObservable();
            });

            it("takes the expected initial value", function () {
                var vm = new LatestOccurrencesViewModel();
                expect(vm.occurrences()).toEqual([]);
            });

            it("sets its value when the 'admin-unit-selected' event is fired", function () {
                // Arrange
                var occurrences = [1, 2, 3];
                var data = { occurrences: occurrences };
                var vm = new LatestOccurrencesViewModel();
                // Act
                ko.postbox.publish("admin-unit-selected", data);
                // Assert
                expect(vm.occurrences().length).toBe(3);
                expect(vm.occurrences()).toBe(occurrences);
            });
        });

        describe("holds a boolean to indicate whether to show the list of occurrences which", function () {
            var vm = new LatestOccurrencesViewModel();
            it("is an observable", function () {
                expect(vm.showOccurrences).toBeObservable();
            });

            it("defaults to true", function () {
                expect(vm.showOccurrences()).toBe(true);
            });

            it("swaps its value to false via the 'toggle' function", function () {
                // Arrange
                vm.showOccurrences(true);
                // Act
                vm.toggle();
                // Assert
                expect(vm.showOccurrences()).toBe(false);
            });

            it("swaps its value to true via the 'toggle' function", function () {
                // Arrange
                vm.showOccurrences(false);
                // Act
                vm.toggle();
                // Assert
                expect(vm.showOccurrences()).toBe(true);
            });
        });
    });
});
