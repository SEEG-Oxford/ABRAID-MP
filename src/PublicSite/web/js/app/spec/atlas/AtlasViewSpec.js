/* A suite of tests for the AtlasView AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "squire"
], function (ko, Squire) {
    "use strict";

    describe("The 'AtlasView", function () {
        var AtlasView;
        var leafletMock = {
            map:  jasmine.createSpy("mapSpy").and.callFake(function () {
                return {
                    fitWorld: jasmine.createSpy("fitWorld"),
                    addLayer: jasmine.createSpy("addLayer"),
                    removeLayer: jasmine.createSpy("removeLayer")
                };
            }),
            tileLayer: {
                wms: jasmine.createSpy("wmsSpy").and.callFake(function (wms, args) {
                    return "wms " + args.layers[0];
                })
            },
            CRS : { EPSG4326 : "fake EPSG4326" }
        };

        beforeEach(function (done) {
            // Clear postbox subscriptions (prevents test from bleeding into each other).
            // Might be problematic if we swap to minified.
            ko.postbox._subscriptions["layer-changed"] = [];  // jshint ignore:line

            // Reset the leaflet spies
            leafletMock.map.calls.reset();
            leafletMock.tileLayer.wms.calls.reset();

            // Before first test, load AtlasView with the leaflet mock
            if (AtlasView === undefined) {
                // Squire is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                jasmine.Ajax.uninstall();
                var injector = new Squire();
                injector.mock("L", leafletMock);
                injector.mock("ko", ko);

                injector.require(["app/atlas/AtlasView"],
                    function (localAtlasView) {
                        AtlasView = localAtlasView;
                        done();
                    }
                );
            } else {
                done();
            }
        });

        describe("creates a Leaflet map", function () {
            it("on the correct DOM element", function () {
                new AtlasView(); // jshint ignore:line
                expect(leafletMock.map).toHaveBeenCalled();
                expect(leafletMock.map.calls.count()).toBe(1);
                expect(leafletMock.map.calls.first().args[0]).toBe("map");
            });

            it("showing the full extent", function () {
                var view = new AtlasView();
                expect(view.map.fitWorld).toHaveBeenCalled();
            });

            it("with the standard abraid options", function () {
                new AtlasView(); // jshint ignore:line
                expect(leafletMock.map.calls.first().args[1].attributionControl).toBe(false);
                expect(leafletMock.map.calls.first().args[1].zoomControl).toBe(false);
                expect(leafletMock.map.calls.first().args[1].zoomsliderControl).toBe(true);
                expect(leafletMock.map.calls.first().args[1].maxBounds).toEqual([ [-60, -220], [85, 220] ]);
                expect(leafletMock.map.calls.first().args[1].maxZoom).toBe(7);
                expect(leafletMock.map.calls.first().args[1].minZoom).toBe(3);
                expect(leafletMock.map.calls.first().args[1].animate).toBe(true);
                expect(leafletMock.map.calls.first().args[1].crs).toBe(leafletMock.CRS.EPSG4326);
                expect(leafletMock.map.calls.first().args[1].bounceAtZoomLimits).toBe(false);
            });
        });

        describe("adds a wms layer to the map", function () {
            it("in response to 'layer-changed' events ", function () {
                var view = new AtlasView();
                ko.postbox.publish("layer-changed", "new layer");
                expect(leafletMock.tileLayer.wms).toHaveBeenCalled();
                expect(leafletMock.tileLayer.wms.calls.count()).toBe(1);
                expect(leafletMock.tileLayer.wms.calls.first().args[1].layers[0]).toBe("new layer");
                expect(view.map.addLayer).toHaveBeenCalled();
                expect(view.map.addLayer.calls.count()).toBe(1);
                expect(view.map.addLayer.calls.first().args[0]).toBe("wms new layer");
                expect(view.currentLayer).toBe("wms new layer");
            });

            it("replacing existing layers", function () {
                var view = new AtlasView();
                ko.postbox.publish("layer-changed", "old layer");
                expect(view.currentLayer).toBe("wms old layer");
                view.map.addLayer.calls.reset();

                ko.postbox.publish("layer-changed", "new layer");
                expect(view.map.removeLayer).toHaveBeenCalled();
                expect(view.map.removeLayer.calls.count()).toBe(1);
                expect(view.map.removeLayer.calls.first().args[0]).toBe("wms old layer");
                expect(view.map.addLayer).toHaveBeenCalled();
                expect(view.map.addLayer.calls.count()).toBe(1);
                expect(view.map.addLayer.calls.first().args[0]).toBe("wms new layer");
                expect(view.currentLayer).toBe("wms new layer");
            });

            it("with the standard abraid options", function () {
                new AtlasView("wms url qwertyuiop"); // jshint ignore:line
                ko.postbox.publish("layer-changed", "new layer");
                expect(leafletMock.tileLayer.wms.calls.first().args[0]).toBe("wms url qwertyuiop");
                expect(leafletMock.tileLayer.wms.calls.first().args[1].format).toBe("image/png");
                expect(leafletMock.tileLayer.wms.calls.first().args[1].styles).toBe("abraid_raster");
                expect(leafletMock.tileLayer.wms.calls.first().args[1].reuseTiles).toBe(true);
            });
        });
    });
});
