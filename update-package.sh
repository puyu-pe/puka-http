#!/bin/bash
# script for update package.json version from VERSION file, git tag

VERSION=$(cat VERSION)
# update version package.json
jq --arg new_version "$VERSION" '.version = $new_version' package.json > temp.json && mv temp.json package.json
echo "Versión actualizada a $VERSION en package.json"
