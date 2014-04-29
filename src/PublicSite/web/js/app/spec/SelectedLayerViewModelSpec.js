/* A suite of tests for the SelectedLayerViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/SelectedLayerViewModel",
    "ko",
    "underscore",
    "app/spec/util/observableMatcher",
    "app/spec/util/forceFailureMatcher"
], function (SelectedLayerViewModel, ko, _, observableMatcher, forceFailureMatcher) {
    "use strict";

    describe("The selected layer view model", function () {
        var addCustomMatchers = function () {
            jasmine.addMatchers({ toBeObservable: observableMatcher });
            jasmine.addMatchers({ thisLineToNotExecute: forceFailureMatcher });
        };

        var diseaseInterests = [ { name: "dengue", diseaseGroups: [ { name: "dengue" } ] } ];
        var vm = new SelectedLayerViewModel(diseaseInterests, []);
        beforeEach(addCustomMatchers);

        describe("holds the validation type which", function () {

            it("is an observable", function () {
                expect(vm.selectedType).toBeObservable();
            });

            it("is initially 'disease occurrences'", function () {
                expect(vm.selectedType()).toBe("disease occurrences");
            });

            it("fires the 'layers-changed' event when its value changes", function () {
                // Arrange
                var expectation = "foo";
                // Arrange assertions
                var subscription = ko.postbox.subscribe("layers-changed", function (value) {
                    expect(value.type).toBe(expectation);
                });
                // Act
                vm.selectedType(expectation);
                subscription.dispose();     // Stop subscribing
            });
        });

        describe("holds the lists of diseases which", function () {

            it("has two groups", function () {
                expect(vm.groups.length).toBe(2);
            });

            it("contains 'Your Disease Interests'", function () {
                expect(_(vm.groups).findWhere({groupLabel: "Your Disease Interests"})).toBeDefined();
            });

            it("contains 'Other Diseases'", function () {
                expect(_(vm.groups).findWhere({groupLabel: "Other Diseases"})).toBeDefined();
            });
        });

        describe("holds the validator disease group which", function () {

            it("is an observable", function () {
                expect(vm.selectedDiseaseSet).toBeObservable();
            });

            it("is initially the first disease interest", function () {
                expect(vm.selectedDiseaseSet()).toBe(diseaseInterests[0]);
            });

            it("fires the 'layers-changed' event when its value changes, for 'disease occurrences'", function () {
                // Arrange
                var expectation = "foo";
                vm.selectedType("disease occurrences");

                // Arrange assertions
                var subscription = ko.postbox.subscribe("layers-changed", function (value) {
                    expect(value.diseaseSet).toBe(expectation);
                });

                // Act
                vm.selectedDiseaseSet(expectation);
                subscription.dispose();
            });

            it("does not fire the 'layers-changed' event when its value changes, for 'disease extent'", function () {
                // Arrange
                var expectation = "foo";
                vm.selectedType("disease extent");

                // Arrange assertions
                var subscription = ko.postbox.subscribe("layers-changed", function () {
                    expect().thisLineToNotExecute("The 'layers-changed' event should not be fired here.");
                });

                // Act
                vm.selectedDiseaseSet(expectation);
                subscription.dispose();
            });
        });

        describe("holds the (child) disease group which", function () {

            it("is an observable", function () {
                expect(vm.selectedDisease).toBeObservable();
            });

            describe("fires the 'layers-changed' event when its value changes", function () {

                it("for 'disease occurrences'", function () {
                    // Arrange
                    var expectation = "foo";
                    vm.selectedType("disease occurrences");

                    // Arrange assertions
                    var subscription = ko.postbox.subscribe("layers-changed", function (value) {
                        expect(value.disease).toBe(expectation);
                    });

                    // Act
                    vm.selectedDisease(expectation);
                    subscription.dispose();
                });

                it("for 'disease extent'", function () {
                    // Arrange
                    var expectation = "foo";
                    vm.selectedType("disease extent");

                    // Arrange assertions
                    var subscription = ko.postbox.subscribe("layers-changed", function (value) {
                        expect(value.disease).toBe(expectation);
                    });

                    // Act
                    vm.selectedDisease(expectation);
                    subscription.dispose();
                });
            });
        });

        describe("holds a field indicating whether there are features for review which", function () {

            it("is an observable", function () {
                expect(vm.noFeaturesToReview).toBeObservable();
            });

            describe("reacts to the 'no-features-to-review' event", function () {

                it("when true", function () {
                    ko.postbox.publish("no-features-to-review", true);
                    expect(vm.noFeaturesToReview()).toBe(true);
                });

                it("when false", function () {
                    ko.postbox.publish("no-features-to-review", false);
                    expect(vm.noFeaturesToReview()).toBe(false);
                });
            });
        });
    });
});
