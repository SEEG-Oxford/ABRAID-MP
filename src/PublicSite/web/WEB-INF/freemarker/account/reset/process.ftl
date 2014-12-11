<#--
    The page from which password reset requests are completed.
    Copyright (c) 2014 University of Oxford
-->
<#import "../../shared/layout/common.ftl" as c/>
<#import "../../shared/layout/form.ftl" as f/>
<#import "../../shared/layout/panel.ftl" as p/>

<#assign bootstrapData>
<script type="text/javascript">
    // bootstrapped data for js viewmodels
    var data = {
        id: ${id},
        key: "${key}"
    };
</script>
</#assign>

<@c.page title="ABRAID MP - Password Reset" mainjs="/js/kickstart/account/reset/process" bootstrapData=bootstrapData>
<div class="container">
    <@p.panel "request-account-reset-body" "Password Reset">
        <p>Complete the fields below to set a new password.</p>
        <@f.form "request-account-reset-form">
            <@f.formGroupBasic "new-password" "New Password" "newPassword" "fa fa-lg fa-lock" "password" />
            <@f.formGroupBasic "confirm-password" "New Password (confirm)" "confirmPassword" "fa fa-lg fa-lock" "password" />
        </@f.form>
    </@p.panel>
</div>
</@c.page>
