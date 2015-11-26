/**
 * An AMD defining the SpinnerViewModel to hold the state of the spinner, ie whether it should be displayed.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'map-view-update-in-progress' - published by MapView
 */
define(["ko"], function (ko) {
    "use strict";

    return function () {
        var self = this;
        ko.postbox.subscribe("map-view-update-in-progress", function (value) {
            if (value) {
                self.visible(true);
            } else {
                setTimeout(function () {
                    self.visible(false);
                }, 1000);
            }
        });
        self.visible = ko.observable(false);
    };
});
