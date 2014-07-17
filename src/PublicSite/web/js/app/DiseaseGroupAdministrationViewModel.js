/*
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery"
], function (ko, $) {
    "use strict";

    return function (baseUrl, diseaseGroupSettingsViewModel, modelRunParametersViewModel, diseaseGroupSelectedEventName) {
        var self = this;
        self.diseaseGroupSettingsViewModel = diseaseGroupSettingsViewModel;
        self.modelRunParametersViewModel = modelRunParametersViewModel;

        var diseaseGroupId;
        self.submit = function () {
            var data = {
                settings: diseaseGroupSettingsViewModel.data(),
                runParameters: modelRunParametersViewModel.data()
            };

            $.ajax({
                method: "POST",
                url: baseUrl + "admin/diseasegroup/" + diseaseGroupId,
                data: JSON.stringify(data),
                contentType : "application/json"
            })
                .done(function () { self.notice({ message: "Saved successfully", priority: "success" }); })
                .fail(function () { self.notice({ message: "Error saving", priority: "warning"}); });
        };

        self.notice = ko.observable();

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            diseaseGroupId = diseaseGroup.id;
        });
    };
});
