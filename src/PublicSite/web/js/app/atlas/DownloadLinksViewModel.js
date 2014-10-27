/* AMD defining the download links for the atlas view.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'active-atlas-layer' - published by LayerSelectorViewModel
 */
define([
    "ko",
    "jquery"
], function (ko, $) {
    "use strict";

    return function (baseUrl, wmsUrl) {
        var self = this;

        self.activeLayer = ko.observable().subscribeTo("active-atlas-layer");

        self.png = ko.computed(function () {
            var wmsParams = {
                service: "WMS",
                version: "1.1.0",
                request: "GetMap",
                styles: "abraid_raster",
                bbox: "-180.0,-60.0,180.0,85.0",
                width: 1656,
                height: 667,
                srs: "EPSG:4326",
                format: "image/png",
                layers: "abraid:" + self.activeLayer()
            };

            return self.activeLayer() ? wmsUrl + "?" + $.param(wmsParams) : "#";
        }, self);

        self.tif = ko.computed(function () {
            return self.activeLayer() ? baseUrl + "atlas/results/" + self.activeLayer() + ".tif" : "#";
        }, self);
    };
});
