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

        var activeLayer = ko.observable().subscribeTo("active-atlas-layer");
        var activeRun = ko.observable().subscribeTo("selected-run");

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
                layers: "abraid:" + activeLayer()
            };

            return activeLayer() ? wmsUrl + "?" + $.param(wmsParams) : "#";
        }, self);

        self.tif = ko.computed(function () {
            return activeLayer() ? baseUrl + "atlas/results/" + activeLayer() + ".tif" : "#";
        }, self);

        self.occurrences = ko.computed(function () {
            return activeRun() ?
                baseUrl + "atlas/details/modelrun/" + activeRun().id + "/inputoccurrences.csv": "#";
        }, self);

        self.showOccurrences = ko.computed(function () {
            return activeRun() ? activeRun().automatic : false;
        }, self);
    };
});
