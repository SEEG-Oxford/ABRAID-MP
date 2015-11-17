/* View model for the HealthMap administration page.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "underscore",
    "app/admin/HealthMapDiseaseRowViewModel",
    "shared/app/BaseTableViewModel"
], function (ko, _, HealthMapDiseaseRowViewModel, BaseTableViewModel) {
    "use strict";

    return function (baseUrl, healthMapDiseases, healthMapSubDiseases, abraidDiseases) {
        var self = this;

        // Marry up the linked diseases, so that they point to the same objects, instead of just having the same id.
        // KO "options" bindings requires this.
        var replaceFieldById = function (field, source) {
            return function (target) {
                target[field] = target[field] ? _(source).findWhere({id: target[field].id}) : undefined;
            };
        };
        _(healthMapDiseases).each(replaceFieldById("abraidDisease", abraidDiseases));
        _(healthMapSubDiseases).each(replaceFieldById("abraidDisease", abraidDiseases));
        _(healthMapSubDiseases).each(replaceFieldById("parent", healthMapDiseases));

        // Generate row/form view models for each table
        var diseaseRows = _(healthMapDiseases).map(function (healthMapDisease) {
            return new HealthMapDiseaseRowViewModel(healthMapDisease, "admin/healthmap/updateDisease", baseUrl);
        });
        var subdiseaseRows = _(healthMapSubDiseases).map(function (healthMapSubDisease) {
            return new HealthMapDiseaseRowViewModel(healthMapSubDisease, "admin/healthmap/updateSubDisease", baseUrl);
        });

        // Create a subview model for each table
        self.healthMapDiseasesTable = new BaseTableViewModel(diseaseRows, "name", false, ["name"]);
        self.healthMapSubdiseasesTable = new BaseTableViewModel(subdiseaseRows, "name", false, ["name"]);

        // Expose the full lists for use in drop down lists
        self.abraidDiseases = abraidDiseases;
        self.healthMapDiseases = healthMapDiseases;

        // Add dummy "isSubmitting" field, need for table templates, but not used in this view model
        self.isSubmitting = false;
    };
});
