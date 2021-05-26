#!/bin/sh

rm -rf ../teste/*

for i in {5..15}
do
    touch ../teste/test"${i}".txt
done
exit 0
