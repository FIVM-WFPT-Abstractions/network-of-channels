public class MultiValueWriter implements Runnable {
    MultiValueWFPT wfpt;

    public MultiValueWriter(MultiValueWFPT wfpt) {
	this.wfpt = wfpt;
    }

    public void run() {
	synchronized (StaticStorage.lock) {
	    StaticStorage.first();
	    wfpt.i = 86400;
	    wfpt.d = 6.022e23;
	    wfpt.s = "Four score and seven years ago";
	    wfpt.b = true;
	    wfpt.t = 42;
	}
	synchronized (StaticStorage.lock) {
	    StaticStorage.first();
	    if (!wfpt.s.equals("Four score and seven years ago")) {
		System.out.println("Failure before commit: "+wfpt);
		StaticStorage.failed = true;
	    }
	    wfpt.commit();
	    wfpt.i = 65536;
	    wfpt.d = 355.0/113.0;
	    wfpt.s = "When in the course of human events";
	    wfpt.b = false;
	    wfpt.t = 73;
	    if (wfpt.t != 73 || wfpt.i != 65536) {
		System.out.println("Failure after commit: "+wfpt);
		StaticStorage.failed = true;
	    }
	}
    }
}
