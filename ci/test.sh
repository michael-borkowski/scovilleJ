#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
RETRIES=3

function build() {
	cd "$DIR/../scovilleJ"
	# -P ci to activate release-like things like javadoc generation (and warnings)
	mvn -P ci clean verify
}



try=0

while [ $try -lt $RETRIES ]; do
	build && break
	((try++))
done

[ $try -eq 3 ] && exit 1
exit 0
