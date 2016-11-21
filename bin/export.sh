#!/usr/bin/env bash

BIN_PATH=$(cd `dirname $0`; pwd)

confPath=$1

if [ ! -n "$confPath" ] ;then
    echo "Usage: export.sh <sql definition file path>"
    exit 1
fi


for jarFile in `ls ${BIN_PATH}/../lib/*jar`
do
  CLASSPATH=${CLASSPATH}:${jarFile}
done

java -cp ${CLASSPATH} com.asiainfo.ocdp.sql.launcher.Main ${confPath} -Dlog4j.configurationFile=${BIN_PATH}/../logs/export.log

if [ $? -ne 0 ]; then
   echo "Export data failed!"
   exit 2
fi

exit 0