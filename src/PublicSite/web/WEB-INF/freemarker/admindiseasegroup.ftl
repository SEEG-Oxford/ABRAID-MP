<#--
    The system administration page for disease groups.
    Copyright (c) 2014 University of Oxford
-->
<#import "common.ftl" as c/>

<#assign bootstrapData>
<script type="text/javascript">
    // bootstrapped data for js viewmodels
    var diseaseGroups = ${diseaseGroups};
    var validatorDiseaseGroups = ${validatorDiseaseGroups};
</script>
</#assign>

<@c.page title="ABRAID-MP Administration: Disease Group" mainjs="/js/adminDiseaseGroup" bootstrapData=bootstrapData>
<div class="container">
    <div id="disease-groups-list">
        <label for="disease-group-picker" class="side-by-side">Selected Disease Group:</label>
        <span class="input-group">
            <span class="input-group-addon">
                <i class="fa fa-medkit"></i>
            </span>
            <select id="disease-group-picker" class="form-control" data-bind="options: diseaseGroups, value: selectedDiseaseGroup, optionsText: 'name'" ></select>
        </span>
    </div>
    <br />
    <br />
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
                                <input class="form-control" id="disease-group-name" data-bind="value: name">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="public-name" class="col-sm-3 control-label">Public Name</label>
                            <div class="col-sm-8">
                                <input class="form-control" id="public-name" data-bind="value: publicName">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="short-name" class="col-sm-3 control-label">Short Name</label>
                            <div class="col-sm-8">
                                <input class="form-control" id="short-name" data-bind="value: shortName">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="abbreviation" class="col-sm-3 control-label">Abbreviation</label>
                            <div class="col-sm-8">
                                <input class="form-control" id="abbreviation" data-bind="value: abbreviation">
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
                    <button type="button" class="btn btn-primary" data-bind="enable: enableSaveButton, click: saveChanges">Save</button>
                </div>
                <div class="form-group col-sm-12" data-bind="if: notice">
                    <div data-bind="alert: notice"></div>
                </div>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a data-toggle="collapse" href="#model-run-parameters">
                    Model Run Parameters
                </a>
            </h2>
        </div>
        <div class="panel-collapse collapse in" id="model-run-parameters">
            <div class="panel-body">
                <form class="form-horizontal" role="form">
                    <div class="form-group">
                        <label for="min-new-occurrences" class="col-sm-4 control-label">Minimum Number of New Occurrences</label>
                        <div class="col-sm-1">
                            <input class="form-control" id="min-new-occurrences" data-bind="value: minNewOccurrences">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="min-data-volume" class="col-sm-4 control-label">Minimum Data Volume</label>
                        <div class="col-sm-1">
                            <input class="form-control" id="min-data-volume" data-bind="value: minDataVolume">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="min-distinct-countries" class="col-sm-4 control-label">Minimum Number of Distinct Countries</label>
                        <div class="col-sm-1">
                            <input class="form-control" id="min-distinct-countries" data-bind="value: minDistinctCountries">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="min-high-frequency-countries" class="col-sm-4 control-label">Minimum Number of High Frequency Countries</label>
                        <div class="col-sm-1">
                            <input class="form-control" id="min-high-frequency-countries" data-bind="value: minHighFrequencyCountries">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="high-frequency-threshold" class="col-sm-4 control-label">Minumum Number of Occurrences to be deemed a High Frequency Country</label>
                        <div class="col-sm-1">
                            <input class="form-control" id="high-frequency-threshold" data-bind="value: highFrequencyThreshold">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="occurs-in-africa" class="col-sm-4 control-label">Occurs in Africa</label>
                        <div class="col-sm-1">
                            <input type="checkbox" id="occurs-in-africa" data-bind="checked: occursInAfrica">
                        </div>
                    </div>
                    <div class="col-sm-12">
                        <button type="button" class="btn btn-primary" data-bind="click: saveChanges">Save</button>
                    </div>
                    <div class="form-group col-sm-12" data-bind="if: notice">
                        <div data-bind="alert: notice"></div>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a data-toggle="collapse" href="#setup-body">
                    Disease Group Setup
                </a>
            </h2>
        </div>
        <div class="panel-collapse collapse in" id="setup-body">
            <div class="panel-body">
            </div>
        </div>
    </div>
</div>
</@c.page>