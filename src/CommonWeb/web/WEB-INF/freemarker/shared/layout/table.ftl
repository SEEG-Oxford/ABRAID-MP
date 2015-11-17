<#--
    Macro for including consistent bootstrap tables. For use with BaseFormViewModel.
    Copyright (c) 2014 University of Oxford
-->
<#macro tableTemplates numberOfColumns plural="entries" subpath="" name="">
    <#if (subpath?length > 0)>
        <#assign innerPath="${subpath}.">
    <#else>
        <#assign innerPath="">
    </#if>

    <script type="text/html" id="no-entries-template">
        <tr class="warning">
            <td colspan="${numberOfColumns}" class="text-muted">No matching ${plural}.</td>
        </tr>
    </script>
    <script type="text/html" id="${name}list-template">
        <!-- ko foreach: ${innerPath}visibleEntries -->
            <#nested/>
        <!-- /ko -->
    </script>
    <script type="text/html" id="${name}list-header-template">
        <th data-bind="click: function () { if (field.name) { $parent.${innerPath}updateSort(field.name) } }">
            <span data-bind="html: display"></span>
            <span data-bind="text: ($parent.${innerPath}sortField() === field.name) ? ($parent.${innerPath}reverseSort() ? '&#9650;' : '&#9660;') : '&nbsp;'" class="up-down"></span>
        </th>
    </script>
</#macro>
<#macro tableBody singular="entry" title="Entries" subpath="" name="">
    <#if (subpath?length > 0)>
        <#assign innerPath="${subpath}.">
    <#else>
        <#assign innerPath="">
    </#if>
    <p class="form-group">
        <label for="${singular}-filter">Filter: </label>
        <span class="input-group">
            <span class="input-group-addon">
                <i class="glyphicon glyphicon-filter"></i>
            </span>
            <span id="filter-clear" class="clear glyphicon glyphicon-remove-circle" data-bind='click: function() { ${innerPath}filter(""); }'>
            </span>
            <input id="${singular}-filter" type="text" class="form-control" placeholder="Filter" data-bind="formValue: ${innerPath}filter">
        </span>
    </p>
    <div>
        <label for="${singular}-list">${title}: </label>
        <div class="table-responsive">
            <table id="${singular}-list" class="table table-condensed table-hover">
                <thead>
                    <tr data-bind="template: { name: '${name}list-header-template', foreach: <#nested/>, as: 'field' }"></tr>
                </thead>
                <tbody data-bind="template: { name: ${innerPath}visibleEntries().length == 0 ? '${name}no-entries-template' : '${name}list-template' }"></tbody>
            </table>
        </div>
    </div>
</#macro>
