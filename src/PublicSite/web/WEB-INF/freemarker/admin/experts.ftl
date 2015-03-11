<#--
    The system administration page for experts.
    Copyright (c) 2014 University of Oxford
-->
<#import "../layout/common.ftl" as c/>
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
    .popover .popover-content p {
        margin-bottom: 3px;
    }
    .popover .popover-content ul {
        margin-bottom: 0;
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
        <td><span data-bind="text: name.replace(/ /g, '&nbsp;')"></td>
        <td><input type="checkbox" data-bind="formChecked: seegmember"></td>
        <td><input type="checkbox" data-bind="formChecked: administrator"></td>
        <td data-bind="if: visibilityRequested"><input type="checkbox" data-bind="checked: visibilityApproved"></td>
        <td><span class="input-group"><input type="text" class="form-control" data-bind="formValue: weighting"></span></td>
        <td data-bind="date: { date: createdDate, format: 'YYYY-MM-DD&nbsp;HH:mm' }"></td>
        <td data-bind="date: { date: updatedDate, format: 'YYYY-MM-DD&nbsp;HH:mm' }"></td>
        <td data-bind="date: { date: lastReviewDate, format: 'YYYY-MM-DD&nbsp;HH:mm', fallback: 'Never' }"></td>
        <td data-bind="text: totalReviews"></td>
    </tr>
</@t.tableTemplates>
<script type="text/html" id="details-template">
    <p><strong>Email:</strong>&nbsp;<span data-bind="text: email"></span></p>
    <p><strong>Job&nbsp;title:</strong>&nbsp;<span data-bind="text: jobTitle"></span></p>
    <p><strong>Institution:</strong>&nbsp;<span data-bind="text: institution"></span></p>
    <p><strong>Occurrence reviews:</strong>&nbsp;<span data-bind="text: occurrenceReviews"></span></p>
    <p><strong>Extent reviews:</strong>&nbsp;<span data-bind="text: extentReviews"></span></p>
    <strong>Interests:</strong>
    <ul data-bind="foreach: diseaseInterestNames">
        <li><span data-bind="text: $data"></span></li>
    </ul>
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
                    { name: 'visibilitySort', display: 'Visibility' },
                    { name: 'weighting', display: 'Weighting' },
                    { name: 'createdDate', display: 'Created' },
                    { name: 'updatedDate', display: 'Updated' },
                    { name: 'lastReviewDate', display: 'Last review' },
                    { name: 'totalReviews', display: 'Reviews' },
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