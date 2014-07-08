/**
 * Apply KO bindings for the data validation page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, data:false, window:false, alert:false*/
require(["require.conf"], function () {
    "use strict";

    require([
        "ko",
        "jquery",
        "app/CounterViewModel",
        "app/LogInViewModel",
        "app/MapView",
        "app/SelectedPointViewModel",
        "app/SelectedLayerViewModel",
        "app/SelectedAdminUnitViewModel",
        "app/SidePanelViewModel",
        "app/SpinnerViewModel",
        "domReady!"
    ], function (ko, $, CounterViewModel, LogInViewModel, setupMap, SelectedPointViewModel, SelectedLayerViewModel,
                 SelectedAdminUnitViewModel, SidePanelViewModel, SpinnerViewModel, doc) {
            setupMap(baseUrl, data.wmsUrl, data.loggedIn, alert);
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

                var forceRebind = function () {
                    // Force the observables to bind "NOW!" this works around the fact that FF's form auto fill doesn't
                    // trigger the events that would cause the bind. See:
                    // https://github.com/knockout/knockout/issues/648
                    // https://bugzilla.mozilla.org/show_bug.cgi?id=87943
                    $("input.ffAutoFillHack").keyup();
                };

                ko.applyBindings(
                    new LogInViewModel(baseUrl, refresh, forceRebind),
                    doc.getElementById("sidePanelLogin")
                );
            }
        }
    );
});
