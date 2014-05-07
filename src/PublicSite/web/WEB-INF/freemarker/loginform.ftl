<form id="logIn" action="">
    <p id="formAlert" data-bind="text: formAlert"></p>
    <p class="form-group">
                <span class="input-group">
                    <span class="input-group-addon">
                        <i class="glyphicon glyphicon-user"></i>
                    </span>
                    <input type="text" class="form-control" placeholder="Email address" data-bind="value: formUsername" >
                </span>
    </p>
    <p class="form-group">
                <span class="input-group">
                    <span class="input-group-addon">
                        <i class="glyphicon glyphicon-lock"></i>
                    </span>
                    <input type="password" class="form-control" placeholder="Password" data-bind="value: formPassword">
                </span>
    </p>
    <p class="form-group">
        <input type="submit" class="btn btn-primary" value="Log in to start validating" data-bind="click: submit">
    </p>
</form>
