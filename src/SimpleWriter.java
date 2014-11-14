public class SimpleWriter implements Runnable {
    PrimitiveWFPT wfpt;

    public SimpleWriter(PrimitiveWFPT wfpt) {
	this.wfpt = wfpt;
    }

    public void run() {
	synchronized (StaticStorage.lock) {
	    StaticStorage.first();
	    System.out.println("Setting to 42");
	    wfpt.set(42);
	}
	synchronized (StaticStorage.lock) {
	    StaticStorage.first();
	    System.out.println("Committing");
	    wfpt.commit();
	    if (wfpt.get() != 42) {
		StaticStorage.failed = true;
	    }
	}
    }
}
