/* A suite of tests for the AtlasView AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/atlas/GeoJsonLayerFactory",
    "L",
    "underscore",
    "squire"
], function (GeoJsonLayerFactory, L, _, Squire) {
    "use strict";

    describe("The 'GeoJsonLayerFactory", function () {
        var GeoJsonLayerFactory;
        beforeEach(function (done) {
            // Before first test, load AtlasView with the leaflet mock
            if (GeoJsonLayerFactory === undefined) {
                // Squire is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                jasmine.Ajax.uninstall();
                var injector = new Squire();
                injector.mock("L", L); // make sure the test has the same instance of L as the class

                injector.require(["app/atlas/GeoJsonLayerFactory"],
                    function (localGeoJsonLayerFactory) {
                        GeoJsonLayerFactory = localGeoJsonLayerFactory;
                        done();
                    }
                );
            } else {
                done();
            }
        });

        describe("has a 'showGeoJsonLayer' method, that", function () {
            it("returns true for 'occurrence' layers", function () {
                var target = new GeoJsonLayerFactory("baseUrl");
                expect(target.showGeoJsonLayer({ type: "occurrences" })).toBe(true);
            });

            it("returns false for other layer types", function () {
                var target = new GeoJsonLayerFactory("baseUrl");
                expect(target.showGeoJsonLayer({ type: "mean" })).toBe(false);
                expect(target.showGeoJsonLayer({ type: "extent" })).toBe(false);
                expect(target.showGeoJsonLayer({ type: "uncertainty" })).toBe(false);
            });
        });

        describe("has a 'buildGeoJsonUrl' method, that", function () {
            it("returns the correct value for 'occurrence' layers", function () {
                var target = new GeoJsonLayerFactory("baseUrl/");
                expect(target.buildGeoJsonUrl({ type: "occurrences", run: { id: "abc" } }))
                    .toBe("baseUrl/atlas/data/modelrun/abc/geojson");
            });
        });

        describe("has a 'buildGeoJsonLayer' method, that", function () {
            describe("returns the correct leaflet layer for 'occurrence' layers, which", function () {
                var layer = { type: "occurrences", run: { id: "abc", end: "2015-06-12" } };
                var fc = { features: [
                    { geometry: { coordinates: [0, 1] }, properties: { occurrenceDate: "2015-06-13" }},
                    { geometry: { coordinates: [6, 7] }, properties: { occurrenceDate: "2012-06-13" }},
                    { geometry: { coordinates: [4, 5] }, properties: { occurrenceDate: "2013-06-10" }},
                    { geometry: { coordinates: [8, 9] }, properties: { occurrenceDate: "2007-06-13" }},
                    { geometry: { coordinates: [2, 3] }, properties: { occurrenceDate: "2014-06-11" }}
                ]};

                it("is a leaflet LayerGroup", function () {
                    var target = new GeoJsonLayerFactory("baseUrl/");
                    var leafletLayer = target.buildGeoJsonLayer(fc, layer);
                    expect(leafletLayer.constructor).toBe(L.layerGroup([]).constructor);
                });

                describe("contains sublayers for each occurrence", function () {
                    it("ordered by date", function () {
                        var target = new GeoJsonLayerFactory("baseUrl/");
                        var leafletLayer = target.buildGeoJsonLayer(fc, layer);
                        var sublayers = _(leafletLayer._layers).values();
                        expect(sublayers[4]._latlng.toString()).toEqual("LatLng(1, 0)");
                        expect(sublayers[3]._latlng.toString()).toEqual("LatLng(3, 2)");
                        expect(sublayers[2]._latlng.toString()).toEqual("LatLng(5, 4)");
                        expect(sublayers[1]._latlng.toString()).toEqual("LatLng(7, 6)");
                        expect(sublayers[0]._latlng.toString()).toEqual("LatLng(9, 8)");
                    });

                    it("as leaflet CircleMarkers", function () {
                        var target = new GeoJsonLayerFactory("baseUrl/");
                        var leafletLayer = target.buildGeoJsonLayer(fc, layer);
                        var sublayers = _(leafletLayer._layers).values();
                        expect(sublayers[0].constructor).toBe(L.circleMarker().constructor);
                        expect(sublayers[0].options.clickable).toBe(false);
                        expect(sublayers[0].options.fill).toBe(true);
                        expect(sublayers[0].options.fillOpacity).toBe(1);
                        expect(sublayers[0].options.radius).toBe(3);
                        expect(sublayers[0].options.stroke).toBe(false);
                    });

                    it("colored by date", function () {
                        var target = new GeoJsonLayerFactory("baseUrl/");
                        var leafletLayer = target.buildGeoJsonLayer(fc, layer);
                        var sublayers = _(leafletLayer._layers).values();
                        expect(sublayers[0].options.fillColor).toBe("#A6C5EA");
                        expect(sublayers[1].options.fillColor).toBe("#87A5C8");
                        expect(sublayers[2].options.fillColor).toBe("#4B6584");
                        expect(sublayers[3].options.fillColor).toBe("#4B6584");
                        expect(sublayers[4].options.fillColor).toBe("#0F2540");
                    });
                });
            });
        });
    });
});
