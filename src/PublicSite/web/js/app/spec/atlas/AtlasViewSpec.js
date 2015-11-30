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
            tileLayer: {
                wms: jasmine.createSpy("wmsSpy").and.callFake(function (wms, args) {
                    return "wms " + args.display;
                })
            }
        };

        var leafletMapFactoryMock = {
            create: jasmine.createSpy("mapSpy").and.callFake(function () {
                return {
                    fitWorld: jasmine.createSpy("fitWorld"),
                    addLayer: jasmine.createSpy("addLayer"),
                    removeLayer: jasmine.createSpy("removeLayer")
                };
            })
        };

        var wmsLayerParameterFactoryMock = {
            createLayerParametersForDisplay: function (layer) {
                return { "display": layer.run.id + "_" + layer.type };
            }
        };

        var geoJsonLayerFactoryMock = {
            showGeoJsonLayer: function () {
                return false;
            }
        };

        var geoJsonLayerFactoryMockFull = {
            showGeoJsonLayer: jasmine.createSpy("showGeoJsonLayer").and.callFake(function () {
                return true;
            }),
            buildGeoJsonUrl: jasmine.createSpy("buildGeoJsonUrl").and.callFake(function (layer) {
                return "url_" + layer.run.id + "_" + layer.type;
            }),
            buildGeoJsonLayer: jasmine.createSpy("buildGeoJsonLayer").and.callFake(function (featureCollection, layer) {
                return "geojsonlayer_" + featureCollection + "$" + layer.run.id + "_" + layer.type;
            })
        };

        var alertMock = function () {
        };

        beforeEach(function (done) {
            // Clear postbox subscriptions (prevents test from bleeding into each other).
            // Might be problematic if we swap to minified.
            ko.postbox._subscriptions["active-atlas-layer"] = [];  // jshint ignore:line
            ko.postbox._subscriptions["tracking-action-event"] = [];  // jshint ignore:line

            // Reset the leaflet spies
            leafletMapFactoryMock.create.calls.reset();
            leafletMock.tileLayer.wms.calls.reset();
            geoJsonLayerFactoryMockFull.showGeoJsonLayer.calls.reset();
            geoJsonLayerFactoryMockFull.buildGeoJsonUrl.calls.reset();
            geoJsonLayerFactoryMockFull.buildGeoJsonLayer.calls.reset();

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
                new AtlasView("wms", leafletMapFactoryMock, wmsLayerParameterFactoryMock, geoJsonLayerFactoryMock, alertMock); // jshint ignore:line
                expect(leafletMapFactoryMock.create).toHaveBeenCalled();
                expect(leafletMapFactoryMock.create.calls.count()).toBe(1);
                expect(leafletMapFactoryMock.create.calls.first().args[0]).toBe("map");
            });
        });

        describe("adds a wms layer to the map", function () {
            it("in response to 'active-atlas-layer' events ", function () {
                var view = new AtlasView(
                    "wms", leafletMapFactoryMock, wmsLayerParameterFactoryMock, geoJsonLayerFactoryMock, alertMock);
                ko.postbox.publish("active-atlas-layer", { type: "qwe", run: { id: "foonew" } });
                expect(leafletMock.tileLayer.wms).toHaveBeenCalled();
                expect(leafletMock.tileLayer.wms.calls.count()).toBe(1);
                expect(leafletMock.tileLayer.wms.calls.first().args[1].display).toBe("foonew_qwe");
                expect(view.map.addLayer).toHaveBeenCalled();
                expect(view.map.addLayer.calls.count()).toBe(1);
                expect(view.map.addLayer.calls.first().args[0]).toBe("wms foonew_qwe");
                expect(view.currentWmsLayer).toBe("wms foonew_qwe");
            });

            it("replacing existing layers", function () {
                var view = new AtlasView(
                    "wms", leafletMapFactoryMock, wmsLayerParameterFactoryMock, geoJsonLayerFactoryMock, alertMock);
                ko.postbox.publish("active-atlas-layer", { type: "qwe", run: { id: "fooold" } });
                expect(view.currentWmsLayer).toBe("wms fooold_qwe");
                view.map.addLayer.calls.reset();

                ko.postbox.publish("active-atlas-layer", { type: "qwe", run: { id: "foonew" } });
                expect(view.map.removeLayer).toHaveBeenCalled();
                expect(view.map.removeLayer.calls.count()).toBe(1);
                expect(view.map.removeLayer.calls.first().args[0]).toBe("wms fooold_qwe");
                expect(view.map.addLayer).toHaveBeenCalled();
                expect(view.map.addLayer.calls.count()).toBe(1);
                expect(view.map.addLayer.calls.first().args[0]).toBe("wms foonew_qwe");
                expect(view.currentWmsLayer).toBe("wms foonew_qwe");
            });

            it("with the standard abraid options", function () {
                new AtlasView("wms url qwertyuiop", leafletMapFactoryMock, wmsLayerParameterFactoryMock, geoJsonLayerFactoryMock, alertMock); // jshint ignore:line
                ko.postbox.publish("active-atlas-layer", { type: "qwe", run: { id: "foo" } });
                expect(leafletMock.tileLayer.wms.calls.first().args[0]).toBe("wms url qwertyuiop");
                expect(leafletMock.tileLayer.wms.calls.first().args[1]).toEqual({ "display": "foo_qwe" });
            });

            it("and announces the change as an analytics trackable event", function () {
                new AtlasView("wms url qwertyuiop", leafletMapFactoryMock,  wmsLayerParameterFactoryMock, geoJsonLayerFactoryMock, alertMock); // jshint ignore:line

                ko.postbox.subscribe("tracking-action-event", function (payload) {
                    expect(payload.category).toBe("atlas");
                    expect(payload.action).toBe("layer-view");
                    expect(payload.label).toBe("foo_qwe");
                    expect(payload.value).toBeUndefined();
                });

                ko.postbox.publish("active-atlas-layer", { type: "qwe", run: { id: "foo" } });
            });
        });

        describe("removes the wms layer on the map", function () {
            it("in response to empty 'active-atlas-layer' events ", function () {
                var view = new AtlasView(
                    "wms", leafletMapFactoryMock, wmsLayerParameterFactoryMock, geoJsonLayerFactoryMock, alertMock);
                ko.postbox.publish("active-atlas-layer", { type: "qwe", run: { id: "fooold" } });
                expect(view.currentWmsLayer).toBe("wms fooold_qwe");
                view.map.addLayer.calls.reset();

                ko.postbox.publish("active-atlas-layer", undefined);
                expect(view.map.removeLayer).toHaveBeenCalled();
                expect(view.map.removeLayer.calls.count()).toBe(1);
                expect(view.map.removeLayer.calls.first().args[0]).toBe("wms fooold_qwe");
                expect(view.map.addLayer).not.toHaveBeenCalled();
                expect(view.currentWmsLayer).toBe(undefined);
            });
        });

        describe("adds a geojson layer to the map when required", function () {
            it("in response to 'active-atlas-layer' events ", function () {
                var view = new AtlasView(
                    "wms", leafletMapFactoryMock, wmsLayerParameterFactoryMock, geoJsonLayerFactoryMockFull, alertMock);
                ko.postbox.publish("active-atlas-layer", { type: "qwe", run: { id: "foonew" } });
                jasmine.Ajax.requests.mostRecent().response({
                    "status": 200,
                    "contentType": "application/json",
                    "responseText": JSON.stringify("fc")
                });
                expect(geoJsonLayerFactoryMockFull.buildGeoJsonLayer).toHaveBeenCalled();
                expect(geoJsonLayerFactoryMockFull.buildGeoJsonLayer.calls.count()).toBe(1);
                expect(geoJsonLayerFactoryMockFull.buildGeoJsonLayer.calls.first().args[1])
                    .toEqual({ type: "qwe", run: { id: "foonew" } });
                expect(view.map.addLayer).toHaveBeenCalled();
                expect(view.map.addLayer.calls.count()).toBe(2);
                expect(view.map.addLayer.calls.argsFor(1)[0]).toBe("geojsonlayer_fc$foonew_qwe");
                expect(view.currentGeoJsonLayer).toBe("geojsonlayer_fc$foonew_qwe");
            });

            it("replacing existing layers", function () {
                var view = new AtlasView(
                    "wms", leafletMapFactoryMock, wmsLayerParameterFactoryMock, geoJsonLayerFactoryMockFull, alertMock);
                ko.postbox.publish("active-atlas-layer", { type: "qwe", run: { id: "fooold" } });
                jasmine.Ajax.requests.mostRecent().response({
                    "status": 200,
                    "contentType": "application/json",
                    "responseText": JSON.stringify("fc")
                });
                expect(view.currentWmsLayer).toBe("wms fooold_qwe");
                view.map.addLayer.calls.reset();

                ko.postbox.publish("active-atlas-layer", { type: "qwe", run: { id: "foonew" } });
                jasmine.Ajax.requests.mostRecent().response({
                    "status": 200,
                    "contentType": "application/json",
                    "responseText": JSON.stringify("fc2")
                });
                expect(view.map.removeLayer).toHaveBeenCalled();
                expect(view.map.removeLayer.calls.count()).toBe(2);
                expect(view.map.removeLayer.calls.argsFor(1)[0]).toBe("geojsonlayer_fc$fooold_qwe");
                expect(view.map.addLayer).toHaveBeenCalled();
                expect(view.map.addLayer.calls.count()).toBe(2);
                expect(view.map.addLayer.calls.argsFor(1)[0]).toBe("geojsonlayer_fc2$foonew_qwe");
                expect(view.currentGeoJsonLayer).toBe("geojsonlayer_fc2$foonew_qwe");
            });

            it("using data obtained from the correct source", function () {
                new AtlasView("wms", leafletMapFactoryMock, wmsLayerParameterFactoryMock, geoJsonLayerFactoryMockFull, alertMock); // jshint ignore:line
                ko.postbox.publish("active-atlas-layer", { type: "qwe", run: { id: "foonew" } });
                expect(jasmine.Ajax.requests.mostRecent().url).toBe("url_foonew_qwe");
            });
        });
    });
});
