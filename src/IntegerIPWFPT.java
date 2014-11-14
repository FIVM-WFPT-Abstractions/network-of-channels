import com.fiji.fivm.r1.InterPartitionWFPT;

public class IntegerIPWFPT extends InterPartitionWFPT {
    int value;

    public IntegerIPWFPT() { value = 0; }

    public void set(int value) {
	this.value=value;
    }

    public int get() {
	return value;
    }
}
