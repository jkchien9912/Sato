#!/bin/bash

g++ list_one_traversal.cpp -o list_one_traversal

rm -f ./seq_sp_output.txt
rm -f ./seq_sp_pf.txt
rm -f ./random_sp_output.txt
rm -f ./random_sp_pf.txt

# Sequential access using standard page 
for i in {6..31}
do
    sudo perf stat -e 'faults,dTLB-loads,dTLB-load-misses,cache-misses,cache-references' --output ./seq_sp_pf.txt --append ./list_one_traversal sequential $i >> ./seq_sp_output.txt
done

# Random access using standard page
for i in {6..31}
do 
    sudo perf stat -e 'faults,dTLB-loads,dTLB-load-misses,cache-misses,cache-references' --output ./random_sp_pf.txt  --append ./list_one_traversal random $i >> ./random_sp_output.txt
done 