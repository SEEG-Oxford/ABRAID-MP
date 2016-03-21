/* An AMD defining a vm to back the upload CSV form.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery",
    "shared/app/BaseFileFormViewModel"
], function (ko, $, BaseFileFormViewModel) {
    "use strict";

    return function (baseUrl, diseaseGroups) {
        var self = this;
        BaseFileFormViewModel.call(self, baseUrl, "tools/uploadcsv/upload", {
            success: "CSV file submitted. The results of the upload will be e-mailed to you."
        });

        self.buildSubmissionData = function () {
            return {
                isGoldStandard: self.isGoldStandard(),
                isBias: self.isBias(),
                diseaseGroup: self.selectedDiseaseGroup().id
            };
        };

        self.diseaseGroups = diseaseGroups;
        self.selectedDiseaseGroup = ko.observable(diseaseGroups[0]);
        self.isGoldStandard = ko.observable(false);
        self.isBias = ko.observable(false);
        self.purgeBiasData = function () {
            self.notices.removeAll();
            self.isSubmitting(true);

            $.ajax({
                url: baseUrl + "tools/uploadcsv/purgeBiasData",
                method: "POST",
                data: { diseaseGroup: self.selectedDiseaseGroup().id }
            })
            .done(function () {
                self.pushNotice(self.selectedDiseaseGroup().name + " background data removed", "success");
            })
            .fail(self.failureHandler)
            .always(function () {
                self.isSubmitting(false);
            });
        };
    };
});
