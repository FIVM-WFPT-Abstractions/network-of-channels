import com.fiji.fivm.r1.WaitFreePairTransaction;

public class MultiValueWFPT extends WaitFreePairTransaction {
    public int i;
    public double d;
    public String s;
    public boolean b;
    public short t;

    public MultiValueWFPT() {
	i = 0;
	d = 0.0;
	s = "";
	b = false;
	t = 0;
    }

    public String toString() {
	return "{i: "+i+", d: "+d+", s: '"+s+"', b: "+b+", t: "+t+"}";
    }
}
