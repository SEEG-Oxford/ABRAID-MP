#!/usr/bin/env bash
echo -en 'travis_fold:start:Log\\r'
echo "================= Full Log ================="
annotate-output +"" ant full 2>&1 | tee build.log
EXIT=${PIPESTATUS[0]}
echo -en 'travis_fold:end:Log\\r'
if cat build.log | grep "^\ E\:" ; then
    echo "================== Error Summary =================="
    cat build.log | grep "^\ E\:"
fi
exit $EXIT