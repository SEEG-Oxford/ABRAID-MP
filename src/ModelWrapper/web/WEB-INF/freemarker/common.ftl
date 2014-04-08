<#--
    The page template, including header and footer.
    Copyright (c) 2014 University of Oxford
-->
<#macro page title endOfHead="" endOfBody="" endOfBodyScript="" requireDataMain="">
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

        ${endOfHead}

        <link rel="stylesheet" href="<@spring.url '/css/main.css'/>">
    </head>
    <body>
        <div class="navbar navbar-default navbar-fixed-top navbar-inverse">
            <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="#">ABRAID-MP Model Wrapper</a>
                </div>
                <div class="collapse navbar-collapse">
                    <ul class="nav navbar-nav">
                        <li class="active"><a href="#">Home</a></li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="common">
            <#nested/>
        </div>
        <div id="footer" class="text-muted">
            <div class="container">
                <div class="links">
                    <a href="http://www.gatesfoundation.org/">BMGF</a><p> | </p>
                    <a href="http://healthmap.org/en/">HealthMap</a><p> | </p>
                    <a href="http://seeg.zoo.ox.ac.uk/">SEEG</a><p> | </p>
                    <a href="http://tghn.org/">TGHN</a>
                </div>
                <div class="copyright">
                    <p>&copy; 2014 University of Oxford</p>
                </div>
            </div>
        </div>
        <script>
            var baseUrl = "<@spring.url '/'/>";
            ${endOfBodyScript}
        </script>
        <script type="text/javascript" data-main="<@spring.url '${requireDataMain}' />" src="https://cdnjs.cloudflare.com/ajax/libs/require.js/2.1.11/require.js"></script>
        ${endOfBody}
    </body>
</html>
</#macro>
