/**
 * JS file for adding Leaflet map and layers.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'admin-unit-reviewed' - published by SelectedAdminUnitViewModel
 * -- 'admin-unit-selected' - published by MapView
 * -- 'layers-changed'      - published by SelectedLayerViewModel
 * -- 'occurrence-reviewed' - published by SelectedPointViewModel
 * - Events published:
 * -- 'admin-unit-selected'
 * -- 'admin-units-to-be-reviewed'
 * -- 'point-selected'
 * -- 'no-features-to-review' if
 * --- the FeatureCollection data is empty
 * --- the last point is remove from its layer
 */
define([
    "L"
], function (L) {
    "use strict";

    return function (baseUrl, wmsUrl, layers) {

        /** MAP AND BASE LAYER */

        // Initialise map at "map" div
        var map = L.map("map", {
            attributionControl: false,
            zoomControl: false,
            zoomsliderControl: true,
            maxBounds: [ [-60, -220], [85, 220] ],
            maxZoom: 7,
            minZoom: 2,
            animate: true,
            bounceAtZoomLimits: false
        }).fitWorld();

        var layer = "abraid:" + layers[0].runs[0].name + "_mean";
        L.tileLayer.wms(wmsUrl, {
            layers: [layer],
            format: "image/png",
            styles: "abraid_raster",
            reuseTiles: true
        }).addTo(map);
    };
});
