#!/bin/bash
${JAVA_HOME}/bin/java --module-path lib/ --add-modules=javafx.controls --add-exports javafx.base/com.sun.javafx.event=ALL-UNNAMED -jar FontAwesomeDemo.jar &
