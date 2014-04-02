<#--
    The page template, including header and footer.
    Copyright (c) 2014 University of Oxford
-->
<#macro page title endOfHead="" endOfBody="" endOfBodyScript="">
<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html class="no-js">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title>${title?html}</title>

        <meta name="description" content="">
        <meta name="viewport" content="width=device-width">

        <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.0.3/css/bootstrap.css">
        <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.0.3/css/bootstrap-theme.css">
        <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.0.3/css/font-awesome.min.css">

        ${endOfHead}

        <link rel="stylesheet" href="<@spring.url "/css/main.css" />">
    </head>
    <body>

        <#include "navbar.ftl"/>

        <div id="common">
            <#nested/>
        </div>

        <#include "footer.ftl"/>

        <script src="//cdnjs.cloudflare.com/ajax/libs/knockout/3.0.0/knockout-debug.js"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/jquery/2.1.0/jquery.js"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/modernizr/2.7.1/modernizr.min.js"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.0.3/js/bootstrap.js"></script>
        <script src="<@spring.url "/js/navbar.js" />"></script>
        <script>
            var baseUrl = "<@spring.url "/" />";
            ${endOfBodyScript}
        </script>
        ${endOfBody}
    </body>
</html>
</#macro>
