#!/bin/bash

WORKDIR=$( pwd; )
MEMCACHED_VERSION=1.6.17

# dependencies
sudo apt update
sudo apt install -y libevent-dev

# compile memcached from source
wget https://memcached.org/files/memcached-${MEMCACHED_VERSION}.tar.gz
tar -zxvf memcached-${MEMCACHED_VERSION}.tar.gz
rm memcached-${MEMCACHED_VERSION}.tar.gz
cd memcached-${MEMCACHED_VERSION}
./configure && make

cd $WORKDIR/../../tools
tar -xvzf intel-pin.tar.gz
cd pin-3.25-98650-g8f6168173-gcc-linux/source/tools/SimpleExamples
make all TARGET=intel64
../../../pin -t obj-intel64/pinatrace.so -- ${WORKDIR}/memcached-${MEMCACHED_VERSION}/memcached
