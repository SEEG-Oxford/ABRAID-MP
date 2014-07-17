/* Payload for data fired by selectedDiseaseGroupEventName
 * Copyright (c) 2014 University of Oxford
 */
define([], function () {
    "use strict";

    var getId = function (diseaseGroup) {
        return (diseaseGroup) ? diseaseGroup.id.toString() : null;
    };

    return function (viewModel) {
        return {
            name: viewModel.name(),
            publicName: viewModel.publicName(),
            shortName: viewModel.shortName(),
            abbreviation: viewModel.abbreviation(),
            groupType: viewModel.selectedType(),
            isGlobal: viewModel.isGlobal(),
            parentDiseaseGroupId: getId(viewModel.selectedParentDiseaseGroup()),
            validatorDiseaseGroupId: getId(viewModel.selectedValidatorDiseaseGroup())
        };
    };
});
