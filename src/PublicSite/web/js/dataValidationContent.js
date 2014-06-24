/**
 * Apply KO bindings for the data validation page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, data:false, window:false, alert:false*/
require(["require.conf"], function () {
    "use strict";

    require([
        "ko",
        "app/CounterViewModel",
        "app/LogInViewModel",
        "app/MapView",
        "app/SelectedPointViewModel",
        "app/SelectedLayerViewModel",
        "app/SelectedAdminUnitViewModel",
        "app/SidePanelViewModel",
        "app/SpinnerViewModel",
        "domReady!"
    ], function (ko, CounterViewModel, LogInViewModel, setupMap, SelectedPointViewModel, SelectedLayerViewModel,
                 SelectedAdminUnitViewModel, SidePanelViewModel, SpinnerViewModel, doc) {
            setupMap(baseUrl, data.wmsUrl, data.loggedIn);
            ko.applyBindings(
                new SpinnerViewModel(),
                doc.getElementById("spinner")
            );
            ko.applyBindings(
                new SidePanelViewModel(
                    new SelectedPointViewModel(baseUrl, alert,
                        new CounterViewModel(data.diseaseOccurrenceReviewCount, "occurrence-reviewed")),
                    new SelectedAdminUnitViewModel(baseUrl, alert,
                        new CounterViewModel(data.adminUnitReviewCount, "admin-unit-reviewed"))
                    ),
                doc.getElementById("sidePanelContent")
            );
            ko.applyBindings(
                new SelectedLayerViewModel(data.diseaseInterests, data.allOtherDiseases),
                doc.getElementById("layerSelector")
            );
            if (!data.loggedIn) {
                var refresh = function () {
                    // Refresh function may change, according to location of iframe (eg on TGHN site)
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
