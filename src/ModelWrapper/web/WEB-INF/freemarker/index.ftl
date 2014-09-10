<#--
    ModelWrapper's index landing page, to display model outputs.
    Copyright (c) 2014 University of Oxford
-->
<#import "shared/layout/common.ftl" as c/>
<#import "shared/layout/form.ftl" as f/>

<#assign bootstrapData>
    <script type="text/javascript">
        // bootstrapped data for js viewmodels
        var initialRepoData = {
            url: "${repository_url?js_string}",
            version: "${model_version?js_string}",
            availableVersions: [<#list available_versions as version>"${version?js_string}"<#if version_has_next>,</#if></#list>]
        };
        var initialMiscData = {
            rPath: "${r_path?js_string}",
            runDuration: ${run_duration?c},
            covariateDirectory: "${covariate_directory?js_string}"
        };
    </script>
</#assign>

<@c.page title="ABRAID-MP ModelWrapper" mainjs="/js/kickstart/index" bootstrapData=bootstrapData templates="">
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
                <@f.form "auth-form">
                    <@f.formGroupBasic "auth-username" "Username" "username" "glyphicon glyphicon-user" />
                    <@f.formGroupBasic "auth-password" "New password" "password" "glyphicon glyphicon-lock" "password" />
                    <@f.formGroupBasic "auth-password-confirm" "New password (confirm)" "passwordConfirmation" "glyphicon glyphicon-lock" "password" />
                </@f.form>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a data-toggle="collapse" href="#misc-body">
                    Miscellaneous Settings
                </a>
            </h2>
        </div>
        <div class="panel-collapse collapse" id="misc-body">
            <div class="panel-body">
                <p>Use the fields below to update advanced model wrapper configuration options.</p>
                <!-- ko with:RExecutableViewModel -->
                    <@f.form "r-form">
                        <@f.formGroupBasic "r-exe" "R executable path" "value" "fa fa-lg fa-terminal" />
                    </@f.form>
                <!-- /ko -->
                <!-- ko with:ModelDurationViewModel -->
                    <@f.form "run-duration-form">
                        <@f.formGroupBasic "max-duration" "Model run duration limit (ms)" "value" "glyphicon glyphicon-dashboard" />
                    </@f.form>
                <!-- /ko -->
                <!-- ko with:CovariateDirectoryViewModel -->
                    <@f.form "covariate-dir-form">
                        <@f.formGroupBasic "covariate-directory" "Covariate directory" "value" "glyphicon glyphicon-folder-open" />
                    </@f.form>
                <!-- /ko -->
            </div>
        </div>
    </div>
</div>
</@c.page>