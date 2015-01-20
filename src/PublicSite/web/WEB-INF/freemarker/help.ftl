<#--
    The page containing help text to explain the data validation system to an expert.
    This content will reside in an IFrame, on the Help Modal accessed from within the Data Validation page.
    Copyright (c) 2014 University of Oxford
-->
<#import "layout/common.ftl" as c/>
<#import "/spring.ftl" as spring />
<#assign endOfHeadContent>
    <link rel="stylesheet" href="<@spring.url"/css/help.css"/>">
</#assign>

<@c.minimalPage title="Help" endOfHead=endOfHeadContent>
<#macro panelHeading panel title>
    <a class="collapsed" data-toggle="collapse" href=${panel}>
        <div class="panel-heading-custom">
            <div class="panel-heading">
                <h4 class="panel-title">
                    <strong>${title}</strong>
                    <button class="close expander">
                        <span class="glyphicon glyphicon-chevron-down"></span>
                    </button>
                    <button class="close collapser">
                        <span class="glyphicon glyphicon-chevron-up"></span>
                    </button>
                </h4>
            </div>
        </div>
    </a>
</#macro>

<div class="panel-group">
    <div class="panel">
        <div class="panel-body">
            <p>
                The Data Validator allows disease experts* to contribute to two components of the ABRAID system.
                Experts can validate new disease occurrence data coming into the system, and provide an expert
                opinion on the geographical extent of a disease at a national/state level. Both contributions will
                feed into our mapping process and help ensure the final outputs are valid.
            </p>
            <p>
                * As a disease expert you are either an expert on a particular disease or disease class, or an
                expert on the range of diseases found within a particular geographical region, or a combination of
                both. All expertise in these fields is useful, and you are not expected to validate or comment on
                areas outside your field of expertise.
            </p>
            <p>
                To see this help text again, click the <i class="fa fa-info-circle"></i> button on the Data Validation
                page at any time.
            </p>
        </div>
    </div>
    <div class="panel">
        <@panelHeading panel="#diseaseOccurrencePanel" title="Validating new disease occurrence data points"></@panelHeading>
        <div id="diseaseOccurrencePanel" class="panel-collapse collapse">
            <div class="panel-body">
                <p>
                    The Data Validator only displays points that our system has identified as questionable. The data
                    displayed does not represent the data that goes into our models. These are points that may be
                    incorrectly reported or they may be the result of disease spread. Once you have selected
                    <i>disease occurrences</i> and the disease of your choice using the dropdown boxes in the top
                    left of the screen, you will see a map with purple dots marking any new occurrence data that is
                    waiting to be validated. New data will be displayed for one week.
                </p>
                <p>
                    When you click on one of the dots on the map, information about that data point will appear in
                    the panel on the right. You can also click the links to see the original report (if the original
                    report is still available online) and/or to translate the summary text from this report. When
                    you click on the dot on the map, a short description of the location will also appear on the map.
                    The dot on the map may represent a precise location such as a town, or a wider area such as a
                    district or state. This will be specified in the text description for each dot on the map.
                </p>
                <p><strong><i>
                    We are asking you to validate whether, in your expert opinion, the named disease is a current*
                    infection risk at the place specified.
                </i></strong></p>
                <p>
                    You can do this solely based on your expert knowledge or with reference to the report information
                    in the right-hand side panel. Three buttons are provided in the right-hand panel - <i>valid</i>,
                    <i>unsure</i>, <i>invalid</i> - and the fourth option is to ignore a data point altogether if it
                    is outside your area of expertise. Click <i>valid</i> if, in your expert opinion, the named
                    disease currently* occurs at the place specified, <i>invalid</i> if your opinion is that it does
                    not, and <i>unsure</i> if there is uncertainty in the occurrence of this disease at this place.
                    Once you have validated a point, it will disappear from the map.
                </p>
                <p>
                    * By "currently" we mean that the disease is extant at the location, i.e. it has not been
                    eliminated, even if there are seasons when new cases are not seen.
                </p>
            </div>
        </div>
    </div>
    <div class="panel">
        <@panelHeading panel="#diseaseExtentPanel" title="Providing an expert opinion on the geographical extent of a disease"></@panelHeading>
        <div id="diseaseExtentPanel" class="panel-collapse collapse">
            <div class="panel-body">
                <p>
                    Once you have selected <i>disease extent</i> and the disease of your choice using the dropdown
                    boxes in the top left of the screen, you will see a map showing all regions of the world, each
                    with a disease classification.
                </p>
                <p>
                    Very large countries have been divided into States and all other countries are shown as single
                    nations. The map displayed does not show the smallest islands and enclaves, which are outside
                    the scope of our global models. To accurately reflect the disease extent, non-contiguous parts
                    of a country are shown separately, for example Easter Island is separate from Chile, and Alaska
                    is separate from the USA.
                </p>
                <p>
                    To select an area, either click on the map or click on the name in the table in the right-hand
                    panel. The map will then zoom to the relevant area and the place name will be highlighted in the
                    table. In addition, a pop-up box will display the most recent disease occurrence points for that
                    area if there are any.
                </p>
                <p>
                    <strong><i>
                        We are asking you to provide your expert opinion on whether the disease is present or possibly
                        present or possibly absent or absent, or whether there is uncertainty on disease presence, for
                        each area of the map.
                    </i></strong>
                    Alternatively you can select <i>Don't know</i> if it is outside your area of expertise.
                </p>
                <p>
                    The map that is currently displayed has been informed by 1) incoming data points that have been
                    validated and 2) expert opinions that have been provided previously. Your opinion will feed into
                    the class assigned to the next map produced after one week.
                </p>
            </div>
        </div>
    </div>
</div>
</@c.minimalPage>
