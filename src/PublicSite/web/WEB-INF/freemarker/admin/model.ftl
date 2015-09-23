<#--
    Admin page to configure the model.
    Copyright (c) 2015 University of Oxford
-->
<#import "../layout/common.ftl" as c/>
<#import "../shared/layout/form.ftl" as f/>
<#import "../shared/layout/table.ftl" as t/>
<#import "../shared/layout/panel.ftl" as p/>
<#assign bootstrapData>
    <script type="text/javascript">
        // bootstrapped data for js viewmodels
        var initialRepoData = {
            url: "${repository_url?js_string}",
            version: "${model_version?js_string}",
            availableVersions: [<#list available_versions as version>"${version?js_string}"<#if version_has_next>,</#if></#list>]
        };
    </script>
</#assign>

<@c.page title="ABRAID-MP Administration: Model" mainjs="/js/kickstart/admin/model" bootstrapData=bootstrapData>
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
</div>
</@c.page>
