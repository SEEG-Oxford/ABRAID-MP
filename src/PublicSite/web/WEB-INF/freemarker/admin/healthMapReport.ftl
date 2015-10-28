<#import "../layout/common.ftl" as c/>
<#import "../shared/layout/table.ftl" as t/>
<#assign css>
<style>
    td, th {
        padding-left: 3px !important;
        padding-right: 3px !important;
        white-space: nowrap;
        border-left: 1px solid #ddd;
    }
    td {
        padding-bottom: 0px !important;
        padding-top: 0px !important;
    }
    tr.active {
        display: none;
    }
</style>
</#assign>
<@c.page title="ABRAID-MP Reports: HealthMap" endOfHead=css>
<table class="table table-condensed table-hover">
    <thead>
        <tr>
            <th></th>
            <#list months as month>
                <th>${month}</th>
            </#list>
        </tr>
    </thead>
    <tbody>
        <#list qualifiers as qualifier>
            <tr onclick="$('.q${qualifier?index}').fadeToggle();" <#if qualifier=='Total'>style="font-weight: bold"</#if>>
                <td>${qualifier}</td>
                <#list months as month>
                    <td <#if month=='Total'>style="font-weight: bold"</#if>>${data[month][qualifier].getDataCountryCount() + data[month][qualifier].getDataAdmin1Count() + data[month][qualifier].getDataAdmin2Count() + data[month][qualifier].getDataPreciseCount()}&nbsp;(${data[month][qualifier].getLocationCountryCount() + data[month][qualifier].getLocationAdmin1Count() + data[month][qualifier].getLocationAdmin2Count() + data[month][qualifier].getLocationPreciseCount()})</td>
                </#list>
            </tr>
            <tr class="active q${qualifier?index}">
                <td>&#9500;&nbsp;A0</td>
                <#list months as month>
                    <td>${data[month][qualifier].getDataCountryCount()}&nbsp;(${data[month][qualifier].getLocationCountryCount()})</td>
                </#list>
            </tr>
            <tr class="active q${qualifier?index}">
                <td>&#9500;&nbsp;A1</td>
                <#list months as month>
                    <td>${data[month][qualifier].getDataAdmin1Count()}&nbsp;(${data[month][qualifier].getLocationAdmin1Count()})</td>
                </#list>
            </tr>
            <tr class="active q${qualifier?index}">
                <td>&#9500;&nbsp;A2</td>
                <#list months as month>
                    <td>${data[month][qualifier].getDataAdmin2Count()}&nbsp;(${data[month][qualifier].getLocationAdmin2Count()})</td>
                </#list>
            </tr>
            <tr class="active q${qualifier?index}">
                <td>&#9492;&nbsp;P</td>
                <#list months as month>
                    <td>${data[month][qualifier].getDataPreciseCount()}&nbsp;(${data[month][qualifier].getLocationPreciseCount()})</td>
                </#list>
            </tr>
        </#list>
    </tbody>
</table>
</@c.page>
