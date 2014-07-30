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
                    <div class="form-group">
                        <label for="public-name" class="col-sm-4 control-label">Public Name</label>
                        <div class="col-sm-8">
                            <input type="text" class="form-control" id="public-name" data-bind="formValue: publicName" placeholder="Enter a name for display on Data Validator">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="short-name" class="col-sm-4 control-label">Short Name</label>
                        <div class="col-sm-8">
                            <input type="text" class="form-control" id="short-name" data-bind="formValue: shortName" placeholder="Enter a shorter version of the name">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="abbreviation" class="col-sm-4 control-label">Abbreviation</label>
                        <div class="col-sm-8">
                            <input type="text" class="form-control" id="abbreviation" data-bind="formValue: abbreviation" placeholder="Enter an abbreviation">
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="disease-group-type" class="col-sm-5 control-label">Group Type</label>
                        <div class="col-sm-7 btn-group" id="disease-group-type" data-bind="foreach: groupTypes">
                            <label class="btn btn-default" data-bind="css: {active : $parent.selectedType() === value}">
                                <input type="radio" name="disease-group-type" data-bind="formRadio: { selected: $parent.selectedType, value: value }">
                                <span data-bind="text: label"></span>
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="global-or-tropical" class="col-sm-5 control-label">Global or Tropical</label>
                        <div class="col-sm-7 btn-group" id="global-or-tropical"  data-bind="foreach: [ {value: true, label: 'Global'}, {value: false, label: 'Tropical'} ]">
                            <label class="btn btn-default" data-bind="css: {active : $parent.isGlobal() === value}">
                                <input type="radio" name="global-or-tropical" data-bind="formRadio: { selected: $parent.isGlobal, value: value }">
                                <span data-bind="text: label"></span>
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="parent-disease-group" class="col-sm-5 control-label">Parent Disease Group</label>
                        <div class="col-sm-7">
                            <select class="form-control" id="parent-disease-group" data-bind="options: parentDiseaseGroups, value: selectedParentDiseaseGroup, optionsText: 'name', optionsCaption:'Select one...', enable: enableParentDiseaseGroups()"></select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="validator-disease-group" class="col-sm-5 control-label">Validator Disease Group</label>
                        <div class="col-sm-7">
                            <select class="form-control" id="validator-disease-group" data-bind="options: validatorDiseaseGroups, value: selectedValidatorDiseaseGroup, optionsText: 'name', optionsCaption:'Select one...'"></select>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>