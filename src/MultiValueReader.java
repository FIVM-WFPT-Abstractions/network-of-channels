public class MultiValueReader implements Runnable {
    MultiValueWFPT wfpt;

    public MultiValueReader(MultiValueWFPT wfpt) {
	this.wfpt = wfpt;
    }

    public void run() {
	synchronized (StaticStorage.lock) {
	    StaticStorage.second();
	    if (wfpt.i != 0
		|| wfpt.d != 0.0
		|| !wfpt.s.equals("")
		|| wfpt.b != false
		|| wfpt.t != 0) {
		System.out.println("Failure in initialized values: "+wfpt);
		StaticStorage.failed = true;
	    }
	    wfpt.s = "We the People";
	}
	synchronized (StaticStorage.lock) {
	    StaticStorage.second();
	    if (wfpt.t != 0
		|| !wfpt.s.equals("We the People")) {
		System.out.println("Failure before update: "+wfpt);
		StaticStorage.failed = true;
	    }
	    wfpt.update();
	    if (wfpt.i != 86400
		|| wfpt.d != 6.022e23
		|| !wfpt.s.equals("Four score and seven years ago")
		|| wfpt.b != true
		|| wfpt.t != 42) {
		System.out.println("Failure after update: "+wfpt);
		StaticStorage.failed = true;
	    }
	}
	synchronized (StaticStorage.signal) {
	    StaticStorage.signal.notify();
	}
    }
}
