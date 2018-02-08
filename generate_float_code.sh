#!/bin/bash
echo "script: script for generating the float part of the project"
echo "script: what javac?"
javac -version
echo "script: compiling FloatVersionGenerator"
javac src/test/java/hageldave/generator/FloatVersionGenerator.java
echo "script: running FloatVersionGenerator"
java -cp  src/test/java/ hageldave/generator/FloatVersionGenerator
echo "script: deleting compiled class file of FloatVersionGenerator again"
rm src/test/java/hageldave/generator/FloatVersionGenerator.class
