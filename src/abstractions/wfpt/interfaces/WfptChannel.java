package abstractions.wfpt.interfaces;

/**
 * Created by adam.czerniejewski on 10/25/14.
 */
public interface WfptChannel {
    /**
     * Wraps object in ReadRequest and then commits to the wfpt
     * callback = false
     * @param payload
     */
    public void send(byte[] payload);

    /**
     * Wraps object in ReadRequest and then commits to the wfpt
     * with option for callback
     * @param payload
     * @param callback
     */
    public void send(byte[] payload,boolean callback);
}
