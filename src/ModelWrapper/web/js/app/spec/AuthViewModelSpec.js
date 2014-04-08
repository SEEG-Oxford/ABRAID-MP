/* A suite of tests for the AuthViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false, describe:false, it:false, expect:false, beforeEach:false*/
define([ 'app/AuthViewModel', "jasmine" ], function(AuthViewModel) {
    "use strict";

    describe("The auth view model", function() {
        var vm = {};
        beforeEach(function() {
            vm = new AuthViewModel();
        });

        it("starts with an empty username", function() {
            expect(vm.username()).toBeUndefined();
        });
    });
});