package abstractions.wfpt.impl;

import abstractions.wfpt.interfaces.WfptManager;
import com.fiji.fivm.r1.WaitFreePairTransaction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adam.czerniejewski on 10/25/14.
 */
public class DumbGCWFPTManager implements WfptManager {
    private static final Object mapLock = new Object();
    private final Map<Long, WaitFreePairTransaction> wfptMap;
    private volatile static WfptManager wfptManagerInstance;
    private static volatile Long idCounter = 0L;

    private DumbGCWFPTManager() {
        synchronized (mapLock) {
            wfptMap = new HashMap<Long, WaitFreePairTransaction>();
        }
//        idCounter = new AtomicLong(0);
    }

    public static WfptManager getInstance() {
        if (wfptManagerInstance == null) {
            synchronized (DumbGCWFPTManager.class) {
                if (wfptManagerInstance == null) {
                    wfptManagerInstance = new DumbGCWFPTManager();
                }
            }
        }
        return wfptManagerInstance;
    }

    public long createWFPT(String readerThreadName) {
        final WaitFreePairTransaction wfpt = new ByteArrayWFPT();
//        System.out.println("Setting writer to " + Thread.currentThread().getName());
        wfpt.setWriter(Thread.currentThread());
//        System.out.println("Setting reader to "+readerThreadName);
        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
//        System.out.println("Reader thread name is "+ readerThreadName);
//        System.out.println("Currently active threads : "+ Thread.activeCount() + "Thread Array count : " + threads.length);

        StringBuilder threadsString = new StringBuilder();
        threadsString.append("Thread is : " + Thread.currentThread().getName() + " ");
        for(Thread t:threads) {
            if(t.getName().equals(readerThreadName)) {
                threadsString.append("----- Setting thread + " + t.getName() + " as reader");
//                System.out.print("Setting thread + " + t.getName() + " as reader");
                wfpt.setReader(t);
            }
        }
//        System.out.println(threadsString);
        long id = getNextId();
        synchronized (mapLock) {
            wfptMap.put(id, wfpt);
        }
        return id;
    }

    public WaitFreePairTransaction remove(long id) {
       synchronized (mapLock) {
//           System.out.println("Map size is "+wfptMap.size());
           return wfptMap.remove(id);
       }
    }

    public WaitFreePairTransaction getWfptReference(long id) {
        synchronized (mapLock) {
            return wfptMap.get(id);
        }
    }

    public boolean wfptExists(long wfptId) {
        synchronized (mapLock) {
            return wfptMap.containsKey(wfptId);
        }
    }

    public void addWfpt(long wfptId, ByteArrayWFPT wfpt) {
        synchronized (mapLock) {
            wfptMap.put(wfptId,wfpt);
        }
    }

    public long getNextId() {
        synchronized (idCounter) {
            return idCounter++;
        }
    }
}
