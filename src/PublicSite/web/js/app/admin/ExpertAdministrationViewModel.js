/* foo.
 * Copyright (c) 2014 University of Oxford
 */
define(["ko", "underscore", "app/BaseFormViewModel", "app/BaseTableViewModel"], function (ko, _, BaseFormViewModel, BaseTableViewModel) {
    "use strict";

    return function (baseUrl, experts) {
        var self = this;

        var converter = {
            incomingJsonToRowViewModel: function (expert) {
                return {
                    id: expert.id,
                    name: expert.name,
                    email: expert.email,
                    jobTitle: expert.jobTitle,
                    seegmember: ko.observable(expert.seegmember),                    // editable
                    administrator: ko.observable(expert.administrator),              // editable
                    visibilityRequested: expert.visibilityRequested,
                    visibilityApproved: ko.observable(expert.visibilityApproved),    // editable
                    weighting: ko.observable(expert.weighting.toString())            // editable as string
                        .extend({ number: true, required: true, min: 0, max: 1 }),   // with validation
                    createdDate: new Date(expert.createdDate),                       // convert from text
                    updatedDate: new Date(expert.updatedDate)                        // convert from text
                };
            },
            rowViewModelsToOutgoingJson: function (expert) {
                return {
                    id: expert.id,
                    seegmember: expert.seegmember(),                                 // unwrap
                    administrator: expert.administrator(),                           // unwrap
                    visibilityApproved: expert.visibilityApproved(),                 // unwrap
                    weighting: parseFloat(expert.weighting())                        // unwrap & parse back to float
                };
            }
        };

        var rows = _(experts || []).map(converter.incomingJsonToRowViewModel);

        BaseFormViewModel.call(self, baseUrl, "admin/experts", true, true);
        BaseTableViewModel.call(self, rows, "updatedDate", true, ["name", "email", "jobTitle", "institution"]);

        self.buildSubmissionData = function () {
            return _(self.entries()).map(converter.rowViewModelsToOutgoingJson);
        };
    };
});
