#!/bin/bash
echo "script: script for deploying artifact to nexus"
./generate_float_code.sh
echo "script: now building and deploying to nexus"
mvn clean deploy -P release
