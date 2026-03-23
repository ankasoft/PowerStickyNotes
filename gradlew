#!/bin/bash
APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`
CLASSPATH=`find . -name "gradle-wrapper.jar" 2>/dev/null | head -1`
if [ -z "$CLASSPATH" ]; then
    echo "gradle-wrapper.jar not found"
    exit 1
fi
exec java -cp "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
