#!/usr/bin/env bash

sudo apt-get install jq

usage() {
  echo "$0 <tagname> <releasename> <body> <artifact> <artifactname> <token>"
}

response=$(
  curl --fail \
       --location \
       --data "{\"tag_name\": \"$1\", \"name\": \"$2\", \"body\": \"$3\"}" \
       --header "Content-Type: application/json" \
       --header "Authorization: token $6" \
       "https://api.github.com/repos/$CIRCLE_PROJECT_USERNAME/$CIRCLE_PROJECT_REPONAME/releases"
)

url2="$(echo "$response" | jq -r .upload_url | sed -e "s/{?name,label}//")"

curl --header "Content-Type:application/gzip" \
     --header "Authorization: token $6" \
     --data-binary "@$4" \
     "$url2?name=$5"