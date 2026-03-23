#!/bin/bash
# Gradle wrapper JAR'ı indir
mkdir -p gradle/wrapper
cd gradle/wrapper
wget -q https://repo.gradle.org/gradle/gradle-6.9.4/gradle-6.9.4-wrapper.jar -O gradle-wrapper.jar
cd ../..
echo "Gradle wrapper JAR downloaded successfully"
