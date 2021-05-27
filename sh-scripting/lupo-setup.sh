#!/bin/sh

rm -rf ../test/*

for i in {5..15}
do
    touch ../test/test"${i}".txt
done
exit 0
