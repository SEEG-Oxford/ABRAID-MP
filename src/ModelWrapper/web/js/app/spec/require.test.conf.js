/* Configuration for require.js in test contexts.
 * Copyright (c) 2014 University of Oxford
 */
/*global requirejs:false, require:false, jasmineRequire: false, define:false, baseUrl:false, console:false */
(function() {
    "use strict";

    requirejs.config({
        baseUrl: baseUrl + 'js/',
        paths : {
            'jasmine' : 'https://cdnjs.cloudflare.com/ajax/libs/jasmine/2.0.0/jasmine',
            'jasmine-html' : 'https://cdnjs.cloudflare.com/ajax/libs/jasmine/2.0.0/jasmine-html',
            'jasmine-boot' : 'https://cdnjs.cloudflare.com/ajax/libs/jasmine/2.0.0/boot',
            'jasmine-console' : 'https://cdnjs.cloudflare.com/ajax/libs/jasmine/2.0.0/console'
        },
        shim : {
            'jasmine-boot' : {
                deps : [ 'jasmine', 'jasmine-html', 'jasmine-console' ],
                exports : 'jasmine'
            },
            'jasmine-html' : {
                deps : [ 'jasmine' ]
            },
            'jasmine-console' : {
                deps: [ "jasmine" ]
            }
        }
    });

    require(['jasmine-boot' ], function(jasmine) {
        var ConsoleReporter = jasmineRequire.ConsoleReporter();
        var options = {
            timer: new jasmine.Timer,
            print: function () {
                console.log.apply(console,arguments);
            },
            onComplete: function(state) {
                // Stash this so we can get it elsewhere
                window.exitCode = state ? 0 : 1;
            }
        };

        var consoleReporter = new ConsoleReporter(options);

        // Change the error details to not include a full stack trace
        consoleReporter.specFailureDetails = function (result) {
            print('\n');
            print(result.fullName);

            for (var i = 0; i < result.failedExpectations.length; i++) {
                var failedExpectation = result.failedExpectations[i];
                print(indent(failedExpectation.message, 2));
            }

            print('\n');
        };

        jasmine.getEnv().addReporter(consoleReporter);
    });
}());