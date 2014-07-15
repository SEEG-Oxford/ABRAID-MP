<div class="panel panel-default">
    <div class="panel-heading">
        <h2 class="panel-title">
            <a data-toggle="collapse" href="#main-settings">
                Main Settings
            </a>
        </h2>
    </div>
    <div class="panel-collapse collapse in" id="main-settings">
        <div class="panel-body">
            <div class="col-sm-6">
                <form class="form-horizontal" role="form">
                    <div class="form-group">
                        <label for="disease-group-name" class="col-sm-3 control-label">Name</label>
                        <div id="disease-group-name-input-group" class="input-group col-sm-8">
                            <input class="form-control" id="disease-group-name" data-bind="value: name, valueUpdate: 'input'">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="public-name" class="col-sm-3 control-label">Public Name</label>
                        <div class="col-sm-8">
                            <input class="form-control" id="public-name" data-bind="value: publicName, valueUpdate: 'input'">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="short-name" class="col-sm-3 control-label">Short Name</label>
                        <div class="col-sm-8">
                            <input class="form-control" id="short-name" data-bind="value: shortName, valueUpdate: 'input'">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="abbreviation" class="col-sm-3 control-label">Abbreviation</label>
                        <div class="col-sm-8">
                            <input class="form-control" id="abbreviation" data-bind="value: abbreviation, valueUpdate: 'input'">
                        </div>
                    </div>
                </form>
            </div>
            <div class="col-sm-6">
                <form class="form-horizontal" role="form">
                    <div class="form-group form-group-radio-inline">
                        <label for="disease-group-type" class="col-sm-5 control-label">Group Type</label>
                        <div class="col-sm-7" id="disease-group-type" data-bind="foreach: groupTypes">
                            <label class="radio-inline">
                                <input type="radio" data-bind="value: value, checked: $parent.selectedType">
                                <span data-bind="text: label"></span>
                            </label>
                        </div>
                    </div>
                    <div class="form-group form-group-radio-inline">
                        <label for="global-or-tropical" class="col-sm-5 control-label">Global or Tropical</label>
                        <div class="col-sm-7" id="global-or-tropical">
                            <label class="radio-inline">
                                <input type="radio" name="globalOrTropical" value="true" data-bind="checked: isGlobal">Global
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="globalOrTropical" value="false" data-bind="checked: isGlobal">Tropical
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
                </form>
            </div>
            <div class="col-sm-12">
                <button type="button" class="btn btn-primary" data-bind="enable: enableSaveButton, click: save">Save</button>
            </div>
            <div class="form-group col-sm-12" data-bind="if: notice">
                <div data-bind="alert: notice"></div>
            </div>
        </div>
    </div>
</div>