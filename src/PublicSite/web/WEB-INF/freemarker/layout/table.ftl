<#--
    Macro for including consistant bootstrap tables. For use with BaseFormViewModel.
    Copyright (c) 2014 University of Oxford
-->
<#macro tableTemplates numberOfColumns plural="entries" subpath="">
    <script type="text/html" id="no-entries-template">
        <tr class="warning">
            <td colspan="${numberOfColumns}" class="text-muted">No matching ${plural}.</td>
        </tr>
    </script>
    <script type="text/html" id="list-template">
        <!-- ko foreach: ${subpath}visibleEntries -->
            <#nested/>
        <!-- /ko -->
    </script>
    <script type="text/html" id="list-header-template">
        <th data-bind="click: function () { $parent.${subpath}updateSort(field.name) }">
            <span data-bind="html: display"></span>
            <span data-bind="text: ($parent.${subpath}sortField() === field.name) ? ($parent.${subpath}reverseSort() ? '&#9650;' : '&#9660;') : '&nbsp;'" class="up-down"></span>
        </th>
    </script>
</#macro>
<#macro tableBody singular="entry" title="Entries" subpath="">
<p class="form-group">
    <label for="${singular}-filter">Filter: </label>
    <span class="input-group">
    <span class="input-group-addon">
        <i class="glyphicon glyphicon-filter"></i>
    </span>
    <span id="filter-clear" class="clear glyphicon glyphicon-remove-circle" data-bind='click: function() { ${subpath}filter(""); }'></span>
        <input id="${singular}-filter" type="text" class="form-control" placeholder="Filter" data-bind="formValue: ${subpath}filter">
    </span>
</p>
<div>
    <label for="${singular}-list">${title}: </label>
    <div class="table-responsive">
        <table id="${singular}-list" class="table table-condensed table-hover">
            <thead>
                <tr data-bind="template: { name: 'list-header-template', foreach: <#nested/>, as: 'field' }"></tr>
            </thead>
            <tbody data-bind="template: { name: ${subpath}visibleEntries().length == 0 ? 'no-entries-template' : 'list-template' }"></tbody>
        </table>
    </div>
</div>
</#macro>