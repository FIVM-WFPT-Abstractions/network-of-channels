import abstractions.wfpt.impl.ByteArrayWFPT;
import abstractions.wfpt.impl.ManagedWFPTCommunication;
import abstractions.wfpt.impl.Message;
import abstractions.wfpt.impl.ReaderManagerWithMessageQueue;
import abstractions.wfpt.interfaces.WfptChannel;
import com.fiji.fivm.ThreadPriority;
import com.fiji.fivm.r1.MemoryAreas;
import com.fiji.fivm.r1.WFPTAccessException;
import com.fiji.fivm.r1.WaitFreePairTransaction;
import com.fiji.mvm.InterVMCommunication;
import com.fiji.mvm.Payload;
import com.fiji.mvm.TimeSliceManager;
import com.fiji.mvm.VMConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Driver {
    public static void main(String[] args) {
	String test = "allocate";
    int sleepTimeSec = 0;
	if (args.length == 1) {
	    test = args[0];
	} else if (args.length == 2) {
        test = args[0];
        sleepTimeSec = Integer.parseInt(args[1]);
    }

    Thread.currentThread().setName("Driver");

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
        testWFPTAbstractions(sleepTimeSec);
    } else if (test.equals("wfptabstractionssleep")) {
        testWFPTAbstractions(sleepTimeSec);
    } else if (test.equals("wfptabstractionsnthreads")) {
        testWFPTAbstrationsNThreads(sleepTimeSec);
    } else if (test.equals("wfptabstractionsquantitative")) {
        testWFPTAbstrationsQunatitiative(sleepTimeSec);
    } else if (test.equals("wfptabstractionsquantitativesend")) {
        testWFPTAbstrationsQunatitiativeSend(sleepTimeSec);
    } else if (test.equals("wfptabstractionsquantitativerecieve")) {
        testWFPTAbstrationsQunatitiativeRecieve(sleepTimeSec);
    } else if (test.equals("wfptabstractionsCSV")) {
        testWFPTAbstrationsCollection();
    } else if (test.equals("wfptsendmb")) {
        testWFPTSendMB(sleepTimeSec);
    } else if (test.equals("wfptrecievemb")) {
        testWFPTRecieveMB(sleepTimeSec);
    }
    else {
	    System.out.println("Unknown test "+test);
	    System.exit(1);
	}
    }

    private static void testWFPTRecieveMB(final int numberOfRecieves) {
        final ByteArrayWFPT wfpt = new ByteArrayWFPT();
        wfpt.setReader(Thread.currentThread());

        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                wfpt.setWriter(Thread.currentThread());
                wfpt.set("message".getBytes());
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread1.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long start = System.nanoTime();
        for(int i = 0; i< numberOfRecieves; i++) {
            wfpt.update();
            wfpt.get();
        }
        System.out.println(numberOfRecieves+" messages update and get in "+(System.nanoTime()-start)+" ns.");
        System.exit(1);

    }

    private static void testWFPTSendMB(final int numberOfSends) {
        ByteArrayWFPT waitFreePairTransaction = new ByteArrayWFPT();
        waitFreePairTransaction.setWriter(Thread.currentThread());
        long start = System.nanoTime();
        for(int i = 0; i< numberOfSends; i++) {
            waitFreePairTransaction.set("something".getBytes());
            waitFreePairTransaction.commit();
        }
        System.out.println(numberOfSends+" messages set and committed in "+(System.nanoTime()-start)+" ns.");
    }

    private static void testWFPTAbstrationsCollection() {
        System.out.println("Threads,nanoRunTime");
        for(int i=2;i<=100;i++) {
            testWFPTAbstrationsQunatitiative(i);
        }
    }

    private static void testWFPTAbstrationsQunatitiativeRecieve(final int numberOfRecieves) {
        List<Thread> threadList = new ArrayList<Thread>();

        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                String nextReader = "Thread1";
                WfptChannel wfptChannel = ManagedWFPTCommunication.getInstance().getChannel(nextReader);
                while(true) {
//                    System.out.println("Sending message to "+nextReader);
                    wfptChannel.send(("message").getBytes());
                }
            }
        });
        thread1.setName("Thread"+0);
        thread1.setPriority(1);
        threadList.add(thread1);

        Thread thread3 = new Thread(new Runnable() {
            public void run() {
                String nextReader = "Thread1";
                WfptChannel wfptChannel = ManagedWFPTCommunication.getInstance().getChannel(nextReader);
                while(true) {
//                    System.out.println("Sending message to "+nextReader);
                    wfptChannel.send(("message").getBytes());
                }
            }
        });
        thread3.setName("Thread"+2);
        thread3.setPriority(1);
        threadList.add(thread3);

        Thread thread4 = new Thread(new Runnable() {
            public void run() {
                String nextReader = "Thread1";
                WfptChannel wfptChannel = ManagedWFPTCommunication.getInstance().getChannel(nextReader);
                while(true) {
//                    System.out.println("Sending message to "+nextReader);
                    wfptChannel.send(("message").getBytes());
                }
            }
        });
        thread4.setName("Thread"+3);
        thread4.setPriority(1);
        threadList.add(thread4);


        Thread thread2 = new Thread(new Runnable() {
            public void run() {
                Message m = null;
                ReaderManagerWithMessageQueue.getInstance().addReader(Thread.currentThread().getName());
                while(m==null) {
                    try {
                        m = ManagedWFPTCommunication.getInstance().readNext();
                        m.getPayload();
                    } catch (IllegalStateException e) {
                    }
                }
                long start = System.nanoTime();
                int i = 0;
                while(i < numberOfRecieves) {
                    m=null;
                    while(m==null) {
                        try {
                            m = ManagedWFPTCommunication.getInstance().readNext();
                        } catch (IllegalStateException e){
//                            System.out.println(e.getMessage());
                        }
                    }
//                    System.out.println("Got message #" + i);
                    m.getPayload();
                    i++;
                }
                System.out.println(numberOfRecieves+" messages recieved in "+(System.nanoTime()-start)+" ns.");
                System.exit(1);
            }
        });
        thread2.setName("Thread" + 1);
        thread2.setPriority(1);
        threadList.add(thread2);
        ReaderManagerWithMessageQueue.getInstance().addReader(thread2.getName());

        for (Thread thread: threadList) {
            thread.start();
        }

        for (Thread thread: threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void testWFPTAbstrationsQunatitiativeSend(final int numberOfSends) {
        List<Thread> threadList = new ArrayList<Thread>();

            Thread thread1 = new Thread(new Runnable() {
                public void run() {
                    String temp = Thread.currentThread().getName();
                    int nextThread = Integer.parseInt(temp.replace("Thread", "")) + 1;
                    String nextReader = "Thread" + nextThread;
                    WfptChannel wfptChannel = ManagedWFPTCommunication.getInstance().getChannel(nextReader);
                    long start = System.nanoTime();
                    int i = 0;

                    while(i<numberOfSends) {
                        wfptChannel.send(("message" + "i").getBytes());
                        i++;
                    }
                    System.out.println(numberOfSends+" messages sent in "+(System.nanoTime()-start)+" ns.");
                    System.exit(1);
                }
            });
            thread1.setName("Thread" + 0);
            thread1.setPriority(1);
            threadList.add(thread1);

        Thread thread2 = new Thread(new Runnable() {
            public void run() {
                while(true){
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread2.setName("Thread" + 1);
        thread2.setPriority(1);
        threadList.add(thread2);
        ReaderManagerWithMessageQueue.getInstance().addReader(thread2.getName());

        for (Thread thread: threadList) {
            thread.start();
        }

        for (Thread thread: threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void testWFPTAbstrationsQunatitiative(final int numOfThreads) {
        List<Thread> threadList = new ArrayList<Thread>();
        for (int num=0; num < numOfThreads; num++) {

            Thread thread = new Thread(new Runnable() {
                public void run() {
                    // register current thread with reader manager
                    String temp = Thread.currentThread().getName();
                    ReaderManagerWithMessageQueue.getInstance().addReader(temp);

                    int nextThread = Integer.parseInt(temp.replace("Thread", "")) + 1;
                    String nextReader = "Thread" + nextThread;
                    if(nextThread == 1){
                        // first thread
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        WfptChannel wfptChannel = ManagedWFPTCommunication.getInstance().getChannel(nextReader);
                        wfptChannel.send((System.nanoTime()+"").getBytes());
                    }
                    else {
                        Message incomingMsg = null;
                        while(incomingMsg==null) {
                            try {
                                incomingMsg = ManagedWFPTCommunication.getInstance().readNext();
                            } catch (IllegalStateException e) {
//                                System.out.println(e.getMessage());
                            }
                        }
                        if (nextThread < numOfThreads) { //middle passer threads
                            WfptChannel wfptChannel = ManagedWFPTCommunication.getInstance().getChannel(nextReader);
                            wfptChannel.send(incomingMsg.getPayload());
                        } else {
                            //This is the last thread
                            long gotit = System.nanoTime();
                            System.out.println(numOfThreads + "," + (gotit - Long.parseLong(new String(incomingMsg.getPayload()))));
                        }
                    }
                }
            });

            thread.setName("Thread" + num);
            thread.setPriority(1);
            threadList.add(thread);
        }

        for (Thread thread: threadList) {
            thread.start();
        }

        for (Thread thread: threadList) {
            try {
                thread.join();
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }

    private static void testWFPTAbstrationsNThreads(final int numOfThreads) {

        List<Thread> threadList = new ArrayList<Thread>();

        for (int num=0; num < numOfThreads; num++) {

            Thread thread = new Thread(new Runnable() {

                public void run() {

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (true) {

//                        System.out.println("My name is " + Thread.currentThread().getName());
                        try {
                            /**
                             * Sleep between 0 and 400 milliseconds.
                             */
                            Thread.sleep((System.currentTimeMillis()%5 * 100) + 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        String randomReader = Thread.currentThread().getName();
                        while (randomReader.equals(Thread.currentThread().getName())) {
                            randomReader = "Thread" + System.currentTimeMillis()%numOfThreads;
                        }

                        WfptChannel wfptChannel = ManagedWFPTCommunication.getInstance().getChannel(randomReader);
                        String message = "Hello reader "+randomReader+". I am "+ Thread.currentThread().getName();
                        wfptChannel.send(message.getBytes());

                        while (true) {

                            Message incomingMsg = null;
                            try {
                                incomingMsg = ManagedWFPTCommunication.getInstance().readNext();
                            } catch (IllegalStateException e) {
                                System.out.println(e.getMessage());
                                break;
                            }

                            String msg = new String(incomingMsg.getPayload());

                            System.out.println("Message from " + incomingMsg.getSender() +
                                    " is ***" +
                                    msg +
                                    "*** with priority " +
                                    incomingMsg.getMessagePriority());
                        }
                    }
                    }
            });

            thread.setName("Thread" + num);
            thread.setPriority(num%10 + 1);
            threadList.add(thread);
            ReaderManagerWithMessageQueue.getInstance().addReader(thread.getName());
        }

        for (Thread thread: threadList) {
            thread.start();
        }

        for (Thread thread: threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void testWFPTAbstractions(final int sleepSeconds) {

        Thread writer = new Thread(new Runnable() {

            public void run() {
                System.out.println("I'm writer thread!");

                WfptChannel wfptChannel = ManagedWFPTCommunication.getInstance().getChannel("Reader");
                wfptChannel.send("Hello Reader".getBytes());
            }
        });

        Thread reader = new Thread(new Runnable() {

            public void run() {

                System.out.println("I'm reader thread");
                try {
                    Thread.sleep(sleepSeconds * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message incomingMsg = ManagedWFPTCommunication.getInstance().readNext();

                String msg = new String(incomingMsg.getPayload());

                System.out.println("Message from " + incomingMsg.getSender() +
                        " is " +
                        msg +
                        " with priority " +
                        incomingMsg.getMessagePriority());
            }
        });

        writer.setName("Writer");
        reader.setName("Reader");

        ReaderManagerWithMessageQueue.getInstance().addReader("Reader");
        writer.start();

        reader.start();

        try {
            writer.join();
            reader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
