define([], function () {
    "use strict";

    var MainSettingsPayload =
        function (name, publicName, shortName, abbreviation, groupType, isGlobal, parentId, validatorId) {
            return {
                name: name,
                publicName: publicName,
                shortName: shortName,
                abbreviation: abbreviation,
                groupType: groupType,
                isGlobal: isGlobal,
                parentDiseaseGroupId: parentId,
                validatorDiseaseGroupId: validatorId
            };
        };

    var getId = function (diseaseGroup) {
        return (diseaseGroup) ? diseaseGroup.id.toString() : null;
    };

    return {
        fromJson: function (disease) {
            return new MainSettingsPayload(
                disease.name,
                disease.publicName,
                disease.shortName,
                disease.abbreviation,
                disease.groupType,
                disease.isGlobal,
                getId(disease.parentDiseaseGroup),
                getId(disease.validatorDiseaseGroup)
            );
        },
        fromViewModel: function (disease) {
            return new MainSettingsPayload(
                disease.name(),
                disease.publicName(),
                disease.shortName(),
                disease.abbreviation(),
                disease.selectedType(),
                disease.isGlobal(),
                getId(disease.selectedParentDiseaseGroup()),
                getId(disease.selectedValidatorDiseaseGroup())
            );
        }
    };
});
