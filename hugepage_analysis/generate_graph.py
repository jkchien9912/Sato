import re
import os
import math
import matplotlib.pyplot as plt

def get_size(line):
    r = re.findall('\d+ ', line)
    # get log2 of btyes accessed
    return math.log(int(r[0].split(" ")[0]), 2)

def get_value(line):
    r = re.findall('\d+\.\d+ ', line)
    return float(r[0].split(" ")[0])

def get_page_fault(line):
    line = line.replace(',', '')
    r = re.findall('\d+', line)
    return int(r[0].split(" ")[0])


def plot_line_graph(x, y1, y2, filename, title, ylabel):
    plt.plot(x, y1, label = "Standard Page")
    plt.plot(x, y2, label = "Huge Page")
    plt.xlabel("Bytes accessed (log)")
    plt.ylabel(ylabel)
    plt.title(title)
    plt.legend() 
    plt.savefig(filename)
    plt.clf()

def main():
    seq_sp_exec_path = "./ram_bench/seq_sp_output.txt"
    random_sp_exec_path = "./ram_bench/random_sp_output.txt"

    seq_hp_exec_path = "./ram_bench/seq_hp_output.txt"
    random_hp_exec_path = "./ram_bench/random_hp_output.txt"

    seq_sp_pf_path = "./ram_bench/seq_sp_pf.txt"
    random_sp_pf_path = "./ram_bench/random_hp_pf.txt"

    seq_hp_pf_path = "./ram_bench/seq_hp_pf.txt"
    random_hp_pf_path = "./ram_bench/random_hp_pf.txt"

    if not os.path.isfile(seq_sp_exec_path) or \
        not os.path.isfile(random_sp_exec_path) or \
        not os.path.isfile(seq_hp_exec_path) or \
        not os.path.isfile(random_hp_exec_path) or \
        not os.path.isfile(seq_sp_pf_path) or \
        not os.path.isfile(random_sp_pf_path) or \
        not os.path.isfile(seq_hp_pf_path) or \
        not os.path.isfile(random_hp_pf_path): 
        print("Please run memory_access_test.sh first")

    seq_sp_exec_file = open("./ram_bench/seq_sp_output.txt")
    random_sp_exec_file = open("./ram_bench/random_sp_output.txt")

    seq_hp_exec_file = open("./ram_bench/seq_hp_output.txt")
    random_hp_exec_file = open("./ram_bench/random_hp_output.txt")

    seq_sp_pf_file = open("./ram_bench/seq_sp_pf.txt")
    random_sp_pf_file = open("./ram_bench/random_sp_pf.txt")

    seq_hp_pf_file = open("./ram_bench/seq_hp_pf.txt")
    random_hp_pf_file = open("./ram_bench/random_hp_pf.txt")

    bytes = []

    seq_sp_time = []
    seq_hp_time = []

    random_sp_time = []
    random_hp_time = []

    seq_sp_pf = []
    seq_hp_pf = []

    random_sp_pf = []
    random_hp_pf = []

    # Get execution time
    for line in seq_sp_exec_file: 
        if line in ('\n', '\r\n', ''):
            continue
        bytes.append(get_size(line))
        seq_sp_time.append(get_value(line))
    #print(len(seq_sp_time))

    for line in seq_hp_exec_file: 
        if line in ('\n', '\r\n', ''):
            continue
        seq_hp_time.append(get_value(line))
    #print(len(seq_hp_time))

    for line in random_sp_exec_file:
        if line in ('\n', '\r\n', ''):
            continue
        random_sp_time.append(get_value(line))
    #print(len(random_sp_time))

    for line in random_hp_exec_file: 
        if line in ('\n', '\r\n', ''):
            continue
        random_hp_time.append(get_value(line))
    #print(len(random_hp_time))
    
    # Get page fault
    for line in seq_sp_pf_file: 
        if line.find(" page-faults ") != -1:
            seq_sp_pf.append(get_page_fault(line))
    #print(len(seq_sp_pf))

    for line in seq_hp_pf_file: 
        if line.find(" page-faults ") != -1:
            seq_hp_pf.append(get_page_fault(line))
    #print(len(seq_hp_pf))
    
    for line in random_sp_pf_file: 
        if line.find(" page-faults ") != -1:
            random_sp_pf.append(get_page_fault(line))
    #print(len(random_sp_pf))

    for line in random_hp_pf_file: 
        if line.find(" page-faults ") != -1:
            random_hp_pf.append(get_page_fault(line))
    #print(len(random_hp_pf))

    plot_line_graph(bytes, seq_sp_time, seq_hp_time, "./seq_exec_time_compare.jpg", "Sequential Access", "ns per element")
    plot_line_graph(bytes, seq_sp_pf, seq_hp_pf, "./seq_page_fault_compare.jpg", "Sequential Access", "Times")
    plot_line_graph(bytes, random_sp_time, random_hp_time, "./random_exec_time_compare.jpg", "Random Access", "ns per element")
    plot_line_graph(bytes, random_sp_pf, random_hp_pf, "./random_page_fault_compare.jpg", "Random Access", "Times")

if __name__ == "__main__":
    main()