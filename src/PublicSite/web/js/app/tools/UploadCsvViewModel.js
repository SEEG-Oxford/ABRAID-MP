/* An AMD defining a vm to back the upload CSV form.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "shared/app/BaseFileFormViewModel"
], function (ko, BaseFileFormViewModel) {
    "use strict";

    return function (baseUrl) {
        var self = this;
        BaseFileFormViewModel.call(self, baseUrl, "tools/uploadcsv/upload", {
            success: "CSV file submitted. The results of the upload will be e-mailed to you."
        });
    };
});
