/* A suite of tests for the LeafletMapFactory AMD.
 * Copyright (c) 2015 University of Oxford
 */
define([
    "ko",
    "squire",
    "L"
], function (ko, Squire, L) {
    "use strict";

    describe("The 'LeafletMapFactory", function () {
        describe("has a create function, which creates a Leaflet map", function () {
            var map;
            var jqueryMock = function () {
                return {
                    width: function () { return 1900; },
                    height: function () { return 1000; }
                };
            };

            var leafletMock = {
                latLngBounds: L.latLngBounds,
                Point: L.Point,
                latLng: L.latLng,
                CRS: L.CRS,
                map: jasmine.createSpy("map")
            };
            spyOn(L, "map").and.callThrough();

            beforeEach(function (done) {
                // Before first test, load LeafletMapFactory with the leaflet mock
                if (map === undefined) {
                    // Squire is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                    jasmine.Ajax.uninstall();
                    var injector = new Squire();
                    injector.mock("L", leafletMock);
                    injector.mock("jquery", jqueryMock);

                    injector.require(["app/LeafletMapFactory"],
                        function (LocalLeafletMapFactory) {
                            map = (new LocalLeafletMapFactory()).create("map");
                            done();
                        }
                    );
                } else {
                    done();
                }
            });

            it("on the correct DOM element", function () {
                expect(leafletMock.map).toHaveBeenCalled();
                expect(leafletMock.map.calls.count()).toBe(1);
                expect(leafletMock.map.calls.first().args[0]).toBe("map");
            });


            it("with the options provided by the parameter factory", function () {
                expect(leafletMock.map.calls.first().args[1].attributionControl).toBe(false);
                expect(leafletMock.map.calls.first().args[1].zoomControl).toBe(false);
                expect(leafletMock.map.calls.first().args[1].zoomsliderControl).toBe(true);
                expect(leafletMock.map.calls.first().args[1].maxBounds).toEqual(L.latLngBounds([
                    [-60, -220],
                    [85, 220]
                ]));
                expect(leafletMock.map.calls.first().args[1].maxZoom).toBe(8);
                expect(leafletMock.map.calls.first().args[1].minZoom).toBe(1);
                expect(leafletMock.map.calls.first().args[1].zoom).toBe(2);
                expect(leafletMock.map.calls.first().args[1].center).toEqual(L.latLng([12.5, 0]));
                expect(leafletMock.map.calls.first().args[1].animate).toBe(true);
                expect(leafletMock.map.calls.first().args[1].crs).toBe(L.CRS.EPSG4326);
                expect(leafletMock.map.calls.first().args[1].bounceAtZoomLimits).toBe(false);
            });
        });
    });
});
