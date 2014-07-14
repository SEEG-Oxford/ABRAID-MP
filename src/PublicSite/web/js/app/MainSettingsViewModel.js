/* An AMD defining the view-model for the main settings of the selected disease group.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "jquery",
    "ko",
    "underscore"
], function ($, ko, _) {
    "use strict";

    var SINGLE = "SINGLE";
    var MICROCLUSTER = "MICROCLUSTER";
    var CLUSTER = "CLUSTER";
    var groupTypes = [
        {value: SINGLE,       label: "Single Disease"},
        {value: MICROCLUSTER, label: "Microcluster"},
        {value: CLUSTER,      label: "Cluster"}
    ];

    function DiseaseGroup(name, publicName, shortName, abbreviation, groupType, isGlobal, parentId, validatorId) {
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
    }

    function getId(diseaseGroup) {
        return (diseaseGroup) ? diseaseGroup.id.toString() : null;
    }

    function createDiseaseGroup(d) {
        return new DiseaseGroup(d.name, d.publicName, d.shortName, d.abbreviation, d.groupType, d.isGlobal,
            getId(d.parentDiseaseGroup), getId(d.validatorDiseaseGroup));
    }

    function findInList(diseaseGroup, diseaseGroupsList) {
        if (diseaseGroup) {
            return _(diseaseGroupsList).find(function (dg) { return dg.id === diseaseGroup.id; });
        }
    }

    function filterOnType(diseaseGroups, groupType) {
        return _(diseaseGroups).filter(function (diseaseGroup) { return diseaseGroup.groupType === groupType; });
    }

    return function (baseUrl, diseaseGroups, validatorDiseaseGroups, diseaseGroupSelectedEventName) {
        var self = this;
        var microclusterDiseaseGroups = filterOnType(diseaseGroups, MICROCLUSTER);
        var clusterDiseaseGroups = filterOnType(diseaseGroups, CLUSTER);

        function getParentDiseaseGroups(type) {
            switch (type) {
                case SINGLE:
                    return microclusterDiseaseGroups;
                case MICROCLUSTER:
                    return clusterDiseaseGroups;
                case CLUSTER:
                    return null;
            }
        }

        self.name = ko.observable().extend({ required: true });
        self.publicName = ko.observable();
        self.shortName = ko.observable();
        self.abbreviation = ko.observable();
        self.groupTypes = groupTypes;
        self.selectedType = ko.observable();
        self.isGlobal = ko.observable();
        self.parentDiseaseGroups = ko.computed(function () { return getParentDiseaseGroups(self.selectedType()); });
        self.selectedParentDiseaseGroup = ko.observable();
        self.enableParentDiseaseGroups = ko.computed(function () { return self.selectedType() !== CLUSTER; });
        self.validatorDiseaseGroups = validatorDiseaseGroups;
        self.selectedValidatorDiseaseGroup = ko.observable();

        var diseaseGroupId;
        var originalDiseaseGroup;
        var data = ko.computed(function () {
            return new DiseaseGroup(
                self.name(), self.publicName(), self.shortName(), self.abbreviation(), self.selectedType(),
                self.isGlobal(), getId(self.selectedParentDiseaseGroup()), getId(self.selectedValidatorDiseaseGroup()));
        });

        self.enableSaveButton = ko.computed(function () { return !(_.isEqual(originalDiseaseGroup, data())); });
        self.notice = ko.observable();
        self.saveChanges = function () {
            var url = baseUrl + "admindiseasegroup/" + diseaseGroupId + "/savemainsettings";
            $.post(url, data())
                .done(function () { self.notice({ message: "Saved successfully", priority: "success" }); })
                .fail(function () { self.notice({ message: "Error saving", priority: "warning"}); });
        };

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            diseaseGroupId = diseaseGroup.id;
            originalDiseaseGroup = createDiseaseGroup(diseaseGroup);

            self.name(diseaseGroup.name);
            self.publicName(diseaseGroup.publicName);
            self.shortName(diseaseGroup.shortName);
            self.abbreviation(diseaseGroup.abbreviation);
            self.selectedType(diseaseGroup.groupType);
            self.isGlobal(diseaseGroup.isGlobal);
            var parentDiseaseGroup = findInList(diseaseGroup.parentDiseaseGroup, self.parentDiseaseGroups());
            self.selectedParentDiseaseGroup(parentDiseaseGroup);
            var validatorDiseaseGroup = findInList(diseaseGroup.validatorDiseaseGroup, self.validatorDiseaseGroups);
            self.selectedValidatorDiseaseGroup(validatorDiseaseGroup);
        });
    };
});
