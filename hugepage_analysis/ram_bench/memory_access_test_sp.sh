#!/bin/bash

g++ list_one_traversal.cpp -o list_one_traversal

# Sequential access using standard page 
for i in {6..30}
do
    sudo perf stat --output ./seq_sp_pf.txt --append ./list_one_traversal sequential $i >> ./seq_sp_output.txt
done

# Random access using standard page
for i in {6..30}
do 
    sudo perf stat --output ./random_sp_pf.txt  --append ./list_one_traversal random $i >> ./random_sp_output.txt
done 