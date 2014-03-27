#!/usr/bin/env bash
# Kickoff script for the data acquisition process of ABRAID-MP, targeting Unix environments.
# Copyright (c) 2014 University of Oxford

echo Starting Data Acquisition...

# Ensure the working directory is the one that this script is in
cd `dirname $0`

# Run the Data Acquisition main class, passing in any arguments provided to this script
java -Dlog4j.configuration=file:log4j.properties -cp lib/* uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.Main $*
