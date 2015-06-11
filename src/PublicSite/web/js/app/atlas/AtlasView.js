/**
 * AMD for adding Leaflet map and layers for the Atlas View.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'active-atlas-layer' - published by LayerSelectorViewModel
 */
define([
    "L",
    "ko",
    "jquery"
], function (L, ko, $) {
    "use strict";

    return function (wmsUrl, wmsLayerParameterFactory, geoJsonLayerParameterFactory, alert) {
        var self = this;

        self.wmsUrl = wmsUrl;

        self.map = L.map("map", {
            attributionControl: false,
            zoomControl: false,
            zoomsliderControl: true,
            maxBounds: [ [-60, -220], [85, 220] ],
            maxZoom: 7,
            minZoom: 3,
            animate: true,
            crs: L.CRS.EPSG4326,
            bounceAtZoomLimits: false
        });

        self.map.fitWorld();

        self.currentWmsLayer = undefined;
        self.currentGeoJsonLayer = undefined;
        self.geoJsonAjax = undefined;

        self.resetView = function () {
            if (self.currentWmsLayer) {
                self.map.removeLayer(self.currentWmsLayer);
                self.currentWmsLayer = undefined;
            }

            if (self.currentGeoJsonLayer) {
                self.map.removeLayer(self.currentGeoJsonLayer);
                self.currentGeoJsonLayer = undefined;
            }
            if (self.geoJsonAjax) {
                self.geoJsonAjax.abort();
            }
        };

        ko.postbox.subscribe("active-atlas-layer", function (layer) {
            self.resetView();

            if (layer) {
                if (geoJsonLayerParameterFactory.showGeoJsonLayer(layer)) {
                    self.geoJsonAjax = $.getJSON(geoJsonLayerParameterFactory.buildGeoJsonUrl(layer))
                        .done(function (featureCollection) {
                            self.currentGeoJsonLayer =
                                geoJsonLayerParameterFactory.buildGeoJsonLayer(featureCollection, layer);
                            self.map.addLayer(self.currentGeoJsonLayer);
                        }).fail(function () {
                            alert("Error fetching occurrences");
                        }).always(function () {
                            self.geoJsonAjax = undefined;
                        });
                }

                self.currentWmsLayer = L.tileLayer.wms(self.wmsUrl,
                    wmsLayerParameterFactory.createLayerParametersForDisplay(layer));

                self.map.addLayer(self.currentWmsLayer);

                ko.postbox.publish(
                    "tracking-action-event",
                    { "category": "atlas", "action": "layer-view", "label": layer }
                );
            }
        });
    };
});
