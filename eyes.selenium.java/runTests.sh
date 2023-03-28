#!/bin/bash

# Abort on Error
set -e;

# Setup web drivers
echo "Chromedriver setup"
chmod +x ./../initChromeDriver.sh;
sh ./../initChromeDriver.sh;
export CHROME_DRIVER_PATH="/usr/local/bin/chromedriver";

echo "Geckodriver setup"
chmod +x ./../initGeckoDriver.sh;
sh ./../initGeckoDriver.sh;
export FIREFOX_DRIVER_PATH="/usr/local/bin/geckodriver";

# Setup test type
#TEST_TYPE_ARRAY=$(echo "$TEST_TYPE" | jq --raw-input -r 'split(" ")')
#ACTUAL_TEST_TYPE=""
#
#parse_type() {
#  case $1 in
#    unit) echo "unitTestsSuite.xml";;
#    it) echo "integrationTestsSuite.xml";;
#    e2e) echo "e2eTestsSuite.xml";;
#    *) :;;
#  esac
#}
#
#if [[ $(echo "$TEST_TYPE_ARRAY" | jq length) -gt 1 ]]; then
#  # the input is an array
#  for value in $(echo "$TEST_TYPE_ARRAY" | jq --raw-output '.[]'); do
#    type=$(parse_type "$value")
#    ACTUAL_TEST_TYPE+="$type,"
#  done
#else
#  # the input is a single value
#  type=$(parse_type "$TEST_TYPE")
#  ACTUAL_TEST_TYPE="$type"
#fi

echo "Testing with type: $TEST_TYPE"
if [[ ! "$TEST_TYPE" == *"coverage"* ]]; then
  # Run the default suite file
  mvn test -e -X

  # Send module report
  if [ -d "$BUILD_DIR/report" ]; then
    chmod +x ./../sendTestResults.sh;
    sh ./../sendTestResults.sh;
  else
    echo "Module report was not created. \"$BUILD_DIR/report\" doesn't exist"
  fi
fi


if [[ "$TEST_TYPE" == *"coverage"* || "$TEST_TYPE" == *"all"* ]]; then
  # Run coverage tests
  cd ../coverage-tests;
  chmod +x ./generic_tests.sh;
  ./generic_tests.sh true "selenium";

  # Send coverage results
  if [[ $REPORT_LEVEL == "deploy" ]]; then
    yarn report:prod-selenium;
  else
    yarn report:selenium;
  fi
fi