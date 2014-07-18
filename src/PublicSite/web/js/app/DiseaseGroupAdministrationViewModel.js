/*
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery",
    "app/DiseaseGroupPayload"
], function (ko, $, DiseaseGroupPayload) {
    "use strict";

    return function (
        baseUrl, diseaseGroupSettingsViewModel, modelRunParametersViewModel, diseaseGroupSelectedEventName) {

        var self = this;
        self.diseaseGroupSettingsViewModel = diseaseGroupSettingsViewModel;
        self.modelRunParametersViewModel = modelRunParametersViewModel;

        var diseaseGroupId;
        self.isSubmitting = ko.observable(false);
        self.submit = function () {
            self.isSubmitting(true);
            var data = new DiseaseGroupPayload(self.diseaseGroupSettingsViewModel, self.modelRunParametersViewModel);
            $.ajax({
                method: "POST",
                url: baseUrl + "admin/diseasegroup/" + diseaseGroupId + "/save",
                data: JSON.stringify(data),
                contentType : "application/json"
            })
                .done(function () { self.notice({ message: "Saved successfully", priority: "success" }); })
                .fail(function () { self.notice({ message: "Error saving", priority: "warning"}); })
                .always(function () { self.isSubmitting(false); });
        };

        self.notice = ko.observable();

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            diseaseGroupId = diseaseGroup.id;
        });
    };
});
