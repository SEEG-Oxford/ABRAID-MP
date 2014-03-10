<#--
    The map page for an expert to submit reviews.
    Copyright (c) 2014 University of Oxford
-->
<#import "common.ftl" as c/>
<@c.page title="ABRAID MP">

    <#include "datavalidationsidepanel.ftl"/>

    <div style="position:absolute; z-index:10; margin-left:50px;">
        <h4>You are validating
            <div class="btn-group">
                <button data-toggle="dropdown" class="btn btn-xs dropdown-toggle transparent-btn"><h4>disease occurrences <span class="caret"></span></h4></button>
                <ul class="dropdown-menu">
                    <li>disease extent</li>
                </ul>
            </div>
        of
            <div class="btn-group">
                <button data-toggle="dropdown" class="btn btn-xs dropdown-toggle transparent-btn"><h4>diseases <span class="caret"></span></h4></button>
                <ul class="dropdown-menu">
                <#list diseaseInterests as disease>
                    <li><a href="#">${disease.name}</a></li>
                </#list>
                </ul>
            </div>
        </h4>
    </div>

    <div id="map" style="position:relative"></div>

</@c.page>