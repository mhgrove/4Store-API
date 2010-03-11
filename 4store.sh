#!/bin/sh

if [ -n "${JAVA_HOME}" -a -x "${JAVA_HOME}/bin/java" ]; then
  java="${JAVA_HOME}/bin/java"
else
  java=java
fi

if [ -z "${fourstore_java_args}" ]; then
  fourstore_java_args="-Xmx512m"
fi

exec ${java} ${fourstore_java_args} -jar lib/cp-common-fourstore-0.3.1.jar "$@"