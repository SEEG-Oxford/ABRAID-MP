/*
 * An AMD defining and registering a set of custom knockout bindings.
 * Copyright (c) 2014 University of Oxford.
 */
define([
    "knockout",
    "jquery",
    "moment",
    "flipclock"
], function (ko, $, moment) {
    "use strict";

    ko.utils.recursiveUnwrap = function (func) {
        if (typeof func !== "function") {
            return func;
        }
        return ko.utils.recursiveUnwrap(func());
    };

    // Set the width of the element to fit the number of digits (2, 3 or 4)
    function adjustElementWidthForCounterValue(element, value) {
        if ((value > 999) && ($(element).width() !== 280)) {
            $(element).width(280);
        } else if ((value > 99) && ($(element).width() !== 210)) {
            $(element).width(210);
        }
    }

    // Custom binding to set the value on the flipclock.js counter
    ko.bindingHandlers.counter = {
        init: function (element, valueAccessor) {
            var counter = $(element).FlipClock(ko.utils.recursiveUnwrap(valueAccessor), { clockFace: "Counter" });
            ko.utils.domData.set(element, "counter", counter);
        },
        update: function (element, valueAccessor) {
            var counter = ko.utils.domData.get(element, "counter");
            var value = ko.utils.recursiveUnwrap(valueAccessor);
            adjustElementWidthForCounterValue(element, value);
            counter.setValue(value);
        }
    };

    // Custom binding to format the datetime display with moment.js library
    ko.bindingHandlers.date = {
        update: function (element, valueAccessor) {
            var date = ko.utils.recursiveUnwrap(valueAccessor);
            $(element).text(moment(date).lang("en-gb").format("LL"));
        }
    };

    // Custom binding used to bind each child member of a group to an option
    ko.bindingHandlers.option = {
        update: function (element, valueAccessor) {
            var value = ko.utils.recursiveUnwrap(valueAccessor);
            ko.selectExtensions.writeValue(element, value);
        }
    };

    // Custom binding to fade the spinner in and out
    ko.bindingHandlers.fadeVisible = {
        update: function (element, valueAccessor, allBindings) {
            var visible = ko.utils.recursiveUnwrap(valueAccessor);
            var duration = allBindings.get("fadeDuration") || 1000;
            if (visible) {
                $(element).show();
            } else {
                $(element).delay(500).hide(duration);
            }
        }
    };
});
