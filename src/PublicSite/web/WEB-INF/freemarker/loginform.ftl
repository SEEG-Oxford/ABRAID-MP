<div class="login-pane">
    <form action="">
        <p class="form-group">
            <a class="btn btn-primary">Log in via TGHN</a>
        </p>
    </form>
    <hr/>
    <form action="">
        <p id="login-header" data-bind="html: formAlert"></p>
        <p class="form-group">
            <span class="input-group">
                <span class="input-group-addon">
                    <i class="glyphicon glyphicon-user"></i>
                </span>
                <input type="text" class="form-control ffAutoFillHack" placeholder="Email address" data-bind="value: formUsername, valueUpdate: 'keyup'" >
            </span>
        </p>
        <p class="form-group">
            <span class="input-group">
                <span class="input-group-addon">
                    <i class="glyphicon glyphicon-lock"></i>
                </span>
                <input type="password" class="form-control ffAutoFillHack" placeholder="Password" data-bind="value: formPassword, valueUpdate: 'keyup'">
            </span>
        </p>
        <p class="form-group">
            <a class="btn btn-primary" data-bind="click: submit, css: { 'disabled': submitting }">Log in</a>
        </p>
        <p class="form-group form-info">
            Forgotten password? <a href="#">Reset it</a>.
        </p>
        <p class="form-group form-info">
            New user? <a href="#">Create account</a>.
        </p>
    </form>
</div>
