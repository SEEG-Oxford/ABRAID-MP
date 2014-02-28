#!/usr/bin/env bash
echo "================= Full Log ================="
annotate-output +"" ant full 2>&1 | tee build.log
EXIT=${PIPESTATUS[0]}
echo ""
echo "================== Error Summary =================="
cat build.log | grep "^\ E\:"
exit $EXIT