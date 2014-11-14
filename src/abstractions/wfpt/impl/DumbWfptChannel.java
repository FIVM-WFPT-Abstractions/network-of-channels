package wfpt.impl;

import abstractions.wfpt.interfaces.WfptChannel;

/**
 * Created by bhushan on 11/8/14.
 */
public class DumbWfptChannel implements WfptChannel{
    String wfptId;


    public DumbWfptChannel(String wfptId) {
        this.wfptId=wfptId;
    }

    public void send(byte[] payload) {

    }

    public void send(byte[] payload, boolean callback) {

    }
}
