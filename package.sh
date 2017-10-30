sbt eclipse
zip -9 -r lib/workspace.zip -x=*target* -x=*build.sbt -x=*.class -x="workspace/project/*" workspace
sbt cslib/package
cp workspace/cslib/target/scala-2.12/cslib*.jar lib/cslib.jar
