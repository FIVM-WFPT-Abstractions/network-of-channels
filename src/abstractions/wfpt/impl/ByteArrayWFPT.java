package abstractions.wfpt.impl;

import com.fiji.fivm.r1.WaitFreePairTransaction;

/**
 * Created by adam.czerniejewski on 10/25/14.
 */
public class ByteArrayWFPT extends WaitFreePairTransaction{
    byte[] value;

    public ByteArrayWFPT() {
        value = null;
    }

    public void set(byte[] value) {
        this.value = value;
    }

    public byte[] get() {
        return value;
    }
}
