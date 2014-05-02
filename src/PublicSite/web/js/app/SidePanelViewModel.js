/**
 * An AMD to define which view model is bound to the map's side panel, depending on the selected validation type layer.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'layers-changed' - published by SelectedLayerViewModel.
 * - Events published:
 * -- none
 */
define(["ko"], function (ko) {
    "use strict";

    return function (selectedPointViewModel, selectedAdminUnitViewModel) {
        var self = this;

        self.templateName = ko.observable();
        self.selectedPointViewModel = selectedPointViewModel;
        self.selectedAdminUnitViewModel = selectedAdminUnitViewModel;

        ko.postbox.subscribe("layers-changed", function (value) {
            self.templateName(value.type === "disease occurrences" ? "occurrences-template" : "admin-units-template");
        });
    };
});
