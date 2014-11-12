/* An AMD to act as view model for the "sync diseases with model wrapper" button, and associated action status feedback.
 * Copyright (c) 2014 University of Oxford
 */
define(["shared/app/BaseFormViewModel"], function (BaseFormViewModel) {
    "use strict";

    return function (baseUrl) {
        var self = this;
        BaseFormViewModel.call(self, false, false, baseUrl, "/admin/diseases/sync", {
            fail: "Failed to synchronise the disease groups with all model wrapper instances.",
            error: "Failed to synchronise the disease groups with all model wrapper instances.",
            success: "Disease groups synchronised successfully with all model wrapper instances."
        });
    };
});