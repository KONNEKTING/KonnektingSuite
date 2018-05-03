#!/bin/bash

# this is what is needed
TOOLS="slicKnx jul-logger RootEventBus"
REPOS="KonnektingXmlSchema KonnektingDeviceConfig KonnektingSuite"
BRANCH="develop_beta5"

# always stop on error
set -e 

BASEDIR=$(mktemp -dt KonnektingSuiteDelivery.XXXXX)
# save current dir and ensure that we return on exit
pushd .
trap popd EXIT
cd $BASEDIR

echo "+++++ CLONING into $BASEDIR"

for i in $TOOLS; do 
  git clone https://github.com/tuxedo0801/$i ;
done
for i in $REPOS; do
  git clone https://github.com/KONNEKTING/$i ;
  if [ ! "x$BRANCH" == "x" ]; then
    cd $i
    git checkout $BRANCH
    cd ..
  fi;
done

echo "+++++ PATCHING"
# java seems to have changes conversion warnings to errors
sed -i "s/conf.setId(id);/conf.setId((short)id);/g" KonnektingDeviceConfig/src/main/java/de/konnekting/deviceconfig/DeviceConfigContainer.java 


echo "+++++ INSTALLING"
for i in $TOOLS $REPOS; do
  cd $BASEDIR/$i
  mvn install 
done

echo "+++++ done. leaving from $BASEDIR."

