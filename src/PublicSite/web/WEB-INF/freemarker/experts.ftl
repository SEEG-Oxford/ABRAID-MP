<#--
    PublicSite's experts listing page.
    Copyright (c) 2014 University of Oxford
-->
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<#import "layout/common.ftl" as c/>
<#import "layout/pagination.ftl" as p/>
<#import "/spring.ftl" as spring />
<@c.page title="ABRAID MP - Experts">
    <div class="container">
        <div class="jumbotron">
            <div>Some text about experts.</div>
        </div>
        <#list page?chunk(2) as row>
            <div class="row">
                <#list row as expert>
                    <div class="col-sm-6">
                        <div style="border-bottom: 2px #eeeeee solid; border-top: 2px #eeeeee solid; padding: 10px ">
                            <h4>${expert.name}</h4>
                            <ul class="fa-ul">
                                <li><i class="text-muted fa-li glyphicon glyphicon-briefcase" style="line-height: 14px; font-size: 10px; top: 0.2em;"></i>${expert.jobTitle}</li>
                                <li><i class="text-muted fa-li glyphicon glyphicon-home" style="line-height: 14px; font-size: 10px; top: 0.2em;"></i>${expert.institution}</li>
                            </ul>
                        </div>
                    </div>
                </#list>
            </div>
        </#list>
        <@p.pages pageNumber=pageNumber pageCount=pageCount url='/experts' />
    </div>
</@c.page>
