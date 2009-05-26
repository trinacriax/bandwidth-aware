package bandwidth;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

public class BandwidthTesterInitializer implements Control {

    /**
     * Initialize the alternate protocol using parameter given in the configuration file.
     * 
     * @author Alessandro Russo
     * @version 1.0
     */

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
        chunksize = Configuration.getLong(prefix+"."+PAR_CHUNKSIZE,0);
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    public boolean execute() {
        System.err.print("- >> Alternate Initializer: Start...");
        Node source = Network.get(Network.size() - 1);//the source is always the last node.
        for (int i = 0; i < Network.size(); i++) {
            Node aNode = Network.get(i);
            BandwidthDataSkeleton prot = (BandwidthDataSkeleton) aNode.getProtocol(pid);
            prot.resetAll();
            prot.Initialize(number_of_chunks);
            prot.setChunkSize(chunksize);
            prot.setBandwidth(bandwidthp);            
            prot.setDebug(debug);
        }
//        Node zero = Network.get(0);
        BandwidthTester zbt = null;
        for(int p = 0; p < Network.size(); p++){
            zbt = (BandwidthTester) Network.get(p).getProtocol(pid);
        for(int i = 0; i < 10-p; i++){
            if(p==0)
                zbt.chunk_list[i] = Message.NOT_OWNED;
                else

                    zbt.chunk_list[i] = 1000;
        }
            System.out.println("Node "+p+" > " +zbt.bitmap());
        }

//        BandwidthAwareProtocol zbp = (BandwidthAwareProtocol) zero.getProtocol(bandwidthp);
//        zbp.sendData(chunksize, Network.get(1), zero, 0, bandwidthp);
        EDSimulator.add(100, new BandwidthTesterMessage(null, source, Message.SWITCH_PUSH, 0L), source, pid);
        EDSimulator.add(200, new BandwidthTesterMessage(null, Network.get(1), Message.SWITCH_PUSH, 0L), Network.get(1), pid);
        EDSimulator.add(350, new BandwidthTesterMessage(null, Network.get(2), Message.SWITCH_PUSH, 0L), Network.get(2), pid);
        System.err.print("finished\n");
        return false;
    }
}
