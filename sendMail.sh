#!/bin/sh
curl http://sdk-test-results.herokuapp.com/send_mail -X POST -H "Content-Type: application/json" -d "$(python create_json.py "$1" "$2" "$3" "$4")"
