/*
 * AMD to represent
 * Copyright (c) 2014 University of Oxford
 */
define(["ko", "underscore"], function (ko, _) {
    "use strict";

    return function (initialExpert, diseases) {
        var self = this;

        _(diseases).each(function (disease) {
                disease.interested = ko.observable(_(initialExpert.validatorDiseaseGroups).contains(disease.id));
        });

        // Field state
        self.diseases = ko.observableArray(diseases);

        self.filter = ko.observable("");
        self.sortField = ko.observable("name");
        self.reverseSort = ko.observable(false);
        self.updateSort = function (field) {
            if (self.sortField() === field) {
                self.reverseSort(!self.reverseSort());
            } else {
                self.reverseSort(false);
                self.sortField(field);
            }
        };

        self.visibleDiseases = ko.computed(function () {
            // Wrap with underscore
            var iterable = _(self.diseases()).chain();

            // Filter
            if (self.filter() && !/^\s*$/.test(self.filter())) {
                var filter = self.filter().toLowerCase();

                iterable = iterable.filter(function (disease) {
                    var name = (disease.name || "").toLowerCase();
                    return (name.indexOf(filter) !== -1);
                });
            }

            // Sort
            var sortField = self.sortField();
            iterable = iterable.sortBy(function (rowViewModel) {
                var sortable = ko.utils.recursiveUnwrap(rowViewModel[sortField]);
                if (typeof sortable === "string" || sortable instanceof String) {
                    sortable = sortable.toLowerCase();
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
    };
});
