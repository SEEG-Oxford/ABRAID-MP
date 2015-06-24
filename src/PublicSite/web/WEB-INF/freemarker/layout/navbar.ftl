<#--
    Page header with links between pages.
    Copyright (c) 2014 University of Oxford
-->
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<#import "/spring.ftl" as spring />
<div class="navbar navbar-default navbar-fixed-top navbar-inverse" id="the-nav">
    <script type="text/html" id="login-template">
        <#include "../layout/loginform.ftl" />
    </script>
    <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#site-nav" style="padding: 9px 10px">
            <span class="sr-only">Toggle site navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <@security.authorize ifAnyGranted="ROLE_USER">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#user-nav" style="padding: 6px 13px">
                <span class="sr-only">Toggle user navigation</span>
                <span class="glyphicon glyphicon-user"></span>
            </button>
        </@security.authorize>
        <@security.authorize ifAnyGranted="ROLE_ANONYMOUS">
            <button type="button" class="navbar-toggle" style="padding: 6px 13px; margin-right: 20px" data-bind="popover: {template: 'login-template', placement: 'bottom', title: 'Log in'}">
                <span class="glyphicon glyphicon-user"></span>
            </button>
        </@security.authorize>
        <a class="navbar-brand" href="#">ABRAID-MP</a>
    </div>
    <ul class="collapse navbar-collapse nav navbar-nav" id="site-nav">
        <li><a href="<@spring.url "/"/>">Atlas</a></li>
        <li><a href="<@spring.url "/datavalidation"/>">Data Validation</a></li>
        <li class="dropdown">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">About<span class="caret"></span></a>
            <ul class="dropdown-menu" role="menu">
                <li><a href="<@spring.url "/about"/>">ABRAID-MP</a>
                <li><a href="<@spring.url "/experts"/>">Contributors</a></li>
            </ul>
        </li>
        <@security.authorize ifAnyGranted="ROLE_ADMIN">
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Tools<span class="caret"></span></a>
                <ul class="dropdown-menu" role="menu">
                    <li><a href="<@spring.url "/tools/uploadcsv/"/>">Upload CSV</a></li>
                    <li><a href="<@spring.url "/admin/diseases/"/>">Administration - Diseases</a></li>
                    <li><a href="<@spring.url "/admin/experts/"/>">Administration - Experts</a></li>
                    <li><a href="<@spring.url "/admin/covariates/"/>">Administration - Covariates</a></li>
                </ul>
            </li>
        </@security.authorize>

    </ul>

    <@security.authorize ifAnyGranted="ROLE_USER">
        <ul class="collapse navbar-collapse nav navbar-nav navbar-right" id="user-nav">
            <li class="centered-nav"><a href="#" class="collapse-only">Signed in as: <br><strong><@security.authentication property="principal.fullName"/></strong></a></li>
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                    <span class="fa fa-lg fa-user"></span>
                    <span class="collapse-only">Profile <span class="caret"></span>
                </a>
                <ul class="dropdown-menu" role="menu">
                    <li class="centered-nav expand-only"><a href="#">Signed in as: <br /><strong><@security.authentication property="principal.fullName"/></strong></a></li>
                    <li><a title="Edit profile" href="<@spring.url "/account/edit"/>">Edit profile</a></li>
                    <li><a title="Change password" href="<@spring.url "/account/email"/>">Change email</a></li>
                    <li><a title="Change password" href="<@spring.url "/account/password"/>">Change password</a></li>
                </ul>
            </li>
            <li>
                <a title="Log out" href="<@spring.url "/j_spring_security_logout"/>">
                    <span class="fa fa-lg fa-sign-out"></span>
                    <span class="collapse-only">Logout</span>
                </a>
            </li>
        </ul>
    </@security.authorize>
    <@security.authorize ifAnyGranted="ROLE_ANONYMOUS">
        <ul class="collapse navbar-collapse nav navbar-nav navbar-right" id="login-nav">
            <li style="width: 280px; text-align: right; padding-right: 50px">
                <a href="#" data-bind="popover: {template: 'login-template', placement: 'bottom', title: 'Log in'}">
                    Log in
                    <span class="fa fa-lg fa-sign-in"></span>
                </a>
            </li>
        </ul>
    </@security.authorize>
</div>
