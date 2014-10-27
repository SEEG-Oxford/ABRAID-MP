/**
 * AMD for adding Leaflet map and layers for the atlas view.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'active-atlas-layer' - published by LayerSelectorViewModel
 */
define([
    "L",
    "ko"
], function (L, ko) {
    "use strict";

    return function (wmsUrl) {
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

        self.currentLayer = undefined;

        ko.postbox.subscribe("active-atlas-layer", function (layer) {
            if (self.currentLayer) {
                self.map.removeLayer(self.currentLayer);
                self.currentLayer = undefined;
            }

            if (layer.name) {
                self.currentLayer = L.tileLayer.wms(self.wmsUrl, {
                    layers: [layer.name],
                    format: "image/png",
                    styles: "abraid_raster",
                    reuseTiles: true
                });

                self.map.addLayer(self.currentLayer);
            }
        });
    };
});
