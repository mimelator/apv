#!/bin/bash

JDK_VERSION=jdk1.8.0_202.jdk

../PlugIns/$JDK_VERSION/Contents/Home/jre/bin/java -Dconfig.file=../conf/application.conf \
 -classpath ../Java/ com.arranger.apv.Main
