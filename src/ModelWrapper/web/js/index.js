/* Kick-start JS for the index page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, initialRepoData:false, initialMiscData:false, baseUrl: false*/
//Load base configuration, then load the app logic for this page.
require(["require.conf"], function () {
    "use strict";

    require(["ko",
             "app/RepositoryViewModel",
             "app/AuthViewModel",
             "app/MiscViewModel",
              "domReady!"],
        function(ko, RepositoryViewModel, AuthViewModel, MiscViewModel, doc) {
            ko.applyBindings(ko.validatedObservable(new RepositoryViewModel(initialRepoData, baseUrl)), doc.getElementById("repo-body"));
            ko.applyBindings(ko.validatedObservable(new AuthViewModel(baseUrl)), doc.getElementById("auth-body"));
            ko.applyBindings(new MiscViewModel(initialMiscData, baseUrl), doc.getElementById("misc-body"));
    });
});
