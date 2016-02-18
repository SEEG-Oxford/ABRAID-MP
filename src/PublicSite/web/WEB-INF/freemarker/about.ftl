<#import "layout/common.ftl" as c/>
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
                <div><strong>v${applicationVersion?html}</strong></div>
            </div>
            <div id="about-text">
                <p>The Atlas of Baseline Risk Assessment for Infectious Diseases (ABRAID) aims to produce continually updated maps of disease risk. The online ABRAID prototype was established in 2014 by Professor Simon Hay and Dr Catherine Moyes of the <a target="_blank" href="http://seeg.zoo.ox.ac.uk/">Spatial Ecology &amp; Epidemiology Group</a> (SEEG) at the University of Oxford with funding from the <a target="_blank" href="http://www.gatesfoundation.org/">Bill &amp; Melinda Gates Foundation</a> (B&amp;MGF). The current system, built by <a target="_blank" href="https://tessella.com/">Tessella</a> in collaboration with SEEG, uses data provided by <a target="_blank" href="http://healthmap.org/en/">HealthMap</a> and datasets collated by SEEG.</p>
                <p>The system relies on disease experts who have knowledge of specific diseases and/or knowledge of the diseases found at specific locations to validate a subset of the data coming in. If you are interested in contributing to ABRAID, please <a href="<@spring.url "/register/account"/>">register for an account</a> and click on the <a href="<@spring.url "/datavalidation"/>">Data Validation</a> tab. You can find a video that demonstrates the expert validation of disease data on our <a target="_blank" href="https://www.youtube.com/watch?v=baZXKaSxI4M">YouTube channel</a>. Validated data is then used to train a machine learning process that checks all incoming data before it is used.</p>
                <p>We can be contacted at <a href="mailto:abraid@zoo.ox.ac.uk">abraid@zoo.ox.ac.uk</a>.</p>
                <div id="contact-links">
                    <@linkTemplate newTab=false link="mailto:abraid@zoo.ox.ac.uk" icon="fa-envelope-o"></@linkTemplate>
                    <@linkTemplate newTab=true link="https://www.facebook.com/pages/Atlas-of-Disease-Risk/391369784359235?sk=timeline" icon="fa-facebook"></@linkTemplate>
                    <@linkTemplate newTab=true link="https://twitter.com/abraid_group" icon="fa-twitter"></@linkTemplate>
                    <@linkTemplate newTab=true link="https://github.com/SEEG-Oxford/ABRAID-MP" icon="fa-github"></@linkTemplate>
                    <@linkTemplate newTab=true link="https://www.youtube.com/channel/UC-1ddnjzVGgZoKE_2mQ0LSQ" icon="fa-youtube-play"></@linkTemplate>
                </div>
            </div>
        </div>
        <div class="row">
            <div id="about-text">
                <h4>Related Publications</h4>
                <ul>
                    <li>Patching, H.M.M., Hudson, L.M., Cooke, W., Garcia, A.J., Hay, S.I., Roberts, M. and Moyes, C.L. (2015). <b>A supervised learning process to validate online disease reports for use in predictive models.</b> <i>Big Data</i>, <b>3:4</b> : 230-237. <a target="_blank" href="http://dx.doi.org/10.1089/big.2015.0019">DOI</a></li>
                    <li>Hay, S.I., George, D.B., Moyes, C.L. and Brownstein, J.S. (2013). <b>Big data opportunities for global infectious disease surveillance.</b> <i>Public Library of Science Medicine</i>, <b>10</b> (4) : e1001413. <a target="_blank" href="http://dx.doi.org/10.1371/journal.pmed.1001413">DOI</a></li>
                    <li>Pigott, D.M., Howes, R.E., Wiebe, A., Battle, K.E., Golding, N., Gething, P.W., Dowell, S.F., Farag, T.H., Garcia, A.J., Kimball, A.M., Krause, K.L., Smith, C.H., Brooker, S.J., Kyu, H.H., Vos, T., Murray, C.J.L., Moyes, C.L. and Hay, S.I. (2015). <b>Prioritising infectious disease mapping.</b> <i>Public Library of Science Neglected Tropical Diseases</i>, <b>9</b> (6) : e0003756. <a target="_blank" href="http://dx.doi.org/10.1371/journal.pntd.0003756">DOI</a></li>
                    <li>Bhatt, S., Gething, P.W., Brady, O.J., Messina, J.P., Farlow, A.W., Moyes, C.L, Drake J.M., Brownstein, J.S., Hoen, A.G., Sankoh, O., Myers, M.F., George, D.B., Jaenisch, T., Wint, G.R.W., Simmons, C.P., Scott, T.W., Farrar, J.J. and Hay, S.I. (2013). <b>The global distribution and burden of dengue.</b> <i>Nature</i>, <b>496</b> (7446) : 504-507. <a target="_blank" href="http://dx.doi.org/10.1038/nature12060">DOI</a></li>
                    <li>Pigott, D.M., Bhatt, S., Golding, N., Duda, K.A, Battle, K.E., Brady, O.J., Messina, J.P., Balard, Y., Bastien, P., Pratlong, F., Brownstein, J.S., Freifeld, C.C., Mekaru. S.R., Gething, P.W., George, D.B., Myers, M.F., Reithinger, R. and Hay, S.I. (2014). <b>Global distribution maps of the Leishmaniases.</b> <i>eLife</i>, <b>3</b> : e02851. <a target="_blank" href="http://dx.doi.org/10.7554/eLife.02851">DOI</a></li>
                    <li>Messina, J.P., Pigott, D.M., Golding, N., Duda, K.A., Brownstein, J.S., Weiss, D.J., Gibson, H., Robinson, T.P., Gilbert, M., Wint, G.R.W., Nuttall, P.A., Gething, P.W., Myers, M.F., George, D.B. and Hay, S.I. (2015). <b>The global distribution of Crimean-Congo hemorrhagic fever.</b> <i>Transactions of the Royal Society of Tropical Medicine and Hygiene</i>, <b>109</b> (8) : 503-513. <a target="_blank" href="http://dx.doi.org/10.1093/trstmh/trv050">DOI</a></li>
                    <li>Limmathurotsakul, D., Golding, N., Dance, D.A.B., Messina, J.P., Pigott, D.M., Moyes, C.L., Rolim, D.B., Bertherat, E., Day, N.P.J., Peacock, S.J. and Hay, S.I. (2015). <b>Predicted global distribution of Burkholderia pseudomallei and burden of melioidosis.</b> <i>Nature Microbiology</i>, <b>1</b> : 15008. <a target="_blank" href="http://dx.doi.org/10.1038/nmicrobiol.2015.8">DOI</a></li>
                </ul>
            </div>
        </div>
    </div>
</@c.page>
