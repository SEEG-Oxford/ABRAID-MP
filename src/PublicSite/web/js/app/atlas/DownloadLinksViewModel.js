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

    return function (baseUrl, wmsUrl, wmsLayerParameterFactory) {
        var self = this;

        var activeLayer = ko.observable().subscribeTo("active-atlas-layer");

        self.png = ko.computed(function () {
            var wmsParams = activeLayer() ?
                wmsLayerParameterFactory.createLayerParametersForDownload(activeLayer()) : {};
            return activeLayer() ? wmsUrl + "?" + $.param(wmsParams) : "#";
        }, self);

        self.showPng = ko.computed(function () {
            return activeLayer() ? activeLayer().type !== "occurrences" : false;
        }, self);

        self.tif = ko.computed(function () {
            return activeLayer() ?
                baseUrl + "atlas/results/" + activeLayer().run.id + "_" + activeLayer().type + ".tif" : "#";
        }, self);

        self.showTif = ko.computed(function () {
            return activeLayer() ? activeLayer().type !== "occurrences" : false;
        }, self);

        self.occurrences = ko.computed(function () {
            return activeLayer() ?
                baseUrl + "atlas/data/modelrun/" + activeLayer().run.id + "/inputoccurrences.csv": "#";
        }, self);

        self.showOccurrences = ko.computed(function () {
            return activeLayer() ? activeLayer().run.automatic : false;
        }, self);
    };
});
