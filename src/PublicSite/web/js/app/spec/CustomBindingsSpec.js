define([
    "ko",
    "domReady!",
    "app/spec/lib/squire"
], function (ko, doc, Squire) {
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

        describe("the 'date' binding, which", function () {
            var textSpy, jqSpy, injectorWithJQuerySpy, momentSpy;
            var expectedElement = "expectedElement";
            var expectedText = "expectedText";
            beforeEach(function () {
                textSpy = jasmine.createSpy();
                momentSpy =  jasmine.createSpy().and.returnValue({ lang: function () {
                    return { format: function () { return expectedText; }};
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
        });

        describe("the 'highlight' binding, which", function () {
            // Arrange
            var expectedElement, removeSpy, addSpy, jqSpy, injectorWithJQuerySpy;
            beforeEach(function () {
                removeSpy = jasmine.createSpy();
                addSpy = jasmine.createSpy();
                jqSpy = jasmine.createSpy().and.returnValue({removeClass: removeSpy, addClass: addSpy});

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
    });
});

