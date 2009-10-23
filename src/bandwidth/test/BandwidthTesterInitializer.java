package bandwidth.test;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

/**
 * TEST CLASS
 * @author Alessandro Russo
 * @version 1.0
 */
public class BandwidthTesterInitializer implements Control {

    // ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------
    private static final String PAR_PROT = "protocol";
    private static final String PAR_BANDWIDTH = "bandwidth";
    private static final String PAR_CHUNKS = "chunks";
    private static final String PAR_CHUNKSIZE = "chunk_size";
    private static final String PAR_DEBUG = "debug";
    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------         
    private final int number_of_chunks;
    private final int pid;
    private final int debug;
    private int bandwidthp;
    private long chunksize;

    // //////////////////////////////
    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance and read parameters from the config file.
     */
    public BandwidthTesterInitializer(String prefix) {
        number_of_chunks = Configuration.getInt(prefix + "." + PAR_CHUNKS);
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        debug = Configuration.getInt(prefix + "." + PAR_DEBUG);
        bandwidthp = Configuration.getPid(prefix + "." + PAR_BANDWIDTH);
        chunksize = Configuration.getLong(prefix + "." + PAR_CHUNKSIZE, 0);
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    public boolean execute() {
        System.err.print("- >> Alternate Initializer: Start...");
        for (int i = 0; i < Network.size(); i++) {
            Node aNode = Network.get(i);
            BandwidthDataSkeleton prot = (BandwidthDataSkeleton) aNode.getProtocol(pid);
            prot.resetAll();
            prot.Initialize(number_of_chunks);
            prot.setChunkSize(chunksize);
            prot.setBandwidth(bandwidthp);
            prot.setDebug(debug);
        }
        BandwidthTester zbt = null;
        for (int p = 0; p < Network.size(); p++) {
            zbt = (BandwidthTester) Network.get(p).getProtocol(pid);
            for (int i = 0; i < 10 - p; i++) {
                if (p == 0) {
                    zbt.chunk_list[i] = BandwidthInfo.NOT_OWNED;
                } else {
                    zbt.chunk_list[i] = 1000;
                }
            }
            System.out.println("Node " + p + " > " + zbt.bitmap());
        }
        //the receiver is the node 0
        EDSimulator.add(10, new BandwidthTesterMessage(null, Network.get(1), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(1), pid);
        EDSimulator.add(20, new BandwidthTesterMessage(null, Network.get(2), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(2), pid);
        EDSimulator.add(30, new BandwidthTesterMessage(null, Network.get(3), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(3), pid);
        EDSimulator.add(40, new BandwidthTesterMessage(null, Network.get(4), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(4), pid);
        EDSimulator.add(54, new BandwidthTesterMessage(null, Network.get(5), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(5), pid);
        EDSimulator.add(60, new BandwidthTesterMessage(null, Network.get(6), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(6), pid);
        EDSimulator.add(70, new BandwidthTesterMessage(null, Network.get(7), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(7), pid);
        System.err.print("finished\n");
        return false;
    }
}
