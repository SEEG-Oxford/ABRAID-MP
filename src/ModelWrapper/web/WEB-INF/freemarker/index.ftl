<#--
    ModelWrapper's index landing page, to display model outputs.
    Copyright (c) 2014 University of Oxford
-->
<#import "shared/layout/common.ftl" as c/>
<#import "shared/layout/form.ftl" as f/>

<#assign bootstrapData>
    <script type="text/javascript">
        // bootstrapped data for js viewmodels
        var initialMiscData = {
            rPath: "${r_path?js_string}",
            runDuration: ${run_duration?c}
        };
    </script>
</#assign>

<@c.page title="ABRAID-MP ModelWrapper" mainjs="/js/kickstart/index" bootstrapData=bootstrapData templates="">
<div class="container">
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
        <div class="panel-collapse" id="misc-body">
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
            </div>
        </div>
    </div>
</div>
</@c.page>
