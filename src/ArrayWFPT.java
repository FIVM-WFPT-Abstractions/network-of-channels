import java.util.ArrayList;

import com.fiji.fivm.r1.WaitFreePairTransaction;

public class ArrayWFPT extends WaitFreePairTransaction {
    public ArrayList< Integer > list;

    public ArrayWFPT() {
	refresh();
    }

    public void append(int val) {
	list.add(val);
    }

    public ArrayList< Integer > get() {
	return list;
    }

    public int get(int index) {
	return list.get(index);
    }

    public void refresh() {
	list = new ArrayList< Integer >();
    }
}
