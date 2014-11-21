define(["jquery"], function ($) {
    "use strict";

    return function (element, showInitially) {
        if (showInitially) {
            $(element).modal("show");
        }

        // Prevent mouse events propagating to map movements when help text modal is open.
        $(element).on("mousedown mousewheel", function (e) {
            e.stopPropagation();
        });
    };
});
