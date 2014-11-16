package abstractions.wfpt.impl;

import abstractions.wfpt.interfaces.WfptChannel;


/**
 * Created by bhushan on 11/8/14.
 */
public class DumbWfptChannel implements WfptChannel{
    long wfptId;
    ByteArrayWFPT wfpt;
    String readerThreadName;


    public DumbWfptChannel(String readerThreadName) {
        DumbGCWFPTManager gcwfptManager = (DumbGCWFPTManager) DumbGCWFPTManager.getInstance();
        wfptId = gcwfptManager.createWFPT(readerThreadName);
        wfpt = (ByteArrayWFPT)gcwfptManager.getWfptReference(wfptId);
        this.readerThreadName=readerThreadName;
    }

    public void send(byte[] payload) {
        send(payload,false);
    }

    public void send(byte[] payload, boolean callback) {
        DumbGCWFPTManager gcwfptManager = (DumbGCWFPTManager)DumbGCWFPTManager.getInstance();
        ReaderManagerWithMessageQueue readerManagerWithMessageQueue = (ReaderManagerWithMessageQueue)ReaderManagerWithMessageQueue.getInstance();
        wfpt.set(payload);
        wfpt.commit();
        if(!gcwfptManager.wfptExists(wfptId)) {
            gcwfptManager.addWfpt(wfptId,wfpt);
            Message m = new Message(Thread.currentThread().getName(),callback,wfptId);
            readerManagerWithMessageQueue.enqueueMessage(readerThreadName,m);
        }
    }
}
