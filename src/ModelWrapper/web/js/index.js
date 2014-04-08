/* foo.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, initialRepoData: false, baseUrl: false*/
//Load base configuration, then load the app logic for this page.
require(["require.conf"], function () {
    require(["ko",
             "app/RepositoryViewModel",
             "app/AuthViewModel",
              "domReady!"],
        function(ko, RepositoryViewModel, AuthViewModel, doc) {
            ko.applyBindings(ko.validatedObservable(new RepositoryViewModel(initialRepoData, baseUrl)), doc.getElementById("repo-body"));
            ko.applyBindings(ko.validatedObservable(new AuthViewModel(baseUrl)), doc.getElementById("auth-body"));
    });
});
