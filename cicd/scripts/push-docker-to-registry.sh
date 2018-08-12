#!/usr/bin/env bash
TAG=$1
NAME=$2

echo "TAG=${TAG} NAME=${NAME} DOCKER_HOST=${DOCKER_HOST}"
docker tag $NAME:$TAG registry2.swarm.devfactory.com/aurea-zero-based/$NAME:$TAG
docker push registry2.swarm.devfactory.com/aurea-zero-based/$NAME:$TAG
