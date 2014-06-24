/* A suite of tests for the SelectedLayerViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/SelectedLayerViewModel",
    "ko",
    "underscore"
], function (SelectedLayerViewModel, ko, _) {
    "use strict";

    describe("The 'selected layer' view model", function () {
        var diseaseInterests = [ { name: "dengue", diseaseGroups: [ { name: "dengue" } ] } ];
        var vm = {};
        beforeEach(function () {
            vm = new SelectedLayerViewModel(diseaseInterests, []);
        });

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
                expect(vm.groups[0].groupLabel).toEqual("Your Disease Interests");
            });

            it("contains 'Other Diseases'", function () {
                expect(_(vm.groups).findWhere({groupLabel: "Other Diseases"})).toBeDefined();
            });
        });

        describe("holds the validator disease group which", function () {
            it("is an observable", function () {
                expect(vm.selectedDiseaseSet).toBeObservable();
            });

            describe("is initially", function () {
                it("the first disease group from the 'Disease Interests' list, if the list exists", function () {
                    expect(vm.selectedDiseaseSet()).toBe(diseaseInterests[0]);
                });

                it("or the first disease group from the 'Other Diseases' list, otherwise", function () {
                    var diseaseInterests = [];
                    var allOtherDiseases = [ {name: "ascariasis", diseaseGroups: [ { name: "ascariasis" } ] } ];
                    vm = new SelectedLayerViewModel(diseaseInterests, allOtherDiseases);

                    expect(vm.selectedDiseaseSet()).toBe(allOtherDiseases[0]);
                });
            });

            it("fires the 'layers-changed' event when its value changes, for 'disease occurrences'", function () {
                // Arrange
                var diseaseId = 0;
                var newDiseaseSet = { id: diseaseId };
                vm.selectedType("disease occurrences");

                // Arrange assertions
                var subscription = ko.postbox.subscribe("layers-changed", function (value) {
                    expect(value.diseaseId).toBe(diseaseId);
                });

                // Act
                vm.selectedDiseaseSet(newDiseaseSet);
                subscription.dispose();
            });

            it("does not fire the 'layers-changed' event when its value changes, for 'disease extent'", function () {
                // Arrange
                var diseaseId = 0;
                var newDiseaseSet = { id: diseaseId };
                vm.selectedType("disease extent");

                // Arrange assertions
                var subscription = ko.postbox.subscribe("layers-changed", function () {
                    expect().toNotExecuteThisLine("The 'layers-changed' event should not be fired here.");
                });

                // Act
                vm.selectedDiseaseSet(newDiseaseSet);
                subscription.dispose();
            });
        });

        describe("holds the (child) disease group which", function () {
            it("is an observable", function () {
                expect(vm.selectedDisease).toBeObservable();
            });

            describe("fires the 'layers-changed' event when its value changes", function () {
                it("for 'disease extent'", function () {
                    // Arrange
                    var diseaseId = 0;
                    var newDisease = { id: diseaseId };
                    vm.selectedType("disease extent");

                    // Arrange assertions
                    var subscription = ko.postbox.subscribe("layers-changed", function (value) {
                        expect(value.diseaseId).toBe(diseaseId);
                    });

                    // Act
                    vm.selectedDisease(newDisease);
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
