#!/bin/bash

WORKDIR=$( pwd; )
MEMCACHED_VERSION=1.6.17

cd $WORKDIR/../tools/pin-3.25-98650-g8f6168173-gcc-linux/source/tools/SimpleExamples
../../../pin -t obj-intel64/pinatrace.so -- ${WORKDIR}/memcached/memcached-${MEMCACHED_VERSION}/memcached
