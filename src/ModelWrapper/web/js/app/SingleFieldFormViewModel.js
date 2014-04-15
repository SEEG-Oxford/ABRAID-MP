/* foo.
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false*/
define(["ko", "jquery"], function (ko, $) {
    "use strict";

    return function (baseUrl, formUrl, initialValue, validationRules) {
        var self = this;
        self.value = ko.observable(initialValue).extend(validationRules);
        self.saving = ko.observable(false);
        self.notices = ko.observableArray();
        self.submit = function () {
            self.notices.removeAll();
            if (self.isValid()) {
                self.saving(true);
                $.post(baseUrl + formUrl, { value: self.value() })
                    .done(function () { self.notices.push({ 'message': "Saved successfully.", 'priority': 'success'}); })
                    .fail(function () { self.notices.push({ 'message': "Value could not be saved.", 'priority': 'warning'}); })
                    .always(function () { self.saving(false); });
            } else {
                self.notices.push({ message: "Field must be valid before saving.", priority: 'warning'});
            }
        };
    };
});
