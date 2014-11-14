public class MultiCommitWriter implements Runnable {
    PrimitiveWFPT wfpt;

    public MultiCommitWriter(PrimitiveWFPT wfpt) {
	this.wfpt = wfpt;
    }

    public void run() {
	synchronized (StaticStorage.lock) {
	    StaticStorage.first();
	    System.out.println("Setting to 73");
	    wfpt.set(73);
	    System.out.println("Committing");
	    wfpt.commit();
	    if (wfpt.get() != 73) {
		StaticStorage.failed = true;
	    }
	}
	synchronized (StaticStorage.lock) {
	    StaticStorage.first();
	    System.out.println("Setting to 42");
	    wfpt.set(42);
	    System.out.println("Committing");
	    wfpt.commit();
	    if (wfpt.get() != 42) {
		StaticStorage.failed = true;
	    }
	}
    }
}
