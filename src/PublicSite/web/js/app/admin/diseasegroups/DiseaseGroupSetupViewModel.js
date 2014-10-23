/* An AMD defining the view-model for the disease group setup (in administration).
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery",
    "moment",
    "shared/app/BaseFormViewModel"
], function (ko, $, moment, BaseFormViewModel) {
    "use strict";

    function formatDate(date) {
        return moment(date, "DD MMM YYYY").format();
    }

    return function (baseUrl, diseaseGroupSelectedEventName, diseaseGroupSavedEventName) {
        var self = this;
        BaseFormViewModel.call(self, false, true, undefined, undefined, { success: "Model run requested." }, true);

        self.selectedDiseaseGroupId = ko.observable();
        self.isAutomaticModelRunsEnabled = ko.observable(false);
        self.hasModelBeenSuccessfullyRun = ko.observable(false);
        self.lastModelRunText = ko.observable("");
        self.diseaseOccurrencesText = ko.observable("");
        self.canRunModel = ko.observable(false);
        self.batchStartDate = ko.observable("").extend({ required: true, date: true });
        self.batchEndDate = ko.observable("").extend({ required: true, date: true, min: self.batchStartDate });
        self.batchDateMinimum = ko.observable("");
        self.batchDateMaximum = ko.observable("");
        self.hasGoldStandardOccurrences = ko.observable(false);
        self.useGoldStandardOccurrences = ko.observable(false);

        self.buildSubmissionUrl = function () {
            return baseUrl + "admin/diseases/" + self.selectedDiseaseGroupId() + "/requestmodelrun";
        };

        self.buildSubmissionData = function () {
            return {
                batchStartDate: formatDate(self.batchStartDate()),
                batchEndDate: formatDate(self.batchEndDate()),
                useGoldStandardOccurrences: self.useGoldStandardOccurrences()
            };
        };

        // The Run Model button is disabled if:
        // (1) either of the batch dates are invalid
        // (unless we are using gold standard occurrences as these do not require a batch end date); or
        // (2) the model cannot be run; or
        // (3) the form is being submitted (requesting model run, enabling automatic model runs, or generating extent)
        self.disableButtonThatRunsModel = ko.computed(function () {
            var datesValid = self.batchStartDate.isValid() && self.batchEndDate.isValid();
            return (!self.useGoldStandardOccurrences() && !datesValid) ||
                !self.canRunModel() ||
                self.isSubmitting() ||
                self.isEnablingAutomaticModelRuns() ||
                self.isGeneratingDiseaseExtent();
        });

        self.isGeneratingDiseaseExtent = ko.observable(false);
        self.generateDiseaseExtent = function () {
            self.notices.removeAll();
            self.isGeneratingDiseaseExtent(true);
            var data = { useGoldStandardOccurrences: self.useGoldStandardOccurrences() };
            $.post(baseUrl + "admin/diseases/" + self.selectedDiseaseGroupId() + "/generatediseaseextent", data)
                .done(function () { self.notices.push({ message: "Disease extent generated.", priority: "success"}); })
                .fail(function () { self.notices.push({ message: "Server error.", priority: "warning"}); })
                .always(function () { self.isGeneratingDiseaseExtent(false); });
        };
        self.disableButtonThatGeneratesDiseaseExtent = ko.computed(function () {
            return !self.canRunModel() ||
                self.hasModelBeenSuccessfullyRun() ||
                self.isSubmitting() ||
                self.isGeneratingDiseaseExtent() ||
                self.isEnablingAutomaticModelRuns();
        });

        self.isEnablingAutomaticModelRuns = ko.observable(false);
        self.enableAutomaticModelRuns = function () {
            self.notices.removeAll();
            self.isEnablingAutomaticModelRuns(true);
            $.post(baseUrl + "admin/diseases/" + self.selectedDiseaseGroupId() + "/automaticmodelruns")
                .done(function () { self.isAutomaticModelRunsEnabled(true); })
                .fail(function () { self.notices.push({ message: "Server error.", priority: "warning"}); })
                .always(function () { self.isEnablingAutomaticModelRuns(false); });
        };
        self.disableButtonThatEnablesAutomaticModelRuns = ko.computed(function () {
            return !self.canRunModel() ||
                self.isSubmitting() ||
                self.isEnablingAutomaticModelRuns() ||
                self.isGeneratingDiseaseExtent();
        });

        self.resetState = function () { // only public for testing
            self.notices.removeAll();
            self.lastModelRunText("");
            self.diseaseOccurrencesText("");
            self.batchStartDate("");
            self.batchEndDate("");
            self.batchDateMinimum("");
            self.batchDateMaximum("");
            self.hasModelBeenSuccessfullyRun(false);
            self.canRunModel(false);
            self.hasGoldStandardOccurrences(false);
            self.useGoldStandardOccurrences(false);
        };

        self.updateModelRunInfo = function (diseaseGroupId) { // only public for testing
            self.resetState();
            self.selectedDiseaseGroupId(diseaseGroupId);

            if (self.selectedDiseaseGroupId()) {
                self.isSubmitting(true);
                // Get information regarding model runs for this disease group
                var url = baseUrl + "admin/diseases/" + self.selectedDiseaseGroupId() + "/modelruninformation";
                $.getJSON(url)
                    .done(function (data) {
                        self.lastModelRunText(data.lastModelRunText);
                        self.diseaseOccurrencesText(data.diseaseOccurrencesText);
                        self.batchStartDate(data.batchDateMinimum);
                        self.batchEndDate(data.batchEndDateDefault);
                        self.batchDateMinimum(data.batchDateMinimum);
                        self.batchDateMaximum(data.batchDateMaximum);
                        self.hasModelBeenSuccessfullyRun(data.hasModelBeenSuccessfullyRun);
                        self.canRunModel(data.canRunModel);
                        self.hasGoldStandardOccurrences(data.hasGoldStandardOccurrences);
                        if (!self.canRunModel()) {
                            var errorMessage = "Cannot run model or generate disease extent because " +
                                data.cannotRunModelReason;
                            self.notices.push({ message: errorMessage, priority: "warning"});
                        }
                    })
                    .fail(function () {
                        self.notices.push({ message: "Could not retrieve model run details.", priority: "warning"});
                    })
                    .always(function () {
                        self.isSubmitting(false);
                    });
            }
        };

        // Called when a disease group is selected from the drop-down list
        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (selectedDiseaseGroup) {
            self.isAutomaticModelRunsEnabled(selectedDiseaseGroup.automaticModelRuns);
            self.updateModelRunInfo(selectedDiseaseGroup.id);
        });

        // Called when changes to a disease group are successfully saved
        ko.postbox.subscribe(diseaseGroupSavedEventName, function (diseaseGroupId) {
            self.updateModelRunInfo(diseaseGroupId);
        });
    };
});
