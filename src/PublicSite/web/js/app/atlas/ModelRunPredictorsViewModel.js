/* AMD defining the predictors table on the atlas view.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'selected-run-covariates' - published by LayerSelectorViewModel
 */
define(["ko"], function (ko) {
    "use strict";

    return function () {
        var self = this;

        self.covariateInfluences = ko.observable().subscribeTo("selected-run-covariates");
    };
});
