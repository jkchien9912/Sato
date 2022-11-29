git clone https://github.com/emilk/ram_bench.git
cd ./ram_bench
g++ list_traversal.cpp -o list_traversal
sudo perf stat -e 'faults,dTLB-loads,dTLB-load-misses,cache-misses,cache-references' ./list_traversal
sudo perf stat -e 'faults,dTLB-loads,dTLB-load-misses,cache-misses,cache-references' env LD_PRELOAD=libhugetlbfs.so HUGETLB_MORECORE=yes ./list_traversal
cd ..