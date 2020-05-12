#!/bin/bash

default="jersey2x"

runTest() {
	sed -i "s/$default/$1/g" eyes.sdk.core/pom.xml
	mvn test -Dtest=BasicDemo -DfailIfNoTests=false -e -X
	sed -i "s/$1/$default/g" eyes.sdk.core/pom.xml
}

runTest "jboss"
runTest "jersey1x"