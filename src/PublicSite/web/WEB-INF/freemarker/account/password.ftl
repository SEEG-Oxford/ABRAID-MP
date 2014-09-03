<#--
    User password change page.
    Copyright (c) 2014 University of Oxford
-->
<#import "../layout/common.ftl" as c/>
<#import "../layout/form.ftl" as f/>
<#import "../layout/panel.ftl" as p/>
<@c.page title="ABRAID MP - Change Password" mainjs="/js/kickstart/account/password">
    <div class="container">
        <@p.panel "password-body" "Password">
            <p>Some text about password changes</p>
            <@f.form "password-form">
                <@f.formGroupBasic "old-password" "Current Password" "oldPassword" "fa fa-lg fa-unlock" />
                <@f.formGroupBasic "new-password" "New Password" "newPassword" "fa fa-lg fa-lock" />
                <@f.formGroupBasic "confirm-password" "New Password (confirm)" "confirmPassword" "fa fa-lg fa-lock" />
            </@f.form>
        </@p.panel>
    </div>
</@c.page>
