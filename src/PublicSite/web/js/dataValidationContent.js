/**
 * Apply KO bindings for the data validation page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, wmsUrl:false, loggedIn:false, baseUrl: false, diseaseOccurrenceReviewCount: false*/
require(["require.conf"], function () {
    "use strict";

    require(["ko",
        "app/LogInViewModel",
        "app/MapView",
        "app/SelectedPointViewModel",
        "app/SelectedLayerViewModel",
        "app/CounterViewModel",
        "domReady!"],
        function(ko, LogInViewModel, MapView, SelectedPointViewModel, SelectedLayerViewModel, CounterViewModel, doc) {
            MapView(wmsUrl, loggedIn);
            ko.applyBindings(new SelectedPointViewModel(baseUrl), doc.getElementById("datapointInfo"));
            ko.applyBindings(new SelectedLayerViewModel(), doc.getElementById("layerSelector"));
            if (loggedIn) {
                ko.applyBindings(new CounterViewModel(diseaseOccurrenceReviewCount), doc.getElementById("counterDiv"));
            } else {
                ko.applyBindings(new LogInViewModel(baseUrl), doc.getElementById("logIn"));
            }
        }
    );
});
