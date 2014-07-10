<#--
    The page template, including header and footer.
    Copyright (c) 2014 University of Oxford
-->
<#macro page title endOfHead="" bootstrapData="" templates="" mainjs="/js/kickstart/default">
<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html class="no-js">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

        <title>${title?html}</title>

        <meta name="description" content="">
        <meta name="viewport" content="width=device-width">

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.1.1/css/bootstrap.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.1.1/css/bootstrap-theme.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.0.3/css/font-awesome.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jasny-bootstrap/3.1.2/css/jasny-bootstrap.css">

        ${endOfHead}

        <link rel="stylesheet" href="<@spring.url '/css/main.css'/>">
    </head>
    <body>
        <#include "navbar.ftl"/>
        <div id="common">
            <#nested/>
        </div>
        <#include "footer.ftl"/>

        <!-- Base url -->
        <script>
            var baseUrl = "<@spring.url '/'/>";
        </script>

        <!-- Bootstrapped JS data for KO view models -->
        ${bootstrapData}

        <!-- Templates -->
        ${templates}
        <script type="text/html" id="validation-template">
            <!-- ko if: field.rules().length != 0 -->
            <span class="input-group-addon" data-container="body" data-bind="css: field.isValid() ? 'bg-success-important' : 'bg-danger-important', tooltip: { title: field.error, placement: 'right' } ">
                <i class="fa fa-lg" data-bind="css: field.isValid() ? 'text-success fa-check-circle' : 'text-danger fa-exclamation-circle'"></i>
            </span>
            <!-- /ko -->
        </script>

        <!-- Require -->
        <script type="text/javascript" data-main="<@spring.url '${mainjs}' />" src="https://cdnjs.cloudflare.com/ajax/libs/require.js/2.1.11/require.js"></script>
    </body>
</html>
</#macro>
