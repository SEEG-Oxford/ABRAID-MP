/* A suite of tests for the SidePanelViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/SidePanelViewModel",
    "ko"
], function (SidePanelViewModel, ko) {
    "use strict";

    describe("The 'side panel' view model", function () {
        describe("holds the name of the template to be displayed on the side panel, which", function () {
            var vm = {};
            beforeEach(function () {
                vm = new SidePanelViewModel("", "");
            });

            it("is an observable", function () {
                expect(vm.templateName).toBeObservable();
            });

            describe("returns the expected template", function () {
                it("for disease occurrences layer", function () {
                    ko.postbox.publish("layers-changed", { type: "disease occurrences" });
                    expect(vm.templateName()).toBe("occurrences-template");
                });

                it("for disease extent layer", function () {
                    ko.postbox.publish("layers-changed", { type: "disease extent" });
                    expect(vm.templateName()).toBe("admin-units-template");
                });
            });
        });

        describe("holds the two view models to bind to the side panel which", function () {
            it("take the expected values on construction", function () {
                var vm = new SidePanelViewModel("foo", "bar");
                expect(vm.selectedPointViewModel).toBe("foo");
                expect(vm.selectedAdminUnitViewModel).toBe("bar");
            });
        });
    });
});