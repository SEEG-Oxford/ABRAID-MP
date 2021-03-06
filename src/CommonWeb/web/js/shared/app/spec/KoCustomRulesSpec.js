define([
    "squire",
    "moment"
], function (Squire, realMoment) {
    "use strict";

    describe("KoCustomRules defines", function () {
        var wrap = function (value) { return function () { return value; }; };
        var ko = { validation: { rules:  undefined }, utils: {}, bindingContext: { prototype: {} } };
        var momentSpy = jasmine.createSpy("momentSpy").and.callFake(realMoment);
        beforeEach(function (done) {
            if (!ko.validation.rules) {
                ko.validation.rules = { digit: {} };
                jasmine.Ajax.uninstall();
                var injector = new Squire();

                injector.mock("moment", momentSpy);
                injector.mock("knockout", ko);
                injector.mock("knockout.validation", function () {
                });

                injector.require(["shared/app/KoCustomRules"], function () {
                    done();
                });
            } else {
                done();
            }
        });

        describe("the 'minDate' rule which", function () {
            var format = "DD MMM YYYY";

            it("parses the dates according to the provided format", function () {
                // Arrange
                var date = "";
                var thresholdDate = "";
                var options = { date: thresholdDate, format: format };
                // Act
                ko.validation.rules.minDate.validator(date, options);
                // Assert
                expect(momentSpy).toHaveBeenCalledWith(date, format);
                expect(momentSpy).toHaveBeenCalledWith(thresholdDate, format);
            });

            it("checks the threshold date is less than the query date", function () {
                // Arrange
                var minDate = "7 Apr 2008";
                var options = { date: minDate, format: format };
                // Act
                expect(ko.validation.rules.minDate.validator("31 Dec 2008", options)).toBe(true);
                expect(ko.validation.rules.minDate.validator("1 Apr 2007", options)).toBe(false);
            });

            it("allows the dates to be equal", function () {
                var date = "24 Nov 2014";
                var options = { date: date, format: format };
                expect(ko.validation.rules.minDate.validator(date, options)).toBe(true);
            });

            it("can handle wrapped values", function () {
                var minDate = wrap("02 Nov 2013");
                var options = { date: minDate, format: format };
                expect(ko.validation.rules.minDate.validator(wrap("01 Dec 2013"), options)).toBe(true);
                expect(ko.validation.rules.minDate.validator(wrap("31 Oct 2013"), options)).toBe(false);
            });
        });

        describe("the 'maxDate' rule which", function () {
            var format = "DD MMM YYYY";

            it("parses the dates according to the provided format", function () {
                // Arrange
                var date = "";
                var thresholdDate = "";
                // Act
                ko.validation.rules.maxDate.validator(date, { date: thresholdDate, format: format });
                // Assert
                expect(momentSpy).toHaveBeenCalledWith(date, format);
                expect(momentSpy).toHaveBeenCalledWith(thresholdDate, format);
            });

            it("checks the threshold date is greater than the query date", function () {
                // Arrange
                var maxDate = "7 Apr 2008";
                var options = { date: maxDate, format: format };
                // Act
                expect(ko.validation.rules.maxDate.validator("31 Dec 2008", options)).toBe(false);
                expect(ko.validation.rules.maxDate.validator("1 Apr 2007", options)).toBe(true);
            });

            it("allows the dates to be equal", function () {
                var date = "24 Nov 2014";
                var options = { date: date, format: format };
                expect(ko.validation.rules.maxDate.validator(date, options)).toBe(true);
            });

            it("can handle wrapped values", function () {
                var maxDate = wrap("02 Nov 2013");
                var options = { date: maxDate, format: format };
                expect(ko.validation.rules.maxDate.validator(wrap("01 Dec 2013"), options)).toBe(false);
                expect(ko.validation.rules.maxDate.validator(wrap("31 Oct 2013"), options)).toBe(true);
            });
        });

        describe("the 'emailChanged' rule which", function () {
            it("checks the second value does not match the first, in lower case", function () {
                expect(ko.validation.rules.emailChanged.validator("Abc", "bbc")).toBe(true);
                expect(ko.validation.rules.emailChanged.validator("Abc", "Abc")).toBe(false);
                expect(ko.validation.rules.emailChanged.validator("abc", "Abc")).toBe(false);
            });

            it("can validate wrapped values", function () {
                expect(ko.validation.rules.emailChanged.validator(wrap("Abc"), wrap("bbc"))).toBe(true);
                expect(ko.validation.rules.emailChanged.validator(wrap("Abc"), wrap("Abc"))).toBe(false);
                expect(ko.validation.rules.emailChanged.validator(wrap("abc"), wrap("Abc"))).toBe(false);
            });

            it("has a suitable failure message", function () {
                expect(ko.validation.rules.emailChanged.message).toBe(
                    "New email address must be different from your current email address.");
            });
        });

        describe("the 'customMin' rule which", function () {
            it("checks the second value is strictly less than the first", function () {
                expect(ko.validation.rules.customMin.validator(1, 0)).toBe(true);
                expect(ko.validation.rules.customMin.validator(1, 1)).toBe(false);
                expect(ko.validation.rules.customMin.validator(0, 1)).toBe(false);
            });

            it("skips the check if either value is undefined", function () {
                expect(ko.validation.rules.customMin.validator("", 1)).toBe(true);
                expect(ko.validation.rules.customMin.validator(1, "")).toBe(true);
                expect(ko.validation.rules.customMin.validator("", "")).toBe(true);
                expect(ko.validation.rules.customMin.validator(0, 0)).toBe(false);
            });

            it("can validate wrapped values", function () {
                expect(ko.validation.rules.customMin.validator(wrap(2), wrap(1))).toBe(true);
                expect(ko.validation.rules.customMin.validator(wrap(1), wrap(1))).toBe(false);
                expect(ko.validation.rules.customMin.validator(wrap(1), wrap(2))).toBe(false);
            });

            it("has a suitable failure message", function () {
                expect(ko.validation.rules.customMin.message).toBe("Please enter a number greater than {0}.");
            });
        });

        describe("the 'customMax' rule which", function () {
            it("checks the second value is strictly greater than the first", function () {
                expect(ko.validation.rules.customMax.validator(0, 1)).toBe(true);
                expect(ko.validation.rules.customMax.validator(1, 1)).toBe(false);
                expect(ko.validation.rules.customMax.validator(1, 0)).toBe(false);
            });

            it("skips the check if either value is undefined", function () {
                expect(ko.validation.rules.customMax.validator("", 1)).toBe(true);
                expect(ko.validation.rules.customMax.validator(1, "")).toBe(true);
                expect(ko.validation.rules.customMax.validator("", "")).toBe(true);
                expect(ko.validation.rules.customMax.validator(0, 0)).toBe(false);
            });

            it("can validate wrapped values", function () {
                var wrap = function (value) { return function () { return value; }; };

                expect(ko.validation.rules.customMax.validator(wrap(1), wrap(2))).toBe(true);
                expect(ko.validation.rules.customMax.validator(wrap(1), wrap(1))).toBe(false);
                expect(ko.validation.rules.customMax.validator(wrap(2), wrap(1))).toBe(false);
            });

            it("has a suitable failure message", function () {
                expect(ko.validation.rules.customMax.message).toBe("Please enter a number less than {0}.");
            });
        });

        describe("the 'areSame' rule which", function () {
            it("checks the two values are the same", function () {
                expect(ko.validation.rules.areSame.validator(1, 1)).toBe(true);
                expect(ko.validation.rules.areSame.validator(1, 2)).toBe(false);
            });

            it("can validate wrapped values", function () {
                expect(ko.validation.rules.areSame.validator(wrap(1), wrap(wrap(1)))).toBe(true);
                expect(ko.validation.rules.areSame.validator(wrap(1), wrap(wrap(2)))).toBe(false);
            });

            it("has a suitable failure message", function () {
                expect(ko.validation.rules.areSame.message).toBe("Password fields must match");
            });
        });

        describe("the 'startWith' rule which", function () {
            it("checks that the value string starts with the argument string", function () {
                expect(ko.validation.rules.startWith.validator("123", "1")).toBe(true);
                expect(ko.validation.rules.startWith.validator("123", "2")).toBe(false);
            });

            it("can validate wrapped values", function () {
                var wrap = function (value) {
                    return function () {
                        return value;
                    };
                };

                expect(ko.validation.rules.startWith.validator(wrap("123"), wrap(wrap("1")))).toBe(true);
                expect(ko.validation.rules.startWith.validator(wrap("123"), wrap(wrap("2")))).toBe(false);
            });

            it("has a suitable failure message", function () {
                expect(ko.validation.rules.startWith.message).toBe("Must start with '{0}'");
            });
        });

        describe("the 'endWith' rule which", function () {
            it("checks that the value string ends with the argument string", function () {
                expect(ko.validation.rules.endWith.validator("123", "3")).toBe(true);
                expect(ko.validation.rules.endWith.validator("123", "2")).toBe(false);
            });

            it("can validate wrapped values", function () {
                var wrap = function (value) {
                    return function () {
                        return value;
                    };
                };

                expect(ko.validation.rules.endWith.validator(wrap("123"), wrap(wrap("3")))).toBe(true);
                expect(ko.validation.rules.endWith.validator(wrap("123"), wrap(wrap("2")))).toBe(false);
            });

            it("has a suitable failure message", function () {
                expect(ko.validation.rules.endWith.message).toBe("Must end with '{0}'");
            });
        });

        describe("the 'notContain' rule which", function () {
            it("checks that the value string does not contain the argument string", function () {
                expect(ko.validation.rules.notContain.validator("123", "4")).toBe(true);
                expect(ko.validation.rules.notContain.validator("123", "2")).toBe(false);
            });

            it("checks that the value string does not contain any of the array of argument strings", function () {
                expect(ko.validation.rules.notContain.validator("123", ["4", "5", "6"])).toBe(true);
                expect(ko.validation.rules.notContain.validator("123", ["4", "2", "6"])).toBe(false);
            });

            it("can validate wrapped values", function () {
                var wrap = function (value) {
                    return function () {
                        return value;
                    };
                };

                expect(ko.validation.rules.notContain.validator(wrap("123"), wrap(wrap("4")))).toBe(true);
                expect(ko.validation.rules.notContain.validator(wrap("123"), wrap(wrap("2")))).toBe(false);
            });

            it("has a suitable failure message, for a single parameter", function () {
                expect(ko.validation.rules.notContain.message("this")).toBe("Must not contain: 'this'");
            });

            it("has a suitable failure message, for an array of parameters", function () {
                expect(ko.validation.rules.notContain.message(["tom", "dick", "harry"]))
                    .toBe("Must not contain: 'tom', 'dick' or 'harry'");
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

        describe("the 'usernameComplexity' rule which", function () {
            it("rejects passwords below 6 chars", function () {
                expect(ko.validation.rules.passwordComplexity.validator("aBc12")).toBe(false);
            });

            it("rejects usernames over 15 chars", function () {
                var longString = "abcdefghijklmno";
                expect(ko.validation.rules.usernameComplexity.validator(longString + "p")).toBe(false);
            });

            it("rejects usernames under 3 chars", function () {
                expect(ko.validation.rules.usernameComplexity.validator("ab")).toBe(false);
            });

            it("rejects usernames with non-alpha numeric (+ '_' & '-') chars", function () {
                expect(ko.validation.rules.usernameComplexity.validator("username&")).toBe(false);
            });

            it("accepts underscores", function () {
                expect(ko.validation.rules.usernameComplexity.validator("user_name")).toBe(true);
            });

            it("accepts hypens", function () {
                expect(ko.validation.rules.usernameComplexity.validator("user-name")).toBe(true);
            });

            it("accepts suitable usernames", function () {
                expect(ko.validation.rules.usernameComplexity.validator("username")).toBe(true);
            });

            it("has a suitable failure message", function () {
                expect(ko.validation.rules.usernameComplexity.message)
                    .toContain("Username must be between 3 and 15 characters long and consist of only letters, " +
                               "numbers, '_' or '-'");
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

            it("accepts a value if present in the list with different case (caseInsensitive = undefined)", function () {
                var options = { array: diseaseGroups, id: 1, property: "name" };
                expect(ko.validation.rules.isUniqueProperty.validator("nAme 2", options)).toBe(true);
            });

            it("accepts a value if present in the list with different case (caseInsensitive = false)", function () {
                var options = { array: diseaseGroups, id: 1, property: "name", caseInsensitive: false };
                expect(ko.validation.rules.isUniqueProperty.validator("nAme 2", options)).toBe(true);
            });

            it("rejects a value if present in the list with different case (caseInsensitive = true)", function () {
                var options = { array: diseaseGroups, id: 1, property: "name", caseInsensitive: true };
                expect(ko.validation.rules.isUniqueProperty.validator("nAme 2", options)).toBe(false);
            });

            it("accepts a value if the only occurrence in the list is itself", function () {
                var options = { array: diseaseGroups, id: 2, property: "name" };
                expect(ko.validation.rules.isUniqueProperty.validator("Name 2", options)).toBe(true);
            });

            it("accepts a value if the only occurrence in the list is itself, with wrapped ID", function () {
                var id = function () { return 2; };
                var options = { array: diseaseGroups, id: id, property: "name" };
                expect(ko.validation.rules.isUniqueProperty.validator("Name 2", options)).toBe(true);
            });

            it("works with wrapped arrays", function () {
                var array = function () { return [ { name: "1" }, { name: "2" }, { name: "3" } ]; };
                var options = { array: array, id: undefined, property: "name" };
                expect(ko.validation.rules.isUniqueProperty.validator("2", options)).toBe(false);
            });

            it("works with wrapped properties (e.g. on an array of view models)", function () {
                var array = [
                    { name: function () { return "1"; } },
                    { name: function () { return "2"; } },
                    { name: function () { return "3"; } }
                ];

                var options = { array: array, id: undefined, property: "name" };
                expect(ko.validation.rules.isUniqueProperty.validator("2", options)).toBe(false);
            });

            it("accepts a undefined ID", function () {
                var options = { array: diseaseGroups, id: undefined, property: "name" };
                expect(ko.validation.rules.isUniqueProperty.validator("Name 2", options)).toBe(false);
                expect(ko.validation.rules.isUniqueProperty.validator("Name 10", options)).toBe(true);
            });

            it("always accepts empty values", function () {
                var options = { array: diseaseGroups, id: 2, property: "publicName" };
                expect(ko.validation.rules.isUniqueProperty.validator(undefined, options)).toBe(true);
                expect(ko.validation.rules.isUniqueProperty.validator(null, options)).toBe(true);
                expect(ko.validation.rules.isUniqueProperty.validator("", options)).toBe(true);
            });

            it("has a suitable failure message", function () {
                expect(ko.validation.rules.isUniqueProperty.message)
                    .toContain("Value must be unique");
            });
        });
    });
});
