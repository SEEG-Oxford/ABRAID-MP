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

    function filterOnType(diseaseGroups, groupType) {
        return _.filter(diseaseGroups, function (diseaseGroup) { return diseaseGroup.groupType === groupType; });
    }

    function findInList(diseaseGroup, diseaseGroupsList) {
        if (diseaseGroup) {
            var id = diseaseGroup.id;
            return _.find(diseaseGroupsList, function (dg) { return dg.id === id; });
        }
    }

    function diseaseGroupsDiffer(vdg1, vdg2) {
        if (vdg1 && vdg2) {
            return (vdg1.id !== vdg2.id);
        } else {
            return (vdg1 || vdg2);
        }
    }

    function anyFieldsDiffer(dg1, dg2) {
        var result = false;
        if (dg1 && dg2) {
            result = result ||
                (dg1.name !== dg2.name) ||
                (dg1.publicName !== dg2.publicName) ||
                (dg1.shortName !== dg2.shortName) ||
                (dg1.abbreviation !== dg2.abbreviation) ||
                (dg1.groupType !== dg2.groupType) ||
                (dg1.isGlobal !== dg2.isGlobal) ||
                diseaseGroupsDiffer(dg1.parentDiseaseGroup, dg2.parentDiseaseGroup) ||
                diseaseGroupsDiffer(dg1.validatorDiseaseGroup, dg2.validatorDiseaseGroup);
        }
        return result;
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

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            originalDiseaseGroup = diseaseGroup;
            self.name(diseaseGroup.name);
            self.publicName(diseaseGroup.publicName);
            self.shortName(diseaseGroup.shortName);
            self.abbreviation(diseaseGroup.abbreviation);
            self.selectedType(diseaseGroup.groupType);
            self.isGlobal(diseaseGroup.isGlobal);
            var parentDiseaseGroup = findInList(diseaseGroup.parentDiseaseGroup, self.parentDiseaseGroups());
            var validatorDiseaseGroup = findInList(diseaseGroup.validatorDiseaseGroup, self.validatorDiseaseGroups);
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
        self.parentDiseaseGroups = ko.computed(function () { return getParentDiseaseGroups(self.selectedType()); });
        self.selectedParentDiseaseGroup = ko.observable();
        self.enableParentDiseaseGroups = ko.computed(function () { return self.selectedType() !== CLUSTER; });
        self.validatorDiseaseGroups = validatorDiseaseGroups;
        self.selectedValidatorDiseaseGroup = ko.observable();

        var originalDiseaseGroup;
        var data = ko.computed(function () {
            return {
                name: self.name(),
                publicName: self.publicName(),
                shortName: self.shortName(),
                abbreviation: self.abbreviation(),
                groupType: self.selectedType(),
                isGlobal: self.isGlobal(),
                parentDiseaseGroup: self.selectedParentDiseaseGroup(),
                validatorDiseaseGroup: self.selectedValidatorDiseaseGroup()
            };
        });

        self.enableButton = ko.computed(function () { return anyFieldsDiffer(originalDiseaseGroup, data()); });
        self.saveChanges = function () {
            var url = baseUrl + "admindiseasegroup/" + originalDiseaseGroup.id + "/save";
            $.post(url, data())
                .done(function () {
                    alert("Done");
                    self.notices.push({message: "Changes successfully saved."});
                })
                .fail(function (xhr) {
                    alert("Error: " + xhr.responseText);
                    self.notices.push({message: "Error"});
                });
        };
    };
});
