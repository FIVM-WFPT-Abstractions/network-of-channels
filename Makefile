FIVM = $(HOME)/APL_Work/FijiVM/fivm-wfpt-new
FIVMC = $(FIVM)/bin/fivmc
JAVAC = $(FIVM)/ecj/ecj

WRAPPER =

SOURCEFILES = src/Driver.java src/PrimitiveWFPT.java \
              src/StaticStorage.java src/SimpleReader.java \
              src/SimpleWriter.java src/MultiCommitWriter.java \
              src/ArrayWFPT.java src/Allocator.java \
              src/ArrayChecker.java src/HashReader.java \
              src/HashWriter.java src/ObjectWFPT.java \
              src/MultiValueWFPT.java src/MultiValueWriter.java \
              src/MultiValueReader.java src/CastReader.java \
              src/SimpleReaderPayload.java src/SimpleWriterPayload.java \
              src/IntegerIPWFPT.java src/IntBox.java \
              src/IntegerStorage.java src/InterfaceReader.java \
              src/abstractions/wfpt/base/AbstractWfptCommunication.java \
              src/abstractions/wfpt/impl/ByteArrayWFPT.java \
              src/abstractions/wfpt/impl/DumbGCWFPTManager.java \
              src/abstractions/wfpt/impl/DumbWfptChannel.java \
              src/abstractions/wfpt/impl/Message.java \
              src/abstractions/wfpt/impl/ReaderManagerWithMessageQueue.java \
              src/abstractions/wfpt/impl/ManagedWFPTCommunication.java \
              src/abstractions/wfpt/interfaces/ReaderManager.java \
              src/abstractions/wfpt/interfaces/WfptChannel.java \
              src/abstractions/wfpt/interfaces/WfptManager.java \
              src/abstractions/wfpt/interfaces/WfptCommunication.java

all: wfpt-test

rebuild:
	$(FIVMC) -o wfpt-test $(FIVMCFLAGS) build/wfpt-test.jar

check:
	$(WRAPPER) ./wfpt-test allocate
	$(WRAPPER) ./wfpt-test bindwriter
	$(WRAPPER) ./wfpt-test bindreader
	$(WRAPPER) ./wfpt-test simplecommit
	$(WRAPPER) ./wfpt-test cast
	$(WRAPPER) ./wfpt-test multicommit
	$(WRAPPER) ./wfpt-test object
	$(WRAPPER) ./wfpt-test gctest
	$(WRAPPER) ./wfpt-test multivalue
	$(WRAPPER) ./wfpt-test interfaces
	$(WRAPPER) ./wfpt-test ipwfptinit
	$(WRAPPER) ./wfpt-test wfptabstractions

wfpt-test: build/wfpt-test.jar simplereader.a simplewriter.a
	$(FIVMC) -o wfpt-test -m Driver --g-scoped-memory $(FIVMCFLAGS) \
	         build/wfpt-test.jar --link-payload simplereader \
	         --link-payload simplewriter

simplereader.a: build/wfpt-test.jar
	$(FIVMC) -o simplereader -m SimpleReaderPayload -b payload \
	         --g-scoped-memory $(FIVMCFLAGS) build/wfpt-test.jar

simplewriter.a: build/wfpt-test.jar
	$(FIVMC) -o simplewriter -m SimpleWriterPayload -b payload \
	         --g-scoped-memory $(FIVMCFLAGS) build/wfpt-test.jar

build/wfpt-test.jar: build/classes.list
	(cd build && jar cf wfpt-test.jar @classes.list)

build/classes.list: $(SOURCEFILES)
	$(JAVAC) -source 1.5 -target 1.5 \
	    -classpath $(FIVM)/lib/fivmr.jar:$(FIVM)/lib/fijirt.jar:$(FIVM)/lib/fivmcommon.jar \
	    -d build $(SOURCEFILES)
	(cd build && find . -name '*.class' -print) \
	    | sed -e 's#^\./##' > build/classes.list

clean:
	rm -rf wfpt-test wfpt-test.build build/* classes.list
	rm -rf simplereader.a simplewriter.a simplereader.a.build
	rm -rf simplewriter.a.build
	rm -f core core.* vgcore.*
	find . -name '*~' -delete

.PHONY: all rebuild clean check
