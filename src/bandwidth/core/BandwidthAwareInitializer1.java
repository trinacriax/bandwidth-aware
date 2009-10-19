package bandwidth.core;

/**
 *
 * @author ax
 */
import peersim.config.*;
import peersim.core.*;
import peersim.vector.VectControl;

public class BandwidthAwareInitializer1 extends  VectControl{

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
    private static final String PAR_PROT = "protocol";//associated protocol
    private static final String PAR_UP_BAND = "uplink";//uplink resources
    private static final String PAR_DOWN_BAND = "downlink";//downlink resources
    private static final String PAR_BW_PROB = "bdist";//bandiwdth distribution
    private static final String PAR_ACTIVE_UPLOAD = "active_upload"; //connection initilizied by the current node which involve the uplink
    private static final String PAR_ACTIVE_DOWNLOAD = "active_download";//connection initilizied by the current node which involve the downlink
    private static final String PAR_PASSIVE_UPLOAD = "passive_upload";//connection received by another node which involve the uplink
    private static final String PAR_PASSIVE_DOWNLOAD = "passive_download";//connection received by another node which involve the downlink
    private static final String PAR_DEBUG = "debug";//debug level
    private static final String PAR_BMP = "bmp";//peers' bandwidth multiplicator
    private static final String PAR_BMS = "bms";//source's bandwidth multiplicaotr
    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    /**Protocol Identifier */
    private final int pid;
    /**Value uses for debugging*/
    private final int debug;
    /**Matrtix for base-upload bandwidth distribution*/
    private int UploadBandwidth[];
    /**Matrtix for base-download bandwidth distribution*/
    private int DownloadBandwidth[];
    /**Matrtix for bandwidth distribution*/
    private double BandwidthProb[];
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
    /**Source download bandwidth*/
    private int srcdw;
    /**Peers' bandwidth multiplicator*/
    private double bmp[];
    /**Source bandwidth multiplicator*/
    private double bms;

    // 
    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance and read parameters from the config file.
     */
    public BandwidthAwareInitializer1(String prefix) {
        super(prefix);
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        active_upload = Integer.valueOf(Configuration.getInt(prefix + "." + PAR_ACTIVE_UPLOAD, 1));
        active_download =Integer.valueOf(Configuration.getInt(prefix + "." + PAR_ACTIVE_DOWNLOAD, 1));
        passive_upload = Integer.valueOf(Configuration.getInt(prefix + "." + PAR_PASSIVE_UPLOAD, 1));
        passive_download = Integer.valueOf(Configuration.getInt(prefix + "." + PAR_PASSIVE_DOWNLOAD, 1));
        debug = Integer.valueOf(Configuration.getInt(prefix + "." + PAR_DEBUG, 0));


        String _bmp[] = Configuration.getString(prefix + "." + PAR_BMP, "1").split(" ");        
        String _bprob[] = Configuration.getString(prefix + "." + PAR_BW_PROB, "1").split(" ");
        System.err.println("Init Bandwidth. Debug " + debug);
        bms = Configuration.getDouble(prefix + "." + PAR_BMS,0);
        if (_bmp.length == 1) {//BANDWIDTH HOMOGENEOUS NETWORK
            this.UploadBandwidth = new int[_bmp.length];
            this.DownloadBandwidth = new int[_bmp.length];
            this.BandwidthProb = new double[_bmp.length];
            bmp = new double[1];
            bmp[0] = Configuration.getDouble(prefix + "." + PAR_BMP, 1);
            System.err.print("Bmp[0] >" + bmp[0] + ">");
            double _upload = Configuration.getDouble(prefix + "." + PAR_UP_BAND, -1);
            if(bms == 0)//source has the same bandwidth of peers
                bms = bmp[0];
            srcup = (int) Math.ceil(bms * _upload);
            _upload = Math.round(_upload * bmp[0]);
            this.UploadBandwidth[0] = (int) _upload;
            System.err.print("UP " + this.UploadBandwidth[0] + "; ");
            double _download = Configuration.getDouble(prefix + "." + PAR_DOWN_BAND, -1);
            srcdw = (int) Math.ceil(bms * _download);
            _download = Math.round(_download * bmp[0]);
            this.DownloadBandwidth[0] = (int) _download;
            System.err.print("DW " + this.DownloadBandwidth[0] + "; ");
            this.BandwidthProb[0] = 1;
            System.err.print("Prob " + this.BandwidthProb[0] + ".\n");

        } else {//BANDWIDTH HETEROGENEOUS NETWORK
            this.UploadBandwidth = new int[_bmp.length];
            this.DownloadBandwidth = new int[_bmp.length];
            this.BandwidthProb = new double[_bmp.length];
            System.err.println("Init Bandwidth  " + _bmp.length);
            double _upload = Configuration.getDouble(prefix + "." + PAR_UP_BAND, -1);
            double _download = Configuration.getDouble(prefix + "." + PAR_DOWN_BAND, -1);
            if(bms == 0)//source has the highest bandwidth available
                bms = bmp[bmp.length-1];
            srcup = (int) Math.ceil(bms * _upload);
            srcdw = (int) Math.ceil(bms * _download);
            for (int i = 0; i < _bmp.length; i++) {
                bmp[i] = Configuration.getDouble(_bmp[i], 1);
                System.err.print("Bmp[" + i + "] >" + bmp[i] + ">");
                this.UploadBandwidth[i] = (int) Math.round(_upload * bmp[i]);
                System.err.print("UPBW [" + i + "] =" + this.UploadBandwidth[i] + "; ");
                this.DownloadBandwidth[0] = (int) Math.round(_download * bmp[i]);
                System.err.print("DWBW [" + i + "] =" + this.DownloadBandwidth[i] + "\n");
                this.BandwidthProb[i] = Double.parseDouble(_bprob[i]);
                System.err.print("\tBWPROB [" + i + "] =" + this.BandwidthProb[i] + "\n");
            }
        }        
        System.err.print("#Bandwidth init done\n");
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    /**
     * Initialize peers' fields.
     * @return Always return false.
     */
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
            if (debug >= 6) {
                System.out.println("\tNode index " + aNode.getIndex());
            }
            if (i == Network.size() - 1 && this.srcup != -1) {//set source bandwidth
                upload = srcup;
                download = srcdw;
            } else {//set peers bandwidth.
                upload = this.UploadBandwidth[0];
                download = this.DownloadBandwidth[0];
                long upmax = this.UploadBandwidth[this.UploadBandwidth.length - 1];
                long banda = CommonState.r.nextLong(((long) (upmax)));
                for (int j = 0; j < this.BandwidthProb.length - 1; j++) {
                    if (debug >= 6) {
                        System.out.println("\t" + j + ") " + banda + " > " + (upmax * this.BandwidthProb[j]));
                    }
                    if (banda > upmax * this.BandwidthProb[j]) {
                        if (debug >= 6) {
                            System.out.println("\tUpMax=" + this.UploadBandwidth[j + 1] + " UpMin=" + ((long) (this.UploadBandwidth[j + 1] / 4)) + " Upload=" + this.UploadBandwidth[j + 1] +
                                    "if(debug > 6)\n\tDwMax=" + this.DownloadBandwidth[j + 1] + " DwMin=" + ((long) (this.DownloadBandwidth[j + 1] / 4)) + " Download=" + (this.DownloadBandwidth[j + 1]));
                        }
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
            int minup = (int) (upload * .20);
            int mindw = (int) (download * .10);
            bwa.setUploadMin(minup);
            bwa.setDownloadMin(mindw);
            bwa.initialize();
            if (debug >= 6) {
                System.out.println("\t\t>>> Upload Max : " + upload + " Current " + upload + " Min " + minup+" ["+this.active_upload+","+this.passive_upload+"]");
                System.out.println("\t\t>>> Download Max : " + download + " Current " + download + " Min " + mindw+" ["+this.active_download+","+this.passive_download+"]");
            }
        }
        this.UploadBandwidth = this.DownloadBandwidth = null;
        this.BandwidthProb = null;
        return false;
    }
}