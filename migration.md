# Migration guide Scala 2 -> Scala 3

## How to see current versions in all subprojects?
In terminal on project top level run:
```
sh show-scala-versions.h
```

## How to migrate inside sbt?
Start sbt in terminal on project top level and then:
```
sbt:introprog root> show w03_irritext/Compile/scalacOptions
sbt:introprog root> set w03_irritext/Compile/scalacOptions := Seq("-rewrite","-new-syntax")
sbt:introprog root> w03_irritext/compile
sbt:introprog root> set w03_irritext/Compile/scalacOptions := Seq("-rewrite","-indent")
sbt:introprog root> w03_irritext/compile
```
It is important to rewrite to `-new-syntax` before rewriting `-indent`.

The rewritings can be reverted:
```
sbt:introprog root> show w03_irritext/Compile/scalacOptions
sbt:introprog root> set w03_irritext/Compile/scalacOptions := Seq("-rewrite","-no-indent")
sbt:introprog root> w03_irritext/compile
sbt:introprog root> set w03_irritext/Compile/scalacOptions := Seq("-rewrite","-old-syntax")
sbt:introprog root> w03_irritext/compile
```

It is important to revert to `-no-indent` before reverting to `-old-syntax`.
