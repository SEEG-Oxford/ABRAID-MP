/* An AMD to bundle up knockout + the extensions we use so that they can all be required in one statement.
 * Note: Can't be an in-line define due to use of Squire in tests (based on requirejs multi-context).
 * Copyright (c) 2014 University of Oxford
 */
/*global define:false*/
define("ko", ["knockout", "knockout.bootstrap", "knockout.validation", "app/KoCustomRules"], function(ko) {
    "use strict";

    // Bundle up all the knockout stuff
    return ko;
});