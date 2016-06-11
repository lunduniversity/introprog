#!/bin/sh

# change CLASSDIR to correct path to compiled class files from prepend.scala:
CLASSDIR="/home/bjornr/github/lunduniversity/introprog/util/target/scala-2.11/classes"
scala -cp $CLASSDIR prepend
