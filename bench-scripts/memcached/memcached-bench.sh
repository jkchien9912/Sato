#!/bin/bash

WORKDIR="$( pwd; )"
# compile memcached from source
wget https://memcached.org/files/memcached-1.6.17.tar.gz
tar -zxvf memcached-1.6.17.tar.gz
cd memcached-1.6.17
./configure && make

# Install Java
sudo apt install -y default-jre maven

# clone YCSB
cd $WORKDIR
git clone https://github.com/brianfrankcooper/YCSB.git

## bug here
cd $WORKDIR/../../tools/intel_pin/source/tools/SimpleExamples
make
../../../pin -t obj-intel64/pinatrace.so -- ~/memcached-1.6.17/memcached
