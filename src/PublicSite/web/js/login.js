/**
 * The AJAX call to Spring Security when login button is clicked.
 * Copyright (c) 2014 University of Oxford
 */
'use strict';

(function () {
    var logInViewModel = (function () {
        var formUsername = ko.observable("");
        var formPassword = ko.observable("");
        var formAlert = ko.observable("");
        var attemptFormLogin = function() {
            formAlert("");
            if (formUsername() !== "" && formPassword() !== "") {
                $.post(baseUrl + "j_spring_security_check", {j_username: formUsername() , j_password: formPassword()})
                    .done(function () {
                        // Status 2xx
                        location.reload();
                    })
                    .fail(function (xhr) {
                        // Status Unauthorized 401 - Display authentication error message to user: eg Bad Credentials
                        formAlert(xhr.responseText);
                    });
            } else {
                formAlert("Enter username and/or password");
            }
        }

        return {
            formUsername: formUsername,
            formPassword: formPassword,
            formAlert: formAlert,
            attemptFormLogin: attemptFormLogin
        };
    }());

    $(document).ready(function () {
        ko.applyBindings(logInViewModel, $("#logIn")[0]);
    });
}());

