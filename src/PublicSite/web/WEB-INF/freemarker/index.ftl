<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<#import "common.ftl" as c/>
<@c.page title="ABRAID MP">

    <div id="main" class="container">

    <@security.authorize  ifAnyGranted="ROLE_USER">
        <div>All logged in users can see this.<br> </div>
    </@security.authorize>

    <@security.authorize  ifAnyGranted="ROLE_ADMINISTRATOR">
        <div>Only administrators can see this.</div>
    </@security.authorize>

    <ul style="padding: 10px">
        <#list diseases as disease>
            <li>${disease.name}</li>
        </#list>
    </ul>

    </div> <!-- /container -->
</@c.page>
