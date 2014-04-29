/* A suite of tests for the CounterViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/CounterViewModel",
    "ko",
    "app/spec/util/observableMatcher"
], function (CounterViewModel, ko, observableMatcher) {
    "use strict";

    describe("The counter view model", function () {
        var addCustomMatchers = function () {
            jasmine.addMatchers({ toBeObservable: observableMatcher });
        };

        beforeEach(addCustomMatchers);

        describe("holds the count which", function () {

            it("is an observable", function () {
                var vm = new CounterViewModel(0);
                expect(vm.count).toBeObservable();
            });

            it("takes the expected initial value", function () {
                // Arrange
                var expectedCount = 0;
                // Act
                var vm = new CounterViewModel(expectedCount);
                // Assert
                expect(vm.count()).toBe(expectedCount);
            });

            it("increments its value when the 'point-reviewed' event is fired", function () {
                // Arrange
                var initialCount = 0;
                var vm = new CounterViewModel(initialCount);
                // Act
                ko.postbox.publish("point-reviewed");
                // Assert
                var expectedCount = initialCount + 1;
                expect(vm.count()).toBe(expectedCount);
            });
        });
    });
});