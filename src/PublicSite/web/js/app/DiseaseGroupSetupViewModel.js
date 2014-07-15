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

        self.selectedDiseaseGroupId = 0;
        self.hasModelBeenSuccessfullyRun = false;
        self.lastModelRunText = ko.observable("");
        self.diseaseOccurrencesText = ko.observable("");
        self.canRunModel = ko.observable(false);

        self.working = ko.observable(false);
        self.notices = ko.observableArray();

        // When a disease group is selected from the drop-down list...
        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (selectedDiseaseGroup) {
            self.selectedDiseaseGroupId = selectedDiseaseGroup.id;
            self.working(true);
            self.notices.removeAll();

            // Get information regarding model runs for this disease group
            var url = baseUrl + "admin/diseasegroup/" + self.selectedDiseaseGroupId + "/modelruninformation";
            $.getJSON(url)
                .done(function (data) {
                    self.lastModelRunText(data.lastModelRunText);
                    self.diseaseOccurrencesText(data.diseaseOccurrencesText);
                    self.hasModelBeenSuccessfullyRun = data.hasModelBeenSuccessfullyRun;
                    self.canRunModel(data.canRunModel);
                    if (!self.canRunModel()) {
                        var errorMessage = "Cannot run model because " + data.cannotRunModelReason;
                        self.notices.push({ message: errorMessage, priority: "warning"});
                    }
                })
                .fail(function () {
                    self.notices.push({ message: "Could not retrieve model run details.", priority: "warning"});
                })
                .always(function () { self.working(false); });
        });

        self.runModel = function () {
            self.notices.removeAll();
            if (self.canRunModel()) {
                self.working(true);
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
                    .always(function () { self.working(false); });
            }
        };
    };
});
