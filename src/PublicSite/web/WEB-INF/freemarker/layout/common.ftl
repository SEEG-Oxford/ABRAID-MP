<#--
    The page template, including header and footer, adapted for publicsite
    Copyright (c) 2014 University of Oxford
-->
<#macro page title endOfHead="" bootstrapData="" templates="" mainjs="/js/kickstart/default"
        includeNavBar=true includeFooter=true>
    <#import "../shared/layout/common.ftl" as shared/>
    <#import "/spring.ftl" as spring />
    <#assign publicSiteEndOfHeadContent>
        <#if includeNavBar>
            <link rel="stylesheet" href="<@spring.url "/css/login.css" />">
        </#if>
        <link rel="stylesheet" href="<@spring.url "/js/shared/lib/jquery.cookiecuttr.css" />">
        ${endOfHead}
    </#assign>

    <@shared.page title=title endOfHead=publicSiteEndOfHeadContent mainjs=mainjs templates=templates
                  includeNavBar=includeNavBar includeFooter=includeFooter bootstrapData=bootstrapData>
        <#nested/>
    </@shared.page>
</#macro>

<#macro minimalPage title endOfHead="">
    <#assign publicSiteEndOfHeadContent>
        <link rel="stylesheet" href="<@spring.url "/js/shared/lib/jquery.cookiecuttr.css" />">
        ${endOfHead}
    </#assign>
    <@page title=title endOfHead=publicSiteEndOfHeadContent mainjs="/js/kickstart/minimal" includeNavBar=false includeFooter=false>
        <#nested/>
    </@page>
</#macro>