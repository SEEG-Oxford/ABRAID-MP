<#--
    The page template, including header and footer.
    Copyright (c) 2014 University of Oxford
-->
<#macro page title endOfHead="" endOfBody="" endOfBodyScript="" requireDataMain="/js/default">
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
        <script>
            var baseUrl = "<@spring.url "/" />";
            ${endOfBodyScript}
        </script>
        <script type="text/javascript" data-main="<@spring.url '${requireDataMain}' />" src="https://cdnjs.cloudflare.com/ajax/libs/require.js/2.1.11/require.js"></script>
        ${endOfBody}
    </body>
</html>
</#macro>
