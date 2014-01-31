<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">ABRAID MP</a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <#list navBarItems?keys as key>
                    <li class=${(activeTab == key) ? string("active","")}> <a href="/site/hello${key}"> ${navBarItems[key].getTitle()} </a></li>
                </#list>
            </ul>
        </div> <!--/.navbar-collapse -->
    </div> <!--/.container -->
</div>