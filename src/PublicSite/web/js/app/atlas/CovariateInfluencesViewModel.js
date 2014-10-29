/* AMD defining the predictors table on the atlas view.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'selected-run' - published by LayerSelectorViewModel
 */
define([
    "ko",
    "jquery"
], function (ko, $) {
    "use strict";

    return function (baseUrl) {
        var self = this;

        self.covariateInfluences = ko.observable([]);

        ko.postbox.subscribe("selected-run", function (run) {
            if (run.id) {
                $.getJSON(baseUrl + "atlas/details/modelrun/" + run.id + "/covariates")
                    .done(function (covariates) {
                        self.covariateInfluences(covariates);
                    }).fail(function () {
                        self.covariateInfluences();
                    });
            }
        });
    };
});
