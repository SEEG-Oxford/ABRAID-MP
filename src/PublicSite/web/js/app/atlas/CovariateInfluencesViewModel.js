/* AMD defining the predictors table on the atlas view.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'selected-run' - published by LayerSelectorViewModel
 */
define([
    "ko",
    "jquery",
    "underscore"
], function (ko, $, _) {
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

        self.covariateInfluencesToPlot = ko.computed(function () {
            var covariates = self.covariateInfluences();
            if (covariates) {
                return _(covariates).filter(function (covariate) {
                    return covariate.meanInfluence > (100.0 / covariates.length);
                });
            } else {
                return [];
            }
        });
        self.maxEffectCurveValue = ko.computed(function () {
            var covariates = self.covariateInfluencesToPlot();
            if (covariates) {
                return _(covariates).chain().pluck("effectCurve").flatten().pluck("upperQuantile").max().value();
            } else {
                return undefined;
            }
        });

        self.minEffectCurveValue = ko.computed(function () {
            var covariates = self.covariateInfluencesToPlot();
            if (covariates) {
                return _(covariates).chain().pluck("effectCurve").flatten().pluck("lowerQuantile").min().value();
            } else {
                return undefined;
            }
        });

        self.activeCurve = ko.observable();

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
