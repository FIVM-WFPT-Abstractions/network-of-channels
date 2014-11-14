import com.fiji.rt.Configuration;
import com.fiji.mvm.InterVMCommunication;

class SimpleReaderPayload {
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
	    System.out.println("Reader failed to find its InterPartitionWFPT!");
	    System.out.println("FAILED");
	    return;
	}
	int before = wfpt.get();
	try {
	    Thread.sleep(500);
	} catch (InterruptedException e) {
	    System.out.println("Unexpected interruption in reader!");
	}
	wfpt.update();
	int after = wfpt.get();
	System.out.println("Updated");
	if (before != 0 || after != 42) {
	    System.out.println("Incorrect values in reader: before = "+before+", after = "+after);
	    System.out.println("FAILED");
	} else {
	    System.out.println("Success!");
	}
    }
}
