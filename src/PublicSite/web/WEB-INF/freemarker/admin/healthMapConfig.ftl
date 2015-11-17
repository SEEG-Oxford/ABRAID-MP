<#--
    The system administration page for HealthMap configuration.
    Copyright (c) 2015 University of Oxford
-->
<#import "../layout/common.ftl" as c/>
<#import "../shared/layout/table.ftl" as t/>

<#assign bootstrapData>
<script type="text/javascript">
    // bootstrapped data for js viewmodels
    var healthMapDiseases = ${healthMapDiseases};
    var healthMapSubDiseases = ${healthMapSubDiseases};
    var abraidDiseases = ${abraidDiseases};
</script>
</#assign>

<#assign css>
<style>
    td button.btn {
        padding: 2px 4px;
        line-height: 14px;
    }
    td button.btn[type=submit] {
        min-width: 0;
    }
    td > span > i {
        color: #aaaaaa;
    }
</style>
</#assign>

<#assign templates>
<@t.tableTemplates numberOfColumns=3 plural="diseases" name="disease-">
    <tr>
        <td  style="padding: 3px"><span data-bind="html: name"></span></td>
        <td  style="padding: 3px" data-bind="template: editing() ? 'edit-abraid-disease-template' : 'view-abraid-disease-template'"></td>
        <td  style="padding: 3px" data-bind="template: editing() ? 'save-button-template' : 'edit-button-template'"></td>
    </tr>
</@t.tableTemplates>
<@t.tableTemplates numberOfColumns=4 plural="subdiseases" name="subdisease-">
    <tr>
        <td  style="padding: 3px"><span data-bind="html: name"></span></td>
        <td  style="padding: 3px" data-bind="template: editing() ? 'edit-parent-disease-template' : 'view-parent-disease-template'"></td>
        <td  style="padding: 3px" data-bind="template: editing() ? 'edit-abraid-disease-template' : 'view-abraid-disease-template'"></td>
        <td  style="padding: 3px" data-bind="template: editing() ? 'save-button-template' : 'edit-button-template'"></td>
    </tr>
</@t.tableTemplates>
<script type="text/html" id="view-abraid-disease-template">
    <span data-bind="html: abraidDisease() ? abraidDisease().name : '<i>--- Not of interest ---</i>' "></span>
</script>
<script type="text/html" id="edit-abraid-disease-template">
    <select data-bind="options: $parents[1].abraidDiseases, optionsCaption: '--- Not of interest ---', optionsText: 'name', value: abraidDiseaseNew, valueAllowUnset: true, bootstrapDisable: isSubmitting()"></select>
</script>
<script type="text/html" id="view-parent-disease-template">
    <span data-bind="html: parentDisease() ? parentDisease().name : '<i>--- Not of interest ---</i>' "></span>
</script>
<script type="text/html" id="edit-parent-disease-template">
    <select data-bind="options: $parents[1].healthMapDiseases, optionsCaption: '--- Not of interest ---', optionsText: 'name', value: parentDiseaseNew, valueAllowUnset: true, bootstrapDisable: isSubmitting()"></select>
</script>
<script type="text/html" id="edit-button-template">
    <button class="btn btn-default" style="float: right" data-bind="click: function(data, event) { editing(true); }"><i class="fa fa-pencil"></i></button>
</script>
<script type="text/html" id="save-button-template">
    <form action="" data-bind="formSubmit: submit" style="margin: 0; float: right; white-space: nowrap" >
        <button type="submit" class="btn btn-default" data-bind="bootstrapDisable: isSubmitting"><i data-bind="css: isSubmitting() ? 'fa fa-spinner fa-spin' : 'fa fa-save'"></i></button>
        <span class="fa fa-exclamation-triangle text-danger" style="padding: 3px 0 1px 2px;" data-bind="visible: notices().length > 0, tooltip: { title: warning, placement: 'left'}"></span>
    </form>
</script>
</#assign>

<@c.page title="ABRAID-MP Administration: HealthMap" mainjs="/js/kickstart/admin/healthMap" bootstrapData=bootstrapData templates=templates endOfHead=css>
<div class="container" id="healthMapAdminPage">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a href="#healthMapDiseases-body">
                    HealthMap Diseases
                </a>
            </h2>
        </div>
        <div class="panel-body" id="healthMapDiseases-body" data-bind="with: healthMapDiseasesTable">
            <@t.tableBody singular="healthMapDisease" title="HealthMap Diseases" name="disease-">
            [
                { name: 'name', display: 'Name' },
                { name: 'abraidDisease.name', display: 'ABRAID Disease' }
            ]
            </@t.tableBody>
        </div>
    </div>

    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a href="#healthMapSubdiseases-body">
                    HealthMap Disease Abbreviations
                </a>
            </h2>
        </div>
        <div class="panel-body" id="healthMapSubdiseases-body" data-bind="with: healthMapSubdiseasesTable">
            <@t.tableBody singular="healthMapSubdisease" title="HealthMap Diseases" name="subdisease-">
                [
                { name: 'name', display: 'Abbreviation' },
                { name: 'parent.name', display: 'Parent Disease' },
                { name: 'abraidDisease.name', display: 'ABRAID Disease' }
                ]
            </@t.tableBody>
        </div>
    </div>
</div>
</@c.page>
