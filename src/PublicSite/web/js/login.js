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
                    success: function(data, status, xhr) {
                        if (data.success) {
                            location.reload();
                        } else {
                            $("#message").text(data.message);
                        }
                    },
                    error: function(xhr, status, error) {
                        alert(status + " " + error);
                    }
                });
            } else {
                $("#message").text("Invalid credentials");
            }
            e.preventDefault();
        }
    );
})