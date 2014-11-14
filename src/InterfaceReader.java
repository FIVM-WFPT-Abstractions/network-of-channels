public class InterfaceReader implements Runnable {
    Callback callback;
    IntBox ib;

    public static class Callback {
	public void call() { }
    }

    public InterfaceReader(IntBox ib, Callback callback) {
	this.ib = ib;
	this.callback = callback;
    }

    public void run() {
	int before = ib.get();
	callback.call();
	int after = ib.get();
	if (before != 0 || after != 42) {
	    System.out.println("Problem reading data via interface!");
	    StaticStorage.failed = true;
	}
    }
}
