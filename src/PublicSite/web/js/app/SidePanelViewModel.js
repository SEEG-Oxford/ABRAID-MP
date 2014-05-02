define(["ko"], function (ko) {
    "use strict";

    return function (selectedPointViewModel, selectedAdminUnitViewModel) {
        var self = this;

        self.templateName = ko.observable();
        self.selectedPointViewModel = selectedPointViewModel;
        self.selectedAdminUnitViewModel = selectedAdminUnitViewModel;

        ko.postbox.subscribe("validation-type-changed", function (value) {
            self.templateName(value === "disease occurrences" ? "occurrences-template" : "admin-units-template");
        });
    };
});
