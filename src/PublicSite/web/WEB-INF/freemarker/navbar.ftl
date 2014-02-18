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

            <#-- If user not logged in: display login form -->
            <@security.authorize  ifAnyGranted="ROLE_ANONYMOUS">
                <form class="navbar-form navbar-right" action="">
                    <p id="logInMessage"></p>
                    <input type="text" id="username" placeholder="Email address">
                    <input type="password" id="password" placeholder="Password">
                    <input type="submit" id="loginButton" class="btn btn-primary" value="Log in">
                </form>
            </@security.authorize>

            <#-- If user logged in: display welcome and logout button -->
            <@security.authorize ifAnyGranted="ROLE_USER">
                <ul class="nav navbar-nav navbar-right">
                    <li id="hello">${welcomemessage}</li>
                    <li><a href="j_spring_security_logout">Log out</a></li>
                </ul>
            </@security.authorize>

        </div> <!--/.navbar-collapse -->
    </div> <!--/.container -->
</div>