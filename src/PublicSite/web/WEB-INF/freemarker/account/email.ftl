<#--
    User email change page.
    Copyright (c) 2014 University of Oxford
-->
<#import "../layout/common.ftl" as c/>
<#import "../shared/layout/form.ftl" as f/>
<#import "../shared/layout/panel.ftl" as p/>
<style>
    #password-form .fa-lock {
        padding: 0 3px;
    }
</style>
<#assign bootstrapData>
<script type="text/javascript">
    // bootstrapped data for js viewmodels
    var currentEmail = "${email?js_string}";
</script>
</#assign>
<@c.page title="ABRAID MP - Change Email" mainjs="/js/kickstart/account/email" bootstrapData=bootstrapData>
    <div class="container">
        <@p.panel "email-body" "Email">
            <p>Complete the fields below to change your email address.</p>
            <@f.form "email-form">
                <@f.formGroupBasic "email" "New Email Address" "email" "fa fa-lg fa-user" />
                <@f.formGroupBasic "password" "Current Password" "password" "fa fa-lg fa-lock" "password" />
            </@f.form>
        </@p.panel>
    </div>
</@c.page>
