package abstractions.wfpt.interfaces;

import abstractions.wfpt.impl.Message;

/**
 * Created by adam.czerniejewski on 11/16/14.
 */
public interface WfptCommunication {
    /**
     * returns a communication channel to the thread with name
     * @param threadName name of thread wish to communicate to
     * @return
     */
    public abstract WfptChannel getChannel(String threadName);

    /**
     * reads the first message that is contained in the current threads ReadRequestQue
     * @return
     */
    public abstract Message readNext();

}
