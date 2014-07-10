/* An AMD defining the view-model for the main settings of the selected disease group.
 * Copyright (coffee) 2014 University of Oxford
 */
define([
    "ko",
    "underscore"
], function (ko, _) {
    "use strict";

    var SINGLE = "SINGLE";
    var MICROCLUSTER = "MICROCLUSTER";
    var CLUSTER = "CLUSTER";
    var groupTypes = [
        {value: SINGLE,       label: "Single Disease"},
        {value: MICROCLUSTER, label: "Microcluster"},
        {value: CLUSTER,      label: "Cluster"}
    ];

    function filterOnType(diseaseGroups, groupType) {
        return _.filter(diseaseGroups, function (diseaseGroup) { return diseaseGroup.groupType === groupType; });
    }

    return function (baseUrl, diseaseGroups, validatorDiseaseGroups, diseaseGroupSelectedEventName) {
        var self = this;
        var microclusterDiseaseGroups = filterOnType(diseaseGroups, MICROCLUSTER);
        var clusterDiseaseGroups = filterOnType(diseaseGroups, CLUSTER);

        function findInList(diseaseGroup, diseaseGroupsList) {
            if (diseaseGroup) {
                var id = diseaseGroup.id;
                return _.find(diseaseGroupsList, function (dg) { return dg.id === id; });
            }
        }

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            self.name(diseaseGroup.name);
            self.publicName(diseaseGroup.publicName);
            self.shortName(diseaseGroup.shortName);
            self.abbreviation(diseaseGroup.abbreviation);
            self.selectedType(diseaseGroup.groupType);
            self.isGlobal(diseaseGroup.isGlobal);
            var parentDiseaseGroup = findInList(diseaseGroup.parentDiseaseGroup, self.parentDiseaseGroups());
            var validatorDiseaseGroup = findInList(diseaseGroup.validatorDiseaseGroup, self.validatorDiseaseGroups());
            self.selectedParentDiseaseGroup(parentDiseaseGroup);
            self.selectedValidatorDiseaseGroup(validatorDiseaseGroup);
        });

        self.name = ko.observable();
        self.publicName = ko.observable();
        self.shortName = ko.observable();
        self.abbreviation = ko.observable();
        self.groupTypes = groupTypes;
        self.selectedType = ko.observable();
        self.isGlobal = ko.observable();
        self.parentDiseaseGroups = ko.computed(function () {
            switch (self.selectedType()) {
                case SINGLE:
                    return microclusterDiseaseGroups;
                case MICROCLUSTER:
                    return clusterDiseaseGroups;
                case CLUSTER:
                    return null;
            }
        });
        self.selectedParentDiseaseGroup = ko.observable();
        self.enableParentDiseaseGroups = ko.computed(function () { return self.selectedType() !== CLUSTER; });
        self.validatorDiseaseGroups = ko.observableArray(validatorDiseaseGroups);
        self.selectedValidatorDiseaseGroup = ko.observable();
    };
});
