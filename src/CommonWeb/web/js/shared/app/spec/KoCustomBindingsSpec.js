define([
    "ko",
    "squire"
], function (ko, Squire) {
    "use strict";

    describe("KoCustomBindings defines", function () {
        describe("the 'option' binding which", function () {
            it("writes a value to the given element", function () {
                // Arrange
                var element = {};
                var value = "value";
                // Act
                ko.bindingHandlers.option.update(element, value);
                // Assert
                expect(ko.selectExtensions.readValue(element)).toBe(value);
            });

            it("writes a wrapped value to the given element", function () {
                // Arrange
                var element = {};
                var value = "value";
                var wrappedValue = function () { return value; };
                // Act
                ko.bindingHandlers.option.update(element, wrappedValue);
                // Assert
                expect(ko.selectExtensions.readValue(element)).toBe(value);
            });
        });

        describe("the 'fadeVisible' binding which", function () {
            var showSpy, delaySpy, fadeSpy, jqSpy, injectorWithJQuerySpy;
            var expectedElement = "expectedElement";
            beforeEach(function () {
                showSpy = jasmine.createSpy();
                fadeSpy = jasmine.createSpy();
                delaySpy = jasmine.createSpy().and.returnValue({ fadeOut: fadeSpy });
                var noop = function () {};

                jqSpy = jasmine.createSpy().and.callFake(function (arg) {
                    return (arg === expectedElement) ?
                    { show: showSpy, delay: delaySpy } :
                    { show: noop, delay: noop };
                });

                // Squire.require is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                jasmine.Ajax.uninstall();
                injectorWithJQuerySpy = new Squire();

                injectorWithJQuerySpy.mock("jquery", jqSpy);
            });

            describe("shows the element,", function () {
                it("when taking an unwrapped 'true' value", function (done) {
                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.fadeVisible.update(expectedElement, { visible: true });

                        // Assert
                        expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                        expect(showSpy).toHaveBeenCalled();
                        done();
                    });
                });

                it("when taking a wrapped 'true' value", function (done) {
                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.fadeVisible.update(expectedElement, function () { return {visible: true}; });

                        // Assert
                        expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                        expect(showSpy).toHaveBeenCalled();
                        done();
                    });
                });
            });

            describe("fades out the element on a 'false' value", function () {
                it("after a 500 ms delay", function (done) {
                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.fadeVisible.update(expectedElement, { visible: false });

                        // Assert
                        expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                        expect(delaySpy).toHaveBeenCalledWith(500);
                        done();
                    });
                });

                it("with the specified duration, when provided", function (done) {
                    // Arrange
                    var duration = 54321;

                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.fadeVisible.update(expectedElement, { visible: false, duration: duration });

                        // Assert
                        expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                        expect(fadeSpy).toHaveBeenCalledWith(duration);
                        done();
                    });
                });

                it("with the correct default duration otherwise", function (done) {
                    var defaultDuration = 1000;
                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.fadeVisible.update(expectedElement, { visible: false });

                        // Assert
                        expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                        expect(fadeSpy).toHaveBeenCalledWith(defaultDuration);
                        done();
                    });
                });
            });
        });

        describe("the 'file' binding which", function () {
            var jqSpy, injectorWithJQuerySpy;
            beforeEach(function () {
                jqSpy = jasmine.createSpy("jqSpy").and.returnValue({
                    change: jasmine.createSpy("changeSpy")
                });

                // Squire.require is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                jasmine.Ajax.uninstall();
                injectorWithJQuerySpy = new Squire();

                injectorWithJQuerySpy.mock("jquery", jqSpy);
            });

            it("subscribes to changes on the value of the target element, with a callback that updates the value of the observable", function (done) { // jshint ignore:line
                injectorWithJQuerySpy.require(["ko"], function (ko) {
                    // Arrange
                    var expectedElement = { files: [ "expectedFile" ] };
                    var observableSpy = jasmine.createSpy("observableSpy");

                    // Act
                    ko.bindingHandlers.file.init(expectedElement, function () { return observableSpy; });

                    // Assert
                    expect(observableSpy).toHaveBeenCalledWith("expectedFile");
                    expect(jqSpy().change).toHaveBeenCalled();
                    var callback = jqSpy().change.calls.argsFor(0)[0];
                    expectedElement.files[0] = "newFile";
                    callback();
                    expect(observableSpy).toHaveBeenCalledWith("newFile");
                    done();
                });
            });

            it("updates the initial value of the observable", function (done) {
                injectorWithJQuerySpy.require(["ko"], function (ko) {
                    // Arrange
                    var expectedElement = { files: [ "expectedFile" ] };
                    var observableSpy = jasmine.createSpy("observableSpy");

                    // Act
                    ko.bindingHandlers.file.init(expectedElement, function () { return observableSpy; });

                    // Assert
                    expect(observableSpy).toHaveBeenCalledWith("expectedFile");
                    done();
                });
            });
        });

        describe("the 'date' binding, which", function () {
            var textSpy, jqSpy, injectorWithJQuerySpy, momentSpy, formatSpy;
            var expectedElement = "expectedElement";
            var expectedText = "expectedText";
            beforeEach(function () {
                textSpy = jasmine.createSpy("textSpy");
                formatSpy = jasmine.createSpy("formatSpy").and.returnValue(expectedText);
                momentSpy =  jasmine.createSpy().and.returnValue({ lang: function () {
                    return { format: formatSpy };
                } });
                jqSpy = jasmine.createSpy().and.returnValue({ text: textSpy });

                // Squire.require is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                jasmine.Ajax.uninstall();
                injectorWithJQuerySpy = new Squire();

                injectorWithJQuerySpy.mock("jquery", jqSpy);
                injectorWithJQuerySpy.mock("moment", momentSpy);
            });

            it("adds text to the element", function (done) {
                // Arrange
                injectorWithJQuerySpy.require(["ko"], function (ko) {
                    // Act
                    var expectedDate = "expectedDate";
                    ko.bindingHandlers.date.update(expectedElement, expectedDate);
                    // Assert
                    expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                    expect(momentSpy).toHaveBeenCalledWith(expectedDate);
                    expect(textSpy).toHaveBeenCalledWith(expectedText);
                    done();
                });
            });

            it("allows the date format to be specified", function (done) {
                // Arrange
                injectorWithJQuerySpy.require(["ko"], function (ko) {
                    // Act
                    var expectedDate = "expectedDate";
                    var expectedFormat = "expectedFormat";
                    var accessor = { date: expectedDate, format: expectedFormat };
                    ko.bindingHandlers.date.update(expectedElement, accessor);
                    // Assert
                    expect(momentSpy).toHaveBeenCalledWith(expectedDate);
                    expect(formatSpy).toHaveBeenCalledWith(expectedFormat);
                    done();
                });
            });

            it("allows a fallback string to be specified", function (done) {
                // Arrange
                injectorWithJQuerySpy.require(["ko"], function (ko) {
                    // Act
                    var expectedDate = null;
                    var expectedFormat = "expectedFormat";
                    var expectedFallback = "fallback";
                    var accessor = { date: expectedDate, format: expectedFormat, fallback: expectedFallback };
                    ko.bindingHandlers.date.update(expectedElement, accessor);
                    // Assert
                    expect(momentSpy).not.toHaveBeenCalled();
                    expect(formatSpy).not.toHaveBeenCalled();
                    expect(textSpy).toHaveBeenCalledWith(expectedFallback);
                    done();
                });
            });
        });

        describe("the 'highlight' binding, which", function () {
            // Arrange
            var expectedElement, injectorWithJQuerySpy;
            var removeSpy, addSpy, isSpy, parentSpy, animateSpy, scrollTopSpy, jqSpy, positionSpy, cssSpy;
            beforeEach(function () {
                removeSpy = jasmine.createSpy("removeSpy");
                addSpy = jasmine.createSpy("addSpy");
                isSpy = jasmine.createSpy("isSpy").and.returnValue(false);
                cssSpy = jasmine.createSpy("cssSpy").and.returnValue("5px");
                animateSpy = jasmine.createSpy("animateSpy");
                scrollTopSpy = jasmine.createSpy("scrollTopSpy").and.returnValue(100);
                positionSpy = jasmine.createSpy("positionSpy").and.returnValue({top: 50});
                parentSpy = jasmine.createSpy("parentSpy").and.returnValue({
                    animate: animateSpy,
                    scrollTop: scrollTopSpy
                });
                jqSpy = jasmine.createSpy().and.returnValue({
                    removeClass: removeSpy,
                    addClass: addSpy,
                    is: isSpy,
                    parent: parentSpy,
                    position: positionSpy,
                    css: cssSpy
                });

                // Squire.require is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                jasmine.Ajax.uninstall();
                injectorWithJQuerySpy = new Squire();

                injectorWithJQuerySpy.mock("jquery", jqSpy);
            });

            it("adds the css class to the selected admin unit", function (done) {
                // Arrange
                var expectedAdminUnit = { id: "0" };
                injectorWithJQuerySpy.require(["ko"], function (ko) {
                    // Act
                    ko.bindingHandlers.highlight.update(expectedElement, { target: expectedAdminUnit, compareOn: "id" },
                        {}, {}, { "$data": expectedAdminUnit });
                    // Assert
                    expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                    expect(removeSpy).toHaveBeenCalledWith("highlight");
                    expect(addSpy).toHaveBeenCalledWith("highlight");
                    done();
                });
            });

            describe("sets the scroll position of selected admin unit's parent", function () {
                it("to the position of the element", function (done) {
                    // Arrange
                    var expectedAdminUnit = { id: "0" };
                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.highlight.update(expectedElement,
                            { target: expectedAdminUnit, compareOn: "id" }, {}, {}, { "$data": expectedAdminUnit });
                        // Assert
                        expect(parentSpy).toHaveBeenCalled();
                        expect(animateSpy).toHaveBeenCalledWith({ scrollTop: 155 }, 250);
                        done();
                    });
                });

                it("if the mouse is not over the element", function (done) {
                    // Arrange
                    var expectedAdminUnit = { id: "0" };
                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.highlight.update(expectedElement,
                            { target: expectedAdminUnit, compareOn: "id" }, {}, {}, { "$data": expectedAdminUnit });
                        // Assert
                        expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                        expect(isSpy).toHaveBeenCalledWith(":hover");
                        expect(parentSpy).toHaveBeenCalled();
                        expect(animateSpy).toHaveBeenCalled();
                        done();
                    });
                });

                it("unless the mouse is over the element", function (done) {
                    // Arrange
                    var expectedAdminUnit = { id: "0" };
                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        isSpy.and.returnValue(true);
                        // Act
                        ko.bindingHandlers.highlight.update(expectedElement,
                            { target: expectedAdminUnit, compareOn: "id" }, {}, {}, { "$data": expectedAdminUnit });
                        // Assert
                        expect(animateSpy).not.toHaveBeenCalled();
                        done();
                    });
                });
            });

            it("does not add the css class to any other admin unit", function (done) {
                // Arrange
                var expectedAdminUnit = { id: 0 };
                var anotherAdminUnit = { id: 1 };
                injectorWithJQuerySpy.require(["ko"], function (ko) {
                    // Act
                    ko.bindingHandlers.highlight.update(expectedElement, { target: expectedAdminUnit, compareOn: "id" },
                        {}, {}, { "$data": anotherAdminUnit });
                    // Assert
                    expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                    expect(removeSpy).toHaveBeenCalledWith("highlight");
                    expect(addSpy).not.toHaveBeenCalled();
                    done();
                });
            });

            it("does not add the css class when target is null", function (done) {
                // Arrange
                injectorWithJQuerySpy.require(["ko"], function (ko) {
                    // Act
                    ko.bindingHandlers.highlight.update(expectedElement, { target: null, compareOn: "id" },
                        {}, {}, { "$data": {} });
                    // Assert
                    expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                    expect(removeSpy).toHaveBeenCalledWith("highlight");
                    expect(addSpy).not.toHaveBeenCalled();
                    done();
                });
            });
        });

        describe("multiple composite bindings, including", function () {
            var findBuilder = function (valueForValid, valueForSubmitting) {
                return function (arg) {
                    if ("isSubmitting" === arg) {
                        return valueForSubmitting;
                    }

                    if ("isValid" === arg) {
                        return valueForValid;
                    }

                    return false;
                };
            };

            describe("the 'bootstrapDisable' binding, which", function () {
                ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                var element = "1234";
                var accessor = function () { return true; };
                ko.bindingHandlers.bootstrapDisable.init(element, accessor);
                var subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];

                it("adds composite bindings to the same element", function () {
                    expect(ko.applyBindingAccessorsToNode.calls.count()).toBe(1);
                    expect(ko.applyBindingAccessorsToNode.calls.mostRecent().args[0]).toBe(element);
                });

                it("applies an 'enable' binding with a negated version of the value accessor", function () {
                    expect(subBindings.enable).toBeDefined();
                    expect(typeof subBindings.enable).toBe("function");
                    expect(subBindings.enable()).toBe(!accessor());
                });

                it("applies a 'css' binding with a value accessor enabling the disabled class by the parent accessor",
                    function () {
                        expect(subBindings.css).toBeDefined();
                        expect(typeof subBindings.css).toBe("function");
                        expect(subBindings.css().disabled).toBeDefined();
                        expect(subBindings.css().disabled).toBe(accessor());
                    }
                );

                it("can handle wrapped values", function () {
                    // Arrange
                    ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                    var element = "5678";
                    var wrap = function (value) { return function () { return value; }; };
                    var accessor = function () { return true; };
                    // Act
                    ko.bindingHandlers.bootstrapDisable.init(element, wrap(accessor));
                    // Assert
                    expect(ko.applyBindingAccessorsToNode.calls.mostRecent().args[0]).toBe(element);
                    var subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];
                    expect(subBindings.enable()).toBe(!accessor());
                    expect(subBindings.css().disabled).toBe(accessor());
                });
            });

            describe("the 'formSubmit' binding, which", function () {
                var context, subBindings, submitFunction;
                var element = "1234";
                var accessor = function () { return submitFunction; };

                beforeEach(function () {
                    ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                    submitFunction = jasmine.createSpy("submitFunction");
                    context = { find: function () { return false; } };
                    ko.bindingHandlers.formSubmit.init(element, accessor, {}, {}, context);
                    subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];
                });


                it("adds composite bindings to the same element", function () {
                    expect(ko.applyBindingAccessorsToNode.calls.count()).toBe(1);
                    expect(ko.applyBindingAccessorsToNode.calls.mostRecent().args[0]).toBe(element);
                });


                describe("applies a 'submit' binding with a wrapped version of the value accessor, which", function () {
                    it("is fired for valid and non-submitting forms", function () {
                        expect(subBindings.submit).toBeDefined();
                        expect(typeof subBindings.submit).toBe("function");
                        expect(typeof subBindings.submit()).toBe("function");

                        context.find = findBuilder(true, false);
                        subBindings.submit()();
                        expect(submitFunction).toHaveBeenCalledWith(element);
                    });

                    it("applies a 'submit' binding which only fires for valid forms", function () {
                        context.find = findBuilder(false, false);
                        subBindings.submit()();
                        expect(submitFunction).not.toHaveBeenCalled();
                    });

                    it("applies a 'submit' binding which only fires for non-submitting forms", function () {
                        context.find = findBuilder(true, true);
                        subBindings.submit()();
                        expect(submitFunction).not.toHaveBeenCalled();
                    });
                });
            });

            describe("the 'formFile' binding, which", function () {
                var context;
                var accessor = function () {
                    return "value";
                };

                beforeEach(function () {
                    ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                    context = { find: function () { return false; } };
                });

                it("adds composite bindings to the same element", function () {
                    var element = "1234";
                    ko.bindingHandlers.formFile.init(
                        element, accessor, function () { return { useFormData: true }; }, {}, context);

                    expect(ko.applyBindingAccessorsToNode.calls.count()).toBe(1);
                    expect(ko.applyBindingAccessorsToNode.calls.mostRecent().args[0]).toBe(element);
                });

                it("applies a 'bootstrapDisable' binding with a submitting based accessor when useFormData is true",
                    function () {
                        ko.bindingHandlers.formFile.init(
                            {}, accessor, function () { return { useFormData: true }; }, {}, context);
                        var subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];

                        expect(subBindings.bootstrapDisable).toBeDefined();
                        expect(typeof subBindings.bootstrapDisable).toBe("function");

                        context.find = findBuilder(true, false); // not submitting
                        expect(subBindings.bootstrapDisable()).toBe(false);

                        context.find = findBuilder(true, true);  // submitting
                        expect(subBindings.bootstrapDisable()).toBe(true);
                    }
                );

                it("does not apply a 'bootstrapDisable' binding when useFormData is false", function () {
                    ko.bindingHandlers.formFile.init(
                        {}, accessor, function () { return { useFormData: false }; }, {}, context);
                    var subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];

                    expect(subBindings.bootstrapDisable).toBeUndefined();
                });

                it("applies a 'file' binding with the value accessor", function () {
                    ko.bindingHandlers.formFile.init(
                        {}, accessor, function () { return { useFormData: true }; }, {}, context);
                    var subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];

                    expect(subBindings.file).toBeDefined();
                    expect(typeof subBindings.file).toBe("function");
                    expect(subBindings.file()).toBe(accessor());
                });
            });

            describe("the 'formButton' binding, which", function () {
                var context, subBindings;
                var element = "1234";
                var accessor = function () { return { submitting: "submitting", standard: "standard" }; };

                beforeEach(function () {
                    ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                    context = { find: function () { return false; } };
                    ko.bindingHandlers.formButton.init(element, accessor, {}, {}, context);
                    subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];
                });

                it("adds composite bindings to the same element", function () {
                    expect(ko.applyBindingAccessorsToNode.calls.count()).toBe(1);
                    expect(ko.applyBindingAccessorsToNode.calls.mostRecent().args[0]).toBe(element);
                });

                it("applies a 'bootstrapDisable' binding with a validity/submitting based accessor", function () {
                    expect(subBindings.bootstrapDisable).toBeDefined();
                    expect(typeof subBindings.bootstrapDisable).toBe("function");

                    context.find = findBuilder(true, false); // valid & not submitting
                    expect(subBindings.bootstrapDisable()).toBe(false);     // enabled

                    context.find = findBuilder(true, true);  // valid & submitting
                    expect(subBindings.bootstrapDisable()).toBe(true);      // disabled

                    context.find = findBuilder(false, false); // invalid & not submitting
                    expect(subBindings.bootstrapDisable()).toBe(true);       // disabled

                    context.find = findBuilder(false, true);  // invalid & submitting
                    expect(subBindings.bootstrapDisable()).toBe(true);       // disabled
                });

                it("applies a 'text' binding with a submitting based accessor, using the values from parent accessor",
                    function () {
                        expect(subBindings.text).toBeDefined();
                        expect(typeof subBindings.text).toBe("function");

                        context.find = findBuilder(true, false); // not submitting
                        expect(subBindings.text()).toBe(accessor().standard);

                        context.find = findBuilder(true, true);  // submitting
                        expect(subBindings.text()).toBe(accessor().submitting);
                    }
                );
            });

            describe("the 'formRadio' binding, which", function () {
                var context, subBindings;
                var element = "1234";
                var accessor = function () { return { selected: "1234", value: "4321" }; };

                beforeEach(function () {
                    ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                    context = { find: function () { return false; } };
                    ko.bindingHandlers.formRadio.init(element, accessor, {}, {}, context);
                    subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];
                });

                it("adds composite bindings to the same element", function () {
                    expect(ko.applyBindingAccessorsToNode.calls.count()).toBe(1);
                    expect(ko.applyBindingAccessorsToNode.calls.mostRecent().args[0]).toBe(element);
                });

                it("applies a 'checked' binding with the selected sub-accessor", function () {
                    expect(subBindings.checked).toBeDefined();
                    expect(typeof subBindings.checked).toBe("function");
                    expect(subBindings.checked()).toBe(accessor().selected);
                });

                it("applies a 'checkedValue' binding with the value sub-accessor", function () {
                    expect(subBindings.checkedValue).toBeDefined();
                    expect(typeof subBindings.checkedValue).toBe("function");
                    expect(subBindings.checkedValue()).toBe(accessor().value);
                });

                it("applies a 'bootstrapDisable' binding with a submitting based accessor", function () {
                    expect(subBindings.bootstrapDisable).toBeDefined();
                    expect(typeof subBindings.bootstrapDisable).toBe("function");

                    context.find = findBuilder(true, false); // not submitting
                    expect(subBindings.bootstrapDisable()).toBe(false);

                    context.find = findBuilder(true, true);  // submitting
                    expect(subBindings.bootstrapDisable()).toBe(true);
                });
            });

            describe("the 'formChecked' binding, which", function () {
                var context, subBindings;
                var element = "1234";
                var accessor = function () { return { checked: "1234", value: "4321" }; };

                beforeEach(function () {
                    ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                    context = { find: function () { return false; } };
                    ko.bindingHandlers.formChecked.init(element, accessor, {}, {}, context);
                    subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];
                });

                it("adds composite bindings to the same element", function () {
                    expect(ko.applyBindingAccessorsToNode.calls.count()).toBe(1);
                    expect(ko.applyBindingAccessorsToNode.calls.mostRecent().args[0]).toBe(element);
                });

                it("applies a 'checked' binding with the value accessor", function () {
                    expect(subBindings.checked).toBeDefined();
                    expect(typeof subBindings.checked).toBe("function");
                    expect(subBindings.checked).toBe(accessor);
                });

                it("applies a 'bootstrapDisable' binding with a submitting based accessor", function () {
                    expect(subBindings.bootstrapDisable).toBeDefined();
                    expect(typeof subBindings.bootstrapDisable).toBe("function");

                    context.find = findBuilder(true, false); // not submitting
                    expect(subBindings.bootstrapDisable()).toBe(false);

                    context.find = findBuilder(true, true);  // submitting
                    expect(subBindings.bootstrapDisable()).toBe(true);
                });
            });

            describe("the 'formValue' binding, which", function () {
                var context, subBindings;
                var element = "1234";
                var accessor = function () { return { checked: "1234", value: "4321" }; };

                beforeEach(function () {
                    ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                    context = { find: function () { return false; } };
                    ko.bindingHandlers.formValue.init(element, accessor, {}, {}, context);
                    subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];
                });

                it("adds composite bindings to the same element", function () {
                    expect(ko.applyBindingAccessorsToNode.calls.count()).toBe(1);
                    expect(ko.applyBindingAccessorsToNode.calls.mostRecent().args[0]).toBe(element);
                });

                it("disables autocomplete on the element", function (done) {
                    // Squire.require is going to load js files via ajax so get rid of the jasmine mock ajax stuff first
                    jasmine.Ajax.uninstall();
                    var injector = new Squire();

                    var attrSpy = jasmine.createSpy("attr");
                    var jqSpy = jasmine.createSpy("$").and.callFake(function () {
                        return { attr: attrSpy };
                    });
                    injector.mock("jquery", jqSpy);

                    injector.require(["ko"], function (ko) {
                        ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                        ko.bindingHandlers.formValue.init(element, accessor, {}, {}, context);

                        expect(jqSpy.calls.count()).toBe(1);
                        expect(jqSpy).toHaveBeenCalledWith(element);
                        expect(attrSpy.calls.count()).toBe(1);
                        expect(attrSpy).toHaveBeenCalledWith("autocomplete", "off");

                        done();
                    });
                });

                it("applies a 'syncValue' binding with the parent accessor", function () {
                    expect(subBindings.syncValue).toBeDefined();
                    expect(typeof subBindings.syncValue).toBe("function");
                    expect(subBindings.syncValue).toBe(accessor);
                });

                it("applies a 'bootstrapDisable' binding with a submitting based accessor", function () {
                    expect(subBindings.bootstrapDisable).toBeDefined();
                    expect(typeof subBindings.bootstrapDisable).toBe("function");

                    context.find = findBuilder(true, false); // not submitting
                    expect(subBindings.bootstrapDisable()).toBe(false);

                    context.find = findBuilder(true, true);  // submitting
                    expect(subBindings.bootstrapDisable()).toBe(true);
                });
            });

            describe("the 'syncValue' binding which", function () {
                var context, subBindings;
                var element = "1234";
                var accessor = function () { return { checked: "1234", value: "4321" }; };

                beforeEach(function () {
                    ko.applyBindingAccessorsToNode = jasmine.createSpy("ko.applyBindingAccessorsToNode");
                    context = { find: function () { return false; } };
                    ko.bindingHandlers.syncValue.init(element, accessor, {}, {}, context);
                    subBindings = ko.applyBindingAccessorsToNode.calls.mostRecent().args[1];
                });

                it("applies a 'value' binding with the parent accessor", function () {
                    expect(subBindings.value).toBeDefined();
                    expect(typeof subBindings.value).toBe("function");
                    expect(subBindings.value).toBe(accessor);
                });

                it("applies a 'valueUpdate' binding with the value accessor that returns 'input'", function () {
                    expect(subBindings.valueUpdate).toBeDefined();
                    expect(typeof subBindings.valueUpdate).toBe("function");
                    expect(subBindings.valueUpdate()).toBe("input");
                });
            });
        });
    });
});

