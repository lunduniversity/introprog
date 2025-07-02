mkdir -p bin
echo scalac -cp lib/cslib.jar -d bin src/main/scala/graphics/*.scala
scalac -cp lib/cslib.jar -d bin src/main/scala/graphics/*.scala
