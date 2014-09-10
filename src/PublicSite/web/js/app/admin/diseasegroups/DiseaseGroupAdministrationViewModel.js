/* AMD combines and POSTs all data across multiple panels on administration page.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery",
    "app/admin/diseasegroups/DiseaseGroupPayload",
    "shared/app/BaseFormViewModel"
], function (ko, $, DiseaseGroupPayload, BaseFormViewModel) {
    "use strict";

    return function (baseUrl, refresh,
                     diseaseGroupSettingsViewModel, modelRunParametersViewModel, diseaseExtentParametersViewModel,
                     diseaseGroupSelectedEventName, diseaseGroupSavedEventName) {

        var self = this;
        var messages = { error: "Server error. Please refresh the page and try again." };
        BaseFormViewModel.call(self, true, false, undefined, undefined, messages);

        self.diseaseGroupSettingsViewModel = diseaseGroupSettingsViewModel;
        self.modelRunParametersViewModel = modelRunParametersViewModel;
        self.diseaseExtentParametersViewModel = diseaseExtentParametersViewModel;

        var diseaseGroupId;
        self.buildSubmissionUrl = function () {
            return baseUrl + "admin/diseases/" + (diseaseGroupId ?  (diseaseGroupId + "/save") : "add");
        };

        var baseSuccessHandler = self.successHandler;
        self.successHandler = function (data, textStatus, xhr) {
            baseSuccessHandler(data, textStatus, xhr);
            if (diseaseGroupId) {
                ko.postbox.publish(diseaseGroupSavedEventName, diseaseGroupId);
            } else {
                refresh();
            }
        };

        self.buildSubmissionData = function () {
            return new DiseaseGroupPayload(
                self.diseaseGroupSettingsViewModel,
                self.modelRunParametersViewModel,
                self.diseaseExtentParametersViewModel);
        };

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            diseaseGroupId = diseaseGroup.id;
        });
    };
});
