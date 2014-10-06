/**
 * JS file for adding Leaflet map and layers.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "L",
    "ko"
], function (L, ko) {
    "use strict";

    return function (wmsUrl) {
        var map = L.map("map", {
            attributionControl: false,
            zoomControl: false,
            zoomsliderControl: true,
            maxBounds: [ [-60, -220], [85, 220] ],
            maxZoom: 7,
            minZoom: 3,
            animate: true,
            crs: L.CRS.EPSG4326,
            bounceAtZoomLimits: false
        }).fitWorld();

        var currentLayer;
        ko.postbox.subscribe("layer-changed", function (payload) {
            if (currentLayer) {
                map.removeLayer(currentLayer);
            }

            currentLayer = L.tileLayer.wms(wmsUrl, {
                layers: [payload],
                format: "image/png",
                styles: "abraid_raster",
                reuseTiles: true
            });

            map.addLayer(currentLayer);
        });
    };
});
