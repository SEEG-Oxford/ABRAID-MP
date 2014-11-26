/* A suite of tests for the ModalView AMD.
 * Copyright (c) 2014 University of Oxford
 */
define(["squire"], function (Squire) {
    "use strict";

    describe("The 'ModalView'", function () {
        var ModalView;
        var element = { foo: "bar" };
        var modalSpy = jasmine.createSpy();
        var onSpy = jasmine.createSpy();

        var jqMock = jasmine.createSpy().and.callFake(function (arg) {
            return (arg === element) ? { modal: modalSpy, on: onSpy } : null;
        });

        beforeEach(function (done) {
            // Reset the jquery spies
            modalSpy.calls.reset();
            onSpy.calls.reset();

            // Before first test, load ModalView with the jQuery mock
            if (ModalView === undefined) {
                // Squire is going to load js files via ajax, so get rid of the jasmine mock ajax stuff first
                jasmine.Ajax.uninstall();
                var injector = new Squire();
                injector.mock("jquery", jqMock);

                injector.require(["app/datavalidation/ModalView"],
                    function (localModalView) {
                        ModalView = localModalView;
                        done();
                    }
                );
            } else {
                done();
            }
        });

        it("shows the modal initially, if required", function () {
            new ModalView(element, true); // jshint ignore:line
            expect(modalSpy).toHaveBeenCalledWith("show");
        });

        it("does not open the modal initially, when not required", function () {
            new ModalView(element, false); // jshint ignore:line
            expect(modalSpy).not.toHaveBeenCalled();
        });

        it("prevents mouse events propagating beyond the expected element", function () {
            new ModalView(element, true); // jshint ignore:line
            expect(onSpy).toHaveBeenCalledWith("mousedown mousewheel", jasmine.any(Function));
        });
    });
});
