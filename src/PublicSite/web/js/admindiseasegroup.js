/* Kick-start JS for the administration disease group page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, initialData:false*/
//Load base configuration, then load the app logic for this page.
require(["require.conf"], function () {
    "use strict";

    require(["ko",
        "app/DiseaseGroupsListViewModel",
        "app/DiseaseGroupSetupViewModel",
        "navbar",
        "domReady!"
    ], function (ko, DiseaseGroupsListViewModel, DiseaseGroupSetupViewModel, setupNavbar, doc) {
        setupNavbar();

        var diseaseGroupSelectedEventName = "disease-group-selected";

        // Bind to view-models
        var diseaseGroupsListViewModel =
            new DiseaseGroupsListViewModel(baseUrl, initialData, diseaseGroupSelectedEventName);

        ko.applyBindings(
            diseaseGroupsListViewModel,
            doc.getElementById("disease-groups-list"));

        ko.applyBindings(
            ko.validatedObservable(new DiseaseGroupSetupViewModel(baseUrl, diseaseGroupSelectedEventName)),
            doc.getElementById("setup-body"));

        // Publish the initial state of the disease group drop-down list
        ko.postbox.publish(diseaseGroupSelectedEventName, diseaseGroupsListViewModel.selectedDiseaseGroup());
    });
});
