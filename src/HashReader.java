import java.util.HashSet;

public class HashReader implements Runnable {
    ObjectWFPT< HashSet< Integer > > wfpt;

    public HashReader(ObjectWFPT< HashSet< Integer > > wfpt) {
	this.wfpt = wfpt;
    }

    public void run() {
	synchronized (StaticStorage.lock) {
	    StaticStorage.second();
	    if (wfpt.get() != null) {
		StaticStorage.failed = true;
	    }
	    wfpt.update();
	    if (wfpt.get() == null || wfpt.get().contains(42)) {
		StaticStorage.failed = true;
	    }
	}
	synchronized (StaticStorage.lock) {
	    StaticStorage.second();
	    if (wfpt.get() == null || wfpt.get().contains(42)) {
		StaticStorage.failed = true;
	    }
	    wfpt.update();
	    if (wfpt.get() == null || !wfpt.get().contains(42)
		|| wfpt.get().contains(73)) {
		StaticStorage.failed = true;
	    }
	}
	synchronized (StaticStorage.signal) {
	    StaticStorage.signal.notify();
	}
    }
}
