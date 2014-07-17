/*
 * AMD to represent
 * Copyright (c) 2014 University of Oxford
 */
define(["ko", "underscore", "jquery"], function (ko, _, $) {
    "use strict";

    return function (baseUrl, initialExpert, redirectPage, diseaseInterestListViewModel) {
        var self = this;

        // Function to convert the alerts into knockout.bootstrap notices.
        var buildNotices = function (alerts, priority) {
            return _(alerts).map(function (alert) {
                return { "message": alert, "priority": priority};
            });
        };

        // Field state
        self.name = ko.observable(initialExpert.name)
            .extend({ required: true });

        self.job = ko.observable(initialExpert.job)
            .extend({ required: true });

        self.institution = ko.observable(initialExpert.institution)
            .extend({ required: true });

        self.publiclyVisible = ko.observable(initialExpert.publiclyVisible);

        self.diseaseInterestListViewModel = diseaseInterestListViewModel;

        // Meta state
        self.notices = ko.observableArray([]);
        self.isSubmitting = ko.observable(false);

        var buildSubmissionData = function () {
            return {
                name: self.name(),
                job: self.job(),
                institution: self.institution(),
                publiclyVisible: self.publiclyVisible(),
                validatorDiseaseGroups: _(self.diseaseInterestListViewModel.diseases()).chain().filter(function (disease) { return disease.interested(); }).pluck("id").value()
            };
        };

        // Actions
        self.submit = function () {
            self.notices.removeAll();
            if (!self.isValid()) {
                self.notices.push({ message: "Fields must be valid before saving.", priority: "warning"});
            } else {
                self.isSubmitting(true);
                $.ajax({
                    method: "POST",
                    url: baseUrl + "register/details",
                    data: JSON.stringify(buildSubmissionData()),
                    contentType : "application/json"
                })
                    .done(function () {
                        self.notices.push({ "message": "Account created successfully.", "priority": "success"});
                        redirectPage(baseUrl);
                    })
                    .fail(function (xhr) {
                        var alerts = buildNotices(JSON.parse(xhr.responseText), "warning");
                        _(alerts).each(function (alert) { self.notices.push(alert); });
                    })
                    .always(function () { self.isSubmitting(false); });
            }
        };
    };
});
