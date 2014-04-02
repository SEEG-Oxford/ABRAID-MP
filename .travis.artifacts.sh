#!/usr/bin/env bash
#   A project build script for use with travis-ci.org
#   Copyright (c) 2014 University of Oxford


if $TRAVIS_SECURE_ENV_VARS; then
    if git describe --tags --exact-match HEAD &> /dev/null;
    then
        # Only archive artifacts if we are on a tag

        # Setup key
        echo -n $id_rsa_{00..30} >> ~/.ssh/id_rsa_base64
        base64 --decode --ignore-garbage ~/.ssh/id_rsa_base64 > ~/.ssh/id_rsa
        chmod 600 ~/.ssh/id_rsa

        # Get verison
        VERSION=`git describe`

        # Send data
        scp -oStrictHostKeyChecking=no -r artifacts abraid-travis@map1.zoo.ox.ac.uk:~/artifacts/$VERSION

        # Cleanup key
        rm ~/.ssh/id_rsa_base64
        rm ~/.ssh/id_rsa
    fi
    # TBD "deploy" tags to test server?
    # TBD "deploy" master to test server? (check TRAVIS_PULL_REQUEST as well as TRAVIS_BRANCH)
fi

