# Migration guide Scala 2 -> Scala 3

## Currently already completed migrations

* All code in [compendium/examples] has been migrated using `-rewrite`
* All code in [compendium/workspace] has been migrated using `-rewrite`

## Migrations TODO

See issues and progress here: https://github.com/lunduniversity/introprog/issues 

* Exercises - see instructions below
* Slides of lectures and text in compendium
* Discussion and decision on what new stuff to teach in Scala 3 in the second study period of the course, see general discussion here https://github.com/lunduniversity/introprog/issues/509

## Installing Scala 3

There are unfortunately no `.msi` files for Windows for Scala 3 on the [download page](https://www.scala-lang.org/download/scala3.html). Instead you can do one or more of the following:
1. Download the Scala 3 [zip](https://github.com/lampepfl/dotty/releases/download/3.0.0/scala3-3.0.0.zip) on [github](https://github.com/lampepfl/dotty/releases) and make the binaries [available on your path](https://www.architectryan.com/2018/03/17/add-to-the-path-on-windows-10/) so you can write `scala` and `scalac` in terminal.

2. Use Coursier according to [here](https://get-coursier.io/docs/cli-installation) and then:
  `cs install scala3-compiler` and `cs install scala3-repl` to get `scala3-compiler` and `scala3-repl` on your path

3. Use [`sbt`](https://www.scala-sbt.org/download.html) with at least `sbt.version=1.5.3` in `project/build.properties` and `scalaVersion := "3.0.0"` in `build.sbt` and you can launch the REPL with `sbt console`

## Migrating exercises

Exercises are migrated manually (i.e. *not* using the Scala 3 compilers `-rewrite` option) as they are snippets that should work in REPL that often do not compile on their own and they are embedded in the latex code.

Exercises are located here [compendium/modules] prefixed with week and suffixed with `-exercise.tex`

1. Study the syntactic differences between Scala 2 and Scala 3 here:
https://docs.scala-lang.org/scala3/guides/migration/incompat-syntactic.html   and the new coding style here: https://docs.scala-lang.org/scala3/guides/migration/tooling-syntax-rewriting.html

2. Fork this repo and clone it locally, etc.

2. Study how to make a contribution to this repo in Chapter 0 here on page 16 [http://cs.lth.se/pgk/compendium] 

3. Locate the issue corresponding to the exercise you are migrating [here](https://github.com/lunduniversity/introprog/issues) called something like `Migrate exercise w01 expressions`

3. Focus on fixing these most common cases first:
   1. Fix things that does not work anymore, e.g. calling `def f() = 42` with just `f` does not work anymore - you need to match the parenthesis at definition site with the call site.
   2. Use new control syntax: `if then`, `for do`, `for yield`, `while do`
   3. Make braces optional where sensible. One-liners can for space reasons or convenience still use braces.
   4. Avoid unnecessary braces in lambdas; this is legal in Scala 3: `table.map((k,v) => multiple line-breaks here)`

4. Migrate both the task and the facit.

5. Remember to check everything in the Scala 3 REPL before you commit.

6. When you make a Pull Request on a fix to an exercises, then mention `fix #999` in the title of the PR message where `#999` is replaced with the number of the corresponding migration issue. Then the issue will be automatically closed when the PR is merged.

## How to see current versions in all subprojects?

In terminal on project top level run:
```
sh show-scala-versions.sh
```

## How to migrate using the scala3 compiler in terminal?

* make `scala3-compiler` available on your path or as an alias
* open a terminal in this repo's top folder
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
