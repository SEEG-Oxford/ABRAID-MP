<#--
    PublicSite's index/ landing page, to display model outputs
-->
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<#import "common.ftl" as c/>
<@c.page title="ABRAID MP">

<div class="container">
    <div style="padding:10px">
        <p>This page will present our model outputs and links to download data.</p>
    </div>

    <div class="jumbotron" style="padding:10px">

    <@security.authorize  ifAnyGranted="ROLE_USER">
        <div>All logged in users can see this.<br> </div>
    </@security.authorize>

    <@security.authorize  ifAnyGranted="ROLE_ADMINISTRATOR">
        <div>Only administrators can see this.</div>
    </@security.authorize>

    </div>

    <ul style="padding: 10px">
        <#list countries as country>
            <li>${country.name}</li>
        </#list>
    </ul>
</div>

</@c.page>
