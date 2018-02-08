#!/bin/sh
./generate_float_code.sh
mvn -P travis -X -pl :ezfftw clean test jacoco:report