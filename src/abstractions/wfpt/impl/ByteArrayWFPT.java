package abstractions.wfpt.impl;

import com.fiji.fivm.r1.WaitFreePairTransaction;

/**
 * Created by adam.czerniejewski on 10/25/14.
 */
public class ByteArrayWFPT extends WaitFreePairTransaction{
    byte[] value;
    Object writer;
    Object reader;
    final Object lockObject = new Object();
    static int numWFPTs = 0;

    public void setWriterDebug(Object writer) {
        this.writer = writer;
        super.setWriter(writer);
    }

    public void setReaderDebug(Object reader) {
        this.reader = reader;
        super.setReader(reader);
    }

    public ByteArrayWFPT() {
        /*synchronized (lockObject) {
            System.out.println("\n\nAllocating a new WFPT [WFPT No " + numWFPTs++ + "]\n\n");
        }*/
        value = null;
    }

    public void set(byte[] value) {
        this.value = value;
    }

    public byte[] get() {
        return value;
    }
}
