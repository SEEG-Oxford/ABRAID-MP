/* A suite of tests for the atlas DownloadLinksViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/atlas/DownloadLinksViewModel",
    "ko"
], function (DownloadLinksViewModel, ko) {
    "use strict";

    describe("The atlas 'download links' view model", function () {
        var vm = {};

        var wmsLayerParameterFactoryMock = {
            createLayerParametersForDownload: function (name) {
                return { "download" : name };
            }
        };

        beforeEach(function () {
            ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
            ko.postbox._subscriptions["selected-run"] = [];  // jshint ignore:line
            vm = new DownloadLinksViewModel("baseUrl-poiuytrewq", "wmsUrl-qwertyuiop", wmsLayerParameterFactoryMock);
            ko.postbox.publish("active-atlas-layer", undefined);
            ko.postbox.publish("selected-run", undefined);
        });
        afterEach(function () {
            ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
            ko.postbox._subscriptions["selected-run"] = [];  // jshint ignore:line
            vm = undefined;
        });

        describe("holds a field for the PNG download url, which", function () {
            it("is observable", function () {
                expect(vm.png).toBeObservable();
            });

            it("is '#' when there is no active layer", function () {
                expect(vm.png()).toBe("#");
            });

            describe("makes a wms request", function () {
                it("to the right service", function () {
                    ko.postbox.publish("active-atlas-layer", "layer");
                    expect(vm.png().substring(0, "wmsUrl-qwertyuiop".length)).toBe("wmsUrl-qwertyuiop");
                });

                it("with the options provided by the parameter factory", function () {
                    ko.postbox.publish("active-atlas-layer", "layer");
                    expect(vm.png()).toContain("?download=layer");
                });
            });

            it("updates to reflect the 'active-atlas-layer' through an event subscription", function () {
                expect(vm.png()).toBe("#");
                ko.postbox.publish("active-atlas-layer", "asdf");
                expect(vm.png()).toContain("?download=asdf");
                ko.postbox.publish("active-atlas-layer", "wert");
                expect(vm.png()).toContain("?download=wert");
                ko.postbox.publish("active-atlas-layer", undefined);
                expect(vm.png()).toBe("#");
                ko.postbox.publish("active-atlas-layer", "zxcv");
                expect(vm.png()).toContain("?download=zxcv");
            });
        });

        describe("holds a field for the GeoTIFF download url, which", function () {
            it("is observable", function () {
                expect(vm.tif).toBeObservable();
            });

            it("is '#' when there is no active layer", function () {
                ko.postbox.publish("active-atlas-layer", undefined);
                expect(vm.tif()).toBe("#");
            });

            it("makes a request to correct base url", function () {
                ko.postbox.publish("active-atlas-layer", "layer");
                expect(vm.tif().substring(0, "baseUrl-poiuytrewq".length)).toBe("baseUrl-poiuytrewq");
            });

            it("updates to reflect the 'active-atlas-layer' through an event subscription", function () {
                expect(vm.tif()).toBe("#");
                ko.postbox.publish("active-atlas-layer", "asdf");
                expect(vm.tif()).toContain("/results/asdf.tif");
                ko.postbox.publish("active-atlas-layer", "wert");
                expect(vm.tif()).toContain("/results/wert.tif");
                ko.postbox.publish("active-atlas-layer", undefined);
                expect(vm.tif()).toBe("#");
                ko.postbox.publish("active-atlas-layer", "zxcv");
                expect(vm.tif()).toContain("/results/zxcv.tif");
            });
        });

        describe("holds a field for the occurrence download url, which", function () {
            it("is observable", function () {
                expect(vm.occurrences).toBeObservable();
            });

            it("is '#' when there is no selected run", function () {
                ko.postbox.publish("selected-run", undefined);
                expect(vm.occurrences()).toBe("#");
            });

            it("makes a request to correct base url", function () {
                ko.postbox.publish("selected-run", { id: "run", automatic: true });
                expect(vm.occurrences().substring(0, "baseUrl-poiuytrewq".length)).toBe("baseUrl-poiuytrewq");
            });

            it("updates to reflect the 'selected-run' through an event subscription", function () {
                expect(vm.occurrences()).toBe("#");
                ko.postbox.publish("selected-run", { id: "asdf", automatic: true });
                expect(vm.occurrences()).toContain("/details/modelrun/asdf/inputoccurrences.csv");
                ko.postbox.publish("selected-run", { id: "wert", automatic: true });
                expect(vm.occurrences()).toContain("/details/modelrun/wert/inputoccurrences.csv");
                ko.postbox.publish("selected-run", undefined);
                expect(vm.occurrences()).toBe("#");
                ko.postbox.publish("selected-run", { id: "zxcv", automatic: true });
                expect(vm.occurrences()).toContain("/details/modelrun/zxcv/inputoccurrences.csv");
            });
        });

        describe("holds a field to indicate if the occurrence download link should be shown", function () {
            it("is observable", function () {
                expect(vm.showOccurrences).toBeObservable();
            });

            it("is 'false' when there is no selected run", function () {
                ko.postbox.publish("selected-run", undefined);
                expect(vm.showOccurrences()).toBe(false);
            });

            it("is 'false' when there is a manual run selected", function () {
                ko.postbox.publish("selected-run", { id: "run", automatic: false });
                expect(vm.showOccurrences()).toBe(false);
            });

            it("is 'true' when there is an automatic run selected", function () {
                ko.postbox.publish("selected-run", { id: "run", automatic: true });
                expect(vm.showOccurrences()).toBe(true);
            });

            it("updates to reflect the 'selected-run' through an event subscription", function () {
                expect(vm.showOccurrences()).toBe(false);
                ko.postbox.publish("selected-run", { id: "asdf", automatic: true });
                expect(vm.showOccurrences()).toBe(true);
                ko.postbox.publish("selected-run", { id: "wert", automatic: false });
                expect(vm.showOccurrences()).toBe(false);
                ko.postbox.publish("selected-run", undefined);
                expect(vm.showOccurrences()).toBe(false);
                ko.postbox.publish("selected-run", { id: "zxcv", automatic: true });
                expect(vm.showOccurrences()).toBe(true);
            });
        });
    });
});
