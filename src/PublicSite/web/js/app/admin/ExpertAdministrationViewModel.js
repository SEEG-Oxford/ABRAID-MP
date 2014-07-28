/* foo.
 * Copyright (c) 2014 University of Oxford
 */
define(["ko", "underscore"], function (ko, _) {
    "use strict";

    return function (baseUrl, experts) {
        var self = this;

        // Field state
        _(experts || []).each(function (expert) {
            expert.seegmember = ko.observable(expert.seegmember);
            expert.administrator = ko.observable(expert.administrator);
            expert.visibilityApproved = ko.observable(expert.visibilityApproved);
            expert.weighting = ko.observable(expert.weighting.toString())
                .extend({ number: true, required: true, min: 0, max: 1 });
            expert.createdDate = new Date(expert.createdDate);
            expert.updatedDate = new Date(expert.updatedDate);
        });

        self.entries = ko.observableArray(experts);

        // Meta state
        self.filter = ko.observable("");
        self.sortField = ko.observable("updatedDate");
        self.reverseSort = ko.observable(true);
        self.updateSort = function (field) {
            if (self.sortField() === field) {
                self.reverseSort(!self.reverseSort());
            } else {
                self.reverseSort(false);
                self.sortField(field);
            }
        };
        self.searchableFields = ["name", "email", "jobTitle", "institution"];

        self.visibleEntries = ko.computed(function () {
            // Wrap with underscore
            var iterable = _(self.entries()).chain();

            // Filter
            if (self.filter() && !/^\s*$/.test(self.filter())) {
                var filter = self.filter().toLowerCase();

                iterable = iterable.filter(function (disease) {
                    return _(self.searchableFields).some(function (field) {
                        return (disease[field] || "").toLowerCase().indexOf(filter) !== -1;
                    });
                });
            }

            // Sort
            var sortField = self.sortField();
            iterable = iterable.sortBy(function (entry) {
                var sortable = ko.utils.recursivePeek(entry[sortField]);
                if (typeof sortable === "string" || sortable instanceof String) {
                    sortable = sortable.toLowerCase();
                }
                if (typeof sortable === "boolean" || sortable instanceof Boolean) {
                    sortable = sortable ? "a" : "b";
                }
                return sortable;
            });

            // Unwrap underscore
            iterable = iterable.value();

            // Reverse
            if (self.reverseSort()) {
                iterable.reverse();
            }

            return iterable;
        });

        self.buildSubmissionData = function () {
            return _(self.entries()).map(function (expert) {
                return {
                    id: expert.id,
                    seegmember: expert.seegmember(),
                    administrator: expert.administrator(),
                    visibilityApproved: expert.visibilityApproved(),
                    weighting: parseFloat(expert.weighting())
                };
            });
        };

        self.isSubmitting = false;
        self.submit = function () {
            console.log(self.buildSubmissionData());
        };
        self.notices = [];
    };
});
