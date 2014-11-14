package abstractions.wfpt.impl;

import abstractions.wfpt.base.AbstractWfptCommunication;
import abstractions.wfpt.interfaces.ReaderManager;
import abstractions.wfpt.interfaces.WfptChannel;
import abstractions.wfpt.interfaces.WfptManager;

/**
 * Created by adam.czerniejewski on 10/25/14.
 */
public class WFPTCommunication extends AbstractWfptCommunication {

    public WFPTCommunication(WfptManager wfptManager, ReaderManager readerManager) {
        this.readerManager = readerManager;
        this.wfptManager = wfptManager;
    }

    @Override
    public WfptChannel getChannel(String threadName) {
        return null;
    }

    @Override
    public Message readNext() {
        Message m = readerManager.popMessage();
        wfptManager.remove(m.getId());
        return m;
    }
}
