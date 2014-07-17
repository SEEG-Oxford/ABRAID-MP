/* Kick-start JS for the administration disease group page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, diseaseGroups:false, validatorDiseaseGroups:false*/
//Load base configuration, then load the app logic for this page.
require(["require.conf"], function () {
    "use strict";

    require(["ko",
        "app/DiseaseGroupsListViewModel",
        "app/DiseaseGroupSettingsViewModel",
        "app/ModelRunParametersViewModel",
        "app/DiseaseGroupAdministrationViewModel",
        "app/DiseaseGroupSetupViewModel",
        "navbar",
        "domReady!"
    ], function (ko, DiseaseGroupsListViewModel, DiseaseGroupSettingsViewModel, ModelRunParametersViewModel,
                 DiseaseGroupAdministrationViewModel, DiseaseGroupSetupViewModel, setupNavbar, doc) {
        setupNavbar();

        var diseaseGroupSelectedEventName = "disease-group-selected";

        var diseaseGroupsListViewModel =
            new DiseaseGroupsListViewModel(diseaseGroups, diseaseGroupSelectedEventName);
        ko.applyBindings(
            diseaseGroupsListViewModel,
            doc.getElementById("disease-groups-list")
        );

        var diseaseGroupSettingsViewModel =
            new DiseaseGroupSettingsViewModel(diseaseGroups, validatorDiseaseGroups, diseaseGroupSelectedEventName);
        var modelRunParametersViewModel =
            new ModelRunParametersViewModel(diseaseGroupSelectedEventName);
        var diseaseGroupAdministrationViewModel =
            new DiseaseGroupAdministrationViewModel(
                baseUrl, diseaseGroupSettingsViewModel, modelRunParametersViewModel, diseaseGroupSelectedEventName);
        ko.applyBindings(
            ko.validatedObservable(diseaseGroupAdministrationViewModel),
            doc.getElementById("disease-group-administration")
        );

        var diseaseGroupSetupViewModel = new DiseaseGroupSetupViewModel(baseUrl, diseaseGroupSelectedEventName);
        ko.applyBindings(
            ko.validatedObservable(diseaseGroupSetupViewModel),
            doc.getElementById("setup-body"));

        // Publish the initial state of the disease group drop-down list
        ko.postbox.publish(diseaseGroupSelectedEventName, diseaseGroupsListViewModel.selectedDiseaseGroup());
    });
});
