<#--
    User registration page
    Copyright (c) 2014 University of Oxford
-->
<#import "../layout/common.ftl" as c/>
<#assign bootstrapData>
<script type="text/javascript">
    // bootstrapped data for js viewmodels
    var initialAlerts = ${alerts};
    var initialExpert = ${jsonExpert};
</script>
</#assign>
<@c.page title="ABRAID MP - Register" mainjs="/js/kickstart/register/account" bootstrapData=bootstrapData templates="">
<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a href="#account-body">
                    Sign Up for ABRAID-MP
                </a>
            </h2>
        </div>
        <div class="panel-body" id="account-body">
            <form action="#">
                <p>Sign up with:</p>
                <p class="form-group">
                    <a class="btn btn-primary">TGHN account</a>
                </p>
            </form>
            <hr/>
            <form action="#" data-bind="formSubmit: submit">
                <p>Or create a new ABRAID-MP account:</p>
                <p class="form-group">
                    <label for="auth-username">Email address: </label>
                    <span class="input-group">
                        <span class="input-group-addon">
                            <i class="glyphicon glyphicon-user"></i>
                        </span>
                        <input id="auth-username" type="text" class="form-control" placeholder="Email address" data-bind="formValue: email" >
                    </span>
                </p>
                <p class="form-group">
                    <label for="auth-password">Password: </label>
                    <span class="input-group">
                        <span class="input-group-addon">
                            <i class="glyphicon glyphicon-lock"></i>
                        </span>
                        <input id="auth-password" type="password" class="form-control" placeholder="Password" data-bind="formValue: password" >
                    </span>
                </p>
                <p class="form-group">
                    <label for="auth-password-confirm">Password (confirm): </label>
                    <span class="input-group">
                        <span class="input-group-addon">
                            <i class="glyphicon glyphicon-lock"></i>
                        </span>
                        <input id="auth-password-confirm" type="password" class="form-control" placeholder="Password (confirm)" data-bind="formValue: passwordConfirmation" >
                    </span>
                </p>
                <p class="form-group">
                    ${captcha}
                </p>
                <br/>
                <p class="form-group">
                    <button type="submit" class="btn btn-primary" data-bind="formButton: { submitting: 'Creating ...', standard: 'Create a new ABRAID-MP account'}"></button>
                </p>
                <div class="form-group" data-bind="foreach: notices">
                    <div data-bind="alert: $data"></div>
                </div>
            </form>
        </div>
    </div>
</div>

</@c.page>
