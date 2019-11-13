#!/bin/sh
curl http://sdk-test-results.herokuapp.com/send_mail -X POST -H "Content-Type: application/json" -d "{'sdk':'$1', 'version':'$2', 'changeLog':'$3', 'testCoverageGap': '$4'}"
