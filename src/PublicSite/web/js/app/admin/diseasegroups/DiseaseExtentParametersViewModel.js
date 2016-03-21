/* An AMD defining the view-model for the parameters of disease extent generation for the selected disease group.
 * Copyright (c) 2014 University of Oxford
 */
define(["ko"], function (ko) {
    "use strict";

    return function (diseaseGroupSelectedEventName) {
        var self = this;

        self.maxMonthsAgoForHigherOccurrenceScore = ko.observable().extend({ digit: true, min: 0 });
        self.higherOccurrenceScore = ko.observable().extend({ digit: true, min: 0 });
        self.lowerOccurrenceScore = ko.observable().extend({ digit: true, min: 0, max: self.higherOccurrenceScore });
        self.minValidationWeighting = ko.observable().extend({ number: true, min: 0, max: 1 });

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            var parameters = diseaseGroup.diseaseExtentParameters;
            if (parameters === undefined) {
                self.maxMonthsAgoForHigherOccurrenceScore("");
                self.higherOccurrenceScore("");
                self.lowerOccurrenceScore("");
                self.minValidationWeighting("");
            } else {
                self.maxMonthsAgoForHigherOccurrenceScore(ko.utils.normaliseInput(
                    parameters.maxMonthsAgoForHigherOccurrenceScore));
                self.higherOccurrenceScore(ko.utils.normaliseInput(parameters.higherOccurrenceScore));
                self.lowerOccurrenceScore(ko.utils.normaliseInput(parameters.lowerOccurrenceScore));
                self.minValidationWeighting(ko.utils.normaliseInput(parameters.minValidationWeighting));
            }
        });
    };
});
