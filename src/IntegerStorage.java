public class IntegerStorage implements IntBox {
    int value;

    public IntegerStorage() { value = 0; }

    public void set(int value) {
	this.value = value;
    }

    public int get() {
	return value;
    }
}
