package nachos.machine;

import nachos.machine.*;

public class DynamicPageTableEntry implements Cloneable {

    public int vpn;
    public int ppn;
    public boolean valid;
    public boolean readOnly;
    public boolean used;
    public DynamicPageTableEntry next;
    public DynamicPageTableEntry prev;

    public DynamicPageTableEntry(int vpn, int ppn, boolean valid, boolean readOnly, boolean used) {
        this.vpn = vpn;
        this.ppn = ppn;
        this.valid = valid;
        this.readOnly = readOnly;
        this.used = used;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

class DynamicSubPageTableEntry {

}