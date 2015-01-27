<div class="leaflet-bottom leaflet-left" id="legend">
    <div class="legend leaflet-bar leaflet-control" style="display: none" data-bind="visible: type() == 'mean' || type() == 'uncertainty'">
        <div id="continuousKey">
            <div data-bind="visible: type() == 'mean'" style="text-align: center">Probability of<br>infections occurring<br><br></div>
            <div data-bind="visible: type() == 'uncertainty'" style="text-align: center">Range of 95%<br>confidence interval<br><br></div>
            <i style="background-color:#769766;"></i>
            <i style="background-color:#769766; background-image: -webkit-gradient(linear, left, right, from(#769766), to(#b5caaa)); background-image: -webkit-linear-gradient(left, #769766, #b5caaa); background-image: -moz-linear-gradient(left, #769766, #b5caaa); background-image: -o-linear-gradient(left, #769766, #b5caaa); background-image: linear-gradient(to right, #769766, #b5caaa);"></i>
            <i style="background-color:#b5caaa; background-image: -webkit-gradient(linear, left, right, from(#b5caaa), to(#ffffbf)); background-image: -webkit-linear-gradient(left, #b5caaa, #ffffbf); background-image: -moz-linear-gradient(left, #b5caaa, #ffffbf); background-image: -o-linear-gradient(left, #b5caaa, #ffffbf); background-image: linear-gradient(to right, #b5caaa, #ffffbf);"></i>
            <i style="background-color:#ffffbf;"></i>
            <i style="background-color:#ffffbf; background-image: -webkit-gradient(linear, left, right, from(#ffffbf), to(#c478a9)); background-image: -webkit-linear-gradient(left, #ffffbf, #c478a9); background-image: -moz-linear-gradient(left, #ffffbf, #c478a9); background-image: -o-linear-gradient(left, #ffffbf, #c478a9); background-image: linear-gradient(to right, #ffffbf, #c478a9);"></i>
            <i style="background-color:#c478a9; background-image: -webkit-gradient(linear, left, right, from(#c478a9), to(#8e1b65)); background-image: -webkit-linear-gradient(left, #c478a9, #8e1b65); background-image: -moz-linear-gradient(left, #c478a9, #8e1b65); background-image: -o-linear-gradient(left, #c478a9, #8e1b65); background-image: linear-gradient(to right, #c478a9, #8e1b65);"></i>
            <i style="background-color:#8e1b65;"></i><br>
            <span style="width: 50%; float: left">0</span><span style="width: 50%; float: right; text-align: right">1</span>
        </div>
        <div id="legendText" data-bind="visible: type() == 'mean'">
            This map shows the predicted probability of infections occurring at some time within a year,
            for every 5km x 5km. These predictions were generated by a niche model that used disease
            occurrence/presence data captured from a range of online sources.
        </div>
    </div>
    <div class="legend leaflet-bar leaflet-control" style="display: none" data-bind="visible: type() == 'extent'">
        <i style="background:#8e1b65"></i><span>Presence</span><br>
        <i style="background:#c478a9"></i><span>Possible presence</span><br>
        <i style="background:#ffffbf"></i><span>Uncertain</span><br>
        <i style="background:#b5caaa"></i><span>Possible absence</span><br>
        <i style="background:#769766"></i><span>Absence</span><br>
    </div>
</div>