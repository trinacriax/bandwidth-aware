package bandwidth;

/**
 *
 * @author ax
 */

import peersim.config.*;
import peersim.core.*;


public class BandwidthAwareInitializer implements Control {

    /**
     * Initialize the Bandwidth Aware protocol.
     * This protocol is useful for having a network layer where peers have
     * different resources in term of both up-/down-load bandwidth.
     * You have to provide the CDF of the bandwidth.
     * It uses the methods defined in {@link bandwidth.BandwidthAwareSkeleton}.
     *
     * @author Alessandro Russo
     * @version $Revision: 0.01$
     */

    // ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------
    private static final String PAR_PROT = "protocol";
    private static final String PAR_UP_BAND = "uploadBw";
    private static final String PAR_DOWN_BAND = "downloadBw";
    private static final String PAR_UP_PROB = "bandwidthPr";

    private static final String PAR_ACTIVE_UPLOAD = "active_upload";
    private static final String PAR_ACTIVE_DOWNLOAD = "active_download";
    private static final String PAR_PASSIVE_UPLOAD = "passive_upload";
    private static final String PAR_PASSIVE_DOWNLOAD = "passive_download";

    private static final String PAR_SRC_UP = "srcup";
    private static final String PAR_SRC_DOWN = "srcdw";

    private static final String PAR_DEBUG = "debug";

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    /**Protocol Identifier */
    private final int pid;
    /**Value uses for debugging*/
    private final int debug;
    /***/
    private int UploadBandwidth[];
    private int DownloadBandwidth[];
    private double BandwidthProb[];

    private int active_upload;
    private int active_download;
    private int passive_upload;
    private int passive_download;

    private int srcup;
    private int srcdw;

    // 
    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Creates a new instance and read parameters from the config file.
     */
    public BandwidthAwareInitializer(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        active_upload = Configuration.getInt(prefix + "." + PAR_ACTIVE_UPLOAD, 1);
        active_download = Configuration.getInt(prefix + "." + PAR_ACTIVE_DOWNLOAD, 1);
        passive_upload = Configuration.getInt(prefix + "." + PAR_PASSIVE_UPLOAD, 1);
        passive_download = Configuration.getInt(prefix + "." + PAR_PASSIVE_DOWNLOAD, 1);
        debug = Configuration.getInt(prefix + "." + PAR_DEBUG, 0);
        srcup = Configuration.getInt(prefix + "." + PAR_SRC_UP,-1);
        srcdw = Configuration.getInt(prefix + "." + PAR_SRC_DOWN,-1);
        String bandwidths[] = Configuration.getString(prefix + "." + PAR_UP_BAND, "").split(",");
        this.UploadBandwidth = new int[bandwidths.length];
        for (int i = 0; i < bandwidths.length; i++) {
            this.UploadBandwidth[i] = Integer.parseInt(bandwidths[i]);
            if(debug>5)
                System.out.println("UPBW ["+i+"] =" + this.UploadBandwidth[i]);
        }
        bandwidths = null;
        bandwidths = Configuration.getString(prefix + "." + PAR_DOWN_BAND, "").split(",");
        this.DownloadBandwidth = new int[bandwidths.length];
        for (int i = 0; i < bandwidths.length; i++) {
            this.DownloadBandwidth[i] = Integer.parseInt(bandwidths[i]);
            if(debug>5)
                System.out.println("DOWNBW ["+i+"] =" + this.DownloadBandwidth[i]);
        }
        bandwidths = null;
        bandwidths = Configuration.getString(prefix + "." + PAR_UP_PROB, "").split(",");
        this.BandwidthProb = new double[bandwidths.length];
        for (int i = 0; i < bandwidths.length; i++) {
            this.BandwidthProb[i] = Double.parseDouble(bandwidths[i]);
            if(debug>5)
                System.out.println("BWPROB ["+i+"] =" + this.BandwidthProb[i]);
        }
        bandwidths = null;
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    
    public boolean execute() {
        for (int i = 0; i < Network.size(); i++) {
            Node aNode = Network.get(i);
            BandwidthAwareSkeleton bwa = (BandwidthAwareSkeleton) aNode.getProtocol(pid);
            bwa.reset();
            bwa.setDebug(debug);
            bwa.setActiveUpload(active_upload);
            bwa.setActiveDownload(active_download);
            bwa.setPassiveUpload(passive_upload);
            bwa.setPassiveDownload(passive_download);
            long upload = 0;
            long download = 0;
            if(debug > 6)
                System.out.println("\tNode index "+aNode.getIndex());
            if(i == Network.size() - 1 && this.srcup != -1 ){
                upload = srcup;
                download = srcdw;                
            }
            else{
                upload = this.UploadBandwidth[0];
                download = this.DownloadBandwidth[0];
                long upmax = this.UploadBandwidth[this.UploadBandwidth.length - 1];
                long banda = CommonState.r.nextLong(((long) (upmax)));
                for (int j = 0; j < this.BandwidthProb.length - 1; j++) {
                    if(debug > 6)
                        System.out.println("\t" + j + ") " + banda + " > " + (upmax * this.BandwidthProb[j]));
                    if (banda > upmax * this.BandwidthProb[j]) {
                        if(debug > 6)
                            System.out.println("\tUpMax="+this.UploadBandwidth[j+1]+ " UpMin="+((long)(this.UploadBandwidth[j+1]/4))+ " Upload="+this.UploadBandwidth[j+1]+
                                    "if(debug > 6)\n\tDwMax="+this.DownloadBandwidth[j+1]+ " DwMin="+((long)(this.DownloadBandwidth[j+1]/4))+ " Download="+ (this.DownloadBandwidth[j+1]));
                        upload = this.UploadBandwidth[j + 1];
                        download = this.DownloadBandwidth[j + 1];
                    } else {
                        upload = this.UploadBandwidth[j];
                        download = this.DownloadBandwidth[j];
                        j = this.BandwidthProb.length;
                    }
                }
            }
            bwa.setUpload(upload);
            bwa.setDownload(download);
            bwa.setUploadMax(upload);
            bwa.setDownloadMax(download);
            int minup = (int)(upload*.2);
            int mindw = (int)(download*.2);
            bwa.setUploadMin(minup);
            bwa.setDownloadMin(mindw);
            bwa.initialize();
            if(debug > 6){
                System.out.println("\t\t>>> Upload Max : " + upload+" Current "+ upload + " Min " + minup);
                System.out.println("\t\t>>> Download Max : " + download+" Current "+ download + " Min " + mindw);
            }
        }
        this.UploadBandwidth = this.DownloadBandwidth = null;
        this.BandwidthProb = null;
        return false;
    }
}
