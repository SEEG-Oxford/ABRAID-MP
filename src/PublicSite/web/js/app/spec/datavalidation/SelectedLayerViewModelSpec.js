/* A suite of tests for the SelectedLayerViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/datavalidation/SelectedLayerViewModel",
    "ko",
    "underscore"
], function (SelectedLayerViewModel, ko, _) {
    "use strict";

    describe("The 'selected layer' view model", function () {
        var diseaseInterests = [ { name: "dengue", diseaseGroups: [ { id: 87, name: "dengue" } ] } ];
        var vm = {};
        beforeEach(function () {
            vm = new SelectedLayerViewModel(diseaseInterests, [], [], []);
        });

        describe("holds the validation type which", function () {
            it("is an observable", function () {
                expect(vm.selectedType).toBeObservable();
            });

            it("is initially 'disease extent', if there are no diseases requiring input", function () {
                expect(vm.selectedType()).toBe("disease extent");
            });

            it("is initially 'disease extent', if there are only diseases requiring extent input", function () {
                var vm1 = new SelectedLayerViewModel(diseaseInterests, [], [87], []);
                expect(vm1.selectedType()).toBe("disease extent");
            });

            it("is initially 'disease extent', if there are diseases requiring both types of input", function () {
                var vm1 = new SelectedLayerViewModel(diseaseInterests, [], [87], [87]);
                expect(vm1.selectedType()).toBe("disease extent");
            });


            it("is initially 'disease occurrences', if there are only diseases requiring occurrence input", function () {
                var vm1 = new SelectedLayerViewModel(diseaseInterests, [], [], [87]);
                expect(vm1.selectedType()).toBe("disease occurrences");
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
                it("the first disease group from the 'Disease Interests' list which needs reviews, if the list exists",
                    function () {
                        var diseaseInterests = [
                            { id: 1, name: "a", diseaseGroups: [ { id: 2, name: "b" }, { id: 3, name: "c" }] },
                            { id: 4, name: "d", diseaseGroups: [ { id: 5, name: "e" }, { id: 6, name: "f" }] }
                        ];
                        var allOtherDiseases = [];
                        vm = new SelectedLayerViewModel(diseaseInterests, allOtherDiseases, [5], []);
                        expect(vm.selectedDiseaseSet().id).toBe(4);
                    }
                );

                it("or the first disease group from the 'Other Diseases' list which needs reviews, otherwise",
                    function () {
                        var allOtherDiseases = [
                            { id: 1, name: "a", diseaseGroups: [ { id: 2, name: "b" }, { id: 3, name: "c" }] },
                            { id: 4, name: "d", diseaseGroups: [ { id: 5, name: "e" }, { id: 6, name: "f" }] }
                        ];
                        var diseaseInterests = [];
                        vm = new SelectedLayerViewModel(diseaseInterests, allOtherDiseases, [5], []);
                        expect(vm.selectedDiseaseSet().id).toBe(4);
                    }
                );
                it("the first disease group from the 'Disease Interests' if non-need reviews, and the list exists",
                    function () {
                        var diseaseInterests = [
                            { id: 1, name: "a", diseaseGroups: [ { id: 2, name: "b" }, { id: 3, name: "c" }] },
                            { id: 4, name: "d", diseaseGroups: [ { id: 5, name: "e" }, { id: 6, name: "f" }] }
                        ];
                        var allOtherDiseases = [];
                        vm = new SelectedLayerViewModel(diseaseInterests, allOtherDiseases, [], []);
                        expect(vm.selectedDiseaseSet().id).toBe(1);
                    }
                );

                it("or the first disease group from the 'Other Diseases'  if non-need reviews, otherwise",
                    function () {
                        var allOtherDiseases = [
                            { id: 1, name: "a", diseaseGroups: [ { id: 2, name: "b" }, { id: 3, name: "c" }] },
                            { id: 4, name: "d", diseaseGroups: [ { id: 5, name: "e" }, { id: 6, name: "f" }] }
                        ];
                        var diseaseInterests = [];
                        vm = new SelectedLayerViewModel(diseaseInterests, allOtherDiseases, [], []);
                        expect(vm.selectedDiseaseSet().id).toBe(1);
                    }
                );
            });

            it("fires the 'layers-changed' event when its value changes, for 'disease occurrences'", function () {
                // Arrange
                var diseaseId = 0;
                var diseaseName = "abc";
                var newDiseaseSet = { id: diseaseId, name: diseaseName, diseaseGroups: [{ id: 1, name: "abc" }] };
                vm.selectedType("disease occurrences");

                // Arrange assertions
                var subscription = ko.postbox.subscribe("layers-changed", function (value) {
                    expect(value.diseaseId).toBe(diseaseId);
                    expect(value.diseaseName).toBe(diseaseName);
                });

                // Act
                vm.selectedDiseaseSet(newDiseaseSet);
                subscription.dispose();
            });

            it("updates the subdisease, to the first needing review, when changed", function () {
                // Arrange
                var diseaseId = 0;
                var diseaseName = "abc";
                var vm1 = new SelectedLayerViewModel(diseaseInterests, [], [87, 2], []);
                var newDiseaseSet = {
                    id: diseaseId,
                    name: diseaseName,
                    diseaseGroups: [{ id: 1, name: "abc" }, { id: 2, name: "abcd" }]
                };

                // Act
                vm1.selectedDiseaseSet(newDiseaseSet);

                // Assert
                expect(vm1.selectedDisease().id).toBe(2);

            });

            it("fires the 'layers-changed' event when its value changes, for 'disease extent'", function () {
                // Arrange
                var diseaseId = 0;
                var subdiseaseId = 1;
                var subdiseaseName = "abc";
                var diseaseName = "name";
                var newDiseaseSet =
                    { id: diseaseId, name: diseaseName, diseaseGroups: [{ id: subdiseaseId, name: subdiseaseName }] };
                vm.selectedType("disease extent");

                // Arrange assertions
                var subscription = ko.postbox.subscribe("layers-changed", function (value) {
                    expect(value.diseaseId).toBe(subdiseaseId);
                    expect(value.diseaseName).toBe(subdiseaseName);
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
                    var diseaseName = "abc";
                    var newDisease = { id: diseaseId, name: diseaseName };
                    vm.selectedType("disease extent");

                    // Arrange assertions
                    var subscription = ko.postbox.subscribe("layers-changed", function (value) {
                        expect(value.diseaseId).toBe(diseaseId);
                        expect(value.diseaseName).toBe(diseaseName);
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
