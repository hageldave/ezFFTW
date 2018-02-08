#!/bin/sh
echo "script: script for removeing the float part of the project"
echo "script: removing src/main/../fp"
rm -r src/main/java/hageldave/ezfftw/fp
echo "script: removing src/test/../fp"
rm -r src/test/java/hageldave/ezfftw/fp