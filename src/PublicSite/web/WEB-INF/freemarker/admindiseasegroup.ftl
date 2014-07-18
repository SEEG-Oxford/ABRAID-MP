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

<#assign templates>
<script type="text/html" id="modelwrapper-alert-template">
    <p>Please configure ModelWrapper for this disease group if necessary.</p>
    <br /><br />
    <p>
        <span class="btn btn-default" data-bind="click: runModel" data-dismiss="popover">Proceed with Model Run</span>
    </p>
</script>
</#assign>

<@c.page title="ABRAID-MP Administration: Disease Group" mainjs="/js/adminDiseaseGroup" bootstrapData=bootstrapData templates=templates>
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
            <form id="disease-group-administration" data-bind="submit: submit">
                <div data-bind="with: diseaseGroupSettingsViewModel">
                    <#include "diseasegroupsettingspanel.ftl"/>
                </div>
                <div data-bind="with: modelRunParametersViewModel">
                    <#include "modelrunparameterspanel.ftl"/>
                </div>
                <div>
                    <button type="submit" class="btn btn-primary" data-bind="text: isSubmitting() ? 'Saving...' : 'Save', disable: isSubmitting()"></button>
                </div>
                <div class="form-group" data-bind="if: notice">
                    <div data-bind="alert: notice"></div>
                </div>
            </form>
        </div>
    </div>
    <#include "setuppanel.ftl"/>
</div>
</@c.page>