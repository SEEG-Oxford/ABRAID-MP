/**
 * Apply KO bindings for the data validation page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, data:false, window:false*/
require(["require.conf"], function () {
    "use strict";

    require([
        "ko",
        "app/LogInViewModel",
        "app/MapView",
        "app/SelectedFeatureViewModel",
        "app/SelectedLayerViewModel",
        "app/CounterViewModel",
        "domReady!"
    ], function (ko, LogInViewModel, setupMap, SelectedFeatureViewModel, SelectedLayerViewModel, CounterViewModel, doc) {
            setupMap(baseUrl, data.wmsUrl, data.loggedIn);
            ko.applyBindings(
                new SelectedFeatureViewModel(baseUrl),
                doc.getElementById("featureInfo")
            );
            ko.applyBindings(
                new SelectedLayerViewModel(data.diseaseInterests, data.allOtherDiseases),
                doc.getElementById("layerSelector")
            );
            if (data.loggedIn) {
                ko.applyBindings(
                    new CounterViewModel(data.diseaseOccurrenceReviewCount),
                    doc.getElementById("counterDiv")
                );
            } else {
                var refresh = function () {
                    // Maybe check if TGHN
                    window.top.location.reload();
                };
                ko.applyBindings(
                    new LogInViewModel(baseUrl, refresh),
                    doc.getElementById("logIn")
                );
            }
        }
    );
});
