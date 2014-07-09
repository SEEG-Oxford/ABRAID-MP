/* Kick-start JS for the administration disease group page.
 * Copyright (c) 2014 University of Oxford
 */
/*global require:false, baseUrl:false, initialData:false*/
//Load base configuration, then load the app logic for this page.
require(["require.conf"], function () {
    "use strict";

    require(["ko",
        "app/DiseaseGroupsListViewModel",
        "navbar",
        "domReady!"
    ], function (ko, DiseaseGroupsListViewModel, setupNavbar, doc) {
        setupNavbar();

        ko.applyBindings(
            ko.validatedObservable(new DiseaseGroupsListViewModel(baseUrl, initialData)),
            doc.getElementById("disease-group-list"));
    });
});
