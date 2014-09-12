<#--
    Macro for bootstrap panels.
    Copyright (c) 2014 University of Oxford
-->
<#macro panel id title collapsible=false collapsed=false>
<div class="panel panel-default">
    <div class="panel-heading">
        <h2 class="panel-title">
            <a<#if collapsible> data-toggle="collapse"</#if> href="#${id}">${title}</a>
        </h2>
    </div>
    <#if collapsible>
        <div class="panel-collapse collapse<#if !collapsed> in</#if>" id="${id}">
            <div class="panel-body">
                <#nested/>
            </div>
        </div>
    <#else>
        <div class="panel-body" id="${id}">
            <#nested/>
        </div>
    </#if>
</div>
</#macro>
