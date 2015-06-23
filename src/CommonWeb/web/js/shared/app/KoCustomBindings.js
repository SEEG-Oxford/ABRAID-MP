/*
 * An AMD defining and registering a set of custom knockout bindings.
 * Copyright (c) 2014 University of Oxford.
 */
define([
    "jquery",
    "moment",
    "knockout",
    "shared/app/KoCustomUtils",
    "flipclock"
], function ($, moment, ko) {
    "use strict";

    ko.bindingHandlers.toggleClick = {
        init: function (element, valueAccessor) {
            var value = valueAccessor();

            ko.utils.registerEventHandler(element, "click", function () {
                value(!value());
            });
        }
    };

    ko.bindingHandlers.numericText = {
        update: function (element, valueAccessor, allBindings) {
            var value = ko.utils.recursiveUnwrap(valueAccessor);
            var precision = allBindings().precision || 3;
            ko.bindingHandlers.text.update(element, function () { return value ? value.toFixed(precision) : ""; });
        }
    };

    // Custom binding to set the value on the flipclock.js counter
    ko.bindingHandlers.counter = {
        init: function (element, valueAccessor) {
            var value = Math.min(ko.utils.recursiveUnwrap(valueAccessor), 99999);
            var counter = $(element).FlipClock(value, { clockFace: "Counter" });
            ko.utils.domData.set(element, "counter", counter);
        },
        update: function (element, valueAccessor) {
            var counter = ko.utils.domData.get(element, "counter");
            var value = Math.min(ko.utils.recursiveUnwrap(valueAccessor), 99999);
            counter.setValue(value);
        }
    };

    // Custom binding to format the datetime display with moment.js library
    ko.bindingHandlers.date = {
        update: function (element, valueAccessor) {
            var arg = ko.utils.recursiveUnwrap(valueAccessor);

            var date = ko.utils.recursiveUnwrap(arg.date);
            if (typeof date === "undefined") {
                // distinguish null from undefined
                date = arg;
            }

            var format = ko.utils.recursiveUnwrap(arg.format) || "LL";
            var fallback = ko.utils.recursiveUnwrap(arg.fallback) || "Invalid Date";

            if (date !== null) {
                $(element).text(moment(date).lang("en-gb").format(format));
            } else {
                $(element).text(fallback);
            }
        }
    };

    // Custom binding to format the datetime display with moment.js library
    ko.bindingHandlers.formDate = {
        init: function (element, valueAccessor) {
            var arg = ko.utils.recursiveUnwrap(valueAccessor);

            var date = arg.date;
            var format = "dd M yyyy";

            // Start date and end date MUST be ko obs
            var startDate = arg.startDate() || -Infinity;
            var endDate = arg.endDate() || Infinity;

            var picker = $(element).parent().datepicker({
                format: format,
                startDate: startDate,
                endDate: endDate,
                autoclose: true,
                todayHighlight: true
            });

            arg.startDate.subscribe(function (value) {
                picker.datepicker("setStartDate", value || -Infinity);
            });

            arg.endDate.subscribe(function (value) {
                picker.datepicker("setEndDate", value || Infinity);
            });

            ko.applyBindingAccessorsToNode(element, {
                value: function () { return date; },
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

                if (!$(element).is(":hover")) {
                    $(element).parent().animate({
                        scrollTop:
                            $(element).parent().scrollTop() +
                            $(element).position().top +
                            parseInt($(element).css("borderTopWidth"), 10)
                    }, 250);
                }
            }
        }
    };

    ko.bindingHandlers.file = {
        init: function (element, valueAccessor) {
            var updateBinding = function () {
                var file = element.files[0];
                if (typeof valueAccessor() === "function") {
                    valueAccessor()(file);
                }
            };

            $(element).change(updateBinding);
            updateBinding();
        }
    };
    
    ko.bindingHandlers.slideLeft = {
        init: function (element, valueAccessor) {
            var distance = $(element).parent().css("width");
            ko.utils.domData.set(element, "distance", distance);
            if (ko.utils.recursiveUnwrap(valueAccessor)) {
                $(element).css("margin-left", 0);
            }
        },
        update: function (element, valueAccessor) {
            if (ko.utils.recursiveUnwrap(valueAccessor)) {
                $(element).animate({"margin-left": 0}, 500);
            } else {
                var distance = ko.utils.domData.get(element, "distance");
                $(element).animate({"margin-left": "-" + distance}, 500);
            }
        }
    };

    // Plotting
    ko.bindingHandlers.smallEffectPlot = {
        init: function (element) {
            element.style.width = 264;
            element.style.height = 198;
        },
        update: function () {
        }
    };

    ko.bindingHandlers.largeEffectPlot = {
        init: function (element) {
            element.style.width = 668;
            element.style.height = 453;
        },
        update: function () {
        }
    };

    ko.bindingHandlers.covariateHistogramPlot = {
        init: function (element) {
            element.style.width = 668;
            element.style.height = 300;
        },
        update: function () {
        }
    };

    ko.bindingHandlers.savePlot = {
        init: function () {
        }
    };
    
    // Prevent mouse events from bubbling up to higher DOM elements (argument = include click event)
    ko.bindingHandlers.preventBubble = {
        init: function (element, valueAccessor) {
            var includeClickEvent = valueAccessor();
            var bindings = {
                event: function () {
                    var eventBindings = {
                        dblclick: function () { return true; },
                        mousedown: function () { return true; }
                    };

                    if (includeClickEvent) {
                        $.extend(eventBindings, { click: function () { return true; } });
                    }

                    return eventBindings;
                },

                dblclickBubble: function () { return false; },
                mousedownBubble: function () { return false; }
            };

            if (includeClickEvent) {
                $.extend(bindings, { clickBubble: function () { return false; } });
            }

            ko.applyBindingAccessorsToNode(element, bindings);
        }
    };

    // Custom binding to apply the bootstrap "disabled" class, while also adding a "disabled" attribute (form elements)
    ko.bindingHandlers.bootstrapDisable = {
        init: function (element, valueAccessor) {
            ko.applyBindingAccessorsToNode(element, {
                enable: function () { return !ko.utils.recursiveUnwrap(valueAccessor); },
                css: function () {
                    return { disabled: ko.utils.recursiveUnwrap(valueAccessor) };
                }
            });
        }
    };

    ko.bindingHandlers.syncValue = {
        init: function (element, valueAccessor) {
            ko.applyBindingAccessorsToNode(element, {
                value: valueAccessor,
                valueUpdate: function () { return "input"; }
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
                syncValue: valueAccessor,
                bootstrapDisable: function () {
                    return bindingContext.find("isSubmitting");
                }
            });
        }
    };

    ko.bindingHandlers.formFile = {
        init: function (element, valueAccessor, allBindings, deprecated, bindingContext) {
            var useFormData = allBindings().useFormData;
            var bindings = {
                file: valueAccessor
            };

            if (useFormData) {
                bindings.bootstrapDisable = function () {
                    return bindingContext.find("isSubmitting");
                };
            } else {
                bindings.css = function () {
                    return { disabled: valueAccessor() };
                };
            }

            ko.applyBindingAccessorsToNode(element, bindings);
        }
    };

    ko.bindingHandlers.formChecked = {
        init: function (element, valueAccessor, allBindings, deprecated, bindingContext) {
            ko.applyBindingAccessorsToNode(element, {
                checked: valueAccessor,
                bootstrapDisable: function () {
                    return bindingContext.find("isSubmitting");
                }
            });
        }
    };

    ko.bindingHandlers.formRadio = {
        init: function (element, valueAccessor, allBindings, deprecated, bindingContext) {
            ko.applyBindingAccessorsToNode(element, {
                checked: function () { return valueAccessor().selected; },
                checkedValue: function () { return valueAccessor().value; },
                bootstrapDisable: function () {
                    return bindingContext.find("isSubmitting");
                }
            });
        }
    };
});
