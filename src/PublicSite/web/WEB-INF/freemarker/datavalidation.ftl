<#--
    The page corresponding to Data Validation tab. It uses the ABRAID-MP template to ensure consistent layout
    (including header and footer) and all content is displayed in an IFrame for compatibility with TGHN.
    Copyright (c) 2014 University of Oxford
-->
<#import "common.ftl" as c/>
<#import "/spring.ftl" as spring />

<@c.page title="ABRAID MP">
<iframe src="<@spring.url "/datavalidationcontent"/>"></iframe>
</@c.page>
