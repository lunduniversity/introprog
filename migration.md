# Migration guide Scala 2 -> Scala 3

https://docs.scala-lang.org/scala3/guides/migration/incompat-syntactic.html

## How to see current versions in all subprojects?
In terminal on project top level run:
```
sh show-scala-versions.h
```

## How to migrate inside sbt?
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
