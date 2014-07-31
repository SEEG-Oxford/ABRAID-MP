/* View model for the expert administration page.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "underscore",
    "app/BaseFormViewModel",
    "app/BaseTableViewModel"
], function (ko, _, BaseFormViewModel, BaseTableViewModel) {
    "use strict";

    return function (baseUrl, experts) {
        var self = this;

        var converter = {
            incomingJsonToRowViewModel: function (expert) {
                var row = _(expert).pick("id", "name", "email", "jobTitle", "institution", "visibilityRequested");
                row.seegmember = ko.observable(expert.seegmember);                   // editable
                row.administrator = ko.observable(expert.administrator);             // editable
                row.visibilityApproved = ko.observable(expert.visibilityApproved);   // editable
                row.weighting = ko.observable(expert.weighting.toString())           // editable as string
                    .extend({ number: true, required: true, min: 0, max: 1 });       // with validation
                row.createdDate = new Date(expert.createdDate);                      // convert from text
                row.updatedDate = new Date(expert.updatedDate);                      // convert from text

                // Track changes
                row.changed = false;
                var updateChangedStatus = function () { row.changed = true; };
                row.seegmember.subscribe(updateChangedStatus);
                row.administrator.subscribe(updateChangedStatus);
                row.visibilityApproved.subscribe(updateChangedStatus);
                row.weighting.subscribe(updateChangedStatus);

                return row;
            },
            rowViewModelsToOutgoingJson: function (expert) {
                var row = _(expert).pick("id");
                row.seegmember = expert.seegmember();                                // unwrap
                row.administrator = expert.administrator();                          // unwrap
                row.visibilityApproved = expert.visibilityApproved();                // unwrap
                row.weighting = parseFloat(expert.weighting());                      // unwrap & parse back to float
                return row;
            }
        };

        var rows = _(experts || []).map(converter.incomingJsonToRowViewModel);

        BaseFormViewModel.call(self, true, true, baseUrl, "admin/experts");
        BaseTableViewModel.call(self, rows, "updatedDate", true, ["name", "email", "jobTitle", "institution"]);

        self.buildSubmissionData = function () {
            return _(self.entries()).where({ changed: true }).map(converter.rowViewModelsToOutgoingJson);
        };
    };
});
