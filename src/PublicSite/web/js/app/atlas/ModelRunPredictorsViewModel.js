/* AMD defining the predictors table on the atlas view.
 * Copyright (c) 2014 University of Oxford
 */
define(["ko"], function (ko) {
    "use strict";

    return function () {
        var self = this;

        self.activeLayer = ko.observable().subscribeTo("active-atlas-layer");

    };
});
