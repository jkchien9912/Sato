#!/bin/bash

WORKLOAD=$1
cd YCSB

./bin/ycsb.sh run memcached -s -P $WORKLOAD \
    -p "memcached.hosts=127.0.0.1"