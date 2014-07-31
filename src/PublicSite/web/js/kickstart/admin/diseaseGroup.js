/* Kick-start JS for the administration disease group page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, window:false, baseUrl:false, diseaseGroups:false, validatorDiseaseGroups:false*/
//Load base configuration, then load the app logic for this page.
require([baseUrl + "js/require.conf.js"], function () {
    "use strict";


    require([
        "ko",
        "app/admin/diseasegroups/DiseaseGroupAdministrationViewModel",
        "app/admin/diseasegroups/DiseaseGroupsListViewModel",
        "app/admin/diseasegroups/DiseaseGroupSettingsViewModel",
        "app/admin/diseasegroups/ModelRunParametersViewModel",
        "app/admin/diseasegroups/DiseaseGroupSetupViewModel",
        "domReady!",
        "navbar"
    ], function (ko, DiseaseExtentParametersViewModel, DiseaseGroupAdministrationViewModel, DiseaseGroupsListViewModel,
                 DiseaseGroupSettingsViewModel, ModelRunParametersViewModel, DiseaseGroupSetupViewModel, doc) {

        var diseaseGroupSelectedEventName = "disease-group-selected";
        var diseaseGroupSavedEventName = "disease-group-saved";

        var diseaseGroupsListViewModel =
            new DiseaseGroupsListViewModel(diseaseGroups, diseaseGroupSelectedEventName);
        ko.applyBindings(
            diseaseGroupsListViewModel,
            doc.getElementById("disease-groups-list")
        );

        var refresh = function () {
            window.top.location.reload();
        };

        var diseaseGroupAdministrationViewModel =
            new DiseaseGroupAdministrationViewModel(
                baseUrl,
                refresh,
                new DiseaseGroupSettingsViewModel(diseaseGroups, validatorDiseaseGroups, diseaseGroupSelectedEventName),
                new ModelRunParametersViewModel(diseaseGroupSelectedEventName),
                new DiseaseExtentParametersViewModel(diseaseGroupSelectedEventName),
                diseaseGroupSelectedEventName,
                diseaseGroupSavedEventName
            );
        ko.applyBindings(
            ko.validatedObservable(diseaseGroupAdministrationViewModel),
            doc.getElementById("disease-group-administration")
        );

        var diseaseGroupSetupViewModel =
            new DiseaseGroupSetupViewModel(baseUrl, diseaseGroupSelectedEventName, diseaseGroupSavedEventName);
        ko.applyBindings(
            ko.validatedObservable(diseaseGroupSetupViewModel),
            doc.getElementById("setup-body"));

        // Publish the initial state of the disease group drop-down list
        ko.postbox.publish(diseaseGroupSelectedEventName, diseaseGroupsListViewModel.selectedDiseaseGroup());
    });
});
