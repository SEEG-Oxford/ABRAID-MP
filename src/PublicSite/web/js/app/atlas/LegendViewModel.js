/* AMD defining the view model for the atlas legend.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'active-atlas-type'
 */
define(["ko"], function (ko) {
    "use strict";

    return function () {
        var self = this;
        var activeLayer = ko.observable().subscribeTo("active-atlas-layer");

        self.type = ko.computed(function () {
            return activeLayer() ? activeLayer().type : undefined;
        });

        self.startDate = ko.computed(function () {
            return activeLayer() ? activeLayer().run.rangeStart : "???";
        });

        self.endDate = ko.computed(function () {
            return activeLayer() ? activeLayer().run.rangeEnd : "???";
        });
    };
});
