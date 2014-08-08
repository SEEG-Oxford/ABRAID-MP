/* Apply KO bindings for the data validation page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, data:false, window:false, alert:false*/
require([baseUrl + "js/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "jquery",
        "app/datavalidation/CounterViewModel",
        "app/datavalidation/LogInFormViewModel",
        "app/datavalidation/MapView",
        "app/datavalidation/SelectedPointViewModel",
        "app/datavalidation/SelectedLayerViewModel",
        "app/datavalidation/SelectedAdminUnitViewModel",
        "app/datavalidation/SidePanelViewModel",
        "app/datavalidation/SpinnerViewModel",
        "domReady!"
    ], function (ko, $, CounterViewModel, LogInFormViewModel, setupMap, SelectedPointViewModel, SelectedLayerViewModel,
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
                    $("input.ffAutoFillHack").trigger("change");
                };

                ko.applyBindings(
                    new LogInFormViewModel(baseUrl, refresh, forceRebind),
                    doc.getElementById("sidePanelLogin")
                );
            }
        }
    );
});
