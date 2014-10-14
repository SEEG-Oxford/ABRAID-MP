<#--
    PublicSite's index/ landing page, to display model outputs.
    Copyright (c) 2014 University of Oxford
-->
<#import "../layout/common.ftl" as c/>
<#import "/spring.ftl" as spring />
<#assign endOfHeadContent>
<style>
    iframe {
        height: 100%;
        width: 100%;
        border: 0;
        overflow: hidden;
    }
    #common {
        position: absolute;
        top:50px;
        bottom: 30px;
        width: 100%;
        overflow: hidden;
    }
</style>
</#assign>

<@c.page title="ABRAID MP" endOfHead=endOfHeadContent mainjs="/js/kickstart/default">
<iframe src="<@spring.url "/atlas/content"/>"></iframe>
</@c.page>
