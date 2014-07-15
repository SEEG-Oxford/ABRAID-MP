/* An AMD defining the view-model for the list of disease groups.
 * Copyright (c) 2014 University of Oxford
 */
define(["ko"], function (ko) {
    "use strict";

    return function (baseUrl, initialData, diseaseGroupSelectedEventName) {
        var self = this;

        self.diseaseGroups = ko.observableArray(initialData);
        var initialDiseaseGroup = self.diseaseGroups()[0];
        self.selectedDiseaseGroup = ko.observable(initialDiseaseGroup).publishOn(diseaseGroupSelectedEventName);
    };
});