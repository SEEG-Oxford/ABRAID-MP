/* AMD combines and POSTs all data across multiple panels on administration page.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery",
    "app/admin/diseasegroups/DiseaseGroupPayload"
], function (ko, $, DiseaseGroupPayload) {
    "use strict";

    return function (baseUrl, refresh,
                     diseaseGroupSettingsViewModel, modelRunParametersViewModel, diseaseExtentParametersViewModel,
                     diseaseGroupSelectedEventName, diseaseGroupSavedEventName) {

        var self = this;
        self.diseaseGroupSettingsViewModel = diseaseGroupSettingsViewModel;
        self.modelRunParametersViewModel = modelRunParametersViewModel;
        self.diseaseExtentParametersViewModel = diseaseExtentParametersViewModel;

        var diseaseGroupId;

        self.isSubmitting = ko.observable(false);
        self.submit = function () {
            self.isSubmitting(true);
            var data = new DiseaseGroupPayload(self.diseaseGroupSettingsViewModel,
                                               self.modelRunParametersViewModel,
                                               self.diseaseExtentParametersViewModel);
            var url, doneCallback, alwaysCallback = {};
            if (diseaseGroupId) {
                // Disease group already has an ID, so changes should be saved to the existing row in database
                url =  baseUrl + "admin/diseasegroup/" + diseaseGroupId + "/save";
                doneCallback = function () {
                    self.notice({ message: "Saved successfully", priority: "success" });
                    ko.postbox.publish(diseaseGroupSavedEventName, diseaseGroupId);
                };
                alwaysCallback = function () { self.isSubmitting(false); };
            } else {
                // New disease group does not yet have an ID, so add new row to database and refresh page to reload list
                url = baseUrl + "admin/diseasegroup/add";
                doneCallback = refresh;
            }
            $.ajax({
                method: "POST",
                url: url,
                data: JSON.stringify(data),
                contentType : "application/json"
            })
                .done(doneCallback)
                .fail(function () { self.notice({ message: "Error saving disease group", priority: "warning"}); })
                .always(alwaysCallback);
        };
        self.notice = ko.observable();

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            diseaseGroupId = diseaseGroup.id;
        });
    };
});
