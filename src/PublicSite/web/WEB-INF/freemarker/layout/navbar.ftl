<#--
    Page header with links between pages.
    Currently holds log in form, but this will be replaced by ABRAID logo.
    Copyright (c) 2014 University of Oxford
-->
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
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
            <a class="navbar-brand" href="#">ABRAID-MP</a>
        </div>
        <div class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li><a href="<@spring.url "/"/>">Atlas</a></li>
                <li><a href="<@spring.url "/datavalidation"/>">Data Validation</a></li>
                <li><a href="<@spring.url "/experts"/>">Contributors</a></li>
                <li><a href="#">About</a></li>
                <li><a href="#">Publications</a></li>
                <@security.authorize ifAnyGranted="ROLE_ADMIN">
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Administration <span class="caret"></span></a>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="<@spring.url "/admin/diseasegroups/"/>">Disease Groups</a></li>
                            <li><a href="<@spring.url "/admin/experts/"/>">Experts</a></li>
                        </ul>
                    </li>
                </@security.authorize>
            </ul>
            <@security.authorize ifAnyGranted="ROLE_USER">
                <ul class="nav navbar-nav navbar-right">
                    <li><span class="text-muted"><@security.authentication property="principal.fullName"/></span></li>
                    <li><a title="Edit profile" href="<@spring.url "/account/edit"/>"><span class="fa fa-lg fa-gear"></span></a></li>
                    <li><a title="Log out" href="<@spring.url "/j_spring_security_logout"/>"><span class="fa fa-lg fa-sign-out"></span></a></li>
                </ul>
            </@security.authorize>
        </div>
    </div>
</div>