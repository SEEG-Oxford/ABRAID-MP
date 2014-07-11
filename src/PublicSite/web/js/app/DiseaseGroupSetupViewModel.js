/* An AMD defining the view-model for the disease group setup (in administration).
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery"
], function (ko, $) {
    "use strict";

    return function (baseUrl, diseaseGroupSelectedEventName) {
        var self = this;

        self.selectedDiseaseGroupId = 0; // TODO: Try making this a var
        self.hasModelBeenSuccessfullyRun = false;
        self.lastModelRunText = ko.observable("");
        self.diseaseOccurrencesText = ko.observable("");
        self.canRunModel = ko.observable(false);

        self.runningModel = ko.observable(false);
        self.notices = ko.observableArray();

        // When a disease group is selected from the drop-down list...
        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (selectedDiseaseGroup) {
            self.selectedDiseaseGroupId = selectedDiseaseGroup.id;

            // TODO: Spinner on?

            // Get information regarding model runs for this disease group
            var url = baseUrl + "admin/diseasegroup/" + self.selectedDiseaseGroupId + "/modelruninformation";
            $.getJSON(url)
                .done(function (data) {
                    self.lastModelRunText(data.lastModelRunText);
                    self.diseaseOccurrencesText(data.diseaseOccurrencesText);
                    self.hasModelBeenSuccessfullyRun = data.hasModelBeenSuccessfullyRun;
                    self.canRunModel(data.canRunModel);
                    self.notices.removeAll();
                    if (!self.canRunModel()) {
                        var errorMessage = "Cannot run model because " + data.cannotRunModelReason;
                        self.notices.push({ message: errorMessage, priority: "warning"});
                    }
                })
                .fail(function () {
                    // TODO: how to fail?
                })
                .always(function () {
                    // TODO: Spinner off?
                });
        });

        self.runModel = function () {
            self.notices.removeAll();
            if (self.canRunModel()) {
                if (self.modelNeverBeenSuccessfullyRun) {
                    // TODO: Display an OK/Cancel prompt that asks user to ensure ModelWrapper is set up appropriately
                    // for the disease before hitting run. Do this like deleting covariates.
                }

                self.runningModel(true);
                var url = baseUrl + "admin/diseasegroup/" + self.selectedDiseaseGroupId + "/requestmodelrun";
                $.post(url, { diseaseGroupId: self.selectedDiseaseGroupId })
                    .done(function () {
                        self.notices.push({ message: "Model run requested.", priority: "success"});
                    })
                    .fail(function (data) {
                        var errorMessage = data.responseJSON;
                        if (!errorMessage) {
                            errorMessage = "Model run could not be requested.";
                        }
                        self.notices.push({ message: errorMessage, priority: "warning"});
                    })
                    .always(function () { self.runningModel(false); });
            }
        };
    };
});
