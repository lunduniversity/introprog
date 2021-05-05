find . -type f -name build.sbt -exec grep -H "scalaVersion" {} \;
#sed -i 's/scalaVersion := "2.13.3"/scalaVersion := "2.13.5"/g' workspace/*/build.sbt
