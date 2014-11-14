import com.fiji.fivm.r1.WaitFreePairTransaction;

public class ObjectWFPT< T > extends WaitFreePairTransaction {
    T value;

    public ObjectWFPT() {
	value = null;
    }

    public void set(T value) {
	this.value = value;
    }

    public T get() {
	return value;
    }
}
