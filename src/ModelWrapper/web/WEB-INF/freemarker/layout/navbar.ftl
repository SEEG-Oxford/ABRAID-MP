<#--
    Page header with links between pages.
    Currently holds log in form, but this will be replaced by ABRAID logo.
    Copyright (c) 2014 University of Oxford
-->
<#import "/spring.ftl" as spring />
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
                <li><a href="<@spring.url '/' />">Home</a></li>
                <li><a href="<@spring.url '/covariates' />">Covariates</a></li>
            </ul>
        </div>
    </div>
</div>