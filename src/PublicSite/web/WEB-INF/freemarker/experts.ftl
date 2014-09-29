<#--
    PublicSite's experts listing page.
    Copyright (c) 2014 University of Oxford
-->
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<#import "layout/common.ftl" as c/>
<#import "layout/pagination.ftl" as p/>
<#import "/spring.ftl" as spring />
<#assign css>
    <style>
        .experts-list > .row > div > div {
            border-bottom: 2px #eeeeee solid;
            border-top: 2px #eeeeee solid;
            padding: 10px;
            margin-bottom: 5px;
        }
        .experts-list > .row > div > div > div {
            display:table-row;

        }
        .experts-list > .row > div > div > div > div {
            display:table-cell;
        }
        .experts-list ul li i {
            line-height: 14px;
            font-size: 10px;
            top: 0.24em;
        }
        .experts-list ul li span {
            margin-left: 6px;
            display: inline-block;
        }
        .experts-list .fixed-width {
            width: 70px;
        }
        .experts-list .full-width {
            width: 100%;
        }
    </style>
</#assign>
<@c.page title="ABRAID MP - Experts" endOfHead=css mainjs="/js/kickstart/default">
    <div class="container">
        <div class="experts-list">
            <#list page?chunk(2) as row>
                <div class="row">
                    <#list row as expert>
                        <div class="col-sm-6">
                            <div>
                                <div class="full-width">
                                    <div class="full-width">
                                    <h4>${expert.name}</h4>
                                        <ul class="fa-ul" >
                                            <li><i class="text-muted fa-li glyphicon glyphicon-briefcase"></i><span class="fixed-width">Job Title:&nbsp;</span><span>${expert.jobTitle}</span></li>
                                            <li><i class="text-muted fa-li glyphicon glyphicon-home"></i><span class="fixed-width">Institution:&nbsp;</span><span>${expert.institution}</span></li>
                                            <li><i class="text-muted fa-li glyphicon glyphicon-heart"></i><span class="fixed-width">Joined:&nbsp;</span><span>${expert.createdDate.toString("MMMM dd yyyy")}</span></li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </#list>
                </div>
            </#list>
        </div>
        <@p.pages pageNumber=pageNumber pageCount=pageCount url='/experts' />
    </div>
</@c.page>
