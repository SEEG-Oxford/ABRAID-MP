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
        var availableLayers = [
            {
                disease: "Disease Group 2",
                runs: [
                    { date: "1995-10-09", id: "Model Run 2" }
                ]
            }, {
                disease: "Disease Group 1",
                runs: [
                    { date: "2014-10-13", id: "Model Run 1" },
                    { date: "2036-12-18", id: "Model Run 3" }
                ]
            }
        ];
        var vm = {};
        beforeEach(function () {
            vm = new LayerSelectorViewModel(availableLayers);
        });

        describe("holds a field for the available layer types which", function () {
            it("has two entries", function () {
                expect(vm.types.length).toBe(2);
            });

            it("has an entry for mean", function () {
                expect(vm.types).toContain({ display: "disease risk", id: "mean" });
            });

            it("has an entry for uncertainty", function () {
                expect(vm.types).toContain({ display: "risk uncertainty", id: "uncertainty" });
            });
        });

        describe("holds a field for the selected layer type which", function () {
            it("is observable", function () {
                expect(vm.selectedType).toBeObservable();
            });

            it("defaults to mean", function () {
                expect(vm.selectedType().id).toBe("mean");
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
                expect(vm.selectedDisease).toBeObservable();
            });

            it("defaults to the first run by date in the available layers for the current disease", function () {
                expect(vm.selectedRun().id).toBe("Model Run 3");
            });
        });

        describe("holds a field for the selected layer which", function () {
            it("is observable", function () {
                expect(vm.selectedLayer).toBeObservable();
            });

            it("combines the selected run and layer type", function () {
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

            it("publishes changes to it's state changes as 'layer-changed'", function () {
                var expectedValue = "";
                var eventCount = 0;
                ko.postbox.subscribe("layer-changed", function (payload) {
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

                ko.postbox._subscriptions["layer-changed"] = [];  // jshint ignore:line
            });
        });
    });
});
