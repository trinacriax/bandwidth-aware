/*
 * NAPA-WINE project
 * www.napa-wine.eu
 */
package bandwidth.core;

import peersim.config.*;
import peersim.core.*;
import peersim.dynamics.NodeInitializer;

/**
 * Initialize the Bandwidth Aware protocol.<p>
 * This protocol provides a network layer where peers have
 * different resources in term of both up-/down-load bandwidth.<p>
 * You have to provide the CDF of the bandwidth, using the CDF distribution setter.
 * It uses the methods defined in {@link BandwidthAwareSkeleton}.
 *
 * @author Alessandro Russo <russo@disi.unitn.it> <p> DISI - University of Trento (Italy) <p> Napa-Wine <www.napa-wine.eu>.
 * @version $Revision: 0.2$
 */
public class BandwidthAwareInitializer implements Control, NodeInitializer {

    // ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------
    private static final String PAR_PROT = "protocol";//associated protocol
    private static final String PAR_UP_BAND = "base_uplink";//uplink resources
    private static final String PAR_ACTIVE_UPLOAD = "active_upload"; //connection initilizied by the current node which involve the uplink
    private static final String PAR_ACTIVE_DOWNLOAD = "active_download";//connection initilizied by the current node which involve the downlink
    private static final String PAR_PASSIVE_UPLOAD = "passive_upload";//connection received by another node which involve the uplink
    private static final String PAR_PASSIVE_DOWNLOAD = "passive_download";//connection received by another node which involve the downlink
    private static final String PAR_DEBUG = "debug";//debug level
    private static final String PAR_BMS = "bms";//source's bandwidth multiplicaotr
    private static final String PAR_RATIODU = "ratiodu";//source's bandwidth multiplicator
    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    /**Protocol Identifier */
    private final int pid;
    /**Value uses for debugging*/
    private final int debug;
    /**Active upload*/
    private int active_upload;
    /**Active download*/
    private int active_download;
    /**Passive upload*/
    private int passive_upload;
    /**Passive download*/
    private int passive_download;
    /**Source upload bandwidth*/
    private int srcup;
    /**Ratio between download and uplaod */
    private int ratiodu;

    // 
    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance and reads parameters from the config file.
     * @param prefix
     */
    public BandwidthAwareInitializer(String prefix) {
        System.err.print("Init Bandwidth: ");
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        active_upload = Configuration.getInt(prefix + "." + PAR_ACTIVE_UPLOAD, 1);
        active_download = Configuration.getInt(prefix + "." + PAR_ACTIVE_DOWNLOAD, 1);
        passive_upload = Configuration.getInt(prefix + "." + PAR_PASSIVE_UPLOAD, 1);
        passive_download = Configuration.getInt(prefix + "." + PAR_PASSIVE_DOWNLOAD, 1);
        debug = Configuration.getInt(prefix + "." + PAR_DEBUG, 0);
        double bms = Configuration.getDouble(prefix + "." + PAR_BMS, -1);        
        srcup = (int) Math.round(bms * 1.0 * Configuration.getInt(prefix + "." + PAR_UP_BAND, -1));
        ratiodu = Configuration.getInt(prefix + "." + PAR_RATIODU, -1);
        System.err.println("Pid "+pid+", Active upload " + active_upload+", active download "+active_download+", passive upload "+passive_upload+
                ", passive download "+ passive_download+ ", Debug "+ debug+", SrcUp "+srcup );
    }

  // ------------------------------------------------------------------------
  // Methods
  // ------------------------------------------------------------------------
  /**
   * Initialize peers' fields and the source's resources.
   * @return Always return false.
   */
  @Override
  public boolean execute() {
    Node aNode = null;
    BandwidthAwareSkeleton bwa = null;
    for (int i = 0, len = Network.size(); i < len; i++) {
      //retrieve node instance
      aNode = Network.get(i);
      //retrieve protocol skeleton
      bwa = (BandwidthAwareSkeleton) aNode.getProtocol(pid);
      //reset object
      bwa.reset();
      //initilize the data structures in the node
      bwa.initialize();
      //set number of active connections in upload
      bwa.setActiveUpload(active_upload);
      //set number of active connections in download
      bwa.setActiveDownload(active_download);
      //set number of passive connections in upload
      bwa.setPassiveUpload(passive_upload);
      //set number of passive connections in download
      bwa.setPassiveDownload(passive_download);
      //set upload and download for the source
      bwa.setDownload(ratiodu);
      if (i == (len - 1) && this.srcup != -1) {//set source bandwidth
        bwa.initUpload(srcup);
      }
    }
    //set debug in the object,
    bwa.setDebug(debug);
    return false;
  }

  public void initialize(Node node) {
    //retrieve protocol skeleton
    BandwidthAwareSkeleton bwa = (BandwidthAwareSkeleton) node.getProtocol(pid);
    //reset object
    bwa.reset();
    //initilize the data structures in the node
    bwa.initialize();
    //set debug in the object,
    //bwa.setDebug(debug);
    //set number of active connections in upload
    bwa.setActiveUpload(active_upload);
    //set number of active connections in download
    bwa.setActiveDownload(active_download);
    //set number of passive connections in upload
    bwa.setPassiveUpload(passive_upload);
    //set number of passive connections in download
    bwa.setPassiveDownload(passive_download);
    //set upload and download for the source
    bwa.setDownload(ratiodu);   
  }
}
