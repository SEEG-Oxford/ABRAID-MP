/**
 * The AJAX call to Spring Security when login button is clicked.
 * Copyright (c) 2014 University of Oxford
 */

$(document).ready(function () {
    $("#loginButton").click(function (e) {
            var username = $("#username").val();
            var password = $("#password").val();

            if (username && username !== "" && password && password !== "") {
                $.ajax({
                    url: "j_spring_security_check",
                    type: "POST",
                    data: {j_username:username, j_password:password},
                    dataType: "json",
                    success: ajaxSuccess,
                    error: ajaxError
                });
            } else {
                inputError();
            }
            e.preventDefault();
        }
    );

    function ajaxSuccess(data, status, xhr) {
        // boolean success in JSON returned from authentication handler informs whether log in was successful
        if (data.success) {
            // Refresh page to change security section displayed in navbar.ftl
            location.reload();
        } else {
            // Display authentication error message to user and clear input fields
            $("#logInMessage").text(data.message);
        }
    }

    function ajaxError(xhr, status, error) {
        alert(status + " " + error);
    }

    function inputError() {
        $("#logInMessage").text("Enter username and/or password");
    }
})