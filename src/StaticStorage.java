import com.fiji.fivm.r1.WaitFreePairTransaction;

public class StaticStorage {
    public static WaitFreePairTransaction wfpt;
    public static boolean failed;

    public static Object lock = new Object();
    public static int first, prev;

    public static Object signal = new Object();

    public static void reset() {
	wfpt = null;
	failed = false;
	first = 0;
	prev = 0;
    }

    /**
     * Releases before second().  Must be called with lock held.
     */
    public static void first() {
	while (first > prev) {
	    try {
		lock.wait();
	    } catch (InterruptedException e) {
		System.out.println("First interrupted!");
		failed = true;
	    }
	}
	first++;
	lock.notify();
    }

    /**
     * Releases after first().  Must be called with lock held.
     */
    public static void second() {
	while (first == prev) {
	    try {
		lock.wait();
	    } catch (InterruptedException e) {
		System.out.println("Second interrupted!");
		failed = true;
	    }
	}
	prev = first;
	lock.notify();
    }
}
