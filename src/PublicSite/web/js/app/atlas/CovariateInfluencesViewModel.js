/* AMD defining the predictors table on the atlas view.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'selected-run' - published by LayerSelectorViewModel
 */
define(["ko"], function (ko) {
    "use strict";

    return function () {
        var self = this;

        self.covariateInfluences = ko.observable([]);

        ko.postbox.subscribe("selected-run", function (run) {
            self.covariateInfluences(run.covariates || []);
        });
    };
});
