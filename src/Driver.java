import abstractions.wfpt.impl.DumbGCWFPTManager;
import abstractions.wfpt.impl.ManagedWFPTCommunication;
import abstractions.wfpt.impl.ReaderManagerWithMessageQueue;
import abstractions.wfpt.interfaces.ReaderManager;
import abstractions.wfpt.interfaces.WfptCommunication;
import abstractions.wfpt.interfaces.WfptManager;
import com.fiji.fivm.r1.WaitFreePairTransaction;
import com.fiji.fivm.r1.WFPTAccessException;
import com.fiji.fivm.r1.MemoryAreas;
import com.fiji.fivm.ThreadPriority;
import com.fiji.mvm.TimeSliceManager;
import com.fiji.mvm.VMConfiguration;
import com.fiji.mvm.Payload;
import com.fiji.mvm.InterVMCommunication;

import java.util.HashSet;

public class Driver {
    public static void main(String[] args) {
	String test = "allocate";
	if (args.length == 1) {
	    test = args[0];
	}

	if (test.equals("allocate")) {
	    allocateWFPT();
	} else if (test.equals("bindwriter")) {
	    bindWriter();
	} else if (test.equals("bindreader")) {
	    bindReader();
	} else if (test.equals("simplecommit")) {
	    simpleCommit();
	} else if (test.equals("cast")) {
	    castWFPT();
	} else if (test.equals("multicommit")) {
	    multiCommit();
	} else if (test.equals("object")) {
	    simpleObject();
	} else if (test.equals("gctest")) {
	    gcTest();
	} else if (test.equals("multivalue")) {
	    multiValue();
	} else if (test.equals("interfaces")) {
	    interfaces();
	} else if (test.equals("ipwfptinit")) {
	    ipwfptInit();
	} else if (test.equals("wfptabstractions")) {
        testWFPTAbstractions();
    }
    else {
	    System.out.println("Unknown test "+test);
	    System.exit(1);
	}
    }

    private static void testWFPTAbstractions() {
        ReaderManager readerManager = ReaderManagerWithMessageQueue.getInstance();
        WfptManager wfptManager = DumbGCWFPTManager.getInstance();
        WfptCommunication wfptCommunication = ManagedWFPTCommunication.getInstance();

        if (wfptCommunication.getChannel("hello") != null) {
            System.out.println("Abstractions Successful!");
        } else {
            System.out.println("FAILED!");
        }
    }

    public static void allocateWFPT() {
	StaticStorage.wfpt = new PrimitiveWFPT();
	System.out.println("Success!");
    }

    public static void bindWriter() {
	PrimitiveWFPT wfpt = new PrimitiveWFPT();
	int before, after;
	wfpt.setWriter(Thread.currentThread());
	wfpt.set(42);
	before = wfpt.get();
	wfpt.commit();
	after = wfpt.get();
	try {
	    wfpt.update();
	    System.out.println("FAILED");
	    return;
	} catch (WFPTAccessException e) {
	}
	if (before != 42 || after != 42) {
	    System.out.println("FAILED");
	} else {
	    System.out.println("Success!");
	}
    }

    public static void bindReader() {
	PrimitiveWFPT wfpt = new PrimitiveWFPT();
	int before, after;
	wfpt.setReader(Thread.currentThread());
	wfpt.set(42);
	before = wfpt.get();
	wfpt.update();
	after = wfpt.get();
	try {
	    wfpt.commit();
	    System.out.println("FAILED");
	    return;
	} catch (WFPTAccessException e) {
	}
	if (before != 42 || after != 42) {
	    System.out.println("FAILED");
	} else {
	    System.out.println("Success!");
	}
    }

    public static void simpleCommit() {
	PrimitiveWFPT wfpt = new PrimitiveWFPT();
	Thread reader = new Thread(new SimpleReader(wfpt));
	Thread writer = new Thread(new SimpleWriter(wfpt));

	readerwriter(wfpt, reader, writer);

	report();
    }

    public static void castWFPT() {
	ArrayWFPT aw = new ArrayWFPT();
	PrimitiveWFPT wfpt = new PrimitiveWFPT();
	CastReader cr = new CastReader(aw);
	if (cr == null) {
	    System.out.println("Looks like the compiler was right to complain!");
	    StaticStorage.failed = true;
	} if (!StaticStorage.failed) {
	    System.out.println("CastReader(ArrayWFPT) did not fail to init");
	    StaticStorage.failed = true;
	} else {
	    StaticStorage.failed = false;
	}
	Thread reader = new Thread(new CastReader(wfpt));
	if (StaticStorage.failed) {
	    System.out.println("FAILED");
	    return;
	}
	wfpt.setWriter(Thread.currentThread());
	wfpt.setReader(reader);
	StaticStorage.reset();
	reader.start();
	synchronized (StaticStorage.lock) {
	    StaticStorage.first();
	    System.out.println("Setting value and committing");
	    wfpt.set(42);
	    wfpt.commit();
	}
	report();
    }

    public static void multiCommit() {
	PrimitiveWFPT wfpt = new PrimitiveWFPT();
	Thread reader = new Thread(new SimpleReader(wfpt));
	Thread writer = new Thread(new MultiCommitWriter(wfpt));

	readerwriter(wfpt, reader, writer);

	report();
    }

    public static void simpleObject() {
	ObjectWFPT< HashSet< Integer > > wfpt =
	    new ObjectWFPT< HashSet< Integer > >();
	Thread reader = new Thread(new HashReader(wfpt));
	Thread writer = new Thread(new HashWriter(wfpt));

	readerwriter((WaitFreePairTransaction)wfpt, reader, writer);

	report();
	
    }

    public static void gcTest() {
	final int entries = 1048576;
	ArrayWFPT wfpt = new ArrayWFPT();
	Thread allocator = new Thread(new Allocator(wfpt, entries));
	Thread arraychecker = new Thread(new ArrayChecker(wfpt));

	readerwriter(wfpt, arraychecker, allocator);

	report();
    }

    public static void multiValue() {
	MultiValueWFPT wfpt = new MultiValueWFPT();
	Thread mvWriter = new Thread(new MultiValueWriter(wfpt));
	Thread mvReader = new Thread(new MultiValueReader(wfpt));

	readerwriter(wfpt, mvReader, mvWriter);

	report();
    }

    public static void interfaces() {
	System.out.println("Attempting with simple object");
	final IntegerStorage is = new IntegerStorage();
	Thread primReader = new Thread(
	    (Runnable)new InterfaceReader((IntBox)is,
					  new InterfaceReader.Callback() {
		    public void call() {
			is.set(42);
		    }
		}));
	primReader.start();

	try {
	    primReader.join();
	} catch (InterruptedException e) {
	    System.out.println("Join interrupted");
	}

	System.out.println("Attempting with WFPT object");
	final PrimitiveWFPT wfpt = new PrimitiveWFPT();
	Thread wfptReader = new Thread(
	    (Runnable)new InterfaceReader((IntBox)wfpt,
					  new InterfaceReader.Callback() {
		    public void call() {
			wfpt.update();
		    }
		}));
	wfpt.setReader(wfptReader);
	wfpt.setWriter(Thread.currentThread());
	wfpt.set(42);
	wfpt.commit();
	wfptReader.start();

	try {
	    wfptReader.join();
	} catch (InterruptedException e) {
	    System.out.println("Join interrupted");
	}

	if (StaticStorage.failed) {
	    System.out.println("FAILED");
	} else {
	    System.out.println("Success!");
	}
    }

    public static void ipwfptInit() {
	Payload reader = Payload.getPayloadByName("simplereader");
	Payload writer = Payload.getPayloadByName("simplewriter");
	TimeSliceManager tsm = new TimeSliceManager(2, ThreadPriority.FIFO_MIN);

	System.out.println("Initializing timeslices and starting manager");

	tsm.initTimeSlice(0, 250, reader.getNumInternalVMThreads()+2, 1);
	tsm.initTimeSlice(1, 250, writer.getNumInternalVMThreads()+2, 1);

	tsm.start();

	MemoryAreas.allocSharedScopeBacking(1024*1024);
	InterVMCommunication.initialize();
	System.out.println("Starting slice 0");

	tsm.getTimeSlice(0).spawnOneShot(new VMConfiguration(reader));
	tsm.getTimeSlice(1).spawnOneShot(new VMConfiguration(writer));

	System.out.println("Spawned");

	InterVMCommunication.initiateSetup();

	try {
	    Thread.sleep(2500);
	} catch (InterruptedException e) {
	    System.out.println("Sleep interrupted");
	}
    }

    public static void readerwriter(WaitFreePairTransaction wfpt,
				    Thread reader, Thread writer) {
	wfpt.setReader(reader);
	wfpt.setWriter(writer);

	StaticStorage.reset();

	reader.start();
	writer.start();
    }

    public static void report() {
	synchronized (StaticStorage.signal) {
	    try {
		StaticStorage.signal.wait();
	    } catch (InterruptedException e) {
		System.out.println("Signal interrupted!");
	    }
	}
	if (StaticStorage.failed) {
	    System.out.println("FAILED");
	} else {
	    System.out.println("Success!");
	}
    }
}
