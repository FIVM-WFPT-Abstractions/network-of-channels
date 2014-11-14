import com.fiji.rt.Configuration;
import com.fiji.mvm.InterVMCommunication;

class SimpleWriterPayload {
    public static class Config {
	public static void configure(Configuration c) {
	    c.setIVMCEntries(1);
	}
    }

    public static void main(String[] args) {
	InterVMCommunication.setupIVMC(new InterVMCommunication.SetupCallback() {
		public void setup() {
		    declare(IntegerIPWFPT.class, "simplereader", "simplewriter", 0);
		}
	    });

	IntegerIPWFPT wfpt=(IntegerIPWFPT)InterVMCommunication.get(0);

	if (wfpt == null) {
	    System.out.println("Writer failed to find its InterPartitionWFPT!");
	    System.out.println("FAILED");
	    return;
	}
	wfpt.set(42);
	wfpt.commit();
	System.out.println("Committed");
    }
}
