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
                baseUrl + "atlas/details/modelrun/" + activeRun().id + "/effectcurves.csv" :
                "#";
        }, self);

        self.covariateInfluencesToPlot = ko.computed(function () {
            var covariates = self.covariateInfluences();
            if (covariates) {
                var filtered =  _(covariates).filter(function (covariate) {
                    return covariate.meanInfluence > (100.0 / covariates.length);
                });
                var minValue = _(filtered).chain().pluck("effectCurve").flatten().pluck("lowerQuantile").min().value();
                var maxValue = _(filtered).chain().pluck("effectCurve").flatten().pluck("upperQuantile").max().value();
                return _(filtered).map(function (covariate) {
                    return { covariate: covariate, min: minValue, max: maxValue };
                });
            } else {
                return [];
            }
        });

        self.activeCurve = ko.observable();

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
