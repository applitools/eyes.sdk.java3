#!/bin/sh

curl http://applitools-quality-server.herokuapp.com/send_full_regression/sdks \
  -X POST \
  -H "Content-Type: application/json" \
  -d "{\"sdk\":\"$1\"}"
