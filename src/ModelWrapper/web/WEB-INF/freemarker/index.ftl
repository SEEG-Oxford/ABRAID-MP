<#--
    ModelWrapper's index landing page, to display model outputs.
    Copyright (c) 2014 University of Oxford
-->
<#import "common.ftl" as c/>
<@c.page title="ABRAID-MP: ModelWrapper">
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
                <p>Use the fields below to update the repository details used obtain the niche model.</p> <br>
                <p><a class="btn btn-primary" role="button">Sync</a></p>
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
                <p>Use the fields below to update the authentication details used to connect to this site.</p><br>
                <div data-bind="foreach: notices">
                    <div data-bind="alert: $data"></div>
                </div>
                <form>
                    <p><div class="input-group"><span class="input-group-addon glyphicon glyphicon-user"></span>
                        <input type="text" class="form-control" placeholder="New username" autocomplete="off" data-bind="value: username, valueUpdate:'afterkeydown'" >
                    </div></p><br>
                    <p><div class="input-group"><span class="input-group-addon glyphicon glyphicon-lock"></span>
                        <input type="password" class="form-control" placeholder="New password" autocomplete="off" data-bind="value: password, valueUpdate:'afterkeydown'" >
                    </div></p><br>
                    <p><div class="input-group"><span class="input-group-addon glyphicon glyphicon-lock"></span>
                        <input type="password" class="form-control" placeholder="New password (repeat)" autocomplete="off" data-bind="value: passwordDuplicate, valueUpdate:'afterkeydown'" >
                    </div></p><br>
                    <p><a class="btn btn-primary" role="button" data-bind="click: submit, css: { 'disabled': !isValid() || saving }"><!-- ko ifnot: saving -->Save<!-- /ko --><!-- ko if: saving -->Saving<!-- /ko --></a></p>
                </form>
            </div>
        </div>
    </div>
</div>
<script type="text/html" id="validation-template">
    <!-- ko if: field.isValid() -->
        <span class="input-group-addon bg-success-important"><i class="fa fa-check-circle text-success fa-lg"></i></span>
    <!-- /ko -->
    <!-- ko ifnot: field.isValid() -->
        <span class="input-group-addon bg-danger-important" data-bind="tooltip: {title: field.error, placement: 'right'}"><i class="fa fa-exclamation-circle text-danger fa-lg"></i></span>
    <!-- /ko -->
</script>
</@c.page>
