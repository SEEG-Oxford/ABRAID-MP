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
        beforeEach(function () {
            ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
            ko.postbox._subscriptions["selected-run"] = [];  // jshint ignore:line
            vm = new DownloadLinksViewModel("baseUrl-poiuytrewq", "wmsUrl-qwertyuiop");
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

                it("with the standard options", function () {
                    ko.postbox.publish("active-atlas-layer", "layer");
                    expect(vm.png()).toContain("service=WMS");
                    expect(vm.png()).toContain("version=1.1.0");
                    expect(vm.png()).toContain("request=GetMap");
                    expect(vm.png()).toContain("styles=abraid_raster");
                    expect(vm.png()).toContain("bbox=-180.0%2C-60.0%2C180.0%2C85.0");
                    expect(vm.png()).toContain("width=1656");
                    expect(vm.png()).toContain("height=667");
                    expect(vm.png()).toContain("srs=EPSG%3A4326");
                    expect(vm.png()).toContain("format=image%2Fpng");
                });
            });

            it("updates to reflect the 'active-atlas-layer' through an event subscription", function () {
                expect(vm.png()).toBe("#");
                ko.postbox.publish("active-atlas-layer", "asdf");
                expect(vm.png()).toContain("layers=abraid%3Aasdf");
                ko.postbox.publish("active-atlas-layer", "wert");
                expect(vm.png()).toContain("layers=abraid%3Awert");
                ko.postbox.publish("active-atlas-layer", undefined);
                expect(vm.png()).toBe("#");
                ko.postbox.publish("active-atlas-layer", "zxcv");
                expect(vm.png()).toContain("layers=abraid%3Azxcv");
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
    });
});
