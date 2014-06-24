/**
 * An AMD defining the SpinnerViewModel to hold the state of the spinner, ie whether it should be displayed.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'map-view-update-in-progress' - published by MapView
 */
define(["ko"], function (ko) {
    "use strict";

    return function () {
        this.visible = ko.observable(false).subscribeTo("map-view-update-in-progress");
    };
});
