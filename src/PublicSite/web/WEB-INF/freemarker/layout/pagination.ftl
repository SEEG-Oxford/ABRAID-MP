<#--
    foo.
    Copyright (c) 2014 University of Oxford
-->
<#import "/spring.ftl" as spring />
<#macro pages pageNumber pageCount url>
    <#if (pageCount <= 5)>
        <#assign min = 1>
        <#assign max = pageCount>
    <#elseif (pageNumber < 3)>
        <#assign min = 1>
        <#assign max = 5>
    <#elseif (pageNumber > pageCount - 2)>
        <#assign min = pageCount - 4>
        <#assign max = pageCount>
    <#else>
        <#assign min = pageNumber - 2>
        <#assign max = pageNumber + 2>
    </#if>
    <div class="text-center pagination-holder">
        <div class="text-muted">Page ${pageNumber} of ${pageCount}</div>
        <ul class="pagination">
            <#-- first page link -->
            <#if (pageNumber == 1)>
                <li class="disabled"><a href="#"><span class="fa fa-angle-double-left"></span></a></li>
            <#else>
                <li><a href="<@spring.url '${url}?page=1' />"><span class="fa fa-angle-double-left"></span></a></li>
            </#if>

            <#-- page list -->
            <#list min..max as i>
                <#if (pageNumber == i)>
                    <li class="active"><a href="#">${i} <span class="sr-only">(current)</span></a></li>
                <#else>
                    <li><a href="<@spring.url '${url}?page=${i}' />">${i}</a></li>
                </#if>
            </#list>

            <#-- Last page link -->
            <#if (pageNumber == pageCount)>
                <li class="disabled"><a href="#"><span class="fa fa-angle-double-right"></span></a></li>
            <#else>
                <li><a href="<@spring.url '${url}?page=${pageCount}' />"><span class="fa fa-angle-double-right"></span></a></li>
            </#if>
        </ul>
    </div>
</#macro>