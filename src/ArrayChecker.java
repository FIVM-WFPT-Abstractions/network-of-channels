public class ArrayChecker implements Runnable {
    ArrayWFPT wfpt;

    public ArrayChecker(ArrayWFPT wfpt) {
	this.wfpt = wfpt;
    }

    public void run() {
	synchronized (StaticStorage.lock) {
	    StaticStorage.second();
	    wfpt.update();
	}
	synchronized (StaticStorage.lock) {
	    StaticStorage.second();
	    for (int i = 0; i < wfpt.get().size(); i++) {
		if (wfpt.get(i) != i) {
		    StaticStorage.failed = true;
		}
	    }
	    wfpt.update();
	    for (int i = 0; i < wfpt.get().size(); i++) {
		if (wfpt.get(i) != i) {
		    StaticStorage.failed = true;
		}
	    }
	}
	synchronized (StaticStorage.signal) {
	    StaticStorage.signal.notify();
	}
    }
}
