/* Apply KO bindings for the data validation page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, data:false, alert:false, setTimeout:false*/
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "jquery",
        "app/LeafletMapFactory",
        "app/datavalidation/CounterViewModel",
        "app/datavalidation/LatestOccurrencesViewModel",
        "app/datavalidation/MapView",
        "app/datavalidation/ModalView",
        "app/datavalidation/SelectedPointViewModel",
        "app/datavalidation/SelectedLayerViewModel",
        "app/datavalidation/SelectedAdminUnitViewModel",
        "app/datavalidation/SidePanelViewModel",
        "app/datavalidation/SpinnerViewModel",
        "domReady!",
        "analytics"
    ], function (ko, $, LeafletMapFactory, CounterViewModel, LatestOccurrencesViewModel, MapView, ModalView,
                 SelectedPointViewModel, SelectedLayerViewModel, SelectedAdminUnitViewModel, SidePanelViewModel,
                 SpinnerViewModel, doc) {
            var map = new MapView(baseUrl, data.wmsUrl, data.loggedIn, new LeafletMapFactory(), alert, setTimeout); // jshint ignore:line
            var modal = new ModalView(doc.getElementById("helpModal"), data.showHelpText);  // jshint ignore:line

            ko.applyBindings(
                new SpinnerViewModel(setTimeout),
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
                new LatestOccurrencesViewModel(baseUrl, data.loggedIn),
                doc.getElementById("latestOccurrencesPanel")
            );

            ko.applyBindings(
                new SelectedLayerViewModel(data.diseaseInterests, data.allOtherDiseases,
                    data.diseasesRequiringExtentInput, data.diseasesRequiringOccurrenceInput),
                doc.getElementById("layerSelector")
            );
        }
    );
});
