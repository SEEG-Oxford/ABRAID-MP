/* AMD defining the statistics table on the atlas view.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'active-atlas-layer' - published by LayerSelectorViewModel
 */
define(["ko"], function (ko) {
    "use strict";

    return function () {
        var self = this;

        self.statistics = ko.observable({});

        ko.postbox.subscribe("active-atlas-layer", function (layer) {
            self.statistics(layer.statistics);
        });
    };
});
