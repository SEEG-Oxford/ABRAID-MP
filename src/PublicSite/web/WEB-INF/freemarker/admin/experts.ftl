<#--
    The system administration page for experts.
    Copyright (c) 2014 University of Oxford
-->
<#import "../shared/layout/common.ftl" as c/>
<#import "../shared/layout/table.ftl" as t/>

<#assign bootstrapData>
<script type="text/javascript">
    // bootstrapped data for js viewmodels
    var experts = ${experts};
</script>
</#assign>

<#assign css>
<style>
    .popover {
        max-width: 500px;
    }
    .popover .popover-title {
        text-align: center;
    }
    .popover .popover-title button {
        display: none;
    }
    .table-responsive .input-group {
        max-width: 80px;
    }
    .table-responsive .input-group input {
        padding: 5px;
        line-height: 23px;
        height: 23px;
    }
    .table-responsive .input-group .input-group-addon {
        padding: 3px;
    }
</style>
</#assign>

<#assign templates>
<@t.tableTemplates numberOfColumns=8 plural="experts">
    <tr data-bind="popover: { placement: 'bottom', trigger: 'hover', title: name, template: 'details-template' }">
        <td><span data-bind="text: name"></td>
        <td><input type="checkbox" data-bind="formChecked: seegmember"></td>
        <td><input type="checkbox" data-bind="formChecked: administrator"></td>
        <td><input type="checkbox" data-bind="checked: visibilityRequested, bootstrapDisable: true"></td>
        <td data-bind="if: visibilityRequested"><input type="checkbox" data-bind="checked: visibilityApproved"></td>
        <td><span class="input-group"><input type="text" class="form-control" data-bind="formValue: weighting"></span></td>
        <td data-bind="date: { date: createdDate, format: 'LLL' }"></td>
        <td data-bind="date: { date: updatedDate, format: 'LLL' }"></td>
    </tr>
</@t.tableTemplates>
<script type="text/html" id="details-template">
    <p><strong>Email:</strong>&nbsp;<span data-bind="text: email"></span></p><br>
    <p><strong>Job&nbsp;title:</strong>&nbsp;<span data-bind="text: jobTitle"></span></p><br>
    <p><strong>Institution:</strong>&nbsp;<span data-bind="text: institution"></span></p><br>
</script>
</#assign>

<@c.page title="ABRAID-MP Administration: Experts" mainjs="/js/kickstart/admin/experts" bootstrapData=bootstrapData templates=templates endOfHead=css>
<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a href="#experts-body">
                    Experts
                </a>
            </h2>
        </div>
        <div class="panel-body" id="experts-body">
            <form id="experts-form" action="" data-bind="formSubmit: submit">
                <@t.tableBody singular="expert" title="Experts">
                [
                    { name: 'name', display: 'Name' },
                    { name: 'seegmember', display: 'SEEG' },
                    { name: 'administrator', display: 'Administrator' },
                    { name: 'visibilityRequested', display: 'Requested<br>Visibility' },
                    { name: 'visibilityApproved', display: 'Approved<br>Visibility' },
                    { name: 'weighting', display: 'Weighting' },
                    { name: 'createdDate', display: 'Created' },
                    { name: 'updatedDate', display: 'Updated' }
                ]
                </@t.tableBody>
                <p class="form-group">
                    <button type="submit" class="btn btn-primary" data-bind="formButton: { submitting: 'Saving ...', standard: 'Save'}">Loading ...</button>
                </p>
                <div class="form-group" data-bind="foreach: notices">
                    <div data-bind="alert: $data"></div>
                </div>
            </form>
        </div>
    </div>
</div>
</@c.page>