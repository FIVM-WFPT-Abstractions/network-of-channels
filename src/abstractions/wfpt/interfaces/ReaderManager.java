package abstractions.wfpt.interfaces;

import abstractions.wfpt.impl.Message;

/**
 * Created by adam.czerniejewski on 10/25/14.
 */
public interface ReaderManager {
    /**
     * should read message and re-allocate wfpt to pool, set reader and writers to null
     * or remove wfpt fromWfpt Manager so that it can be known to channel that it will not be read
     * if updated
     * @return
     */
    public Message popMessage();

    /**
     * Adds message to the priority queue of the thread with the coresponding name
     * @param threadName
     * @param m
     */
    public void enqueueMessage(String threadName, Message m);

    /**
     * Whatever!
     * @param readerThreadName
     */
    public void addReader(String readerThreadName);

    public Thread getReaderThread(String readerThreadName);

}
