/* An AMD defining the Covariates, a vm to back covariate configuration form.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery",
    "underscore",
    "app/CovariatesListRowViewModel"
], function (ko, $, _, CovariatesListRowViewModel) {
    "use strict";

    return function (baseUrl, initialValue) {
        var self = this;

        self.diseases = ko.observableArray(initialValue.diseases);
        self.selectedDisease = ko.observable(self.diseases()[0]);
       
        self.filter = ko.observable("");
        self.sortField = ko.observable("path");
        self.reverseSort = ko.observable(false);
        self.updateSort = function (field) {
            if (self.sortField() === field) {
                self.reverseSort(!self.reverseSort());
            } else {
                self.reverseSort(false);
                self.sortField(field);
            }
        };

        self.files = ko.observableArray(initialValue.files);
        self.visibleFiles = ko.computed(function () {
            // Note: disease id is extracted here rather than on the fly in row the row view models
            //       because the ko dependency tracker only looks a few levels deep.
            var diseaseId = self.selectedDisease().id;

            // Wrap with underscore
            var iterable = _(self.files()).chain();

            // Filter
            if (self.filter() && !/^\s*$/.test(self.filter())) {
                var filter = self.filter().toLowerCase();

                iterable = iterable.filter(function (file) {
                    var name = (file.name || "").toLowerCase();
                    var path = file.path.toLowerCase();
                    return (name.indexOf(filter) !== -1 || path.indexOf(filter) !== -1);
                });
            }

            // Convert to view models
            iterable = iterable.map(function (file) {
                return new CovariatesListRowViewModel(self, file, diseaseId);
            });

            // Hide
            iterable = iterable.filter(function (rowViewModel) { return !rowViewModel.hide(); });

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
       
        self.saving = ko.observable(false);
        self.notices = ko.observableArray();
        self.hasUnsavedChanges = ko.observable(false);

        self.submit = function () {
            self.notices.removeAll();
            self.saving(true);
            $.ajax({
                method: "POST",
                url: baseUrl + "covariates/config",
                data: JSON.stringify({ diseases: self.diseases(), files: self.files() }),
                contentType : "application/json"
            })
                .done(function () {
                    self.notices.push({ "message": "Saved successfully.", "priority": "success"});
                    self.hasUnsavedChanges(false);
                })
                .fail(function () {
                    self.notices.push({ "message": "Form could not be saved.", "priority": "warning"});
                })
                .always(function () {
                    self.saving(false);
                });
        };
    };
});
