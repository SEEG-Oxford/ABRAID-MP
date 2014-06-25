define([
    "domReady!",
    "app/spec/lib/squire"
], function (doc, Squire) {
    "use strict";

    describe("KoCustomBindings defines", function () {
        describe("the 'fadeVisible' binding which", function () {
            var showSpy, delaySpy, fadeSpy, jqSpy, injectorWithJQuerySpy;
            var emptyBindings = { get : function () { return null; } };
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

                injectorWithJQuerySpy = new Squire();
                injectorWithJQuerySpy.mock("jquery", jqSpy);
            });

            describe("shows the element,", function () {
                it("when taking an unwrapped 'true' value", function (done) {
                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.fadeVisible.update(expectedElement, true, emptyBindings);

                        // Assert
                        expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                        expect(showSpy).toHaveBeenCalled();
                        done();
                    });
                });

                it("when taking a wrapped 'true' value", function (done) {
                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.fadeVisible.update(expectedElement, function () { return true; }, emptyBindings);

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
                        ko.bindingHandlers.fadeVisible.update(expectedElement, false, emptyBindings);

                        // Assert
                        expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                        expect(delaySpy).toHaveBeenCalledWith(500);
                        done();
                    });
                });

                it("with the specified duration, when provided", function (done) {
                    // Arrange
                    var duration = 54321;
                    var bindings =  { get: jasmine.createSpy().and.returnValue(duration) };

                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.fadeVisible.update(expectedElement, false, bindings);

                        // Assert
                        expect(bindings.get).toHaveBeenCalledWith("fadeDuration");
                        expect(bindings.get.calls.count()).toBe(1);
                        expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                        expect(fadeSpy).toHaveBeenCalledWith(duration);
                        done();
                    });
                });

                it("with the correct default duration otherwise", function (done) {
                    var defaultDuration = 1000;
                    injectorWithJQuerySpy.require(["ko"], function (ko) {
                        // Act
                        ko.bindingHandlers.fadeVisible.update(expectedElement, false, emptyBindings);

                        // Assert
                        expect(jqSpy).toHaveBeenCalledWith(expectedElement);
                        expect(fadeSpy).toHaveBeenCalledWith(defaultDuration);
                        done();
                    });
                });
            });
        });

        describe("the 'date' binding which", function () {
            var textSpy, jqSpy, injectorWithJQuerySpy, momentSpy;
            var expectedElement = "expectedElement";
            var expectedText = "expectedText";
            beforeEach(function () {
                textSpy = jasmine.createSpy();
                momentSpy =  jasmine.createSpy().and.returnValue({ lang: function () {
                    return { format: function () { return expectedText; }};
                } });
                jqSpy = jasmine.createSpy().and.returnValue({ text: textSpy });
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
    });
});

