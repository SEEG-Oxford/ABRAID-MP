<#--
    Page header with links between pages.
    Currently holds log in form, but this will be replaced by ABRAID logo.
    Copyright (c) 2014 University of Oxford
-->
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<#import "/spring.ftl" as spring />
<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">

        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">ABRAID MP</a>
        </div>

        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="<@spring.url "/"/>">Atlas</a></li>
                <li><a href="<@spring.url "/datavalidation"/>">Data Validation</a></li>
                <li><a href="<@spring.url "/about"/>">About</a></li>
                <li><a href="<@spring.url "/publications"/>">Publications</a></li>
            </ul>

            <@security.authorize ifAnyGranted="ROLE_USER">
                <ul class="nav navbar-nav navbar-right">
                    <li><div>Hello <@security.authentication property="principal.fullName"/></div></li>
                    <li><a href="<@spring.url "/j_spring_security_logout"/>">Log out</a></li>
                </ul>
            </@security.authorize>
            <@security.authorize ifAnyGranted="ROLE_ANONYMOUS">
                <form class="navbar-form navbar-right" action="">
                    <p data-bind="text: formAlert"></p>
                    <input type="text" placeholder="Email address" data-bind="value: formUsername">
                    <input type="password" placeholder="Password" data-bind="value: formPassword">
                    <input type="submit" class="btn btn-primary" value="Log in" data-bind="click: attemptFormLogin">
                </form>
            </@security.authorize>
        </div> <!--/.navbar-collapse -->
    </div> <!--/.container -->
</div>
