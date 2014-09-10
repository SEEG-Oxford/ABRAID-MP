@echo off
rem Kickoff script for the data manager process of ABRAID-MP, targeting Windows environments.
rem Copyright (c) 2014 University of Oxford

echo Starting Data Manager...

rem Ensure the working directory is the one that this script is in
cd "%~dp0"

rem Run the Data Manager main class, passing in any arguments provided to this script
java -Dlog4j.configuration=file:log4j.properties -cp lib/* uk.ac.ox.zoo.seeg.abraid.mp.datamanager.Main %*
