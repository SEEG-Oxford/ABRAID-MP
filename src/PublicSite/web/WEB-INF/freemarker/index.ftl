<#import "common.ftl" as c/>
<@c.page title="ABRAID MP">

    <div id="main" class="container">

        <div id="message" class="message">Original Message</div>

        <ul style="padding: 10px">
            <#list diseases as disease>
                <li>${disease.name}</li>
            </#list>
        </ul>

    </div> <!-- /container -->
</@c.page>
