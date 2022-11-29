
.PHONY: dep ycsb-build ycsb-run memcached-build memcached-run

dep:
	./scripts/install-deps.sh

ycsb-build:
	./scripts/ycsb-build.sh

ycsb-run:
	./scripts/ycsb-run.sh $(workload)

memcached-build:
	./scripts/memcached-build.sh

memcached-run:
	./scripts/memcached-run.sh

