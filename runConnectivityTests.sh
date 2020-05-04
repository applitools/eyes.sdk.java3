#!/bin/bash

runTest() {
	mv eyes.sdk.core/pom.xml eyes.sdk.core/jersey2_pom.xml
	mv eyes.sdk.core/$1_pom.xml eyes.sdk.core/pom.xml
	mvn -Dtest=BasicDemo -DfailIfNoTests=false test
	mv eyes.sdk.core/pom.xml eyes.sdk.core/$1_pom.xml
	mv eyes.sdk.core/jersey2_pom.xml eyes.sdk.core/pom.xml
}

runTest "jboss"
runTest "jersey1"