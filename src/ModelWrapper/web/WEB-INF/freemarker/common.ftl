<#--
    The page template, including header and footer.
    Copyright (c) 2014 University of Oxford
-->
<#import "/spring.ftl" as spring />
<#macro page title>
<!DOCTYPE html>
<html class="no-js">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title>${title?html}</title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width">
        <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.1.1/css/bootstrap.css">
        <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.1.1/css/bootstrap-theme.css">
        <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.0.3/css/font-awesome.css">
        <link rel="stylesheet" href="<@spring.url '/css/main.css'/>">
    </head>
    <body>
        <div class="navbar navbar-default navbar-fixed-top navbar-inverse" role="navigation">
            <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="#">Project name</a>
                </div>
                <div class="collapse navbar-collapse">
                    <ul class="nav navbar-nav">
                        <li class="active"><a href="#">Home</a></li>
                    </ul>
                </div><!--/.nav-collapse -->
            </div>
        </div>
        <div class="container">
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
                    <p >&copy; 2014 University of Oxford</p>
                </div>
            </div>
        </div>
        <script src="//cdnjs.cloudflare.com/ajax/libs/modernizr/2.7.1/modernizr.min.js"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/jquery/2.1.0/jquery.js"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.1.1/js/bootstrap.js"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/knockout/3.1.0/knockout-debug.js"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/knockout-bootstrap/0.2.1/knockout-bootstrap.js"></script>
        <script src="<@spring.url '/js/knockout.validation.js'/>"></script>
        <script> var baseURL = "<@spring.url '/'/>"; </script>
        <script src="<@spring.url '/js/main.js'/>"></script>
    </body>
</html>
</#macro>