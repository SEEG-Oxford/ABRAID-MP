/**
 * JS file for the model wrapper.
 * Copyright (c) 2014 University of Oxford
 */
(function (document, baseURL, ko, $, initialRepoData) {
    /***************************** Setup *****************************/
    (function configureKnockoutValidation() {
        // Adapted from https://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#are-same
        ko.validation.rules.areSame = {
            getValue: function (o) {
                return (typeof o === 'function' ? o() : o);
            },
            validator: function (val, otherField) {
                return val === this.getValue(otherField);
            },
            message: "Password fields must match"
        };

        // Adapted from https://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#password-complexity
        ko.validation.rules.passwordComplexity = {
            validator: function (val) {
                return /(?=^[^\s]{6,128}$)((?=.*?\d)(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\d)(?=.*?[^\w\d\s])(?=.*?[a-z])|(?=.*?[^\w\d\s])(?=.*?[A-Z])(?=.*?[a-z])|(?=.*?\d)(?=.*?[A-Z])(?=.*?[^\w\d\s]))^.*/.test('' + val + '');
            },
            message: 'Password must be between 6 and 128 characters long and contain three of the following 4 items: upper case letter, lower case letter, a symbol, a number'
        };

        // Adapted from https://github.com/Knockout-Contrib/Knockout-Validation/wiki/User-Contributed-Rules#password-complexity
        ko.validation.rules.usernameComplexity = {
            validator: function (val) {
                return /^[a-z0-9_-]{3,15}$/.test('' + val + '');
            },
            message: 'Username must be between 3 and 15 characters long and consist of only letters, numbers, "_" or "-"'
        };

        ko.validation.configure({
            insertMessages: true,
            messageTemplate: 'validation-template',
            messagesOnModified: true,
            registerExtenders: true
        });
    }());

    /***************************** View Models *****************************/
    var repoViewModel = ko.validatedObservable((function () {
        var lastUrl = ko.observable(initialRepoData.url);
        var url = ko.observable(initialRepoData.url).extend({ required: true });
        var version = ko.observable(initialRepoData.version).extend({ required: true });
        var availableVersions = ko.observableArray(initialRepoData.availableVersions);
        var syncingRepo = ko.observable(false);
        var savingVersion = ko.observable(false);
        var notices = ko.observableArray();
        var urlChanged = ko.computed(function() {
            return url() != lastUrl();
        });
        var enableVersion = ko.computed(function () {
            return (!urlChanged()) && url.isValid() && availableVersions().length != 0;
        });
        var syncRepo = function () {
            if (url.isValid()) {
                if (urlChanged()) {
                    version(null);
                }
                this.syncingRepo(true);
                var currentUrl = this.url();
                var currentVersion = version();
                this.notices.removeAll();
                this.availableVersions.removeAll();
                $.post(baseURL + "repo/sync", { repositoryUrl: this.url() })
                    .done(function (response) {
                        if (response.length == 0) {
                            notices.push({ 'message': "The repository was successfully synced but contained no versions. Try a different url.", 'priority': 'info'});
                        } else {
                            for (var i=0; i < response.length; i++) {
                                availableVersions.push(response[i]);
                            }
                            if (response.indexOf (currentVersion) != -1) {
                                version(currentVersion);
                            }
                            notices.push({ 'message': "Sync successful.", 'priority': 'success'});
                        }})
                    .fail(function () { notices.push({ 'message': "Sync failed, are you sure the url is correct?", 'priority': 'warning'}); })
                    .always(function () { syncingRepo(false); lastUrl(currentUrl); });
            } else {
                this.notices.push({ message: "URL field must be valid before syncing.", priority: 'warning'});
            }
        };

        var saveVersion = function () {
            notices.removeAll();
            if (url.isValid()) {
                this.savingVersion(true);
                $.post(baseURL + "repo/version", { version: this.version() })
                    .done(function () { notices.push({ 'message': "Saved successfully.", 'priority': 'success'}); })
                    .fail(function () { notices.push({ 'message': "Version details could not be saved.", 'priority': 'warning'}); })
                    .always(function () { savingVersion(false); });
            } else {
                this.notices.push({ message: "URL field must be valid before syncing.", priority: 'warning'});
            }
        };

        if (availableVersions().length == 0) {
            notices.push({ message: "The current repository does not appear to have any versions. If the url is correct try syncing the repository, otherwise fix the url.", priority: 'info'});
        }

        return {
            url: url,
            version: version,
            availableVersions: availableVersions,
            enableVersion: enableVersion,
            urlChanged: urlChanged,
            syncRepo: syncRepo,
            syncingRepo: syncingRepo,
            saveVersion: saveVersion,
            savingVersion: savingVersion,
            notices: notices
        };
    }()));

    var authViewModel = ko.validatedObservable((function () {
        var username = ko.observable().extend({ required: true,  usernameComplexity: true });
        var password = ko.observable().extend({ required: true, passwordComplexity: true });
        var passwordConfirmation = ko.observable().extend({ required: true, passwordComplexity: true, areSame: password });
        var saving = ko.observable(false);
        var notices = ko.observableArray();
        var submit = function () {
            notices.removeAll();
            if (this.isValid()) {
                this.saving(true);
                $.post(baseURL + "auth", { username: this.username(), password: this.password(), passwordConfirmation: this.passwordConfirmation() })
                    .done(function () { notices.push({ 'message': "Saved successfully.", 'priority': 'success'}); })
                    .fail(function () { notices.push({ 'message': "Authentication details could not be saved.", 'priority': 'warning'}); })
                    .always(function () { saving(false); });
            } else {
                this.notices.push({ message: "All field must be valid before saving.", priority: 'warning'});
            }
        };

        return {
            username: username,
            password: password,
            passwordConfirmation: passwordConfirmation,
            saving: saving,
            notices: notices,
            submit: submit
        };
    }()));


    /***************************** Binding *****************************/
    $(document).ready(function () {
        ko.applyBindings(repoViewModel, document.getElementById("repo-body"));
        ko.applyBindings(authViewModel, document.getElementById("auth-body"));
    });
}(document, baseURL, ko, $, initialRepoData));
