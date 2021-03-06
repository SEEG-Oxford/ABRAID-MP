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
        var run2 = { date: "1995-10-09", id: "Model Run 2", startDate: "sd1", endDate: "ed1", automatic: false };
        var run1 = { date: "2014-10-13", id: "Model Run 1", startDate: "sd2", endDate: "ed2", automatic: true };
        var run3 = { date: "2036-12-18", id: "Model Run 3", startDate: "sd3", endDate: "ed3", automatic: true };
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
        var occurrenceType = { display: "input occurrences", id: "occurrences" };
        var vm = {};
        beforeEach(function () {
            vm = new LayerSelectorViewModel(availableLayers, true);
        });

        describe("holds a field for the available layer types which", function () {
            it("has three entries for non-seeg members", function () {
                vm = new LayerSelectorViewModel(availableLayers, false);
                expect(vm.types.length).toBe(3);
            });

            it("has four entries for seeg members", function () {
                expect(vm.types.length).toBe(4);
            });

            it("has an entry for mean", function () {
                expect(vm.types).toContain(meanType);
            });

            it("does not have an entry for uncertainty for non-seeg members", function () {
                vm = new LayerSelectorViewModel(availableLayers, false);
                expect(vm.types).not.toContain(uncertaintyType);
            });

            it("has an entry for uncertainty for seeg members", function () {
                expect(vm.types).toContain(uncertaintyType);
            });

            it("has an entry for extent", function () {
                expect(vm.types).toContain(extentType);
            });

            it("has an entry for occurrences", function () {
                expect(vm.types).toContain(occurrenceType);
            });
        });

        describe("holds a field for the selected layer type which", function () {
            it("is observable", function () {
                expect(vm.selectedType).toBeObservable();
            });

            it("defaults to mean", function () {
                expect(vm.selectedType()).toEqual(meanType);
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
                var localVM = new LayerSelectorViewModel([], true);
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
            });

            it("defaults to a fake empty run if no available layers are provided", function () {
                var localVM = new LayerSelectorViewModel([], true);
                expect(localVM.selectedRun().id).toBeUndefined();
                expect(localVM.selectedRun().date).toBe("---");
                expect(localVM.selectedRun().rangeStart).toBe("???");
                expect(localVM.selectedRun().rangeEnd).toBe("???");
            });
        });

        describe("holds a field for the selected layer which", function () {
            it("is observable", function () {
                expect(vm.selectedLayer).toBeObservable();
            });

            it("combines the selected run and layer type", function () {
                expect(vm.selectedLayer().run.id).toBe(vm.selectedRun().id);
                expect(vm.selectedLayer().run.id).toBe("Model Run 3");
                expect(vm.selectedLayer().type).toBe(vm.selectedType().id);
                expect(vm.selectedLayer().type).toBe("mean");

            });

            it("responds to changes in the selected run", function () {
                vm.selectedRun(vm.runs()[1]);
                expect(vm.selectedLayer().run.id).toBe(vm.selectedRun().id);
                expect(vm.selectedLayer().run.id).toBe("Model Run 1");
                expect(vm.selectedLayer().type).toBe(vm.selectedType().id);
                expect(vm.selectedLayer().type).toBe("mean");
            });

            it("responds to changes in the selected layer type", function () {
                vm.selectedType(vm.types[1]);
                expect(vm.selectedLayer().run.id).toBe(vm.selectedRun().id);
                expect(vm.selectedLayer().run.id).toBe("Model Run 3");
                expect(vm.selectedLayer().type).toBe(vm.selectedType().id);
                expect(vm.selectedLayer().type).toBe("uncertainty");
            });

            it("publishes changes to its state as 'active-atlas-layer'", function () {
                var expectedValue = "";
                var eventCount = 0;
                ko.postbox.subscribe("active-atlas-layer", function (payload) {
                    expect(payload).toEqual(expectedValue);
                    eventCount = eventCount + 1;
                });

                expectedValue = { type: "mean", run: run1};
                vm.selectedRun(vm.runs()[1]);
                expectedValue = { type: "uncertainty", run: run1 };
                vm.selectedType(vm.types[1]);
                expectedValue = { type: "uncertainty", run: run3 };
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

                expectedValue = { type: "mean", run: run3 };
                new LayerSelectorViewModel(availableLayers, true); // jshint ignore:line

                expect(eventCount).toBe(1);

                ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
            });

            it("publishes an empty 'active-atlas-layer' event if no available layers are provided", function () {
                var eventCount = 0;
                ko.postbox.subscribe("active-atlas-layer", function (payload) {
                    expect(payload).toBeUndefined();
                    eventCount = eventCount + 1;
                });

                new LayerSelectorViewModel([], true); // jshint ignore:line

                expect(eventCount).toBe(1);

                ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
            });
        });
    });
});
