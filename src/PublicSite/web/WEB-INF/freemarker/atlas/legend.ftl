<div class="leaflet-bottom leaflet-left" id="legend">
     <div class="legend leaflet-bar leaflet-control" style="display: none"  data-bind="visible: type()">
        <table>
            <tr>
                <td class="key continuousKey" data-bind="visible: type() == 'mean', preventBubble: true" >
                    <div style="text-align: center">Relative probability of<br>infections occurring<br><br></div>
                    <div style="text-align: center; padding: 0 10px;">
                        <i style="width: 30px; background-color:#769766; background-image: -webkit-gradient(linear, left, right, from(#769766), to(#b5caaa)); background-image: -webkit-linear-gradient(left, #769766, #b5caaa); background-image: -moz-linear-gradient(left, #769766, #b5caaa); background-image: -o-linear-gradient(left, #769766, #b5caaa); background-image: linear-gradient(to right, #769766, #b5caaa);"></i>
                        <i style="width: 30px; background-color:#b5caaa; background-image: -webkit-gradient(linear, left, right, from(#b5caaa), to(#ffffbf)); background-image: -webkit-linear-gradient(left, #b5caaa, #ffffbf); background-image: -moz-linear-gradient(left, #b5caaa, #ffffbf); background-image: -o-linear-gradient(left, #b5caaa, #ffffbf); background-image: linear-gradient(to right, #b5caaa, #ffffbf);"></i>
                        <i style="width: 30px; background-color:#ffffbf; background-image: -webkit-gradient(linear, left, right, from(#ffffbf), to(#c478a9)); background-image: -webkit-linear-gradient(left, #ffffbf, #c478a9); background-image: -moz-linear-gradient(left, #ffffbf, #c478a9); background-image: -o-linear-gradient(left, #ffffbf, #c478a9); background-image: linear-gradient(to right, #ffffbf, #c478a9);"></i>
                        <i style="width: 30px; background-color:#c478a9; background-image: -webkit-gradient(linear, left, right, from(#c478a9), to(#8e1b65)); background-image: -webkit-linear-gradient(left, #c478a9, #8e1b65); background-image: -moz-linear-gradient(left, #c478a9, #8e1b65); background-image: -o-linear-gradient(left, #c478a9, #8e1b65); background-image: linear-gradient(to right, #c478a9, #8e1b65);"></i>
                        <div style="clear: both;"></div>
                    </div>
                    <div>
                        <span style="width: 20px; float: left; text-align: center">0</span>
                        <span style="width: 20px; float: right; text-align: center">1</span>
                        <div style="clear: both;"></div>
                    </div>
                </td>
                <td class="key continuousKey" data-bind="visible: type() == 'uncertainty', preventBubble: true">
                    <div style="text-align: center">Range of 95%<br>confidence interval<br><br></div>
                    <div style="text-align: center; padding: 0 10px;">
                        <i style="width: 120px; background-color:#d4e7f4; background-image: -webkit-gradient(linear, left, right, from(#d4e7f4), to(#5F8098)); background-image: -webkit-linear-gradient(left, #d4e7f4, #5F8098); background-image: -moz-linear-gradient(left, #d4e7f4, #5F8098); background-image: -o-linear-gradient(left, #d4e7f4, #5F8098); background-image: linear-gradient(to right, #d4e7f4, #5F8098);"></i>
                        <div style="clear: both;"></div>
                    </div>
                    <div>
                        <span style="width: 20px; float: left; text-align: center">0</span>
                        <span style="width: 20px; float: right; text-align: center">1</span>
                        <div style="clear: both;"></div>
                    </div>
                </td>
                <td class="key discreteKey" data-bind="visible: type() == 'extent', preventBubble: true">
                    <i style="background:#8e1b65"></i><span>Presence</span><br>
                    <i style="background:#c478a9"></i><span>Possible presence</span><br>
                    <i style="background:#ffffbf"></i><span>Uncertain</span><br>
                    <i style="background:#b5caaa"></i><span>Possible absence</span><br>
                    <i style="background:#769766"></i><span>Absence</span><br>
                </td>
                <td class="key discreteKey" data-bind="visible: type() == 'occurrences', preventBubble: true">
                    <i style="background:#0F2540"></i><span>Less than 12 months old</span><br>
                    <i style="background:#4B6584"></i><span>Less than 34 months old</span><br>
                    <i style="background:#87A5C8"></i><span>Less than 68 months old</span><br>
                    <i style="background:#A6C5EA"></i><span>More than 68 months old</span><br>
                </td>
                <td id="legendExpander">
                    <a role="button" data-toggle="collapse" href="#legendText" aria-expanded="false" aria-controls="collapseExample">
                        <div><div>
                            <i class="glyphicon glyphicon-chevron-right"></i>
                            <i class="glyphicon glyphicon-chevron-left"></i>
                        </div></div>
                    </a>
                </td>
                <td id="legendText" data-bind="preventBubble: true" class="collapse in">
                    <p class="help" data-bind="visible: type() == 'mean'">
                        This map shows the predicted probability of new infections occurring at some time within a year,
                        for every 5km x 5km. These predictions were generated by a pathogen distribution model that
                        used disease occurrence data captured from a range of online sources.
                    </p>
                    <p>Input data range for current map</p>
                    <p data-bind="text: startDate() + '&nbsp;&rarr;&nbsp;' + endDate()"></p>
                </td>
            </tr>
        </table>
    </div>
</div>
