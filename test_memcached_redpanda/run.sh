# install redpanda
curl -1sLf \
  'https://dl.redpanda.com/nzc4ZYQK3WRGd9sy/redpanda/cfg/setup/bash.deb.sh' \
  | sudo -E bash
  
sudo apt-get install redpanda
# install memcached
sudo apt update
sudo apt install memcached
sudo apt install libmemcached-tools
# install or update node and npm
curl -fsSL https://deb.nodesource.com/setup_14.x | sudo -E bash -
sudo apt-get update
sudo apt-get install -y nodejs
sudo npm install -g npm@latest
# install Java
sudo apt update
sudo apt install default-jdk
