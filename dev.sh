#!/bin/env bash

set -e

echo "1.Remove build dir"
rm -rf build/

echo "2.build project"
./gradlew hivemqExtensionZip
echo "Build successfully"


echo "3.build docker image"
docker build -t energy-monitor-hivemq .
echo "Build docker successfully"


echo "4.run container"
docker run --network=host -e DEVICE_VERIFY_TOKEN_URL='http://localhost:3000/api/auth/devices/token-verify' energy-monitor-hivemq
