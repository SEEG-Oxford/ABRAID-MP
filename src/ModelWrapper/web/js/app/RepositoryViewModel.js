/* An AMD defining the RepositoryViewModel to hold the state of the repository view.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery"
], function (ko, $) {
    "use strict";

    return function (initialData, baseUrl) {
        var self = this;

        // State
        self.lastUrl = ko.observable(initialData.url);
        self.url = ko.validatedObservable(initialData.url).extend({ required: true });
        self.version = ko.observable(initialData.version).extend({ required: true });
        self.availableVersions = ko.observableArray(initialData.availableVersions);
        self.syncingRepo = ko.observable(false);
        self.savingVersion = ko.observable(false);
        self.notices = ko.observableArray();

        if (initialData.availableVersions.length === 0) {
            ko.observableArray([{
                message: "The current repository does not appear to have any versions. If the url is correct try syncing the repository, otherwise fix the url.", /* jshint ignore:line */ // Line length
                priority: "info"
            }]);
        }

        // Computed state
        self.urlChanged = ko.computed(function () {
            return self.url() !== self.lastUrl();
        });
        self.enableVersion = ko.computed(function () {
            return (!self.urlChanged()) && self.url.isValid() && self.availableVersions().length !== 0;
        });

        // Callbacks
        self.syncRepo = function () {
            if (self.url.isValid()) {
                if (self.urlChanged()) {
                    self.version(null);
                }
                self.syncingRepo(true);
                var currentUrl = self.url();
                var currentVersion = self.version();
                self.notices.removeAll();
                self.availableVersions.removeAll();
                $.post(baseUrl + "repo/sync", { repositoryUrl: self.url() })
                    .done(function (response) {
                        if (response.length === 0) {
                            self.notices.push({
                                "message": "The repository was successfully synced but contained no versions. Try a different url.", /* jshint ignore:line */ // Line length
                                "priority": "info"
                            });
                        } else {
                            for (var i = 0; i < response.length; i = i + 1) {
                                self.availableVersions.push(response[i]);
                            }
                            if (response.indexOf(currentVersion) !== -1) {
                                self.version(currentVersion);
                            }
                            self.notices.push({
                                "message": "Sync successful.",
                                "priority": "success"
                            });
                        }
                    })
                    .fail(function () {
                        self.notices.push({
                            "message": "Sync failed, are you sure the url is correct?",
                            "priority": "warning"
                        });
                    })
                    .always(function () {
                        self.syncingRepo(false);
                        self.lastUrl(currentUrl);
                    });
            } else {
                self.notices.push({
                    message: "URL field must be valid before syncing.",
                    priority: "warning"
                });
            }
        };

        self.saveVersion = function () {
            self.notices.removeAll();
            if (self.url.isValid()) {
                self.savingVersion(true);
                $.post(baseUrl + "repo/version", { version: self.version() })
                    .done(function () {
                        self.notices.push({ "message": "Saved successfully.", "priority": "success"});
                    })
                    .fail(function () {
                        self.notices.push({ "message": "Version details could not be saved.", "priority": "warning"});
                    })
                    .always(function () { self.savingVersion(false); });
            } else {
                self.notices.push({ message: "URL field must be valid before syncing.", priority: "warning"});
            }
        };
    };
});
