<#--
    The page shown when an invalid password reset request is attempted.
    Copyright (c) 2014 University of Oxford
-->
<#import "../../layout/common.ftl" as c/>
<#import "../../shared/layout/form.ftl" as f/>
<#import "../../shared/layout/panel.ftl" as p/>
<#import "/spring.ftl" as spring />
<@c.page title="ABRAID MP - Password Reset">
<div class="container">
    <@p.panel "request-account-reset-body" "Password Reset">
        <#list failures as failure>
            <div class="alert alert-danger" role="alert" style="text-align: center">${failure}</div>
        </#list>
        <br/>
        <p style="text-align: center"><a href="<@spring.url '/account/reset/request' />" class="btn btn-primary">Request a new password reset link</a></p>
    </@p.panel>
</div>
</@c.page>
