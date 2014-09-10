/* AMD to represent the data in the account details page.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "underscore",
    "jquery",
    "shared/app/BaseFormViewModel"
], function (ko, _, $, BaseFormViewModel) {
    "use strict";

    return function (baseUrl, targetUrl, messages, initialExpert, redirectPage, diseaseInterestListViewModel) {
        var self = this;
        BaseFormViewModel.call(self, true, true, baseUrl, targetUrl, messages);

        // Field state
        self.name = ko.observable(initialExpert.name || "")
            .extend({ required: true, maxLength: 1000 });

        self.jobTitle = ko.observable(initialExpert.jobTitle || "")
            .extend({ required: true, maxLength: 100 });

        self.institution = ko.observable(initialExpert.institution || "")
            .extend({ required: true,  maxLength: 100 });

        self.visibilityRequested = ko.observable(initialExpert.visibilityRequested || false);

        self.diseaseInterestListViewModel = diseaseInterestListViewModel;

        // Actions
        self.buildSubmissionData = function () {
            return {
                name: self.name(),
                jobTitle: self.jobTitle(),
                institution: self.institution(),
                visibilityRequested: self.visibilityRequested(),
                diseaseInterests: self.diseaseInterestListViewModel.buildSubmissionData()
            };
        };

        self.baseSuccessHandler = self.successHandler;
        self.successHandler = function (data, textStatus, xhr) {
            self.baseSuccessHandler(data, textStatus, xhr);
            redirectPage(baseUrl);
        };

        self.baseFailureHandler = self.failureHandler;
        self.failureHandler = function (xhr) {
            self.baseFailureHandler(xhr);
            if (xhr.status === 409) { // email address conflict
                redirectPage(baseUrl + "register/account");
            }
        };
    };
});
