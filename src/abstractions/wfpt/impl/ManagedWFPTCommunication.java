package abstractions.wfpt.impl;

import abstractions.wfpt.interfaces.ReaderManager;
import abstractions.wfpt.interfaces.WfptChannel;
import abstractions.wfpt.interfaces.WfptCommunication;
import abstractions.wfpt.interfaces.WfptManager;

/**
 * Created by adam.czerniejewski on 10/25/14.
 */
public class ManagedWFPTCommunication implements WfptCommunication {

    private final ReaderManager readerManager;
    private final WfptManager wfptManager;
    private volatile static ManagedWFPTCommunication wfptCommunicationInstance;

    private ManagedWFPTCommunication(final ReaderManager readerManager, final WfptManager wfptManager) {
        this.readerManager = readerManager;
        this.wfptManager = wfptManager;
    }

    public static WfptCommunication getInstance() {
        if (wfptCommunicationInstance == null) {
            synchronized (ManagedWFPTCommunication.class) {
                if (wfptCommunicationInstance == null) {
                    wfptCommunicationInstance = new ManagedWFPTCommunication(
                            ReaderManagerWithMessageQueue.getInstance(),
                            DumbGCWFPTManager.getInstance());
                }
            }
        }
        return wfptCommunicationInstance;
    }

    public WfptChannel getChannel(String readerThreadName) {
        return new DumbWfptChannel(readerThreadName);
    }

    public Message readNext() {
        Message m = readerManager.popMessage();
        //toDo null checks. Error here!
        wfptManager.remove(m.getId());
        return m;
    }
}
