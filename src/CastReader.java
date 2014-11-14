import com.fiji.fivm.r1.WaitFreePairTransaction;

public class CastReader implements Runnable {
    PrimitiveWFPT wfpt;

    public CastReader(WaitFreePairTransaction wfpt) {
	try {
	    this.wfpt = (PrimitiveWFPT)wfpt;
	} catch (ClassCastException e) {
	    wfpt = null;
	    System.out.println("Cast failure");
	    StaticStorage.failed = true;
	}
    }

    public void run() {
	int before, after;
	synchronized (StaticStorage.lock) {
	    StaticStorage.second();
	    before = wfpt.get();
	    System.out.println("Performing update");
	    wfpt.update();
	    System.out.println("Getting value");
	    after = wfpt.get();
	}
	if (before != 0 || after != 42) {
	    System.out.println("Value was incorrect");
	    StaticStorage.failed = true;
	}
	synchronized (StaticStorage.signal) {
	    StaticStorage.signal.notify();
	}
    }
}
