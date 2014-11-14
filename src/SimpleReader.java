public class SimpleReader implements Runnable {
    PrimitiveWFPT wfpt;

    public SimpleReader(PrimitiveWFPT wfpt) {
	this.wfpt = wfpt;
    }

    public void run() {
	int before, during, after;
	synchronized (StaticStorage.lock) {
	    StaticStorage.second();
	    // The writer has written to its copy, but not committed
	    System.out.println("Getting before commit");
	    before = wfpt.get();
	    System.out.println("Mutating read object");
	    wfpt.set(73);
	}
	synchronized (StaticStorage.lock) {
	    StaticStorage.second();
	    // The writer has committed, but we have not updated
	    System.out.println("Getting after commit");
	    during = wfpt.get();
	    wfpt.update();
	    // The writer has committed and we have updated
	    System.out.println("Getting after update");
	    after = wfpt.get();
	}
	if (before != 0 || during != 73 || after != 42) {
	    System.out.println("before: "+before+" during: "+during+" after: "+after);
	    StaticStorage.failed = true;
	}
	synchronized (StaticStorage.signal) {
	    StaticStorage.signal.notify();
	}
    }
}
