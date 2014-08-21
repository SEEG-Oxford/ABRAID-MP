<#macro formGroup id title bind placeholder>
<div class="form-group">
    <label for="${id}" class="col-sm-4 control-label">${title}</label>
    <div class="col-sm-8">
        <input type="text" class="form-control" id="${id}" data-bind="formValue: ${bind}" placeholder="${placeholder}">
    </div>
</div>
</#macro>

<div class="panel panel-default">
    <div class="panel-heading">
        <h2 class="panel-title">
            <a data-toggle="collapse" href="#disease-group-settings">
                Main Settings
            </a>
        </h2>
    </div>
    <div class="panel-collapse collapse in" id="disease-group-settings">
        <div class="panel-body">
            <div class="col-sm-6">
                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="disease-group-name" class="col-sm-4 control-label">Name</label>
                        <div id="disease-group-name-input-group" class="input-group col-sm-8">
                            <input type="text" class="form-control" id="disease-group-name" data-bind="formValue: name" placeholder="Enter disease group name">
                        </div>
                    </div>
                    <@formGroup id="public-name" title="Public Name" bind="publicName" placeholder="Enter a name for display on Data Validator"></@formGroup>
                    <@formGroup id="short-name" title="Short Name" bind="shortName" placeholder="Enter a shorter version of the name"></@formGroup>
                    <@formGroup id="abbreviation" title="Abbreviation" bind="abbreviation" placeholder="Enter an abbreviation"></@formGroup>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="disease-group-type" class="col-sm-5 control-label">Type</label>
                        <div class="col-sm-7 btn-group" id="disease-group-type" data-bind="foreach: groupTypes">
                            <label class="btn btn-default" data-bind="css: {active : $parent.selectedType() === value, disabled: find('isSubmitting')}">
                                <input type="radio" name="disease-group-type" data-bind="formRadio: { selected: $parent.selectedType, value: value }">
                                <span data-bind="text: label"></span>
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="global-or-tropical" class="col-sm-5 control-label">Global or Tropical</label>
                        <div class="col-sm-7 btn-group" id="global-or-tropical"  data-bind="foreach: [ {value: true, label: 'Global'}, {value: false, label: 'Tropical'} ]">
                            <label class="btn btn-default" data-bind="css: {active : $parent.isGlobal() === value, disabled: find('isSubmitting')}">
                                <input type="radio" name="global-or-tropical" data-bind="formRadio: { selected: $parent.isGlobal, value: value }">
                                <span data-bind="text: label"></span>
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="parent-disease-group" class="col-sm-5 control-label">Parent Disease</label>
                        <div class="col-sm-7">
                            <select class="form-control" id="parent-disease-group" data-bind="options: parentDiseaseGroups, value: selectedParentDiseaseGroup, optionsText: 'name', optionsCaption:'Select one...', bootstrapDisable: find('isSubmitting') || !enableParentDiseaseGroups()"></select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="validator-disease-group" class="col-sm-5 control-label">Validator Disease</label>
                        <div class="col-sm-7">
                            <select class="form-control" id="validator-disease-group" data-bind="options: validatorDiseaseGroups, value: selectedValidatorDiseaseGroup, optionsText: 'name', optionsCaption:'Select one...', bootstrapDisable: find('isSubmitting')"></select>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>