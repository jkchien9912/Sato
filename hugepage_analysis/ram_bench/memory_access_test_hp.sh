#!/bin/bash

g++ list_one_traversal.cpp -o list_one_traversal

# Sequential access using huge page 
for i in {6..31}
do
    sudo perf stat -e 'faults,dTLB-loads,dTLB-load-misses,cache-misses,cache-references' --output ./seq_hp_pf.txt --append env LD_PRELOAD=libhugetlbfs.so HUGETLB_MORECORE=yes ./list_one_traversal sequential $i >> ./seq_hp_output.txt
done

# Random access using huge page 
for i in {6..31}
do
    sudo perf stat -e 'faults,dTLB-loads,dTLB-load-misses,cache-misses,cache-references' --output ./random_hp_pf.txt --append env LD_PRELOAD=libhugetlbfs.so HUGETLB_MORECORE=yes ./list_one_traversal sequential $i >> ./random_hp_output.txt
done