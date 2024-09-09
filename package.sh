#workspace is published in repo (should be in release...)
# sbt eclipse  # deprecated, no support for scala-ide, use metals-eclipse plugin 
rm -i lib/workspace.zip
zip -9 -r lib/workspace.zip -x="*target*" -x=*.class -x="workspace/project/*" -x="**/.*" -x="**/.scala-build" workspace

# deprecated cslib will not change:
# sbt cslib/package
# cp workspace/cslib/target/scala-2.12/cslib_2.12-2017.2.jar lib/cslib.jar
# for file in workspace/cslib/target/scala-2.12/cslib*.jar; do echo cp "$file" "lib/cslib.jar";done
