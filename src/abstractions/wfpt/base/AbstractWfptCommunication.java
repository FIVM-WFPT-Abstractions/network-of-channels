package abstractions.wfpt.base;

import abstractions.wfpt.impl.Message;
import abstractions.wfpt.interfaces.ReaderManager;
import abstractions.wfpt.interfaces.WfptChannel;
import abstractions.wfpt.interfaces.WfptManager;

/**
 * Created by adam.czerniejewski on 10/25/14.
 */
public abstract class AbstractWfptCommunication {
    protected WfptManager wfptManager;
    protected ReaderManager readerManager;

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
