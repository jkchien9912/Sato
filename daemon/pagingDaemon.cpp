#include <sys/socket.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <unistd.h>
#include <cstring>
#include <iostream>

#define PORT 8888

int main() {
    // allocate the memory align to a full memory page
    char* mem4k = (char*)aligned_alloc(sysconf(_SC_PAGESIZE), sysconf(_SC_PAGESIZE));
    char* mem8k = (char*)aligned_alloc(sysconf(_SC_PAGESIZE), 2 * sysconf(_SC_PAGESIZE));
    char* mem16k = (char*)aligned_alloc(sysconf(_SC_PAGESIZE), 4 * sysconf(_SC_PAGESIZE));
    
    // allocate the memory smaller than a page
    char* mem1k = (char*)malloc(sizeof(char) * 1024);
    char* mem2k = (char*)malloc(sizeof(char) * 2048);
    char* mem3k = (char*)malloc(sizeof(char) * 3072);
    
    int serverFd = socket(AF_INET, SOCK_STREAM, 0);
    struct sockaddr_in addr;
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = htonl(INADDR_ANY);
    addr.sin_port = htons(PORT);

    if (serverFd < 0) {
        std::cout<<"socket failed"<<std::endl;
        return -1;
    }

    int res = bind(serverFd, (struct sockaddr*)&addr, sizeof(addr));
    if (res < 0) {
        std::cout<<"bind failed"<<std::endl;
        return -1;
    }
    listen(serverFd, 5);

    // clear the allocated memory
    memset(mem4k, 0, sizeof(mem4k));
    memset(mem8k, 0, sizeof(mem8k));
    memset(mem16k, 0, sizeof(mem16k));
    memset(mem1k, 0, sizeof(mem1k));
    memset(mem2k, 0, sizeof(mem2k));
    memset(mem3k, 0, sizeof(mem3k));

    char buf[8];
    struct sockaddr_in clientAddr;
    socklen_t addrLen = sizeof(clientAddr);
    int client = accept(serverFd, (struct sockaddr*)&clientAddr, &addrLen);
    if (client < 0) {
        std::cout<<"Error: accepting request from client"<<std::endl;
        return -1;
    }
    std::cout<<"accept a client connection"<<std::endl;
    // server loop
    // receive tcp request and reply with corresponding data
    while (true) {
        memset(&buf, 0, sizeof(buf));
        recv(client, buf, sizeof(buf), 0);
        std::cout<<"received: "<<buf<<std::endl;
        if (strcmp(buf, "1")) {
            send(client, mem1k,  1024, 0);
        } else if (strcmp(buf, "2")) {
            send(client, mem2k, 2 * 1024, 0);
        } else if (strcmp(buf, "3")) {
            send(client, mem3k, 3 * 1024, 0);
        } else if (strcmp(buf, "4")) {
            send(client, mem4k, 4 * 1024, 0);
        } else if (strcmp(buf, "8")) {
            send(client, mem8k, 8 * 1024, 0);
        } else if (strcmp(buf, "16")) {
            send(client, mem16k, 16 * 1024, 0);
        }
    }

    // deallocation
    free(mem1k);
    free(mem2k);
    free(mem3k);
    free(mem4k);
    free(mem8k);
    free(mem16k);
    close(serverFd);
    close(client);
    return 0;
}
