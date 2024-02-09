#!/bin/bash
# script for update package.json version from VERSION file, git tag

VERSION=$(cat VERSION)
# update version package.develop.json
jq --arg new_version "$VERSION" '.version = $new_version' package.develop.json > temp.json && mv temp.json package.develop.json
echo "Versión develop actualizada a $VERSION en package.develop.json"
# update version package.production.json
jq --arg new_version "$VERSION" '.version = $new_version' package.production.json > temp.json && mv temp.json package.production.json
echo "Versión production actualizada a $VERSION en package.production.json"
