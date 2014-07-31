<#--
    User registration page
    Copyright (c) 2014 University of Oxford
-->
<#import "../layout/common.ftl" as c/>
<#assign bootstrapData>
<script type="text/javascript">
    // bootstrapped data for js viewmodels
    var diseases = ${diseases};
    var initialExpert = ${jsonExpert};
</script>
</#assign>
<#assign templates>
    <script type="text/html" id="no-diseases-template">
        <tr class="warning">
            <td colspan="4" class="text-muted">No matching diseases.</td>
        </tr>
    </script>

    <script type="text/html" id="diseases-list-template">
        <!-- ko foreach: diseaseInterestListViewModel.visibleDiseases -->
        <tr>
            <td><span data-bind="text: name"></td>
            <td><input type="checkbox" data-bind="formChecked: interested"></td>
        </tr>
        <!-- /ko -->
    </script>
    <script type="text/html" id="list-header-template">
        <th data-bind="click: function () { $parent.diseaseInterestListViewModel.updateSort(field.name) }">
            <span data-bind="text: display"></span>
            <span data-bind="text: ($parent.diseaseInterestListViewModel.sortField() === field.name) ? ($parent.diseaseInterestListViewModel.reverseSort() ? '&#9650;' : '&#9660;') : '&nbsp;'" class="up-down"></span>
        </th>
    </script>
</#assign>
<@c.page title="ABRAID MP - Register" mainjs="/js/kickstart/register/details" templates=templates bootstrapData=bootstrapData>
<div class="container">
    <form action="#" data-bind="formSubmit: submit">
        <div class="panel panel-default" >
            <div class="panel-heading">
                <h2 class="panel-title">
                    <a href="#user-body">
                        User details
                    </a>
                </h2>
            </div>
            <div class="panel-body" id="user-body">
                <p class="form-group">
                    <label for="user-name">Name: </label>
                    <span class="input-group">
                        <span class="input-group-addon">
                            <i class="glyphicon glyphicon-user"></i>
                        </span>
                        <input id="user-name" type="text" class="form-control" placeholder="Name" data-bind="formValue: name" >
                    </span>
                </p>
                <p class="form-group">
                    <label for="user-job">Job: </label>
                    <span class="input-group">
                        <span class="input-group-addon">
                            <i class="glyphicon glyphicon-briefcase"></i>
                        </span>
                        <input id="user-job" type="text" class="form-control" placeholder="Job" data-bind="formValue: jobTitle" >
                    </span>
                </p>
                <p class="form-group">
                    <label for="user-institution">Institution: </label>
                    <span class="input-group">
                        <span class="input-group-addon">
                            <i class="glyphicon glyphicon-home"></i>
                        </span>
                        <input id="user-institution" type="text" class="form-control" placeholder="Institution" data-bind="formValue: institution" >
                    </span>
                </p>
                <p class="form-group">
                    <label for="user-show">Show on site?</label>
                    <span id="user-show" class="input-group btn-group" data-bind="foreach: [ { value: true, label: 'Show me' }, { value: false, label: 'Don\'t show me' } ]">
                        <label class="btn btn-default" data-bind="css: { active: $parent.visibilityRequested() === value, disabled: $parent.isSubmitting() }">
                            <input type="radio" name="user-show" data-bind="formRadio: { selected: $parent.visibilityRequested, value: value }">
                            <span data-bind="text: label"></span>
                        </label>
                    </span>
                </p>
            </div>
        </div>
        <div class="panel panel-default">
            <div class="panel-heading">
                <h2 class="panel-title">
                    <a href="#interests-body">
                        Disease interests
                    </a>
                </h2>
            </div>
            <div class="panel-body" id="interests-body">
                <p class="form-group">
                    <label for="file-filter">Filter: </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="glyphicon glyphicon-filter"></i>
                            </span>
                            <span id="filter-clear" class="clear glyphicon glyphicon-remove-circle" data-bind='click: function() { diseaseInterestListViewModel.filter(""); }'></span>
                            <input id="disease-filter" type="text" class="form-control" placeholder="Filter" data-bind="formValue: diseaseInterestListViewModel.filter">
                        </span>
                </p>
                <div>
                    <label for="file-list">Diseases: </label>
                    <div class="table-responsive">
                        <table id="file-list" class="table table-condensed table-hover">
                            <thead>
                            <tr data-bind="template: { name: 'list-header-template', foreach: [
                                    { name: 'name', display: 'Disease' },
                                    { name: 'interested', display: 'Interested?' }
                                ], as: 'field' }"></tr>
                            </thead>
                            <tbody data-bind="template: { name: diseaseInterestListViewModel.visibleDiseases().length == 0 ? 'no-diseases-template' : 'diseases-list-template' }"></tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <p class="form-group">
            <button type="submit" class="btn btn-primary" data-bind="formButton: { submitting: 'Creating ...', standard: 'Create a new ABRAID-MP account'}"></button>
        </p>
        <div class="form-group" data-bind="foreach: notices">
            <div data-bind="alert: $data"></div>
        </div>
    </form>
</div>
</@c.page>
