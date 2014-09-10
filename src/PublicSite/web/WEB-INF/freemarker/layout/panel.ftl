<#--
    Macro for bootstrap panels.
    Copyright (c) 2014 University of Oxford
-->
<#macro panel id title>
<div class="panel panel-default">
    <div class="panel-heading">
        <h2 class="panel-title">
            <a href="#${id}">${title}</a>
        </h2>
    </div>
    <div class="panel-body" id="${id}">
        <#nested/>
    </div>
</div>
</#macro>
