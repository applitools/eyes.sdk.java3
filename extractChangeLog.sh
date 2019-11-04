#!/usr/bin/env bash

awk -v ver="[$1]" '
 /^## \[/ { if (p) { exit}; if ($2 == ver) { p=1; next} } p && NF
' "$2"