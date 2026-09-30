@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@IF "%DEBUG%" == "" @ECHO OFF
@SETLOCAL

SET ERROR_CODE=0

@REM Set local scope for the variables with windows NT shell
IF "%OS%"=="Windows_NT" @SETLOCAL

SET "EXEC_DIR=%~dp0"
SET "WRAPPER_JAR=%EXEC_DIR%.mvn\wrapper\maven-wrapper.jar"

WHERE mvn >nul 2>nul
IF %ERRORLEVEL% EQU 0 (
    mvn %*
    EXIT /B %ERRORLEVEL%
)

IF DEFINED JAVA_HOME (
    SET "JAVACMD=%JAVA_HOME%\bin\java.exe"
) ELSE (
    SET "JAVACMD=java.exe"
)

IF EXIST "%WRAPPER_JAR%" (
    "%JAVACMD%" -jar "%WRAPPER_JAR%" %*
    EXIT /B %ERRORLEVEL%
)

ECHO ERROR: Maven is not installed in PATH and %WRAPPER_JAR% was not found.
EXIT /B 1
