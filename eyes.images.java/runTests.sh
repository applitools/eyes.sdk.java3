#!/bin/bash

# Abort on Error
set -e;

# Run module tests
mvn test -e -X;

# Send module report
if [ -d "$BUILD_DIR/report" ]; then
  chmod +x ./../sendTestResults.sh;
  sh ./../sendTestResults.sh;
else
  echo "Module report was not created. $BUILD_DIR/report doesn't exist"
fi