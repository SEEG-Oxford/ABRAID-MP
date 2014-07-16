/* A suite of tests for the DiseaseGroupSettingsPayload AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "underscore",
    "app/DiseaseGroupSettingsPayload",
    "app/DiseaseGroupSettingsViewModel"
], function (ko, _, DiseaseGroupSettingsPayload, DiseaseGroupSettingsViewModel) {
    "use strict";

    describe("The 'disease group settings payload'", function () {
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
            var result = DiseaseGroupSettingsPayload.fromJson(diseaseGroup);
            // Assert
            expect(_.isEqual(result, expectedResult)).toBe(true);
        });

        it("holds the 'fromViewModel' method which returns the expected payload", function () {
            // Arrange
            var eventName = "event";
            var vm = new DiseaseGroupSettingsViewModel("", [parentDiseaseGroup], [validatorDiseaseGroup], eventName);
            ko.postbox.publish(eventName, diseaseGroup);
            // Act
            var result = DiseaseGroupSettingsPayload.fromViewModel(vm);
            // Assert
            expect(_.isEqual(result, expectedResult)).toBe(true);
        });
    });
});
