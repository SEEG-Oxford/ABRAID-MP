<#--
    ModelWrapper's index landing page, to display model outputs.
    Copyright (c) 2014 University of Oxford
-->
<#import "common.ftl" as c/>
<@c.page title="ABRAID-MP ModelWrapper" requireDataMain="/js/index">
<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a data-toggle="collapse" href="#repo-body">
                    Repository Settings
                </a>
            </h2>
        </div>
        <div class="panel-collapse collapse in" id="repo-body">
            <div class="panel-body">
                <p>Use the fields below to update the repository details used to obtain the niche model.</p>
                <form action="#">
                    <p class="form-group">
                        <label for="repo-url">Repository URL: </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="fa fa-lg fa-github"></i>
                            </span>
                            <input id="repo-url" type="text" class="form-control" placeholder="Repository URL" autocomplete="off" data-bind="value: url, valueUpdate:'afterkeydown'" >
                        </span>
                    </p>
                    <p class="form-group">
                        <a class="btn btn-primary" data-bind="text: syncingRepo() ? 'Syncing ...' : (urlChanged() ? 'Save and Sync' : 'Sync'), css: { 'disabled': !url.isValid() || syncingRepo }, click: syncRepo"></a>
                    </p>
                    <p class="form-group">
                        <label for="repo-version">Model Version: </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="glyphicon glyphicon-tags"></i>
                            </span>
                            <select id="repo-version" class="form-control" data-bind="enable: enableVersion, options: availableVersions, value: version, optionsCaption: 'No version selected'" ></select>
                        </span>
                    </p>
                    <p class="form-group">
                        <a class="btn btn-primary" data-bind="css: { 'disabled': !enableVersion || !version.isValid() || savingVersion }, click: saveVersion">Save</a>
                    </p>
                    <div class="form-group" data-bind="foreach: notices">
                        <div data-bind="alert: $data"></div>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a data-toggle="collapse" href="#auth-body">
                    Authentication Settings
                </a>
            </h2>
        </div>
        <div class="panel-collapse collapse in" id="auth-body">
            <div class="panel-body">
                <p>Use the fields below to update the authentication details used to connect to this site.</p>
                <form action="#">
                    <p class="form-group">
                        <label for="auth-username">Username: </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="glyphicon glyphicon-user"></i>
                            </span>
                            <input id="auth-username" type="text" class="form-control" placeholder="New username" autocomplete="off" data-bind="value: username, valueUpdate:'afterkeydown', disable: saving" >
                        </span>
                    </p>
                    <p class="form-group">
                        <label for="auth-password">Password: </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="glyphicon glyphicon-lock"></i>
                            </span>
                            <input id="auth-password" type="password" class="form-control" placeholder="New password" autocomplete="off" data-bind="value: password, valueUpdate:'afterkeydown', disable: saving" >
                        </span>
                    </p>
                    <p class="form-group">
                        <label for="auth-password-confirm">Password (confirm): </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="glyphicon glyphicon-lock"></i>
                            </span>
                            <input id="auth-password-confirm" type="password" class="form-control" placeholder="New password (confirm)" autocomplete="off" data-bind="value: passwordConfirmation, valueUpdate:'afterkeydown', disable: saving" >
                        </span>
                    </p>
                    <p class="form-group">
                        <a class="btn btn-primary" data-bind="click: submit, css: { 'disabled': !isValid() || saving }, text: saving() ? 'Saving ...' : 'Save'"></a>
                    </p>
                    <div class="form-group" data-bind="foreach: notices">
                        <div data-bind="alert: $data"></div>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a data-toggle="collapse" href="#misc-body">
                    Misc Settings
                </a>
            </h2>
        </div>
        <div class="panel-collapse collapse" id="misc-body">
            <div class="panel-body">
                <p>Use the fields below to update .</p>
                <form action="#" data-bind="with: RExecutableViewModel">
                    <p class="form-group">
                        <label for="r-exe">R executable path: </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="fa fa-lg fa-terminal"></i>
                            </span>
                            <input id="r-exe" type="text" class="form-control" placeholder="R executable path" autocomplete="off" data-bind="value: value, valueUpdate:'afterkeydown', disable: saving" >
                        </span>
                    </p>
                    <p class="form-group">
                        <a class="btn btn-primary" data-bind="click: submit, css: { 'disabled': !isValid() || saving }, text: saving() ? 'Saving ...' : 'Save'"></a>
                    </p>
                    <div class="form-group" data-bind="foreach: notices">
                        <div data-bind="alert: $data"></div>
                    </div>
                </form>
                <form action="#" data-bind="with: ModelDurationViewModel">
                    <p class="form-group">
                        <label for="max-duration">Model run duration limit (ms): </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="glyphicon glyphicon-dashboard"></i>
                            </span>
                            <input id="max-duration" type="text" class="form-control" placeholder="Model run duration limit" autocomplete="off" data-bind="value: value, valueUpdate:'afterkeydown', disable: saving" >
                        </span>
                    </p>
                    <p class="form-group">
                        <a class="btn btn-primary" data-bind="click: submit, css: { 'disabled': !isValid() || saving }, text: saving() ? 'Saving ...' : 'Save'"></a>
                    </p>
                    <div class="form-group" data-bind="foreach: notices">
                        <div data-bind="alert: $data"></div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<script type="text/html" id="validation-template">
    <span class="input-group-addon" data-container="body" data-bind="css: field.isValid() ? 'bg-success-important' : 'bg-danger-important', tooltip: { title: field.error, placement: 'right' } ">
        <i class="fa fa-lg" data-bind="css: field.isValid() ? 'text-success fa-check-circle' : 'text-danger fa-exclamation-circle'"></i>
    </span>
</script>
<script>
    // bootstrapped data for js viewmodels
    var initialRepoData = {
        url: "${repository_url?js_string}",
        version: "${model_version?js_string}",
        availableVersions: [<#list available_versions as version>"${version?js_string}"<#if version_has_next>,</#if></#list>]
    };
    var initialMiscData = {
        rPath: "${r_path?js_string}",
        runDuration: "${run_duration?js_string}"
    };
</script>
</@c.page>