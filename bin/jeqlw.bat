@echo off
set OLD_DIR=%CD%
set JEQL_HOME=%~dp0..%
set JAVA_OPTS=-Xms256M -Xmx1024M

set CLASSPATH=
set LIB=%JEQL_HOME%\lib

for %%i in ("%LIB%\*.jar") do call cpAppend %%i

java -cp "%CLASSPATH%" %JAVA_OPTS% jeql.workbench.Workbench %*

