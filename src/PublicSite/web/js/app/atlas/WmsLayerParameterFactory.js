/* A factory for generating the standard ABRAID WMS request/layer options for a given layer name.
 * Copyright (c) 2014 University of Oxford
 */
define(["underscore"], function (_) {
    "use strict";

    return function () {
        var self = this;

        self.createLayerParametersForDisplay = function (layer) {
            var isExtentLayer = (layer.type === "extent");
            var isOccurrenceLayer = (layer.type === "occurrences");

            var layerName = layer.run.id + "_" + layer.type;
            if (isExtentLayer) {
                layerName = "atlas_extent_layer";
            } else if (isOccurrenceLayer)  {
                layerName = "base_layer";
            }

            var params = {
                layers: "abraid:" + layerName,
                format: "image/png",
                reuseTiles: true, // Enable Leaflet reuse of tiles within single page view
                tiled: true // Enable GeoWebCaching reuse of tiles between all users/page views
            };

            if (isExtentLayer) {
                params = _(params).extend({
                    cql_filter: "model_run_name='" + layer.run.id + "'" // jshint ignore:line
                });
            }

            return params;
        };

        var createBasicDownloadParameters = function () {
            return {
                service: "WMS",
                version: "1.1.0",
                request: "GetMap",
                srs: "EPSG:4326",
                format: "image/png"
            };
        };

        self.createLayerParametersForDownload = function (layer) {
            var params = self.createLayerParametersForDisplay(layer);
            params = _(params).extend(createBasicDownloadParameters());
            params = _(params).extend({
                bbox: "-180.1,-60.0,180.0,85.0", // BBox min set to -180.1 due to bug in Geoserver (should be -180.0)
                width: 1656,
                height: 667
            });
            return params;
        };

        self.createLayerParametersForCroppedDownload = function (layer, gaulCode, box) {
            var params = createBasicDownloadParameters();
            params = _(params).extend({
                bbox: box.bbox,
                width: box.width,
                height: box.height,
                cql_filter: "INCLUDE;gaul_code=" + gaulCode + ";gaul_code<>" + gaulCode + ";INCLUDE", // jshint ignore:line
                layers: "abraid:" + layer + "_mean,abraid:country,abraid:country,abraid:country",
                styles: "abraid_mean,abraid_mask,country_white,country_borders",
                bgcolor: "#eef7fa"
            });
            return params;
        };
    };
});
