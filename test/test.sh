#!/bin/sh

for f in *.java.c;do
echo "==================="
echo "Compiler $f start"
gcc $f ../runtime/runtime.c -o ${f%%.*}
echo "Compiler $f end"
echo "==================="
done
