<#import "/spring.ftl" as spring />
<div class="login-pane">
    <form id="login-form" action="" data-bind="formSubmit: submit">
        <p data-bind="html: message"></p>
        <p class="form-group">
            <span class="input-group">
                <span class="input-group-addon">
                    <i class="glyphicon glyphicon-user"></i>
                </span>
                <input type="text" class="form-control ffAutoFillHack" placeholder="Email address" data-bind="formValue: username" >
            </span>
        </p>
        <p class="form-group">
            <span class="input-group">
                <span class="input-group-addon">
                    <i class="glyphicon glyphicon-lock"></i>
                </span>
                <input type="password" class="form-control ffAutoFillHack" placeholder="Password" data-bind="formValue: password">
            </span>
        </p>
        <p class="form-group">
            <button type="submit" class="btn btn-primary" data-bind="formButton: { submitting: 'Logging in ...', standard:'Log in' }"></button>
        </p>
        <p class="form-info">
            Forgotten password? <a href="<@spring.url "/account/reset/request" />" target="_top">Reset it</a>.
        </p>
        <p class="form-info">
            New user? <a href="<@spring.url "/register/account" />" target="_top">Create account</a>.
        </p>
    </form>
</div>
