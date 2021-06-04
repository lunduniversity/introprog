#!/bin/bash

TMPDIR=tmp/rewrite-target

echo "*** rewriting $# files to scala3 **"
echo "    creating temporary output folder: $TMPDIR"
mkdir -p $TMPDIR

echo "    -rewrite -new-syntax "
scala3-compiler -rewrite -new-syntax -d $TMPDIR $@

echo "    -rewrite -indent "
scala3-compiler -rewrite -indent -d $TMPDIR $@

echo "    removing temporary output folder: $TMPDIR"
rm -rf $TMPDIR
