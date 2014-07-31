<#--
    The system administration page for disease groups.
    Copyright (c) 2014 University of Oxford
-->
<#import "../../layout/common.ftl" as c/>
<#import "../../layout/form.ftl" as f/>

<#assign bootstrapData>
<script type="text/javascript">
    // bootstrapped data for js viewmodels
    var diseaseGroups = ${diseaseGroups};
    var validatorDiseaseGroups = ${validatorDiseaseGroups};
</script>
</#assign>

<#assign css>
<style>
    #disease-group-name-input-group, #model-run-parameters .input-group, #disease-extent-parameters .input-group {
        padding-left: 15px;
        padding-right: 15px;
    }
    #disease-group-settings label, #model-run-parameters label, #disease-extent-parameters label {
        text-align: left;
    }
</style>
</#assign>

<@c.page title="ABRAID-MP Administration: Disease Group" mainjs="/js/kickstart/admin/diseaseGroup" bootstrapData=bootstrapData endOfHead=css>
<div class="container">
    <div id="disease-groups-list">
        <div class="col-sm-8">
            <label for="disease-group-picker" class="side-by-side">Selected Disease Group</label>
            <span class="input-group">
                <span class="input-group-addon">
                    <i class="fa fa-medkit"></i>
                </span>
                <select id="disease-group-picker" class="form-control" data-bind="options: diseaseGroups, value: selectedDiseaseGroup, optionsText: 'name', valueAllowUnset: true" ></select>
            </span>
        </div>
        <div class="col-sm-4">
            <button type="button" class="btn btn-primary" data-bind="click: add">Add new disease group</button>
        </div>
    </div>
    <br />
    <br />
    <div class="panel panel-default">
        <div class="panel-body" id="disease-group-administration-panel">
            <@f.form formId="disease-group-administration">
                <div data-bind="with: diseaseGroupSettingsViewModel">
                    <#include "settingspanel.ftl"/>
                </div>
                <div data-bind="with: modelRunParametersViewModel">
                    <#include "modelrunparameterspanel.ftl"/>
                </div>
                <div data-bind="with: diseaseExtentParametersViewModel">
                    <#include "diseaseextentparameterspanel.ftl"/>
                </div>
            </@f.form>
        </div>
    </div>
    <#include "setuppanel.ftl"/>
</div>
</@c.page>