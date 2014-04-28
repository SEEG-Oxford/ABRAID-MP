/**
 * Apply KO bindings for the data validation page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, data:false*/
require(["require.conf"], function () {
    "use strict";

    require([
        "ko",
        "app/LogInViewModel",
        "app/MapView",
        "app/SelectedPointViewModel",
        "app/SelectedLayerViewModel",
        "app/CounterViewModel",
        "domReady!"
    ], function (ko, LogInViewModel, setupMap, SelectedPointViewModel, SelectedLayerViewModel, CounterViewModel, doc) {
            setupMap(baseUrl, data.wmsUrl, data.loggedIn);
            ko.applyBindings(
                new SelectedPointViewModel(baseUrl),
                doc.getElementById("datapointInfo")
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
                ko.applyBindings(
                    new LogInViewModel(baseUrl),
                    doc.getElementById("logIn")
                );
            }
        }
    );
});
