#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Maven Start Up Batch script
#
# Required ENV Vars:
# ------------------
#   JAVA_HOME - location of a JDK home dir
#
# Optional ENV Vars
# -----------------
#   MAVEN_OPTS - parameters passed to the Java VM when running Maven
#     e.g. to debug Maven itself, use
#       set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
#   MAVEN_SKIP_RC - flag to disable loading of mavenrc files
# ----------------------------------------------------------------------------

die() {
    echo "$*" >&2
    exit 1
}

WRAPPER_DIR="$(cd "$(dirname "$0")" && pwd -P)"
BASE_DIR="$WRAPPER_DIR"
WRAPPER_JAR="$BASE_DIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROP="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"

if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

if [ ! -x "$JAVACMD" ] && ! which "$JAVACMD" >/dev/null 2>&1; then
    die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
fi

# Fallback: if mvn is in PATH, execute mvn directly
if which mvn >/dev/null 2>&1; then
    exec mvn "$@"
fi

if [ -f "$WRAPPER_JAR" ]; then
    exec "$JAVACMD" -jar "$WRAPPER_JAR" "$@"
else
    die "Maven Wrapper JAR not found at $WRAPPER_JAR and 'mvn' is not installed in PATH. Please install Maven or run with a configured JDK."
fi
