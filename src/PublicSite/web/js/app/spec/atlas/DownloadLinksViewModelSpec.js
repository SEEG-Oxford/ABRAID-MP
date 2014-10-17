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
            vm = new DownloadLinksViewModel("baseUrl-poiuytrewq", "wmsUrl-qwertyuiop");
        });
        afterEach(function () {
            ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
            vm = undefined;
        });

        describe("holds a field for the active atlas layer name, which", function () {
            it("is observable", function () {
                expect(vm.activeLayer).toBeObservable();
            });

            it("starts empty", function () {
                expect(vm.activeLayer()).toBeUndefined();
            });

            it("subscribes to the 'active-atlas-layer' event", function() {
                expect(vm.activeLayer()).toBeUndefined();
                ko.postbox.publish("active-atlas-layer", "asdf");
                expect(vm.activeLayer()).toBe("asdf");
                ko.postbox.publish("active-atlas-layer", "hjkl");
                expect(vm.activeLayer()).toBe("hjkl");
                ko.postbox.publish("active-atlas-layer", undefined);
                expect(vm.activeLayer()).toBeUndefined();
                ko.postbox.publish("active-atlas-layer", "vbnm");
                expect(vm.activeLayer()).toBe("vbnm");
            });
        });

        describe("holds a field for the PNG download url, which", function () {
            it("is observable", function () {
                expect(vm.png).toBeObservable();
            });

            it("is '#' when there is no active layer", function () {
                vm.activeLayer(undefined);
                expect(vm.png()).toBe("#");
            });

            describe("makes a wms request", function () {
                it("to the right service", function () {
                    vm.activeLayer("layer");
                    expect(vm.png().substring(0, "wmsUrl-qwertyuiop".length)).toBe("wmsUrl-qwertyuiop");
                });

                it("with the standard options", function () {
                    vm.activeLayer("layer");
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

            it("updates to reflect the active layer", function () {
                expect(vm.png()).toBe("#");
                vm.activeLayer("asdf");
                expect(vm.png()).toContain("layers=abraid%3Aasdf");
                vm.activeLayer("wert");
                expect(vm.png()).toContain("layers=abraid%3Awert");
                vm.activeLayer(undefined);
                expect(vm.png()).toBe("#");
                vm.activeLayer("zxcv");
                expect(vm.png()).toContain("layers=abraid%3Azxcv");
            });
        });

        describe("holds a field for the GeoTIFF download url, which", function () {
            it("is observable", function () {
                expect(vm.tif).toBeObservable();
            });

            it("is '#' when there is no active layer", function () {
                vm.activeLayer(undefined);
                expect(vm.tif()).toBe("#");
            });

            it("makes a request to correct base url", function () {
                vm.activeLayer("layer");
                expect(vm.tif().substring(0, "baseUrl-poiuytrewq".length)).toBe("baseUrl-poiuytrewq");
            });

            it("updates to reflect the active layer", function () {
                expect(vm.tif()).toBe("#");
                vm.activeLayer("asdf");
                expect(vm.tif()).toContain("/results/asdf.tif");
                vm.activeLayer("wert");
                expect(vm.tif()).toContain("/results/wert.tif");
                vm.activeLayer(undefined);
                expect(vm.tif()).toBe("#");
                vm.activeLayer("zxcv");
                expect(vm.tif()).toContain("/results/zxcv.tif");
            });
        });
    });
});
