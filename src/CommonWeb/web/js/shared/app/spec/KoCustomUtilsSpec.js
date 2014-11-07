/* A suite of tests for the KoCustomUtils AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko"
], function (ko) {
    "use strict";

    describe("KoCustomUtils defines", function () {
        describe("the 'normaliseInput' function which", function () {
            it("translates undefined, null and NaN to empty string", function () {
                expect(ko.utils.normaliseInput(undefined)).toBe("");
                expect(ko.utils.normaliseInput(null)).toBe("");
                expect(ko.utils.normaliseInput(NaN)).toBe("");
            });
            it("does not translate other values", function () {
                expect(ko.utils.normaliseInput(0)).toBe(0);
                expect(ko.utils.normaliseInput(false)).toBe(false);
                expect(ko.utils.normaliseInput(123)).toBe(123);
                expect(ko.utils.normaliseInput("123")).toBe("123");
            });
        });
    });
});
