#!/usr/bin/env bash
echo -en 'travis_fold:start:Log\r' # Causes travis to create a folding group in the stdout output with the title "Log"
echo "================= Full Log ================="
annotate-output +"" ant full 2>&1 | tee build.log
EXIT=${PIPESTATUS[0]}
echo -en 'travis_fold:end:Log\r' # End of "Log" fold group
if [ $EXIT -ne 0 ]
then
    echo "================== Summary =================="
    echo "Build exited with code: $EXIT"
    grep -E "(^\ E\:)|(^\ O\:\ \[echo\]\ \[Summary\])" build.log
fi
exit $EXIT