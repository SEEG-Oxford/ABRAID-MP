<#--
    The page from which new password reset requests are issued.
    Copyright (c) 2014 University of Oxford
-->
<#import "../../layout/common.ftl" as c/>
<#import "../../shared/layout/form.ftl" as f/>
<#import "../../shared/layout/panel.ftl" as p/>
<@c.page title="ABRAID MP - Password Reset" mainjs="/js/kickstart/account/reset/request">
<div class="container">
    <@p.panel "request-account-reset-body" "Password Reset">
        <p>Complete the field below to start the password reset process.</p>
        <@f.form "request-account-reset-form" "Submit" "Submitting ...">
            <@f.formGroupBasic "email" "Email Address" "email" "glyphicon glyphicon-user"/>
        </@f.form>
    </@p.panel>
</div>
</@c.page>
