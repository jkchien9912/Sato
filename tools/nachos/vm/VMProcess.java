package nachos.vm;

import java.util.ArrayList;
import java.io.IOException;
import java.net.*;
import java.io.*;

import nachos.machine.*;
import nachos.threads.*;
import nachos.userprog.*;
import nachos.vm.*;

/**
 * A <tt>UserProcess</tt> that supports demand-paging.
 */
public class VMProcess extends UserProcess {
	/**
	 * Allocate a new process.
	 */
	public VMProcess() {
		super();
        
        this.pageTable = new TranslationEntry[6000];
        for(int i = 0; i < 6000; i++){
            pageTable[i] = new TranslationEntry(i, null, false, false, false, false);
        }
	}

	/**
	 * Save the state of this process in preparation for a context switch.
	 * Called by <tt>UThread.saveState()</tt>.
	 */
	public void saveState() {
		super.saveState();
	}

	/**
	 * Restore the state of this process after a context switch. Called by
	 * <tt>UThread.restoreState()</tt>.
	 */
	public void restoreState() {
		super.restoreState();
	}

	/**
	 * Initializes page tables for this process so that the executable can be
	 * demand-paged.
	 * 
	 * @return <tt>true</tt> if successful.
	 */
	protected boolean loadSections() {
		return super.loadSections();
	}

	/**
	 * Release any resources allocated by <tt>loadSections()</tt>.
	 */
	protected void unloadSections() {
		super.unloadSections();
	}

	/**
	 * Handle a user exception. Called by <tt>UserKernel.exceptionHandler()</tt>
	 * . The <i>cause</i> argument identifies which exception occurred; see the
	 * <tt>Processor.exceptionZZZ</tt> constants.
	 * 
	 * @param cause the user exception that occurred.
	 */
	public void handleException(int cause) {
		Processor processor = Machine.processor();
        System.out.println("enter exception handler in VM: " + cause);
		switch (cause) {
            case Processor.exceptionPageFault:
                System.out.println("test");
                pageFaultHandler(Processor.regBadVAddr);
                break;
            default:
                super.handleException(cause);
                break;
		}
	}

    // Not adding any lock here because I'm lazy. 
    // TODO: add lock for further steps beyond benchmarking.
    public void pageFaultHandler(int vaddr) {
        pageFaultCounter++;
        
        // Evict current physical pages to get slots (include remote fetching)
        if(VMKernel.freePhysicalPages.size() < ratio) {
            pageEviction();
        }

        // Get new physical pages (this will actual drain physical page for other processes)
        // Modify if benchmark needs to run multiple process. keep it the same for now.
        PhysPage[] pages = new PhysPage[ratio];

        for(int i = 0; i < ratio; i++){
            int ppn = VMKernel.freePhysicalPages.remove(0);

            // TODO: try reusing the same phys page entries if it can reduce overheads
            pages[i] = new PhysPage(ppn);
        }
        
        for(int i = 0; i < ratio; i++){
            pages[i].head = pages[0];

            if(i < ratio - 1) {
                pages[i].next = pages[i + 1];
            }
        }

        // just a dummy benchmarking function
        fetchRemotePage(ratio);

        // Update page table
        this.pageTable[vaddr].physPage = pages[0];
        this.pageTable[vaddr].valid = true;
        lru.add(vaddr);
    }

    public void pageEviction() {
        int target = this.lru.remove(0);
        this.pageTable[target].valid = false;
        PhysPage cur = this.pageTable[target].physPage;
        while(cur != null){
            // TODO: implement coallocating policy
            VMKernel.freePhysicalPages.add(cur.ppn);
            cur = cur.next;
        }
        this.pageTable[target].physPage = null;
    }

    public void fetchRemotePage(int count) {
        Socket socket;
        PrintStream out;
        try {
            socket = new Socket("127.0.0.1", 8888);
            out = new PrintStream(socket.getOutputStream());
            out.println("4");

            
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // TODO: Yuki: Add TCP with daemon.

        System.out.println("fetch a page");
    }

    

	// private static final int pageSize = Processor.pageSize;
    private static final int physicalPageSize = Processor.physicalPageSize;
    private static final int virtualPageSize = Processor.virtualPageSize;
    private static int pageFaultCounter = 0;
	private static final char dbgProcess = 'a';
    public ArrayList<Integer> lru = new ArrayList<Integer>();

    private static int ratio = Processor.virtualPageSize/Processor.physicalPageSize;
	private static final char dbgVM = 'v';
}
