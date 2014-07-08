/* A suite of tests for the SpinnerViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/SpinnerViewModel",
    "ko"
], function (SpinnerViewModel, ko) {
    "use strict";

    describe("The spinner view model", function () {

        describe("holds the visible value which", function () {
            it("is an observable", function () {
                var vm = new SpinnerViewModel();
                expect(vm.visible).toBeObservable();
            });

            it("has the expected initial value", function () {
                // Arrange
                var expectedValue = false;
                // Act
                var vm = new SpinnerViewModel();
                // Assert
                expect(vm.visible()).toBe(expectedValue);
            });

            describe("reacts to the 'update-map-view-in-progress' event", function () {
                var vm = new SpinnerViewModel();

                it("when true", function () {
                    vm.visible(false);
                    ko.postbox.publish("map-view-update-in-progress", true);
                    expect(vm.visible()).toBe(true);
                });

                it("when false", function () {
                    vm.visible(true);
                    ko.postbox.publish("map-view-update-in-progress", false);
                    expect(vm.visible()).toBe(false);
                });
            });
        });
    });
});
