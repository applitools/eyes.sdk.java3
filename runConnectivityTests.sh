#!/bin/bash

default="jersey2x"

runTest() {
	sed -i "s/$default/$1/g" eyes.sdk.core/pom.xml
	mvn test -Dtest=BasicDemo -DfailIfNoTests=false -e -X
	RESULT="$?"
	sed -i "s/$1/$default/g" eyes.sdk.core/pom.xml
	if [ "$RESULT" != "0" ]; then
		exit "$?"
	fi
}

runTest "jboss"
runTest "jersey1x"