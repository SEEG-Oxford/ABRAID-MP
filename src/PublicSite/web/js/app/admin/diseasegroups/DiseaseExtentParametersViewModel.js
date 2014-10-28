/* An AMD defining the view-model for the parameters of disease extent generation for the selected disease group.
 * Copyright (c) 2014 University of Oxford
 */
define(["ko"], function (ko) {
    "use strict";

    return function (diseaseGroupSelectedEventName) {
        var self = this;

        self.maxMonthsAgoForHigherOccurrenceScore = ko.observable().extend({ digit: true, min: 0 });
        self.higherOccurrenceScore = ko.observable();
        self.lowerOccurrenceScore = ko.observable();
        self.higherOccurrenceScore.extend({ digit: true, customMin: self.lowerOccurrenceScore });
        self.lowerOccurrenceScore.extend({ digit: true, customMax: self.higherOccurrenceScore, min: 0 });

        self.minValidationWeighting = ko.observable().extend({ number: true, min: 0, max: 1 });
        self.minOccurrencesForPresence = ko.observable();
        self.minOccurrencesForPossiblePresence = ko.observable();
        self.minOccurrencesForPresence.extend({ digit: true, customMin: self.minOccurrencesForPossiblePresence });
        self.minOccurrencesForPossiblePresence.extend({digit: true, min: 0, customMax: self.minOccurrencesForPresence});

        function normalise(s) {
            return (s === 0) ? s : (s || "");
        }

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            var parameters = diseaseGroup.diseaseExtentParameters;
            if (parameters === undefined) {
                self.maxMonthsAgoForHigherOccurrenceScore("");
                self.higherOccurrenceScore("");
                self.lowerOccurrenceScore("");
                self.minValidationWeighting("");
                self.minOccurrencesForPresence("");
                self.minOccurrencesForPossiblePresence("");
            } else {
                self.maxMonthsAgoForHigherOccurrenceScore(normalise(parameters.maxMonthsAgoForHigherOccurrenceScore));
                self.higherOccurrenceScore(normalise(parameters.higherOccurrenceScore));
                self.lowerOccurrenceScore(normalise(parameters.lowerOccurrenceScore));
                self.minValidationWeighting(normalise(parameters.minValidationWeighting));
                self.minOccurrencesForPresence(normalise(parameters.minOccurrencesForPresence));
                self.minOccurrencesForPossiblePresence(normalise(parameters.minOccurrencesForPossiblePresence));
            }
        });
    };
});