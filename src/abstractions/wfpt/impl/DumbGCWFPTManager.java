package abstractions.wfpt.impl;

import abstractions.wfpt.interfaces.WfptManager;
import com.fiji.fivm.r1.WaitFreePairTransaction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adam.czerniejewski on 10/25/14.
 */
public class DumbGCWFPTManager implements WfptManager {

    private final Map<Long, WaitFreePairTransaction> wfptMap;
    private volatile static WfptManager wfptManagerInstance;
    private static volatile Long idCounter = 0L;

    private DumbGCWFPTManager() {
        wfptMap = new HashMap<Long, WaitFreePairTransaction>();
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
        wfpt.setWriter(Thread.currentThread().getName());
        wfpt.setReader(readerThreadName);
//        long id = idCounter.getAndIncrement();
        long id = getNextId();
        wfptMap.put(id, wfpt);
        return id;
    }

    public WaitFreePairTransaction remove(long id) {
       return wfptMap.remove(id);
    }

    public WaitFreePairTransaction getWfptReference(long id) {
        return wfptMap.get(id);
    }

    public boolean wfptExists(long wfptId) {
        return wfptMap.containsKey(wfptId);
    }

    public void addWfpt(long wfptId, ByteArrayWFPT wfpt) {
        wfptMap.put(wfptId,wfpt);
    }

    public long getNextId() {
        synchronized (idCounter) {
            return idCounter++;
        }
    }
}
