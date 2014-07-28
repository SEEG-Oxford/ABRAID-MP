<#--
    The system administration page for experts.
    Copyright (c) 2014 University of Oxford
-->
<#import "../layout/common.ftl" as c/>

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
<script type="text/html" id="no-entries-template">
    <tr class="warning">
        <td colspan="4" class="text-muted">No matching experts.</td>
    </tr>
</script>
<script type="text/html" id="list-template">
    <!-- ko foreach: visibleEntries -->
    <tr data-bind="popover: { placement: 'bottom', trigger: 'hover', title: name, template: 'details-template' }">
        <td><span data-bind="text: name"></td>
        <td><input type="checkbox" data-bind="formChecked: { checked: isSEEG, value: true }"></td>
        <td><input type="checkbox" data-bind="formChecked: { checked: isAdministrator, value: true }"></td>
        <td><input type="checkbox" data-bind="checked: publiclyVisible, bootstrapDisable: true"></td>
        <td data-bind="if: publiclyVisible"><input type="checkbox" data-bind="formChecked: { checked: approvedVisible, value: true }"></td>
        <td><span class="input-group"><input type="text" class="form-control" data-bind="formValue: weighting"></span></td>
        <td data-bind="date: { date: createdDate, format: 'LLL' }"></td>
        <td data-bind="date: { date: updatedDate, format: 'LLL' }"></td>
    </tr>
    <!-- /ko -->
</script>
<script type="text/html" id="list-header-template">
    <th data-bind="click: function () { $parent.updateSort(field.name) }">
        <span data-bind="html: display"></span>
        <span data-bind="text: ($parent.sortField() === field.name) ? ($parent.reverseSort() ? '&#9650;' : '&#9660;') : '&nbsp;'" class="up-down"></span>
    </th>
</script>
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
                <p class="form-group">
                    <label for="expert-filter">Filter: </label>
                    <span class="input-group">
                        <span class="input-group-addon">
                            <i class="glyphicon glyphicon-filter"></i>
                        </span>
                        <span id="filter-clear" class="clear glyphicon glyphicon-remove-circle" data-bind='click: function() { filter(""); }'></span>
                        <input id="expert-filter" type="text" class="form-control" placeholder="Filter" data-bind="formValue: filter">
                    </span>
                </p>
                <div>
                    <label for="expert-list">Experts: </label>
                    <div class="table-responsive">
                        <table id="expert-list" class="table table-condensed table-hover">
                            <thead>
                            <tr data-bind="template: { name: 'list-header-template', foreach: [
                                        { name: 'name', display: 'Name' },
                                        { name: 'isSEEG', display: 'SEEG' },
                                        { name: 'isAdministrator', display: 'Administrator' },
                                        { name: 'publiclyVisible', display: 'Requested<br>Visibility' },
                                        { name: 'approvedVisible', display: 'Approved<br>Visibility' },
                                        { name: 'weighting', display: 'Weighting' },
                                        { name: 'createdDate', display: 'Created' },
                                        { name: 'updatedDate', display: 'Updated' }
                                    ], as: 'field' }"></tr>
                            </thead>
                            <tbody data-bind="template: { name: visibleEntries().length == 0 ? 'no-entries-template' : 'list-template' }"></tbody>
                        </table>
                    </div>
                </div>
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