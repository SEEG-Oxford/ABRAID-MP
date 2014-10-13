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
            it("is an observable", function () {
                var vm = new LatestOccurrencesViewModel();
                expect(vm.count).toBeObservable();
            });

            it("takes the expected initial value", function () {
                // Arrange
                var expectedCount = 0;
                // Act
                var vm = new LatestOccurrencesViewModel();
                // Assert
                expect(vm.count()).toBe(expectedCount);
            });

            it("sets its value when the 'admin-unit-selected' event is fired", function () {
                // Arrange
                var expectedCount = 3;
                var data = { count: expectedCount };
                var vm = new LatestOccurrencesViewModel();
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
                // Arrange
                // Act
                var vm = new LatestOccurrencesViewModel();
                // Assert
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
    });
});
