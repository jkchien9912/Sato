if [ ! -d ./ram_bench ]
then 
    git clone https://github.com/emilk/ram_bench.git
fi
cd ./ram_bench
g++ list_traversal.cpp -o list_traversal
sudo perf stat ./list_traversal > ram_bench_output.txt
sudo perf stat env LD_PRELOAD=libhugetlbfs.so HUGETLB_MORECORE=yes ./list_traversal > ram_bench_hp_output.txt
cd ..