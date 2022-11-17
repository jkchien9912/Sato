#!/bin/bash

WORKDIR=$( pwd; )
# dependencies
sudo apt update
sudo apt install -y default-jre maven

# clone YCSB
cd $WORKDIR
git clone https://github.com/brianfrankcooper/YCSB.git

cd YCSB

mvn -pl site.ycsb:memcached-binding -am clean package

./bin/ycsb.sh load memcached -s -P workloads/workloada \
    -p "memcached.hosts=127.0.0.1"

./bin/ycsb.sh run memcached -s -P workloads/workloada \
    -p "memcached.hosts=127.0.0.1"