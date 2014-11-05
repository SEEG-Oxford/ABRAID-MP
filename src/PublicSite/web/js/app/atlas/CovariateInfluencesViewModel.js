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
                baseUrl + "atlas/details/modelrun/" + activeRun() + "/effectcurves.csv" :
                "#";
        }, self);

        var ajax;
        ko.postbox.subscribe("selected-run", function (run) {
            activeRun(undefined);
            self.covariateInfluences(undefined);

            if (run && run.id) {
                activeRun(run.id);
                if (ajax) {
                    ajax.abort();
                }
                ajax = $.getJSON(baseUrl + "atlas/details/modelrun/" + run.id + "/covariates")
                    .done(function (data) {
                        self.covariateInfluences(data);
                    });
            }
        });
    };
});
