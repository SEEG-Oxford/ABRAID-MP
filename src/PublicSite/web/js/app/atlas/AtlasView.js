/**
 * AMD for adding Leaflet map and layers for the atlas view.
 * Copyright (c) 2014 University of Oxford
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

        ko.postbox.subscribe("layer-changed", function (payload) {
            if (self.currentLayer) {
                self.map.removeLayer(self.currentLayer);
            }

            self.currentLayer = L.tileLayer.wms(self.wmsUrl, {
                layers: [payload],
                format: "image/png",
                styles: "abraid_raster",
                reuseTiles: true
            });

            self.map.addLayer(self.currentLayer);
        });
    };
});
