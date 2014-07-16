/* A suite of tests for the MainSettingsPayload AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "underscore",
    "app/MainSettingsPayload",
    "app/MainSettingsViewModel"
], function (ko, _, MainSettingsPayload, MainSettingsViewModel) {
    "use strict";

    describe("The 'main settings payload'", function () {
        var parentDiseaseGroup = {id: 1, groupType: "MICROCLUSTER"};
        var validatorDiseaseGroup = {id: 2};
        var diseaseGroup = {
            name: "Name",
            publicName: "Public name",
            shortName: "Short name",
            abbreviation: "ABBR",
            groupType: "SINGLE",
            isGlobal: true,
            parentDiseaseGroup: parentDiseaseGroup,
            validatorDiseaseGroup: validatorDiseaseGroup
        };
        var expectedResult = {
            name: "Name",
            publicName: "Public name",
            shortName: "Short name",
            abbreviation: "ABBR",
            groupType: "SINGLE",
            isGlobal: true,
            parentDiseaseGroupId: "1",
            validatorDiseaseGroupId: "2"
        };

        it("holds the 'fromJson' method which returns the expected payload", function () {
            // Act
            var result = MainSettingsPayload.fromJson(diseaseGroup);
            // Assert
            expect(_.isEqual(result, expectedResult)).toBe(true);
        });

        it("holds the 'fromViewModel' method which returns the expected payload", function () {
            // Arrange
            var eventName = "event";
            var vm = new MainSettingsViewModel("", [parentDiseaseGroup], [validatorDiseaseGroup], eventName);
            ko.postbox.publish(eventName, diseaseGroup);
            // Act
            var result = MainSettingsPayload.fromViewModel(vm);
            // Assert
            expect(_.isEqual(result, expectedResult)).toBe(true);
        });
    });
});
