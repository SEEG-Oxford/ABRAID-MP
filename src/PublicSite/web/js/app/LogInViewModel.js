/**
 * The AJAX call to Spring Security when login button is clicked.
 * Copyright (c) 2014 University of Oxford
 */
/*global window:false*/
define([
    "ko",
    "jquery"
], function (ko, $) {
    "use strict";

    return function (baseUrl) {
        var self = this;
        self.formUsername = ko.observable("");
        self.formPassword = ko.observable("");
        self.formAlert = ko.observable(" ");
        self.submit = function () {
            self.formAlert(" ");
            if (self.formUsername() !== "" && self.formPassword() !== "") {
                $.post(baseUrl + "j_spring_security_check",
                    {
                        "j_username": self.formUsername(),
                        "j_password": self.formPassword()
                    })
                    .done(function () {
                        // Status 2xx
                        window.top.location.reload();
                    })
                    .fail(function (xhr) {
                        // Status Unauthorized 401 - Display authentication error message to user: eg Bad Credentials
                        self.formAlert(xhr.responseText);
                    });
            } else {
                self.formAlert("Enter username and/or password");
            }
        };
    };
});
