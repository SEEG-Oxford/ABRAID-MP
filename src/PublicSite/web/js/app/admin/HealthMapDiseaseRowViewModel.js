/* Row view model for the disease tables on the HealthMap administration page.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "underscore",
    "shared/app/BaseFormViewModel"
], function (ko, _, BaseFormViewModel) {
    "use strict";

    return function (healthMapDisease, target, baseUrl) {
        var self = this;
        // Each row in the table is a mini form
        BaseFormViewModel.call(self, true, true, baseUrl, target);

        // Add the basic row state fields
        // Note: this can be used for HealthMapDisease and HealthMapSubDisease, as the value of "parentDisease" will
        //       simply always be undefined for HealthMapDisease.
        self.id = healthMapDisease.id;
        self.name = healthMapDisease.name;
        self.abraidDisease = ko.observable(healthMapDisease.abraidDisease);
        self.parentDisease = ko.observable(healthMapDisease.parent);

        // Add fields for the unsaved form state
        self.abraidDiseaseNew = ko.observable(healthMapDisease.abraidDisease);
        self.parentDiseaseNew = ko.observable(healthMapDisease.parent);
        self.editing = ko.observable(false);

        // Add form behavior
        self.buildSubmissionData = function () {
            // The controller is only expecting the linked diseases as JsonNamedEntry, so recreate with just id & name.
            healthMapDisease.abraidDisease = self.abraidDiseaseNew() ?
            { id: self.abraidDiseaseNew().id, name: self.abraidDiseaseNew().name } : undefined;
            healthMapDisease.parent = self.parentDiseaseNew() ?
            { id: self.parentDiseaseNew().id, name: self.parentDiseaseNew().name } : undefined;
            return healthMapDisease;
        };

        self.warning = ko.computed(function () {
            // The UI will be too small for a full form layout, instead merge any warnings into one string to display.
            return _(self.notices()).pluck("message").join();
        });

        self.successHandler = function () {
            // Confirm the changes
            self.abraidDisease(self.abraidDiseaseNew());
            self.parentDisease(self.parentDiseaseNew());
            self.editing(false);
        };
    };
});
