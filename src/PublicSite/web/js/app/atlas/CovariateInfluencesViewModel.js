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

        var activeRun =  ko.observable();
        self.covariateInfluences = ko.observable();
        self.effectCurvesLink = ko.computed(function () {
            return activeRun() ?
                baseUrl + "atlas/details/modelrun/" + activeRun().id + "/effectcurves.csv" :
                "#";
        }, self);

        var ajax;
        ko.postbox.subscribe("active-atlas-layer", function (layer) {
            activeRun(undefined);
            self.covariateInfluences(undefined);

            if (layer) {
                activeRun(layer.run);
                if (ajax) {
                    ajax.abort();
                }
                ajax = $.getJSON(baseUrl + "atlas/details/modelrun/" + layer.run.id + "/covariates")
                    .done(function (data) {
                        self.covariateInfluences(data);
                    }).always(function () {
                        ajax = undefined;
                    });
            }
        });
    };
});
