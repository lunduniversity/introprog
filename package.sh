# workspace is published in repo (should be in release only...)
rm -i lib/workspace.zip
zip -9 -r lib/workspace.zip -x="*target*" -x=*.class -x="workspace/project/*" -x="**/.*" -x="**/.scala-build" workspace

# kärt barn har många namn
scp lib/workspace.zip $LUCATID@$WEB_REMOTE_LU:/Websites/Fileadmin/pgk/ws.zip
scp lib/workspace.zip $LUCATID@$WEB_REMOTE_LU:/Websites/Fileadmin/pgk/workspace.zip
scp workspace/w01_kojo/kojo.scala $LUCATID@$WEB_REMOTE_LU:/Websites/Fileadmin/kojo.scala
scp workspace/w01_kojo/kojo.scala $LUCATID@$WEB_REMOTE_LU:/Websites/Fileadmin/kojolib.scala
scp workspace/w01_kojo/kojo.scala $LUCATID@$WEB_REMOTE_LU:/Websites/Fileadmin/pgk/kojo.scala
scp workspace/w01_kojo/kojo.scala $LUCATID@$WEB_REMOTE_LU:/Websites/Fileadmin/pgk/kojolib.scala

# deprecated cslib will not change:
# sbt cslib/package
# cp workspace/cslib/target/scala-2.12/cslib_2.12-2017.2.jar lib/cslib.jar
# for file in workspace/cslib/target/scala-2.12/cslib*.jar; do echo cp "$file" "lib/cslib.jar";done
