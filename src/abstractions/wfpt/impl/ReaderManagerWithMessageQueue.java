package abstractions.wfpt.impl;

import abstractions.wfpt.interfaces.ReaderManager;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bhushan on 11/8/14.
 */
public class ReaderManagerWithMessageQueue implements ReaderManager {

    private final Map<String, PriorityQueue<Message>> readerMessagesQueueMap;
    private volatile static ReaderManager readerManagerInstance;

    private ReaderManagerWithMessageQueue() {
        readerMessagesQueueMap = new HashMap<String, PriorityQueue<Message>>();
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

    public Message popMessage() {
        String myThreadName = Thread.currentThread().getName();
        Message message = readerMessagesQueueMap.get(myThreadName).poll();

        return message;
    }

    public void enqueueMessage(String threadName, Message m) {
        readerMessagesQueueMap.get(threadName).add(m);
    }

    public void addReader(String readerThreadName) {
        PriorityQueue<Message> messageQ = new PriorityQueue<Message>();
        readerMessagesQueueMap.put(readerThreadName,messageQ);
    }
}
