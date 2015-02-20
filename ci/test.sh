#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

cd "$DIR/../scovilleJ"

# -P ci to activate release-like things like javadoc generation (and warnings)
mvn -P ci clean verify
