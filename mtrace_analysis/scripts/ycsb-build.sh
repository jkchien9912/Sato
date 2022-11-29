#!/bin/bash

git submodule update --init

cd YCSB
mvn -pl site.ycsb:memcached-binding -am clean package