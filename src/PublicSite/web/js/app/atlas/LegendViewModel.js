/* AMD defining the view model for the atlas legend.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'active-atlas-type'
 */
define(["ko"], function (ko) {
    "use strict";

    return function () {
        var self = this;
        self.type = ko.observable().subscribeTo("active-atlas-type");
    };
});
