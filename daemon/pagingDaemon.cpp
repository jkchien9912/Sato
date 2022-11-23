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

    if (serverFd == -1) {
        std::cout<<"socket failed"<<std::endl;
        return -1;
    }

    int res = bind(serverFd, (struct sockaddr*)&addr, sizeof(addr));
    if (res) {
        std::cout<<"bind failed"<<std::endl;
        return -1;
    }

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
    // server loop
    // receive tcp request and reply with corresponding data
    while (true) {
        int bytes = recv(client, buf, sizeof(buf), 0);
        if (bytes != 1) continue;
        int size = buf[0] - '0';
        char* sendBuf = nullptr;
        switch (size) {
            case 1:
                sendBuf = mem1k;
                break;
            case 2:
                sendBuf = mem2k;
                break;
            case 3:
                sendBuf = mem3k;
                break;
            case 4:
                sendBuf = mem4k;
                break;
            case 8:
                sendBuf = mem8k;
                break;
            case 16:
                sendBuf = mem16k;
                break;
        }
        send(client, sendBuf, sizeof(sendBuf), 0);
        sendBuf = nullptr;
    }

    // deallocation
    free(mem1k);
    free(mem2k);
    free(mem3k);
    free(mem4k);
    free(mem8k);
    free(mem16k);
    return 0;
}
