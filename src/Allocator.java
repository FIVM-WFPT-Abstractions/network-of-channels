public class Allocator implements Runnable {
    ArrayWFPT wfpt;
    int entries;

    public Allocator(ArrayWFPT wfpt, int entries) {
	this.wfpt = wfpt;
	this.entries = entries;
    }

    public void run() {
	synchronized (StaticStorage.lock) {
	    StaticStorage.first();
	    for (int i = 0; i < entries; i++) {
		wfpt.append(i);
	    }
	    wfpt.commit();
	    wfpt.refresh();
	}
	synchronized (StaticStorage.lock) {
	    StaticStorage.first();
	    for (int i = 0; i < entries; i++) {
		wfpt.append(i);
	    }
	    wfpt.commit();
	    wfpt.refresh();
	    for (int i = 0; i < entries; i++) {
		wfpt.append(i);
	    }
	}
    }
}
