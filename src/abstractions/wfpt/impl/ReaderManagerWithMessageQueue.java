package abstractions.wfpt.impl;

import abstractions.wfpt.interfaces.ReaderManager;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by bhushan on 11/8/14.
 */
public class ReaderManagerWithMessageQueue implements ReaderManager {

    public static final Object mapObject = new Object();
    private final Map<String, PriorityQueue<Message>> readerMessagesQueueMap;
    private volatile static ReaderManager readerManagerInstance;

    private ReaderManagerWithMessageQueue() {
        synchronized (mapObject) {
            readerMessagesQueueMap = new HashMap<String, PriorityQueue<Message>>();
        }
    }

    public static ReaderManager getInstance() {
        if (readerManagerInstance == null) {
            synchronized (ReaderManagerWithMessageQueue.class) {
                if (readerManagerInstance == null) {
                    readerManagerInstance = new ReaderManagerWithMessageQueue();
                }
            }
        }
        return readerManagerInstance;
    }

    public Message popMessage() throws IllegalStateException{
        String myThreadName = Thread.currentThread().getName();
        Message message;
        synchronized (mapObject) {
            message = readerMessagesQueueMap.get(myThreadName).poll();
        }
        if(message==null) {
            throw new IllegalStateException("No message in readers queue try again later.");
        }
        return message;
    }

    public void enqueueMessage(String threadName, Message m) {
        synchronized (mapObject) {
            readerMessagesQueueMap.get(threadName).add(m);
        }
    }

    public void addReader(String readerThreadName) {
        PriorityQueue<Message> messageQ = new PriorityQueue<Message>();
        synchronized (mapObject) {
            readerMessagesQueueMap.put(readerThreadName, messageQ);
        }
    }
}
