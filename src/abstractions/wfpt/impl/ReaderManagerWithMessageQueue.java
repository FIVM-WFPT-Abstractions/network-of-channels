package abstractions.wfpt.impl;

import abstractions.wfpt.interfaces.ReaderManager;

/**
 * Created by bhushan on 11/8/14.
 */
public class ReaderManagerWithMessageQueue implements ReaderManager {

    public Message popMessage() {
        return null;
    }

    public void enqueueMessage(String threadName, Message m) {

    }
}
