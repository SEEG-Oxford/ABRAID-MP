/* A suite of tests for the atlas LayerSelectorViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/atlas/LayerSelectorViewModel",
    "ko",
    "underscore"
], function (LayerSelectorViewModel, ko, _) {
    "use strict";

    describe("The atlas 'layer selector' view model", function () {
        var run2 = { date: "1995-10-09", id: "Model Run 2", covariates: "covariates2", statistics: "statistics2" };
        var run1 = { date: "2014-10-13", id: "Model Run 1", covariates: "covariates1", statistics: "statistics1" };
        var run3 = { date: "2036-12-18", id: "Model Run 3", covariates: "covariates3", statistics: "statistics3" };
        var availableLayers = [
            {
                disease: "Disease Group 2",
                runs: [ run2 ]
            }, {
                disease: "Disease Group 1",
                runs: [ run1, run3 ]
            }
        ];
        var meanType = { display: "disease risk", id: "mean" };
        var uncertaintyType = { display: "risk uncertainty", id: "uncertainty" };
        var extentType = { display: "disease extent", id: "extent" };
        var vm = {};
        beforeEach(function () {
            vm = new LayerSelectorViewModel(availableLayers);
        });

        describe("holds a field for the available layer types which", function () {
            it("has three entries", function () {
                expect(vm.types.length).toBe(3);
            });

            it("has an entry for mean", function () {
                expect(vm.types).toContain(meanType);
            });

            it("has an entry for uncertainty", function () {
                expect(vm.types).toContain(uncertaintyType);
            });

            it("has an entry for extent", function () {
                expect(vm.types).toContain(extentType);
            });
        });

        describe("holds a field for the selected layer type which", function () {
            it("is observable", function () {
                expect(vm.selectedType).toBeObservable();
            });

            it("defaults to mean", function () {
                expect(vm.selectedType()).toEqual(meanType);
            });

            it("publishes changes to its state as 'active-atlas-type'", function () {
                var expectedValue = "";
                var eventCount = 0;
                ko.postbox.subscribe("active-atlas-type", function (payload) {
                    expect(payload).toEqual(expectedValue);
                    eventCount = eventCount + 1;
                });

                expectedValue = uncertaintyType.id;
                vm.selectedType(vm.types[1]);
                expectedValue = extentType.id;
                vm.selectedType(vm.types[2]);
                expectedValue = meanType.id;
                vm.selectedType(vm.types[0]);

                expect(eventCount).toBe(3);

                ko.postbox._subscriptions["active-atlas-type"] = [];  // jshint ignore:line
            });
        });

        describe("holds a field for the available diseases which", function () {
            it("has an entry for each of the diseases in the available layers", function () {
                expect(vm.diseases.length).toBe(2);
                expect(_(vm.diseases).pluck("disease")).toContain("Disease Group 1");
                expect(_(vm.diseases).pluck("disease")).toContain("Disease Group 2");
            });

            it("is sorted alphabetically", function () {
                expect(vm.diseases[0].disease).toBe("Disease Group 1");
                expect(vm.diseases[1].disease).toBe("Disease Group 2");
            });
        });

        describe("holds a field for the selected disease which", function () {
            it("is observable", function () {
                expect(vm.selectedDisease).toBeObservable();
            });

            it("defaults to the first disease alphabetically in the available layers", function () {
                expect(vm.selectedDisease().disease).toBe("Disease Group 1");
            });

            it("defaults to '---' if no available layers are provided", function () {
                var localVM = new LayerSelectorViewModel([]);
                expect(localVM.selectedDisease().disease).toBe("---");
            });
        });

        describe("holds a field for the available runs which", function () {
            it("is observable", function () {
                expect(vm.runs).toBeObservable();
            });

            it("has an entry for each of the runs in the available layers for the current disease", function () {
                expect(vm.runs().length).toBe(2);
                expect(_(vm.runs()).pluck("id")).toContain("Model Run 3");
                expect(_(vm.runs()).pluck("id")).toContain("Model Run 1");
            });

            it("updates based on the current disease", function () {
                vm.selectedDisease(vm.diseases[1]);
                expect(vm.runs().length).toBe(1);
                expect(_(vm.runs()).pluck("id")).toContain("Model Run 2");
            });

            it("is sorted by descending date", function () {
                expect(vm.runs()[0].date).toBe("2036-12-18");
                expect(vm.runs()[1].date).toBe("2014-10-13");
            });
        });

        describe("holds a field for the selected run which", function () {
            it("is observable", function () {
                expect(vm.selectedRun).toBeObservable();
            });

            it("defaults to the first run by date in the available layers for the current disease", function () {
                expect(vm.selectedRun().id).toBe("Model Run 3");
                expect(vm.selectedRun().covariates).toBe("covariates3");
                expect(vm.selectedRun().statistics).toBe("statistics3");
            });

            it("defaults to '---' if no available layers are provided", function () {
                var localVM = new LayerSelectorViewModel([]);
                expect(localVM.selectedRun().date).toBe("---");
            });

            it("publishes changes to its state as 'selected-run' event", function () {
                ko.postbox.subscribe("selected-run", function (payload) {
                    expect(payload).toEqual(run1);
                });

                vm.selectedRun(vm.runs()[1]);

                ko.postbox._subscriptions["selected-run"] = [];  // jshint ignore:line
            });
        });

        describe("holds a field for the selected layer which", function () {
            it("is observable", function () {
                expect(vm.selectedLayer).toBeObservable();
            });

            it("combines the selected run and layer type, with covariates and statistics", function () {
                expect(vm.selectedLayer()).toBe(vm.selectedRun().id + "_" + vm.selectedType().id);
                expect(vm.selectedLayer()).toBe("Model Run 3" + "_" + "mean");
            });

            it("responds to changes in the selected run", function () {
                vm.selectedRun(vm.runs()[1]);
                expect(vm.selectedLayer()).toBe(vm.selectedRun().id + "_" + vm.selectedType().id);
                expect(vm.selectedLayer()).toBe("Model Run 1" + "_" + "mean");
            });

            it("responds to changes in the selected layer type", function () {
                vm.selectedType(vm.types[1]);
                expect(vm.selectedLayer()).toBe(vm.selectedRun().id + "_" + vm.selectedType().id);
                expect(vm.selectedLayer()).toBe("Model Run 3" + "_" + "uncertainty");
            });

            it("publishes changes to its state as 'active-atlas-layer'", function () {
                var expectedValue = "";
                var eventCount = 0;
                ko.postbox.subscribe("active-atlas-layer", function (payload) {
                    expect(payload).toEqual(expectedValue);
                    eventCount = eventCount + 1;
                });

                expectedValue = "Model Run 1" + "_" + "mean";
                vm.selectedRun(vm.runs()[1]);
                expectedValue = "Model Run 1" + "_" + "uncertainty";
                vm.selectedType(vm.types[1]);
                expectedValue = "Model Run 3" + "_" + "uncertainty";
                vm.selectedRun(vm.runs()[0]);

                expect(eventCount).toBe(3);

                ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
            });

            it("publishes a 'active-atlas-layer' event on construction", function () {
                var expectedValue = "";
                var eventCount = 0;
                ko.postbox.subscribe("active-atlas-layer", function (payload) {
                    expect(payload).toEqual(expectedValue);
                    eventCount = eventCount + 1;
                });

                expectedValue = "Model Run 3" + "_" + "mean";
                new LayerSelectorViewModel(availableLayers); // jshint ignore:line

                expect(eventCount).toBe(1);

                ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
            });

            it("publishes an empty 'active-atlas-layer' event if no available layers are provided", function () {
                var eventCount = 0;
                ko.postbox.subscribe("active-atlas-layer", function (payload) {
                    expect(payload).toBeUndefined();
                    eventCount = eventCount + 1;
                });

                new LayerSelectorViewModel([]); // jshint ignore:line

                expect(eventCount).toBe(1);

                ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
            });
        });
    });
});
