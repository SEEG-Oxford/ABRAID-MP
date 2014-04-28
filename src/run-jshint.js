/* A custom phantomjs jasmine tests runner.
 * Adapted from the phantom js example . Updated for jasmine 2.0, using jasmine built-in console logger & using a global var to avoid dependence of css class names.
 * https://github.com/ariya/phantomjs/blob/master/examples/run-jasmine.js
 */
/*global phantom:false, require:false, console:false */
(function(p) {
    "use strict";

    // PhantomJS doesn't support bind yet
    Function.prototype.bind = Function.prototype.bind || function (thisp) {
        var fn = this;
        return function () {
            return fn.apply(thisp, arguments);
        };
    };

    var fs = require('fs');
    var jshintfile = p.args[0];
    var jsdir = p.args[1];
    var include_pattern = p.args[2];
    var exclude_pattern = p.args[3];
    var hint_options_file = p.args[4];

    if (!p.injectJs(jshintfile)) {
        console.log('Could not inject jshint.');
        p.exit(true);
    }

    var scanDirectory = function (path, pattern, inverse_pattern, action) {
        var matcher = new RegExp(pattern);
        var inverse_matcher = new RegExp(inverse_pattern);
        if (fs.exists(path) && fs.isFile(path)) {
            if (matcher.test(path) && !inverse_matcher.test(path)) {
                action(path);
            }
        } else if (fs.isDirectory(path)) {
            fs.list(path).forEach(function (fileOrDir) {
                if ( fileOrDir !== "." && fileOrDir !== ".." ) {
                    scanDirectory(path + '/' + fileOrDir, pattern, inverse_pattern, action);
                }
            });
        }
    };

    var result = false;
    var runHint = function (options_file, basedir) {
        if (!fs.isReadable(options_file)) {
            console.log('Unreadable file !: ' + options_file);
            p.exit(true);
        }

        var options_json = fs.read(options_file);
        options_json = options_json.replace(/\/\/(.*)/g, ""); // Remove comments
        options_json = options_json.replace(/\w*$/g, ""); // Remove trailing space
        options_json = options_json.replace(/^\s*[\r\n]/gm, ""); // Remove empty lines
        var options = JSON.parse(options_json);

        return function (jsfile) {
            if (!fs.isReadable(jsdir)) {
                console.log('Unreadable file: ' + jsfile);
                p.exit(true);
            }

            JSHINT(fs.read(jsfile), options);

            if (JSHINT.errors.length > 0) {
                JSHINT.errors.forEach(function(err) {
                    console.log("[JSHint] " + jsfile.replace(basedir, ".") + ':' + err.line + ' [' + err.character + '] ' + err.reason);
                });

                result = true;
            }
        };
    };

    scanDirectory(jsdir, include_pattern, exclude_pattern, runHint(hint_options_file, jsdir));
    p.exit(result);
}(phantom));