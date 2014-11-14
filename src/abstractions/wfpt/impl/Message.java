package abstractions.wfpt.impl;

import abstractions.wfpt.interfaces.WfptManager;
import com.fiji.fivm.r1.WaitFreePairTransaction;

/**
 * Created by adam.czerniejewski on 10/25/14.
 */
public class Message{
    private WfptManager manager;
    private WaitFreePairTransaction wfpt;
    private String sender;
    private boolean callBackRequested;
    private String wfptid;

    public Message(WfptManager manager, String sender, boolean callBackRequested, String wfptid) {
        this.sender=sender;
        this.callBackRequested=callBackRequested;
        this.wfptid=wfptid;
        this.manager = manager;
    }


    public String getSender() {
        return sender;
    }

    private boolean readAlready = false;

    public byte[] getPayload() {
        if(!readAlready) {
            ByteArrayWFPT wfpt =  (ByteArrayWFPT)manager.remove(wfptid);
            wfpt.update();
            readAlready=true;
            return wfpt.get();
        } else {
            throw new IllegalStateException("The message was already Read!");
        }
    }


    public boolean callBackRequested() {
        return callBackRequested;
    }

    public String getId() {
        return wfptid;
    }
}
