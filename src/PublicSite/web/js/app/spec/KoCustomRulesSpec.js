define([
    "ko",
    "app/spec/lib/squire"
], function (ko) {
    "use strict";

    describe("KoCustomRules defines", function () {
        describe("the 'areSame' rule which", function () {
            it("checks the two values are the same", function () {
                expect(ko.validation.rules.areSame.validator(1, 1)).toBe(true);
                expect(ko.validation.rules.areSame.validator(1, 2)).toBe(false);
            });

            it("can validated wrapped values", function () {
                var wrap = function (value) {
                    return function () {
                        return value;
                    };
                };

                expect(ko.validation.rules.areSame.validator(wrap(1), wrap(wrap(1)))).toBe(true);
                expect(ko.validation.rules.areSame.validator(wrap(1), wrap(wrap(2)))).toBe(false);
            });

            it("has a suitable failure message", function () {
                expect(ko.validation.rules.areSame.message).toBe("Password fields must match");
            });
        });

        describe("the 'passwordComplexity' rule which", function () {
            it("rejects passwords below 6 chars", function () {
                expect(ko.validation.rules.passwordComplexity.validator("aBc12")).toBe(false);
            });

            it("rejects passwords over 128 chars", function () {
                var longString = (new Array(33)).join("aA*1");
                expect(longString.length).toBe(128);
                expect(ko.validation.rules.passwordComplexity.validator(longString + "a")).toBe(false);
            });

            it("rejects passwords without sufficient distinct char classes", function () {
                expect(ko.validation.rules.passwordComplexity.validator("abc123q")).toBe(false);
                expect(ko.validation.rules.passwordComplexity.validator("abc*&^q")).toBe(false);
                expect(ko.validation.rules.passwordComplexity.validator("ABC*&^Q")).toBe(false);
                expect(ko.validation.rules.passwordComplexity.validator("AAAAaa")).toBe(false);
            });

            it("accepts complex passwords", function () {
                expect(ko.validation.rules.passwordComplexity.validator("qwe123Q")).toBe(true);
                expect(ko.validation.rules.passwordComplexity.validator("Password1")).toBe(true);
            });

            it("has a suitable failure message", function () {
                expect(ko.validation.rules.passwordComplexity.message)
                    .toContain("Password must be between 6 and 128 characters long and contain three of the following");
            });
        });

        it("an alternative failure message for the 'digit' rule", function () {
            expect(ko.validation.rules.digit.message).toBe("Please enter a whole number");
        });

        describe("the 'isUniqueProperty' rule which", function () {
            var diseaseGroup1 = { id: 1, name: "Name 1", publicName: "" };
            var diseaseGroup2 = { id: 2, name: "Name 2", publicName: "Public Name 2" };
            var diseaseGroup3 = { id: 3, name: "Name 3", publicName: "Public Name 3" };
            var diseaseGroup4 = { id: 4, name: "Name 4", publicName: null };
            var diseaseGroups = [diseaseGroup1, diseaseGroup2, diseaseGroup3, diseaseGroup4];

            it("accepts a value if the list is empty", function () {
                var options = { array: [], id: 2, property: "name" };
                expect(ko.validation.rules.isUniqueProperty.validator("Name 2", options)).toBe(true);
            });

            it("accepts a value if absent from the list under the specified property, and the ID is new", function () {
                var options = { array: diseaseGroups, id: 5, property: "name" };
                expect(ko.validation.rules.isUniqueProperty.validator("Name 5", options)).toBe(true);
            });

            it("accepts a value if absent from the list under the specified property", function () {
                var options = { array: diseaseGroups, id: 1, property: "name" };
                expect(ko.validation.rules.isUniqueProperty.validator("Public Name 2", options)).toBe(true);
            });

            it("rejects a value if present in the list under the specified property", function () {
                var options = { array: diseaseGroups, id: 1, property: "name" };
                expect(ko.validation.rules.isUniqueProperty.validator("Name 2", options)).toBe(false);
            });

            it("accepts a value if the only occurrence in the list is itself", function () {
                var options = { array: diseaseGroups, id: 2, property: "name" };
                expect(ko.validation.rules.isUniqueProperty.validator("Name 2", options)).toBe(true);
            });

            it("accepts a value if the only occurrence in the list is itself, with observable ID", function () {
                var id = ko.observable();
                id(2);
                var options = { array: diseaseGroups, id: id, property: "name" };
                expect(ko.validation.rules.isUniqueProperty.validator("Name 2", options)).toBe(true);
            });

            it("accepts a null value even if there are null values in the list", function () {
                var options = { array: diseaseGroups, id: 2, property: "publicName" };
                expect(ko.validation.rules.isUniqueProperty.validator(null, options)).toBe(true);
            });

            it("accepts an empty value even if there are empty values in the list", function () {
                var options = { array: diseaseGroups, id: 2, property: "publicName" };
                expect(ko.validation.rules.isUniqueProperty.validator("", options)).toBe(true);
            });

            it("has a suitable failure message", function () {
                expect(ko.validation.rules.isUniqueProperty.message)
                    .toContain("Value must be unique");
            });
        });
    });
});
