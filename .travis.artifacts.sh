#!/usr/bin/env bash
#   A project build script for use with travis-ci.org
#   Copyright (c) 2014 University of Oxford


if $TRAVIS_SECURE_ENV_VARS; then
    echo "1"
    #if git describe --tags --exact-match HEAD &> /dev/null;
    if true
    then
        echo "2"
        # Only archive artifacts if we are on a tag

        # Setup key
        echo -n $id_rsa_{00..30} >> ~/.ssh/id_rsa_base64
        base64 --decode --ignore-garbage ~/.ssh/id_rsa_base64 > ~/.ssh/id_rsa
        chmod 600 ~/.ssh/id_rsa

        sha256sum ~/.ssh/id_rsa_base64
        sha256sum ~/.ssh/id_rsa

        echo "3"
        # Get verison
        VERSION=`git describe`

        echo $VERSION
        # Send data
        scp -oStrictHostKeyChecking=no -r artifacts abraid-travis@map1.zoo.ox.ac.uk:~/artifacts/$VERSION

        echo "4"
        # Cleanup key
        rm ~/.ssh/id_rsa_base64
        rm ~/.ssh/id_rsa
    fi
    # TBD "deloy" tags to test server?
    # TBD "deloy" master to test server? (check TRAVIS_PULL_REQUEST as well as TRAVIS_BRANCH)
fi

