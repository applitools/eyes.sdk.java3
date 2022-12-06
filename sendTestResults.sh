#!/bin/sh

send_test_results() {
  jq -s '.[0].results = [.[].results[]] | .[0]' "$1"*.json |
    curl -sS \
      -X POST \
      -H 'Content-Type: application/json' \
      -d @- http://applitools-quality-server.herokuapp.com/result
}

cd "$TRAVIS_BUILD_DIR/report" || exit

groups=$(for file in *.json; do echo "${file%_*.json}"; done | sort -u)
for group in $groups; do
  send_test_results "$group"
done
