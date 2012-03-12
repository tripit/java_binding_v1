#!/bin/sh

if [ -z $REVISION ]; then
    REVISION=`svn update . | cut -d' ' -f3 | cut -d'.' -f1`
fi

rm -f tripit_java_v1_*.tgz
rm -f tripit_java_v1_*.zip
mkdir -p tripit_java_v1_${REVISION}/lib

ant

cp build.xml NOTICE.txt tripit_java_v1_${REVISION}
cp *.jar lib/*.jar tripit_java_v1_${REVISION}/lib
tar c --exclude ".svn" src | tar x -C tripit_java_v1_${REVISION}

tar czvf tripit_java_v1_${REVISION}.tgz tripit_java_v1_${REVISION}/*
zip -r tripit_java_v1_${REVISION}.zip tripit_java_v1_${REVISION}/*

rm -rf tripit_java_v1_${REVISION}
