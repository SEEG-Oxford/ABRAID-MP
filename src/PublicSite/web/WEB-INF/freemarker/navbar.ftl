<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />

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
                <li class="active"><a href=""> Atlas</a> </li>
                <li><a href=""> Data Validation</a> </li>
                <li><a href=""> About</a> </li>
                <li><a href=""> Publications</a> </li>
            </ul>

            <#-- Anonymous -->
            <@security.authorize  ifAnyGranted="ROLE_ADMINISTRATOR">
                <form class="navbar-form navbar-right" action="/index" method="post">
                    <input type="text" name="email" placeholder="Email address">
                    <input type="password" name="password" placeholder="Password">
                    <button type="submit" class="btn btn-primary">Log in</button>
                </form>
            </@security.authorize>

            <#-- Logged in -->
            <@security.authorize ifAnyGranted="ROLE_USER">
                <ul class="nav navbar-nav navbar-right">
                    <li id="hello">Hello ${expertname}</li>
                    <li><a href="publicsite/j_spring_security_logout">Log out</a></li>
                </ul>
            </@security.authorize>

        </div> <!--/.navbar-collapse -->
    </div> <!--/.container -->
</div>