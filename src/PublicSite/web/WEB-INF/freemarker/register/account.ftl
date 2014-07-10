<#--
    User registration page
    Copyright (c) 2014 University of Oxford
-->
<#import "../common.ftl" as c/>
<#assign bootstrapData>
<script type="text/javascript">
    // bootstrapped data for js viewmodels
    var initialAlerts = ${initialAlerts};
    var initialExpert = {
        email: "${expert.getEmail()!""?js_string}",
        password: "${expert.getPassword()!""?js_string}"
    };
</script>
</#assign>
<@c.page title="ABRAID MP - Register" mainjs="/js/kickstart/register/account" bootstrapData=bootstrapData templates="">
<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a data-toggle="collapse" href="#auth-body">
                    Sign Up for ABRAID-MP
                </a>
            </h2>
        </div>

        <div class="panel-collapse collapse in" id="auth-body">
            <div class="panel-body">
                <form action="#">
                    <p>Sign up with:</p>
                    <p class="form-group">
                        <a class="btn btn-primary">TGHN account</a>
                    </p>
                </form>
                <hr/>
                <form action="#">
                    <p>Or create a new ABRAID-MP account:</p>
                    <p class="form-group">
                        <label for="auth-username">Email address: </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="glyphicon glyphicon-user"></i>
                            </span>
                            <input id="auth-username" type="text" class="form-control" placeholder="Email address" autocomplete="off" data-bind="value: username, valueUpdate:'afterkeydown', disable: saving" >
                        </span>
                    </p>
                    <p class="form-group">
                        <label for="auth-password">Password: </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="glyphicon glyphicon-lock"></i>
                            </span>
                            <input id="auth-password" type="password" class="form-control" placeholder="Password" autocomplete="off" data-bind="value: password, valueUpdate:'afterkeydown', disable: saving" >
                        </span>
                    </p>
                    <p class="form-group">
                        <label for="auth-password-confirm">Password (confirm): </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="glyphicon glyphicon-lock"></i>
                            </span>
                            <input id="auth-password-confirm" type="password" class="form-control" placeholder="Password (confirm)" autocomplete="off" data-bind="value: passwordConfirmation, valueUpdate:'afterkeydown', disable: saving" >
                        </span>
                    </p>
                    <p class="form-group">
                        captcha
                    </p>
                    <p class="form-group">
                        <a class="btn btn-primary" data-bind="click: submit, css: { 'disabled': !isValid() || saving }, text: saving() ? 'Creating ...' : 'Create a new ABRAID-MP account'"></a>
                    </p>
                    <div class="form-group" data-bind="foreach: notices">
                        <div data-bind="alert: $data"></div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</@c.page>
