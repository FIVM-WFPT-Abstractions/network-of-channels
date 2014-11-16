package abstractions.wfpt.impl;

import abstractions.wfpt.interfaces.WfptManager;
import com.fiji.fivm.ThreadPriority;
import com.fiji.fivm.r1.WaitFreePairTransaction;

import java.util.Comparator;

/**
 * Created by adam.czerniejewski on 10/25/14.
 */
public class Message implements Comparable<Message>{
    private String sender;
    private boolean callBackRequested;
    private long wfptid;
    private final int messagePriority;
    private byte[] payload = null;

    public Message(String sender, boolean callBackRequested, long wfptid) {
        this.sender=sender;
        this.callBackRequested=callBackRequested;
        this.wfptid=wfptid;

        // Right now, the message priority is same as sender priority.
        messagePriority = Thread.currentThread().getPriority();
    }


    public String getSender() {
        return sender;
    }

    private boolean readAlready = false;

    public byte[] getPayload() {
        if(!readAlready) {
            ByteArrayWFPT wfpt =  (ByteArrayWFPT)DumbGCWFPTManager.getInstance().remove(wfptid);
            wfpt.update();
            readAlready=true;
            payload = wfpt.get();
        }
        return payload;
    }


    public boolean callBackRequested() {
        return callBackRequested;
    }

    public long getId() {
        return wfptid;
    }

    public int getMessagePriority() {
        return messagePriority;
    }


    public int compareTo(Message o) {
        return Integer.compare(this.getMessagePriority(),o.getMessagePriority());
    }
}
