/* An AMD defining the Covariates, a vm to back covariate configuration form.
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false, console:false*/
define(["ko", "jquery", "underscore"], function (ko, $, _) {
    "use strict";

    return function (baseUrl, initialValue) {
        var self = this;
        console.log("================ INPUT ================");
        console.log(initialValue);
       
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
            // Wrap with underscore
            var iterable = _(self.files()).chain();
           
            // Filter
            if (self.filter() && !/^\s*$/.test(self.filter())) {
                var filter = self.filter().toLowerCase();

                iterable = iterable.filter(function (file) {
                    var name = (file.name || "").toLowerCase();
                    var path = file.path.toLowerCase();
                    return name.indexOf(filter) !== -1 || path.indexOf(filter) !== -1;
                });
            }
                   
            // Convert to view models
            iterable = iterable.map(function (file) {
                return {
                    jsonFile: file,
                    name: ko.computed({
                        read: function () {
                            return file.name;
                        },
                        write: function (value) {
                            file.name = value;
                        }
                    }),
                    mouseOver: ko.observable(false),
                    path: ko.observable(file.path),
                    info: ko.observable(file.info),
                    state: ko.computed({
                        read: function () {
                            return _(file.enabled).contains(self.selectedDisease().id);
                        },
                        write: function (value) {
                            if (value) {
                                file.enabled.push(self.selectedDisease().id);
                            } else {
                                // Note: using underscore"s indexOf instead of native indexOf because native
                                //       indexOf is flakey in old IEs.
                                file.enabled.splice(_(file.enabled).indexOf(self.selectedDisease().id), 1);
                            }
                        }
                    })
                };
            });

            // Sort
            if (self.sortField() && !/^\s*$/.test(self.sortField())) {
                var sortField = self.sortField();
                iterable = iterable.sortBy(function (file) { return file[sortField](); });
            }

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
            if (self.isValid()) {
                self.saving(true);
                /*
                $.post(baseUrl + formUrl, { value: self.value() })
                    .done(function () {
                        self.notices.push({ "message": "Saved successfully.", "priority": "success"});
                    })
                    .fail(function () {
                        self.notices.push({ "message": "Form could not be saved.", "priority": "warning"});
                    })
                    .always(function () {
                        self.saving(false);
                    });
                */
                self.hasUnsavedChanges(false);
                self.saving(false);
            } else {
                self.notices.push({ message: "Form must be valid before saving.", priority: "warning"});
            }

            console.log("================ OUTPUT ================");
            console.log({ diseases: self.diseases(), files: self.files() });
        };
    };
});
