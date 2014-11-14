package abstractions.wfpt.interfaces;

import com.fiji.fivm.r1.WaitFreePairTransaction;

/**
 * Manages creation of WFPT or pool in future
 */
public interface WfptManager {
    /**
     * gets WFPT with the reader set to be the designated thread
     * @param threadName name of the reader thread
     * @return the id of the wfpt in the managed map
     */
    public String createWFPT(String threadName);

    /**
     * removes the WFPT from the map
     * @param id of the wfpt
     */
    public WaitFreePairTransaction remove(String id);
}
