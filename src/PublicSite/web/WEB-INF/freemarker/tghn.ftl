<#--
    Trivial page (containing only the IFrame) to demonstrate how the data validator will appear on TGHN's website.
    Copyright (c) 2014 University of Oxford
-->
<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html class="no-js">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>TGHN</title>

    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">
</head>
<body style="background-color: #E1E1E1; background-image: url(<@spring.url "/static/tghn_bg.png"/>); background-repeat: no-repeat">
    <iframe style="position: absolute; top: 300px; left:280px; border: 1px solid #E1E1E1;" src="<@spring.url "/datavalidation/content"/>" width="950" height="550" frameborder="0"></iframe>
</body>
