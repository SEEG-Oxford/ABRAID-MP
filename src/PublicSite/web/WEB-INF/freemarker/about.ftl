<#import "shared/layout/common.ftl" as c/>
<#import "/spring.ftl" as spring />
<#macro linkTemplate newTab link icon>
    <span class="fa-stack fa-lg">
        <a <#if newTab>target="_blank"</#if> href="${link}">
            <i class="fa fa-circle fa-stack-2x"></i><i class="fa ${icon} fa-stack-1x fa-inverse"></i>
        </a>
    </span>
</#macro>
<@c.page title="ABRAID MP - About" mainjs="/js/kickstart/default">
    <div id="about" class="container">
        <div class="row">
            <div id="about-img">
                <img src="<@spring.url '/static/logo.jpg'/>" alt="ABRAID Logo">
                <div style="text-align: center"><strong>v${applicationVersion}</strong></div>
            </div>
            <div id="about-text">
                <div>The Atlas of Baseline Risk Assessment for Infectious Diseases (ABRAID) aims to produce continually updated maps of disease risk. The online ABRAID prototype was established in 2014 by Professor Simon Hay and Dr Catherine Moyes of the <a target="_blank" href="http://seeg.zoo.ox.ac.uk/">Spatial Ecology & Epidemiology Group</a> (SEEG) at the University of Oxford with funding from the <a target="_blank" href="http://www.gatesfoundation.org/">Bill & Melinda Gates Foundation</a> (B&amp;MGF). The current system uses data provided by <a target="_blank" href="http://healthmap.org/en/">HealthMap</a> and datasets collated by SEEG.</div>
                <div>The system relies on disease experts with knowledge of specific diseases and/or knowledge of the diseases found at specific locations. If you are interested in contributing to ABRAID, please <a href="<@spring.url "/register/account"/>">register for an account</a> and click on the <a href="<@spring.url "/datavalidation"/>">Data Validator</a> tab.</div>
                <div>We can be contacted at <a href="mailto:abraid@zoo.ox.ac.uk">abraid@zoo.ox.ac.uk</a>.</div>
            </div>
        </div>
        <div>
            <div id="contact-links">
                <@linkTemplate newTab=false link="mailto:abraid@zoo.ox.ac.uk" icon="fa-envelope-o"></@linkTemplate>
                <@linkTemplate newTab=true link="https://www.facebook.com/pages/Atlas-of-Disease-Risk/391369784359235?sk=timeline" icon="fa-facebook"></@linkTemplate>
                <@linkTemplate newTab=true link="https://twitter.com/abraid_group" icon="fa-twitter"></@linkTemplate>
                <@linkTemplate newTab=true link="https://github.com/SEEG-Oxford/ABRAID-MP" icon="fa-github"></@linkTemplate>
            </div>
        </div>
    </div>
</@c.page>
