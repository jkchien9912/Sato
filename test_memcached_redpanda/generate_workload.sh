#!/bin/bash

INPUT=users.csv
OLDIFS=$IFS
IFS=','
[ ! -f $INPUT ] && { echo "$INPUT file not found"; exit 99; }
while read user data type
do
    echo "{$user: $data}" | rpk topic produce my-app --key $user -H header-key:header-value
done < $INPUT
IFS=$OLDIFS