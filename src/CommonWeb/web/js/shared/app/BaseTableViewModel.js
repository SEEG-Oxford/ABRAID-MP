/* A base view model to provide a common implementation of knockout client-side table behaviour.
 * Use "call" to apply. E.g. BaseTableViewModel.call(self, args..)
 * Copyright (c) 2014 University of Oxford.
 */
define([
    "knockout",
    "jquery",
    "underscore"
], function (ko, $, _) {
    "use strict";

    return function (entries, initialSortField, initialSortDescending, filterableFields, mapFunction) {
        var self = this;

        self.entries = ko.observableArray(entries);

        // Meta state
        self.filter = ko.observable("");
        self.sortField = ko.observable(initialSortField);
        self.reverseSort = ko.observable(initialSortDescending);
        self.updateSort = function (field) {
            if (self.sortField() === field) {
                self.reverseSort(!self.reverseSort());
            } else {
                self.reverseSort(false);
                self.sortField(field);
            }
        };

        self.visibleEntries = ko.computed(function () {
            // Wrap with underscore
            var iterable = _(self.entries()).chain();

            // Filter
            if (self.filter() && !/^\s*$/.test(self.filter())) {
                var filter = self.filter().toLowerCase();

                iterable = iterable.filter(function (disease) {
                    return _(filterableFields).some(function (field) {
                        return (ko.utils.recursivePeek(disease[field]) || "").toLowerCase().indexOf(filter) !== -1;
                    });
                });
            }

            // Convert to view models
            if (mapFunction) {
                iterable = iterable.map(mapFunction);
            }

            // Hide
            iterable = iterable.filter(function (entry) { return !(ko.utils.recursiveUnwrap(entry.hide) || false); });

            // Sort
            var sortField = self.sortField();
            iterable = iterable.sortBy(function (disease) {
                var sortable = ko.utils.recursivePeek(disease[sortField]) || "";
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
    };
});
