#!/bin/bash
# Inputs:
#   - EG [boolean] ; should run tests on Execution Cloud
#   - SDK name [string] ; the sdk name used in generating generics

RESULT=0
MESSAGE=""

yarn install
if [ "$?" -ne 0 ]; then
    ((RESULT+=1))
    MESSAGE+=$'\n npm install have failed'
    echo "${MESSAGE}"
fi

# Run UFG tests on EC
if [[ "$1" == "true" ]]; then
  export UFG_ON_EG=true
fi

if [[ "$2" == "selenium" ]]; then
  yarn generate
else
  if [[ "$2" == "playwright" ]]; then
  yarn generate:playwright
  else
    exit 1
  fi
fi

if [ "$?" -ne 0 ]; then
    ((RESULT+=1))
    MESSAGE+=$'\n unable to generate tests'
    echo "${MESSAGE}"
fi

# start eg client and save process id
if [[ "$1" == "true" ]]; then
  export APPLITOOLS_SHOW_LOGS=true
  yarn universal:eg &
  EG_PID="$!"
  export EXECUTION_GRID_URL=http://localhost:8080
fi

mvn clean test
if [ "$?" -ne 0 ]; then
    ((RESULT+=1))
    MESSAGE+=$'\n test run have failed'
    echo "${MESSAGE}"
fi

# Kill eg client by the process id
if [[ "$1" == "true" ]]; then
  echo $EG_PID
  kill $EG_PID
fi

echo "RESULT = ${RESULT}"
echo "$MESSAGE"
if [ "$RESULT" -eq 0 ]; then
    exit 0
else
    exit "$RESULT"
fi