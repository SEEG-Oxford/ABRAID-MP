/* Kick-start JS for the administration disease group page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, diseaseGroups:false, validatorDiseaseGroups:false*/
//Load base configuration, then load the app logic for this page.
require(["require.conf"], function () {
    "use strict";

    require(["ko",
        "app/DiseaseGroupsListViewModel",
        "app/MainSettingsViewModel",
        "app/ModelRunParametersViewModel",
        "navbar",
        "domReady!"
    ], function (ko, DiseaseGroupsListViewModel, MainSettingsViewModel, ModelRunParametersViewModel, setupNavbar, doc) {
        setupNavbar();

        var diseaseGroupSelectedEventName = "disease-group-selected";

        // Bind to view-models
        var diseaseGroupsListViewModel =
            new DiseaseGroupsListViewModel(baseUrl, diseaseGroups, diseaseGroupSelectedEventName);

        ko.applyBindings(
            diseaseGroupsListViewModel,
            doc.getElementById("disease-groups-list")
        );

        ko.applyBindings(
            new MainSettingsViewModel(baseUrl, diseaseGroups, validatorDiseaseGroups, diseaseGroupSelectedEventName),
            doc.getElementById("main-settings")
        );

        ko.applyBindings(
            new ModelRunParametersViewModel(baseUrl, diseaseGroupSelectedEventName),
            doc.getElementById("model-run-parameters")
        );

        // Publish the initial state of the disease group drop-down list
        ko.postbox.publish(diseaseGroupSelectedEventName, diseaseGroupsListViewModel.selectedDiseaseGroup());
    });
});
