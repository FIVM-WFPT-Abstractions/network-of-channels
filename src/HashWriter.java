import java.util.HashSet;

public class HashWriter implements Runnable {
    ObjectWFPT< HashSet< Integer >> wfpt;

    public HashWriter(ObjectWFPT< HashSet< Integer > > wfpt) {
	this.wfpt = wfpt;
    }

    public void run() {
	synchronized (StaticStorage.lock) {
	    StaticStorage.first();
	    wfpt.set(new HashSet< Integer >());
	    wfpt.commit();
	    wfpt.set(new HashSet< Integer >());
	    wfpt.get().add(42);
	}
	synchronized (StaticStorage.lock) {
	    StaticStorage.first();
	    wfpt.commit();
	    wfpt.set(new HashSet< Integer >());
	    wfpt.get().add(73);
	}
    }
}
