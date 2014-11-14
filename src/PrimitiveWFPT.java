import com.fiji.fivm.r1.WaitFreePairTransaction;

public class PrimitiveWFPT extends WaitFreePairTransaction implements IntBox {
    public int value;

    public PrimitiveWFPT() {
	value = 0;
    }

    public void set(int value) {
	this.value = value;
    }

    public int get() {
	return value;
    }
}
