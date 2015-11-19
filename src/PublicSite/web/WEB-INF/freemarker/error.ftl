<#--
    ABRAID error page.
    Copyright (c) 2015 University of Oxford
-->
<#import "layout/common.ftl" as c/>
<#import "/spring.ftl" as spring />
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<#assign show_bars=(uri != "/atlas/content" && uri != "/datavalidation/content")>
<@c.page title="ABRAID MP - Error (${status})" mainjs="/js/kickstart/default" includeFooter=show_bars includeNavBar=show_bars>
<div class="container">
    <div class="jumbotron">
        <h1>Error (${status}) ${uri}</h1>
        <hr/>
        <#switch status>
            <#case 403>
                <p>Sorry, you do not seem to be authorized to access this content.</p>
                <@security.authorize ifAnyGranted="ROLE_ANONYMOUS">
                    <p>Perhaps you've been unexpectedly logged out? Try logging in again.</p>
                </@security.authorize>
                <@security.authorize ifAnyGranted="ROLE_USER">
                    <p>If you believe this is incorrect, please get in touch.</p>
                </@security.authorize>
            <#break>
            <#case 404>
                <p>Sorry, the page you are looking for could not be found.</p>
                <p>You might want to try going to our front page, or going back to your previous page.</p>
            <#break>
            <#default>
                <p>Oh, no! An unexpected error occurred.</p>
                <p>Refresh the page and try again. We track these errors, but if the problem persists, get in touch.</p>
            <#break>
        </#switch>
        <p><em>We apologize for any inconvenience.</em></p>
        <hr/>
        <p>
            <a class="btn btn-default" href="<@spring.url "${uri}"/>">Try this page again</a>
            <a class="btn btn-default" href="mailto:abraid@zoo.ox.ac.uk">Email us</a>
            <a class="btn btn-default" href="<@spring.url "/"/>">Go to the front page</a>
        </p>
        <span style="font-size: 10px">Page loaded at: <b>${.now?string["yyyy-MM-dd HH:mm:ss"]}</b></span>
    </div>
</div>
</@c.page>
