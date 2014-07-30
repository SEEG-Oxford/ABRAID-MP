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
            var arg = ko.utils.recursiveUnwrap(valueAccessor);

            var date = ko.utils.recursiveUnwrap(arg.date) || arg;
            var format = ko.utils.recursiveUnwrap(arg.format) || "LL";

            $(element).text(moment(date).lang("en-gb").format(format));
        }
    };

    // Custom binding to format the datetime display with moment.js library
    ko.bindingHandlers.formDate = {
        init: function (element, valueAccessor) {
            var arg = ko.utils.recursiveUnwrap(valueAccessor);

            var date = arg.date || arg;
            var format = ko.utils.recursiveUnwrap(arg.format) || "dd M yyyy";
            var startDate = ko.utils.recursiveUnwrap(arg.startDate) || undefined;
            var endDate = ko.utils.recursiveUnwrap(arg.endDate) || undefined;

            $(element).parent().datepicker({
                format: format,
                startDate: startDate,
                endDate: endDate,
                autoclose: true,
                todayHighlight: true
            });

            ko.applyBindingAccessorsToNode(element, {
                value: date,
                valueUpdate: function () { return "input"; }
            });
        }
    };

    // Custom binding used to bind each child member of a group to an option
    ko.bindingHandlers.option = {
        update: function (element, valueAccessor) {
            var value = ko.utils.recursiveUnwrap(valueAccessor);
            ko.selectExtensions.writeValue(element, value);
        }
    };

    // Custom binding to fade the spinner in and out, using a default value if a duration is not specified.
    ko.bindingHandlers.fadeVisible = {
        update: function (element, valueAccessor) {
            var value = ko.utils.recursiveUnwrap(valueAccessor);
            var visible = value.visible;
            var duration = value.duration || 1000;
            if (visible) {
                $(element).show();
            } else {
                $(element).delay(500).fadeOut(duration);
            }
        }
    };

    ko.bindingHandlers.highlight = {
        update: function (element, valueAccessor, allBindings, deprecated, bindingContext) {
            var value = ko.utils.recursiveUnwrap(valueAccessor);
            var target = value.target;
            var compareOn = value.compareOn;
            var local = bindingContext.$data;

            $(element).removeClass("highlight");
            if ((target !== null) && (target[compareOn] === local[compareOn])) {
                $(element).addClass("highlight");
            }
        }
    };

    // Custom binding to apply the bootstrap "disabled" class, while also adding a "disabled" attribute (form elements)
    ko.bindingHandlers.bootstrapDisable = {
        init: function (element, valueAccessor) {
            ko.applyBindingAccessorsToNode(element, {
                enable: function () { return !valueAccessor(); },
                css: function () {
                    return { disabled: valueAccessor() };
                }
            });
        }
    };

    ko.bindingHandlers.formSubmit = {
        init: function (element, valueAccessor, allBindings, deprecated, bindingContext) {
            var wrappedValueAccessor = function () {
                return function () {
                    if (bindingContext.find("isValid") && !bindingContext.find("isSubmitting")) {
                        return valueAccessor()(element); // return true to not preventDefault
                    }
                    return false;
                };
            };

            ko.applyBindingAccessorsToNode(element, { submit: wrappedValueAccessor });
        }
    };

    ko.bindingHandlers.formButton = {
        init: function (element, valueAccessor, allBindings, deprecated, bindingContext) {
            ko.applyBindingAccessorsToNode(element, {
                text: function () {
                    return bindingContext.find("isSubmitting") ?
                        valueAccessor().submitting :
                        valueAccessor().standard;
                },
                bootstrapDisable: function () {
                    return !bindingContext.find("isValid") || bindingContext.find("isSubmitting");
                }
            });
        }
    };

    ko.bindingHandlers.formValue = {
        init: function (element, valueAccessor, allBindings, deprecated, bindingContext) {
            $(element).attr("autocomplete", "off");
            ko.applyBindingAccessorsToNode(element, {
                value: valueAccessor,
                valueUpdate: function () { return "input"; },
                bootstrapDisable: function () {
                    return bindingContext.find("isSubmitting");
                }
            });
        }
    };

    ko.bindingHandlers.formChecked = {
        init: function (element, valueAccessor, allBindings, deprecated, bindingContext) {
            ko.applyBindingAccessorsToNode(element, {
                checked: function () { return valueAccessor().checked; },
                checkedValue: function () { return valueAccessor().value; },
                bootstrapDisable: function () {
                    return bindingContext.find("isSubmitting");
                }
            });
        }
    };


});
