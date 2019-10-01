#!/bin/bash
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

mv build/libs/general-consent.war docker/general-consent.war

docker build ./docker/ --build-arg VCS_REF=`git rev-parse --short HEAD` -t janedoegva/general-consent-restapi:$BRANCH

docker push janedoegva/general-consent-restapi:$BRANCH