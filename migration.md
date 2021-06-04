# Migration guide Scala 2 -> Scala 3

https://docs.scala-lang.org/scala3/guides/migration/incompat-syntactic.html

## How to see current versions in all subprojects?
In terminal on project top level run:
```
sh show-scala-versions.sh
```

## How to migrate using the scala3 compiler in terminal?

* make `scala3-compiler` available on your path or as an alias
* open a terminal windiw in this repos top folder
* run in terminal: `./rewrite-to-scala3.sh path/to/where/code/is/*.scala`
* check that the rewriting looks ok


## How to migrate sub-projects of this repo inside sbt?
Start sbt in terminal on project top level and then:
```
sbt:introprog root> project w03_irritext
sbt:introprog root> set scalacOptions := Seq("-rewrite","-new-syntax")
sbt:introprog root> compile
sbt:introprog root> set scalacOptions := Seq("-rewrite","-indent")
sbt:introprog root> compile
```
It is important to rewrite to `-new-syntax` before rewriting `-indent`.

The rewritings can be reverted:
```
sbt:introprog root> set scalacOptions := Seq("-rewrite","-no-indent")
sbt:introprog root> compile
sbt:introprog root> set scalacOptions := Seq("-rewrite","-old-syntax")
sbt:introprog root> compile
```

It is important to revert to `-no-indent` before reverting to `-old-syntax`.
