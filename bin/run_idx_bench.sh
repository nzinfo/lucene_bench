#!/usr/bin/env bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

lib=$bin/../target/lib
dist=$bin/../target
classes=$bin/../target/classes

HEAP_OPTS="-Xmx3g -Xms2g -XX:NewSize=512m"
JAVA_OPTS="-server -d64"

MAIN_CLASS="com.loginsight.bench.lucene.LuceneIndexingBench"
CLASSPATH=$classes/:$lib/*:$dist/*

java $JAVA_OPTS $JMX_OPTS $HEAP_OPTS -classpath $CLASSPATH $MAIN_CLASS $@
