#!/bin/sh

curl -u "$BROWSERSTACK_USERNAME:$BROWSERSTACK_ACCESS_KEY" \
  -X POST "https://api-cloud.browserstack.com/app-automate/upload" \
  -F "data={\"url\": \"$1\", \"custom_id\": \"$2\"}"
