#!/usr/bin/env bash
#   A project build script for use with travis-ci.org
#   Copyright (c) 2014 University of Oxford

# Cause travis to create a folding group in the stdout output with the title "Log"
echo -en 'travis_fold:start:Log\r'

echo "================= Full Log ================="
# Check if "annotate-output" is available
if command -v annotate-output >/dev/null 2>&1
then
    # Run "ant full" with an annotated log file
    annotate-output +"" ant full -k 2>&1 | tee build.log
else
    # Fallback. Run "ant full" with log file. (i.e. not on travis).
    # Note: stderr lines will not be aggregated into the summary.
    ant full -k 2>&1 | tee build.log
fi
# Get the exit code of "ant full"
EXIT=${PIPESTATUS[0]}

# End of "Log" fold group
echo -en 'travis_fold:end:Log\r'

# Define a set of patterns to match in the build log for inclusion the summary output
# 1. Lines marked as error output, 2. Lines marked as summary output, 3. Findbugs output, 4. Checkstyle output, 5. JUnit warnings
INCLUDE_PATTERNS="\
(^\ E\:)|\
\[Summary\]|\
\[findbugs\]|\
\[checkstyle\]|\
\[junit\](.*)ERROR|\
\[junit\](.*)SKIPPED|\
\[junit\](.*)FAILED|\
Cannot\ execute"

# Define a set of patterns to match in the build log for exclusion from the summary output
# 1. Unnecessary findbugs outputs (we only want the warnings), 2. Unnecessary checkstyle outputs (we only want the warnings)
EXCLUDE_PATTERNS="\
\[findbugs\]\ Executing\ findbugs|\
\[findbugs\]\ Running\ FindBugs|\
\[findbugs\]\ Exit\ code\ set\ to|\
\[findbugs\]\ Calculating\ exit|\
\[findbugs\]\ Setting\ |\
\[findbugs\]\ Java\ Result|\
\[findbugs\]\ Warnings\ generated|\
\[findbugs\]\ java.io.IOException\: No files|\
\[findbugs\]\ (.*)FindBugs2\.execute|\
\[findbugs\]\ (.*)FindBugs\.runMain|\
\[findbugs\]\ (.*)FindBugs2\.main|\
\[findbugs\]\ (.*)FileSystemPreferences.1\ run|\
\[findbugs\]\ INFO\:\ Created\ user\ preferences\ directory|\
\[checkstyle\]\ Running"

# Copy a subset of the full log to the screen as a summary
echo "================== Summary =================="
grep -E "${INCLUDE_PATTERNS}" build.log | grep -v -E "${EXCLUDE_PATTERNS}"

# Return exit code of "ant full"
exit $EXIT
