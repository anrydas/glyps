@echo off
rem Run from the distribution directory regardless of the callers working directory
cd /d "%~dp0"

set "JAVAW=javaw.exe"
if defined JAVA_HOME set "JAVAW=%JAVA_HOME%\bin\javaw.exe"

start "" "%JAVAW%" --module-path lib/ --add-modules=javafx.controls --add-exports javafx.base/com.sun.javafx.event=ALL-UNNAMED -jar FontAwesomeDemo.jar
