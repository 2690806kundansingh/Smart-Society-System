#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper Script for UNIX-like environments
# ----------------------------------------------------------------------------
PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

JAVACMD="java"
if [ -n "$JAVA_HOME" ]; then
  if [ -x "$JAVA_HOME/jre/sh/java" ]; then
    JAVACMD="$JAVA_HOME/jre/sh/java"
  elif [ -x "$JAVA_HOME/bin/java" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
  fi
fi

WRAPPER_JAR="$APP_HOME/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_LAUNCHER="org.apache.maven.wrapper.MavenWrapperMain"

exec "$JAVACMD" \
  $MAVEN_OPTS \
  -Dmaven.multiModuleProjectDirectory="$APP_HOME" \
  -cp "$WRAPPER_JAR" \
  "$WRAPPER_LAUNCHER" \
  "$@"
