#!/bin/bash

WORKDIR=$( pwd; )
MEMCACHED_VERSION=1.6.17

# compile memcached from source
wget https://memcached.org/files/memcached-${MEMCACHED_VERSION}.tar.gz
tar -zxvf memcached-${MEMCACHED_VERSION}.tar.gz
rm memcached-${MEMCACHED_VERSION}.tar.gz
cd memcached-${MEMCACHED_VERSION}
./configure && make

# clone YCSB
cd $WORKDIR
git clone https://github.com/brianfrankcooper/YCSB.git

cd $WORKDIR/../../tools/intel_pin/source/tools/SimpleExamples
make all TARGET=intel64
../../../pin -t obj-intel64/pinatrace.so -- ${WORKDIR}/memcached-${MEMCACHED_VERSION}/memcached
