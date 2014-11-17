/* Apply KO bindings for the data validation page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, data:false, alert:false*/
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "jquery",
        "app/datavalidation/CounterViewModel",
        "app/datavalidation/HelpTextViewModel",
        "app/datavalidation/LatestOccurrencesViewModel",
        "app/datavalidation/MapView",
        "app/datavalidation/SelectedPointViewModel",
        "app/datavalidation/SelectedLayerViewModel",
        "app/datavalidation/SelectedAdminUnitViewModel",
        "app/datavalidation/SidePanelViewModel",
        "app/datavalidation/SpinnerViewModel",
        "domReady!"
    ], function (ko, $, CounterViewModel, HelpTextViewModel, LatestOccurrencesViewModel, setupMap, SelectedPointViewModel,
                 SelectedLayerViewModel, SelectedAdminUnitViewModel, SidePanelViewModel, SpinnerViewModel, doc) {
            setupMap(baseUrl, data.wmsUrl, data.loggedIn, alert);
            ko.applyBindings(
                new SpinnerViewModel(),
                doc.getElementById("spinner")
            );

            ko.applyBindings(
                new HelpTextViewModel(),
                doc.getElementById("helpText")
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
                new LatestOccurrencesViewModel(baseUrl),
                doc.getElementById("latestOccurrencesPanel")
            );

            ko.applyBindings(
                new SelectedLayerViewModel(data.diseaseInterests, data.allOtherDiseases),
                doc.getElementById("layerSelector")
            );
        }
    );
});
