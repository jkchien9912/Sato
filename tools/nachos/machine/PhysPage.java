package nachos.machine;

public class PhysPage {

    public PhysPage(int ppn) {
        this.ppn = ppn;
        this.head = null;
        this.next = null;
        this.maxStep = Processor.virtualPageSize/Processor.physicalPageSize;
    }

    public PhysPage getPage(int step) throws AddressException {
        if(step > this.maxStep) {
            System.err.println("incorrect step");
            throw new AddressException();
        }
        PhysPage cur = this.head;
        for(int i = 0; i < step; i++){
            cur = head.next;
        }

        return cur;
    }
    
    public int ppn;
    public PhysPage head;
    public PhysPage next;
    public int maxStep;

    public class AddressException extends Exception {

    }
}
