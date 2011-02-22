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
    @Override
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
        zbt = (BandwidthTester) Network.get(0).getProtocol(pid);
        // thus, the node zero has no chunks, it's the "sink"
        for (int i = 0; i < 10; i++) {
            zbt.chunk_list[i] = BandwidthInfo.NOT_OWNED;
        }
        System.out.println("Sink Node " + 0 + " > " + zbt.bitmap());
        for (int p = 1; p < Network.size(); p++) {//for each node
            zbt = (BandwidthTester) Network.get(p).getProtocol(pid);
            for (int i = 0; i < 10 - p + 1; i++) {
                zbt.chunk_list[i] = 1000;//the p-th node has the i-th chunk                
            }
            System.out.println("Node " + p + " > " + zbt.bitmap());

            // the first node has all the chunks exept the last
            // the second node has all the chunks exept the last two
            // the third node has all the chunks exept the last three, and so on
        }
        //here we schedule some pushes
        EDSimulator.add(2, new BandwidthTesterMessage(null, Network.get(10), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(10), pid);
        EDSimulator.add(5, new BandwidthTesterMessage(null, Network.get(9), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(9), pid);
        EDSimulator.add(10, new BandwidthTesterMessage(null, Network.get(8), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(8), pid);
        EDSimulator.add(20, new BandwidthTesterMessage(null, Network.get(7), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(7), pid);
        EDSimulator.add(30, new BandwidthTesterMessage(null, Network.get(6), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(6), pid);
        EDSimulator.add(40, new BandwidthTesterMessage(null, Network.get(5), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(5), pid);
        EDSimulator.add(54, new BandwidthTesterMessage(null, Network.get(4), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(4), pid);
        EDSimulator.add(60, new BandwidthTesterMessage(null, Network.get(3), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(3), pid);
        EDSimulator.add(70, new BandwidthTesterMessage(null, Network.get(2), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(2), pid);
        EDSimulator.add(80, new BandwidthTesterMessage(null, Network.get(1), BandwidthInfo.SWITCH_PUSH, 0L), Network.get(1), pid);
        System.err.print("finished\n");
        return false;
    }
}
