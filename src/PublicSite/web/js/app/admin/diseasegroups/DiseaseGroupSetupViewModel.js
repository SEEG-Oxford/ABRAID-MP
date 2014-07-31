/* An AMD defining the view-model for the disease group setup (in administration).
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery",
    "moment"
], function (ko, $, moment) {
    "use strict";

    return function (baseUrl, diseaseGroupSelectedEventName, diseaseGroupSavedEventName) {
        var self = this;

        self.selectedDiseaseGroupId = ko.observable();
        self.hasModelBeenSuccessfullyRun = ko.observable(false);
        self.lastModelRunText = ko.observable("");
        self.diseaseOccurrencesText = ko.observable("");
        self.canRunModelServerResponse = ko.observable(false);
        self.canRunModel = ko.computed(function () {
            return self.canRunModelServerResponse() && self.isValid() && !self.working();
        }, self);

        self.batchEndDate = ko.observable("").extend({required: true, date: true});
        self.batchEndDateMinimum = ko.observable("");
        self.batchEndDateMaximum = ko.observable("");

        self.working = ko.observable(false);
        self.notices = ko.observableArray();

        self.runModel = function () {
            self.notices.removeAll();
            if (self.canRunModel()) {
                self.working(true);
                var url = baseUrl + "admin/diseasegroups/" + self.selectedDiseaseGroupId() + "/requestmodelrun";
                var batchEndDateText = moment(self.batchEndDate()).format();
                $.post(url, { batchEndDate: batchEndDateText })
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

        var getModelRunInfo = function (diseaseGroupId) {
            self.selectedDiseaseGroupId(diseaseGroupId);
            self.working(true);
            self.notices.removeAll();

            if (self.selectedDiseaseGroupId() !== undefined) {
                // Get information regarding model runs for this disease group
                var url = baseUrl + "admin/diseasegroups/" + self.selectedDiseaseGroupId() + "/modelruninformation";
                $.getJSON(url)
                    .done(function (data) {
                        self.lastModelRunText(data.lastModelRunText);
                        self.diseaseOccurrencesText(data.diseaseOccurrencesText);
                        self.batchEndDate(data.batchEndDateDefault);
                        self.batchEndDateMinimum(data.batchEndDateMinimum);
                        self.batchEndDateMaximum(data.batchEndDateMaximum);
                        self.hasModelBeenSuccessfullyRun(data.hasModelBeenSuccessfullyRun);
                        self.canRunModelServerResponse(data.canRunModel);
                        if (!self.canRunModelServerResponse()) {
                            var errorMessage = "Cannot run model because " + data.cannotRunModelReason;
                            self.notices.push({ message: errorMessage, priority: "warning"});
                        }
                    })
                    .fail(function () {
                        self.notices.push({ message: "Could not retrieve model run details.", priority: "warning"});
                    })
                    .always(function () { self.working(false); });
            }
        };

        // Called when a disease group is selected from the drop-down list
        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (selectedDiseaseGroup) {
            getModelRunInfo(selectedDiseaseGroup.id);
        });

        // Called when changes to a disease group are successfully saved
        ko.postbox.subscribe(diseaseGroupSavedEventName, function (diseaseGroupId) {
            getModelRunInfo(diseaseGroupId);
        });
    };
});
