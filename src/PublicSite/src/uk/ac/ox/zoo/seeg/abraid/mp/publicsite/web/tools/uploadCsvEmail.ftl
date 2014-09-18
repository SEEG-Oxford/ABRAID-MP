<#--
    A template email sent in response to uploading a CSV file for data acquisition.
    Copyright (c) 2014 University of Oxford
-->
Here are the results of the CSV upload that you submitted.

File: "${filePath}".
Submitted on: ${submissionDate?string.long}.
Completed on: ${completionDate?string.long}.

The upload ${succeededOrFailed} with message:

${message}
