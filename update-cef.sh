#!/bin/sh

mkdir -p shell/lib/jcef/win64/native

cp $1/binary_distrib/win64/bin/*.jar shell/lib/jcef/win64
cp -a $1/binary_distrib/win64/bin/lib/win64/* shell/lib/jcef/win64/native
