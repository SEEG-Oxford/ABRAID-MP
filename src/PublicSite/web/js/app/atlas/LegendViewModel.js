/* AMD defining the view model for the atlas legend.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'active-atlas-layer'
 */
define(["ko"], function (ko) {
    "use strict";

    return function () {
        var self = this;

        var activeLayer = ko.observable().subscribeTo("active-atlas-layer");

        self.type = ko.computed(function () {
            var layer = activeLayer() || "";
            var isExtentLayer = layer.indexOf("extent", layer.length - "extent".length) !== -1;
            return isExtentLayer ? "discrete" : "continuous";
        }, self);
    };
});
