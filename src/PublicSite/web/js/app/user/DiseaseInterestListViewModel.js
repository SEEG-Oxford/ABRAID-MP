/* AMD to represent the data in the disease interests table.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "underscore",
    "app/BaseTableViewModel"
], function (ko, _, BaseTableViewModel) {
    "use strict";

    return function (initialExpert, diseases) {
        var self = this;

        // Prepare the entries
        _(diseases || []).each(function (disease) {
            disease.interested = ko.observable(_(initialExpert.diseaseInterests || []).contains(disease.id));
        });

        // Mix in the table behaviour
        BaseTableViewModel.call(self, diseases, "name", false, ["name"]);

        // Used by parent vm
        self.buildSubmissionData = function () {
            return _(self.entries())
                .chain()
                .filter(function (disease) { return disease.interested(); })
                .pluck("id")
                .value();
        };
    };
});