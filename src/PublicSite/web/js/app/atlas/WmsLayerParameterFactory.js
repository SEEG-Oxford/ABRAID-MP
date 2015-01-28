/* A factory for generating the standard ABRAID WMS request/layer options for a given layer name.
 * Copyright (c) 2014 University of Oxford
 */
define(["underscore"], function (_) {
    "use strict";

    return function () {
        var self = this;

        self.createLayerParametersForDisplay = function (name) {
            var split = name.split("_");
            var type = split[split.length - 1];
            var isExtentLayer = type === "extent";
            var style = "abraid_" + type;
            var layerName = isExtentLayer ? "atlas_extent_layer"  : name;


            var params = {
                layers: "abraid:" + layerName,
                format: "image/png",
                styles: style,
                reuseTiles: true, // Enable Leaflet reuse of tiles within single page view
                tiled: true // Enable GeoWebCaching reuse of tiles between all users/page views
            };

            if (isExtentLayer) {
                var runName = name.substr(0, name.lastIndexOf("_"));
                params = _(params).extend({
                    cql_filter: "model_run_name='" + runName + "'" // jshint ignore:line
                });
            }

            return params;
        };

        self.createLayerParametersForDownload = function (name) {
            var params = self.createLayerParametersForDisplay(name);
            params = _(params).extend({
                service: "WMS",
                version: "1.1.0",
                request: "GetMap",
                bbox: "-180.1,-60.0,180.0,85.0", // BBox min set to -180.1 due to bug in Geoserver (should be -180.0)
                width: 1656,
                height: 667,
                srs: "EPSG:4326"
            });
            return params;
        };
    };
});