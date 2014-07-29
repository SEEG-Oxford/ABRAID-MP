/* AMD combines and POSTs all data across multiple panels on administration page.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery",
    "app/admin/diseasegroup/DiseaseGroupPayload"
], function (ko, $, DiseaseGroupPayload) {
    "use strict";

    return function (baseUrl, diseaseGroupSettingsViewModel, modelRunParametersViewModel,
                     diseaseExtentParametersViewModel, diseaseGroupSelectedEventName) {

        var self = this;
        self.diseaseGroupSettingsViewModel = diseaseGroupSettingsViewModel;
        self.modelRunParametersViewModel = modelRunParametersViewModel;
        self.diseaseExtentParametersViewModel = diseaseExtentParametersViewModel;

        var diseaseGroupId;
        var getUrl = function () {
            if (diseaseGroupId) {
                return baseUrl + "admin/diseasegroup/" + diseaseGroupId + "/save";
            } else {
                return baseUrl + "admin/diseasegroup/add";
            }
        };

        self.isSubmitting = ko.observable(false);
        self.submit = function () {
            self.isSubmitting(true);
            var data = new DiseaseGroupPayload(self.diseaseGroupSettingsViewModel,
                                               self.modelRunParametersViewModel,
                                               self.diseaseExtentParametersViewModel);
            $.ajax({
                method: "POST",
                url: getUrl(),
                data: JSON.stringify(data),
                contentType : "application/json"
            })
                .done(function () { self.notice({ message: "Saved successfully", priority: "success" }); })
                .fail(function () { self.notice({ message: "Error saving disease group", priority: "warning"}); })
                .always(function () { self.isSubmitting(false); });
        };
        self.notice = ko.observable();

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            diseaseGroupId = diseaseGroup.id;
        });
    };
});
