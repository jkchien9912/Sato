if [ ! -d ./tinymembanch ]
then 
    git clone https://github.com/ssvb/tinymembench.git
fi
cd ./tinymembench
make
sudo perf stat ./tinymembench > tinymembench_output.txt
sudo perf stat env LD_PRELOAD=libhugetlbfs.so HUGETLB_MORECORE=yes ./tinymembanch > tinymembanch_hp_output.txt
cd ..