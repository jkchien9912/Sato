# Memory trace using Intel Pin tool and YCSB over Memcached

## Install Dependencies and Building

```bash
# install all required deps
make dep
# build the submodule ycsb with maven
make ycsb-build
# build memcached under mtrace_analysis/memcached/
make memcached-build
```

## Run Expriments

```bash
# run ycsb benchmark. e.g., make ycsb-run workload=workloads/workloada
make ycsb-run workload=$(WORKLOAD_FILE_RELATIVE_TO_YCSB_DIR)
# run memcached with pin-tool
make memcached-run
```

Once the memcached starts, the pin tool will print out all memory traces on the terminal. Redirection can be used to collect the data.