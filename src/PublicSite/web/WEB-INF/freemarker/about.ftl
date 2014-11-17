<#import "shared/layout/common.ftl" as c/>
<#import "/spring.ftl" as spring />
<@c.page title="ABRAID MP - About" mainjs="/js/kickstart/default">
    <div id="about" class="container">
        <div class="row">
            <div class="col-sm-2">
                <img style="margin-top: 55px" src="<@spring.url '/static/logo.jpg'/>" alt="ABRAID Logo">
            </div>
            <div class="col-sm-10">
                <div>The Atlas of Baseline Risk Assessment for Infectious Diseases (ABRAID) aims to produce continually updated maps of disease risk. The online ABRAID prototype was established in 2014 by Professor Simon Hay and Dr Catherine Moyes of the <a target="_blank" href="http://seeg.zoo.ox.ac.uk/">Spatial Ecology & Epidemiology Group</a> (SEEG) at the University of Oxford with funding from the <a target="_blank" href="http://www.gatesfoundation.org/">Bill & Melinda Gates Foundation</a> (BGMF). The current system uses data provided by <a target="_blank" href="http://healthmap.org/en/">HealthMap</a> and datasets collated by SEEG.</div>
                <div>The system relies on disease experts with knowledge of specific diseases and/or knowledge of the diseases found at specific locations. If you are interested in contributing to ABRAID, please <a href="<@spring.url "/register/account"/>">register for an account</a> and click on the <a href="<@spring.url "/datavalidation"/>">Data Validator</a> tab.</div>
                <div>View the project on <a target="_blank" href="https://github.com/SEEG-Oxford/ABRAID-MP">GitHub</a> or contact us at <a href="mailto:abraid@zoo.ox.ac.uk">abraid@zoo.ox.ac.uk</a>.</div>
            </div>
        </div>
    </div>
</@c.page>
