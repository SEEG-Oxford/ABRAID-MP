$(document).ready(function() {
    $("#loginButton").click(function(e) {
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
                inputError;
            }
            e.preventDefault();
        }
    );

    function ajaxSuccess(data, status, xhr) {
        if (data.success) {
            location.reload();
        } else {
            $("#logInMessage").text(data.message);
        }
    }

    function ajaxError(xhr, status, error) {
        alert(status + " " + error);
    }

    function inputError() {
        $("#logInMessage").text("Invalid credentials");
    }
})