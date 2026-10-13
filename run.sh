#!/bin/bash
# Run from the distribution directory regardless of the caller's working directory
cd "$(dirname "$(readlink -f "$0")")" || exit 1

if [ -n "${JAVA_HOME}" ]; then
    JAVA="${JAVA_HOME}/bin/java"
else
    JAVA="$(command -v java)"
fi
if [ ! -x "${JAVA}" ]; then
    echo "Java not found: set JAVA_HOME or add java to PATH (Java 17+ required)" >&2
    exit 1
fi

"${JAVA}" --module-path lib/ --add-modules=javafx.controls --add-exports javafx.base/com.sun.javafx.event=ALL-UNNAMED -jar FontAwesomeDemo.jar &
