#!/usr/bin/env bash

HOME_PATH=$(cd `dirname $0`; pwd)
PROJECT_NAME="Export_Data"

cd ${HOME_PATH}

mvn clean package

if [ $? -ne 0 ]; then
   echo "Build failed..."
   exit 1
fi

rm -fr build

mkdir -p build/${PROJECT_NAME}/logs

cp -r bin build/${PROJECT_NAME}
cp -r conf build/${PROJECT_NAME}

cp -r target/lib build/${PROJECT_NAME}

cp target/export_data-*.jar build/${PROJECT_NAME}/lib

cd build

tar czf ${PROJECT_NAME}.tar.gz ${PROJECT_NAME}

exit 0
