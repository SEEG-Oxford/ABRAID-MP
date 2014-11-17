/**
 * An AMD defining the HelpTextViewModel to determine whether the help text panel should be displayed.
 * Copyright (c) 2014 University of Oxford
 */
define(["ko"], function (ko) {
    "use strict";

    return function () {
        this.visible = ko.observable(false);

        this.showPanel = function () {
            this.visible(true);
        };

        this.hidePanel = function () {
            this.visible(false);
        };
    };
});

