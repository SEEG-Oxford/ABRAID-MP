<#--
    A table of the spread of disease occurrences by country and year, for a particular disease group.
    Copyright (c) 2014 University of Oxford
-->
<#import "../../layout/common.ftl" as c/>

<@c.minimalPage title="ABRAID-MP Administration: Disease Occurrence Spread">

<style>
    th {
        cursor: default !important;
    }
</style>

<div class="container">
    <#if (table.errorMessage)??>
        <div class="alert alert-warning" role="alert">${table.errorMessage}</div>
    <#else>
        <table class="table table-condensed">
            <thead>
            <tr>
                <th>Country</th>
                <th>In Africa?</th>
                <#list table.headingYears as year>
                    <th>${year?string("0")}</th>
                </#list>
            </tr>
            <tr>
                <th>Total</th>
                <th></th>
                <#list table.totalRow as total>
                    <th>${total?c}</th>
                </#list>
            </tr>
            </thead>
            <tbody>
            <#list table.rows as row>
                <tr>
                    <td>${row.countryName}</td>
                    <td>${row.isForMinimumDiseaseSpread?string("Yes","No")}</td>
                    <#list row.occurrenceCounts as occurrenceCount>
                        <td>${occurrenceCount?c}</td>
                    </#list>
                </tr>
            </#list>
            </tbody>
        </table>
    </#if>
</div>
</@c.minimalPage>
