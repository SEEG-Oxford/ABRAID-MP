/* Kick-start JS for the administration disease group page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, diseaseGroups:false, validatorDiseaseGroups:false*/
//Load base configuration, then load the app logic for this page.
require([baseUrl + "js/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "app/DiseaseGroupsListViewModel",
        "app/DiseaseGroupSettingsViewModel",
        "app/ModelRunParametersViewModel",
        "app/DiseaseGroupSetupViewModel",
        "domReady!",
        "navbar"
    ], function (ko, DiseaseGroupsListViewModel, DiseaseGroupSettingsViewModel, ModelRunParametersViewModel,
                 DiseaseGroupSetupViewModel, doc) {

        var diseaseGroupSelectedEventName = "disease-group-selected";

        // Bind to view-models
        var diseaseGroupsListViewModel =
            new DiseaseGroupsListViewModel(baseUrl, diseaseGroups, diseaseGroupSelectedEventName);

        ko.applyBindings(
            diseaseGroupsListViewModel,
            doc.getElementById("disease-groups-list")
        );

        ko.applyBindings(
            ko.validatedObservable(new DiseaseGroupSettingsViewModel(
                baseUrl, diseaseGroups, validatorDiseaseGroups, diseaseGroupSelectedEventName)),
            doc.getElementById("disease-group-settings")
        );

        ko.applyBindings(
            ko.validatedObservable(new ModelRunParametersViewModel(baseUrl, diseaseGroupSelectedEventName)),
            doc.getElementById("model-run-parameters")
        );

        ko.applyBindings(
            ko.validatedObservable(new DiseaseGroupSetupViewModel(baseUrl, diseaseGroupSelectedEventName)),
            doc.getElementById("setup-body"));

        // Publish the initial state of the disease group drop-down list
        ko.postbox.publish(diseaseGroupSelectedEventName, diseaseGroupsListViewModel.selectedDiseaseGroup());
    });
});
