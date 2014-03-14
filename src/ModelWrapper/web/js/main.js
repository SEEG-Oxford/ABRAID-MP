/**
 * JS file for the model wrapper.
 * Copyright (c) 2014 University of Oxford
 */
(function (document, baseURL, ko, $) {
    ko.validation.rules.areSame = {
        getValue: function (o) {
            return (typeof o === 'function' ? o() : o);
        },
        validator: function (val, otherField) {
            return val === this.getValue(otherField);
        },
        message: 'The fields must have the same value'
    };

    ko.validation.configure({
        insertMessages: true,
        messageTemplate: 'validation-template',
        messagesOnModified: true,
        registerExtenders: true
    });


    var authViewModel = ko.validatedObservable((function () {
        var username = ko.observable().extend({ required: true });
        var password = ko.observable().extend({ required: true });
        var passwordDuplicate = ko.observable().extend({ required: true, areSame: { params: password, message: "Password fields must match." }});
        var saving = ko.observable(false);
        var notices = ko.observableArray();
        var submit = function () {
            notices.removeAll();
            if (this.isValid()) {
                this.saving(true);
                this.notices.removeAll();
                $.post(baseURL + "auth", { username: this.username(), password: this.password() })
                    .done(function () { notices.push({ 'message': "Saved successfully.", 'priority': 'success'}); })
                    .fail(function () { notices.push({ 'message': "Authentication details could not be saved.", 'priority': 'warning'}); })
                    .always(function () { saving(false); });
            } else {
                this.notices.push({ message: "All field must be valid before saving.", priority: 'warning'});
            }
        };

        return {
            username: username,
            password: password,
            passwordDuplicate: passwordDuplicate,
            saving: saving,
            notices: notices,
            submit: submit
        };
    }()));

    ko.applyBindings(authViewModel, document.getElementById("auth-body"));
}(document, baseURL, ko, $));