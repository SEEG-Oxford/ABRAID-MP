/* An AMD defining the SingleFieldFormViewModel, a vm to back single field forms that hold a single value.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery",
    "shared/app/BaseFormViewModel"
], function (ko, $, BaseFormViewModel) {
    "use strict";

    return function (initialValue, validationRules,
                     sendJson, receiveJson, baseUrl, targetUrl, messages, excludeGenericFailureMessage) {
        var self = this;
        BaseFormViewModel.call(self, sendJson, receiveJson, baseUrl, targetUrl, messages, excludeGenericFailureMessage);

        self.value = ko.observable(initialValue).extend(validationRules);
        self.buildSubmissionData = function () {
            return { value: self.value() };
        };
    };
});
