echo "  *** scala versions in build.sbt  ***"
find . -type f -name build.sbt -exec grep -H "scalaVersion" {} \;

echo " *** sbt versions in sbt.version ***"
find . -type f -name build.properties -exec grep -H "sbt.version" {} \;
#sed -i 's/scalaVersion := "2.13.3"/scalaVersion := "2.13.5"/g' workspace/*/build.sbt
