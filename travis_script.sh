#!/bin/bash
echo "script: executing generate_float_code.sh"
bash ./generate_float_code.sh
echo "script: executing maven test"
mvn -P travis -X -pl :ezfftw clean test jacoco:report