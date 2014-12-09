<#--
    The page shown when an invalid password reset request is attempted.
    Copyright (c) 2014 University of Oxford
-->
<#import "../../shared/layout/common.ftl" as c/>
<#import "../../shared/layout/form.ftl" as f/>
<#import "../../shared/layout/panel.ftl" as p/>
<@c.page title="ABRAID MP - Password Reset">
<div class="container">
    <@p.panel "request-account-reset-body" "Password Reset">
        <#list failures as failure>
            <div class="alert alert-danger" role="alert">${failure}</div>
        </#list>
    </@p.panel>
</div>
</@c.page>
