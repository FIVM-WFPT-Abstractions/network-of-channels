package abstractions.wfpt.impl;

import abstractions.wfpt.interfaces.WfptManager;
import com.fiji.fivm.r1.WaitFreePairTransaction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by adam.czerniejewski on 10/25/14.
 */
public class DumbGCWFPTManager implements WfptManager {
    Map<String,WaitFreePairTransaction> wfptMap;
    public DumbGCWFPTManager() {
        wfptMap = new HashMap<String, WaitFreePairTransaction>();

    }

    public String createWFPT(String threadName) {
        String id = UUID.randomUUID().toString();
        //TODO make wfpt set reader/writer..
        return id;
    }

    public WaitFreePairTransaction remove(String id) {
       return wfptMap.remove(id);
    }
}
