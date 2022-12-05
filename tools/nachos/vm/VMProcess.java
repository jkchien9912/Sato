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
        this.lruList = new ArrayList<Integer>();

        try {
            this.socket = new Socket("127.0.0.1", 8888);
            this.remoteOutStream = new PrintStream(socket.getOutputStream());
            this.remoteInStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
                pageFaultHandler(processor.readRegister(Processor.regBadVAddr));
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
        
        int vpn = Processor.vpnFromAddress(vaddr);
        // we need the following line for dynamic page sizing
        // numPhysPages = Machine.processor().updatePageSize();
        mappingRatio = Processor.virtualPageSize/Processor.physicalPageSize;
        // Evict current physical pages to get slots (include remote fetching)
        if(VMKernel.freePhysicalPages.size() < numPhysPages) {
            pageEviction(numPhysPages);
        }

        // Get new physical pages (this will actual drain physical page for other processes)
        // Modify if benchmark needs to run multiple process. keep it the same for now.
        PhysPage[] pages = new PhysPage[numPhysPages];
        for(int i = 0; i < numPhysPages; i++){
            int ppn = VMKernel.freePhysicalPages.remove(0);
            // TODO: try reusing the same phys page entries if it can reduce overheads
            pages[i] = new PhysPage(ppn);
        }
        
        // Each entry maintains the head
        for(int i = 0; i < numPhysPages; i++){
            pages[i].head = pages[0];

            if(i < numPhysPages - 1) {
                pages[i].next = pages[i + 1];
            }
        }

        // just a dummy benchmarking function
        fetchRemotePage(numPhysPages);

        // Update page table
        // for this virtual page it points to the head of the physical page list
        for (int i = 0; i < numPhysPages; i += mappingRatio) {
            this.pageTable[vpn].physPage = pages[i];
            this.pageTable[vpn].valid = true;
            Machine.processor().updateLRUList(vpn);
            vpn++;
        }
    }

    public void pageEviction(int num2Evict) {
        // Remove the first one from the LRU list
        for (int i = 0; i < num2Evict; i += mappingRatio) {
            int target = this.lruList.remove(0);
            this.pageTable[target].valid = false;
            PhysPage cur = this.pageTable[target].physPage;
            while(cur != null){
                // TODO: implement coallocating policy
                VMKernel.freePhysicalPages.add(cur.ppn);
                cur = cur.next;
            }
            this.pageTable[target].physPage = null;
        }
    }

    public void fetchRemotePage(int count) {
        System.out.println("fetch a page");
        try {
            remoteOutStream.println("4");
            String line = remoteInStream.readUTF();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    

	// private static final int pageSize = Processor.pageSize;
    private static final int physicalPageSize = Processor.physicalPageSize;
    private static final int virtualPageSize = Processor.virtualPageSize;
    private static int pageFaultCounter = 0;
	private static final char dbgProcess = 'a';

    // The number of physical pages to bring in for this page faults
    // The size of each physical page is fixed to 1K bytes
    // The virtual page size will be fixed but dynamic as the goal of this project
    private int numPhysPages = Processor.virtualPageSize/Processor.physicalPageSize;

    // The number of physical pages matches to a single virtual page
    private int mappingRatio = Processor.virtualPageSize/Processor.physicalPageSize;
	private static final char dbgVM = 'v';

    private static Socket socket;
    private static PrintStream remoteOutStream;
    private static DataInputStream remoteInStream;
}
