# Do the following operations in root mode
echo 4096 > /proc/sys/vm/nr_hugepages
mkdir -p /mnt/hugetlbfs
mount -t hugetlbfs none /mnt/hugetlbfs

# Do the following operations in user mode
sudo chown postfix:postfix /mnt/hugetlbfs
sudo apt-get update -y
sudo apt-get install -y libhugetlbfs-dev

# Install tools to support perf
sudo apt update -y 
sudo apt install linux-tools-common
sudo apt install linux-tools-4.15.0-169-generic
sudo apt install linux-cloud-tools-4.15.0-169-generic