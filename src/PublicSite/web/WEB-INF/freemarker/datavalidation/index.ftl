<#--
    The page corresponding to Data Validation tab. It uses the ABRAID-MP template to ensure consistent layout
    (including header and footer) and all content is displayed in an IFrame for compatibility with TGHN.
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

<@c.page title="ABRAID MP" endOfHead=endOfHeadContent>
<iframe src="<@spring.url "/datavalidation/content"/>"></iframe>
</@c.page>
