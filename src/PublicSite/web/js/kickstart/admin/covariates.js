/* Kick-start JS for the covariates page.
 * Copyright (c) 2014 University of Oxford
 */
/* global require:false, baseUrl: false, initialData:false, window:false */
//Load base configuration, then load the app logic for this page.
require([baseUrl + "js/shared/require.conf.js"], function () {
    "use strict";

    require([
        "ko",
        "app/admin/covariates/CovariatesListViewModel",
        "app/admin/covariates/CovariateUploadViewModel",
        "domReady!",
        "shared/navbar"
    ], function (ko, CovariatesListViewModel, CovariateUploadViewModel, doc) {
        var refresh = function () {
            window.top.location.reload();
        };

        var covariatesListViewModel = new CovariatesListViewModel(baseUrl, initialData);
        ko.applyBindingsWithValidation(
            ko.validatedObservable(covariatesListViewModel),
            doc.getElementById("covariate-body"),
            {
                insertMessages: true,
                messageTemplate: "covariate-validation-template",
                messagesOnModified: true,
                registerExtenders: true,
                grouping: {
                    deep: true,
                    observable: true,
                    live: true
                }
            }
        );

        ko.applyBindings(
            ko.validatedObservable(new CovariateUploadViewModel(baseUrl, covariatesListViewModel, refresh)),
            doc.getElementById("add-covariate-body"));

        covariatesListViewModel.hasUnsavedChanges.subscribe(function (value) {
            if (value) {
                window.onbeforeunload = function (e) {
                    e = e || window.event; // Fallback for grabbing the event old browsers
                    var message = "You have unsaved changes to the covariate configuration.";

                    if (e) { // For IE6-8 and Firefox prior to version 4
                        e.returnValue = message;
                    }
                    return message; // For Chrome, Safari, IE8+ and Opera 12+
                };
            } else {
                window.onbeforeunload = null;
            }
        });
    });
});
