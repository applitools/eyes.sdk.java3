#!/bin/bash

# Abort on Error
set -e;

# Setup web drivers
echo "Chromedriver setup"
chmod +x ./../initChromeDriver.sh;
sh ./../initChromeDriver.sh;
export CHROME_DRIVER_PATH="/usr/local/bin/chromedriver";

# Setup test type
IFS=' ' read -ra TEST_TYPE_ARRAY <<< "$TEST_TYPE"

function parse_type() {
  case $1 in
    unit) echo "unitTestsSuite.xml";;
    it) echo "integrationTestsSuite.xml";;
    e2e) echo "e2eTestsSuite.xml";;
    *) :;;
  esac
}

ACTUAL_TEST_TYPE=""

if (( ${#TEST_TYPE_ARRAY[@]} > 1 )); then
  # the input is an array
  for value in "${TEST_TYPE_ARRAY[@]}"; do
    type=$(parse_type "$value")
    ACTUAL_TEST_TYPE+="$type,"
  done
else
  # the input is a single value
  type=$(parse_type "$value")
  ACTUAL_TEST_TYPE+="$type,"
fi

echo "Test type: $ACTUAL_TEST_TYPE"
if [[ -z $ACTUAL_TEST_TYPE ]]; then
  # Run the default suite file
  mvn test -e -X
else
  mvn test -DsuiteFile="$ACTUAL_TEST_TYPE" -e -X
fi

# Send module report
if [ -d "$BUILD_DIR/report" ]; then
  chmod +x ./../sendTestResults.sh;
  sh ./../sendTestResults.sh;
else
  echo "Module report was not created. $BUILD_DIR/report doesn't exist"
fi

# Run coverage tests
cd ../coverage-tests;
chmod +x ./generic_tests.sh;
sh ./generic_tests.sh false "playwright";

# Send coverage results
if [[ $REPORT_LEVEL == "deploy" ]]; then
  yarn report:prod-playwright;
else
  yarn report:playwright;
fi