#!/bin/bash

KOTLINC_BINARY_NAME=kotlinc-native
KOTLINC_BINARY_DIR=$(cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd)
KOTLINC_HOME_DIR=$(cd "${KOTLINC_BINARY_DIR}"/../ && pwd)
KOTLINC_RESOURCES_DIR=$(cd "$KOTLINC_HOME_DIR"/resources/ && pwd)
KOTLINC_LIB_DIR=$(cd "$KOTLINC_HOME_DIR"/lib/ && pwd)

"$KOTLINC_BINARY_DIR"/$KOTLINC_BINARY_NAME \
  -Djava.home="$JAVA_HOME" \
  -Dkotlin.home="${KOTLINC_HOME_DIR}"/ \
  -Xintellij-plugin-root="${KOTLINC_RESOURCES_DIR}"/ \
  $@
