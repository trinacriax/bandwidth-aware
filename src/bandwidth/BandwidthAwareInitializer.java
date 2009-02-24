/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bandwidth;

/**
 *
 * @author ax
 */

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;


public class BandwidthAwareInitializer implements Control {

    /**
     * Initialize an aggregation protocol using a peak distribution; only one peak
     * is allowed. Note that any protocol implementing
     * {@link peersim.vector.SingleValue} can be initialized by this component.
     *
     * @author Alberto Montresor
     * @version $Revision: 1.12 $
     */

    // ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------
    private static final String PAR_PROT = "protocol";
    private static final String PAR_UP_BAND = "uploadBw";
    private static final String PAR_DOWN_BAND = "downloadBw";
    private static final String PAR_UP_PROB = "bandwidthPr";
//    private static final String PAR_ACTIVE_UPLOAD = "active_upload";
//    private static final String PAR_ACTIVE_DOWNLOAD = "active_download";
//    private static final String PAR_PASSIVE_UPLOAD = "passive_upload";
//    private static final String PAR_PASSIVE_DOWNLOAD = "passive_download";
    private static final String PAR_DEBUG = "debug";
    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    /** Value at the peak node.
     * Obtained from config property {@link #PAR_VALUE}. */
//    private final int number_of_chunks;
    /** Protocol identifier; obtained from config property {@link #PAR_PROT}. */
    private final int pid;

//	    private final int ciclo;
//    private final long chunk_size;
//    private final int source_multi;
//    private final long new_chunk;
    private final int debug;
    private long UploadBandwidth[];
    private long DownloadBandwidth[];
    private double BandwidthProb[];
////    private final int push_retry;
//    private final int pull_retry;
//    private final long switchtime;
//    private final int push_window,  pull_window;
//    private final int active_upload,  active_download,  passive_upload,  passive_download;//, upload_connection, download_connection;
    // //////////////////////////////
    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Creates a new instance and read parameters from the config file.
     */
    public BandwidthAwareInitializer(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
//        active_upload = Configuration.getInt(prefix + "." + PAR_ACTIVE_UPLOAD, 1);
//        active_download = Configuration.getInt(prefix + "." + PAR_ACTIVE_DOWNLOAD, 1);
//        passive_upload = Configuration.getInt(prefix + "." + PAR_PASSIVE_UPLOAD, 1);
//        passive_download = Configuration.getInt(prefix + "." + PAR_PASSIVE_DOWNLOAD, 1);
        debug = Configuration.getInt(prefix + "." + PAR_DEBUG);
        String bandwidths[] = Configuration.getString(prefix + "." + PAR_UP_BAND, "").split(",");
        this.UploadBandwidth = new long[bandwidths.length];
        for (int i = 0; i < bandwidths.length; i++) {
            this.UploadBandwidth[i] = Long.parseLong(bandwidths[i]);
//            System.out.println("UPBW ["+i+"] =" + this.UploadBandwidth[i]);
        }
        bandwidths = null;
        bandwidths = Configuration.getString(prefix + "." + PAR_DOWN_BAND, "").split(",");
        this.DownloadBandwidth = new long[bandwidths.length];
        for (int i = 0; i < bandwidths.length; i++) {
            this.DownloadBandwidth[i] = Long.parseLong(bandwidths[i]);
//            System.out.println("DOWNBW ["+i+"] =" + this.DownloadBandwidth[i]);
        }
        bandwidths = null;
        bandwidths = Configuration.getString(prefix + "." + PAR_UP_PROB, "").split(",");
        this.BandwidthProb = new double[bandwidths.length];
        for (int i = 0; i < bandwidths.length; i++) {
            this.BandwidthProb[i] = Double.parseDouble(bandwidths[i]);
//            System.out.println("BWPROB ["+i+"] =" + this.BandwidthProb[i]);
        }
        bandwidths = null;
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    public boolean execute() {
        Node source = Network.get(Network.size() - 1);
        long source_id = source.getID();
        for (int i = 0; i < Network.size(); i++) {
            Node aNode = Network.get(i);
            BandwidthAwareSkeleton bwa = (BandwidthAwareSkeleton) aNode.getProtocol(pid);
            bwa.reset();
            bwa.setDebug(debug);
//            prot.setActiveUpload(active_upload);
//            prot.setActiveDownload(active_download);
//            prot.setPassiveUpload(passive_upload);
//            prot.setPassiveDownload(passive_download);
            long upload = this.UploadBandwidth[0];
            long download = this.DownloadBandwidth[0];
//            long upload_min = (long) (upload / 4);
//            long download_min = (long) (download / 4);
//            prot = (ChunkList) tmp.getProtocol(pid);
//            prot.setUploadMax(upload);
//            prot.setDownloadMax(download);
//            prot.setUploadMin(upload_min);
//            prot.setDownloadMin(download_min);
//            prot.setUpload(upload);
//            prot.setDownload(download);
            long upmax = this.UploadBandwidth[this.UploadBandwidth.length - 1];
            long banda = CommonState.r.nextLong(((long) (upmax)));
            for (int j = 0; j < this.BandwidthProb.length - 1; j++) {
//                System.out.println("\t" + j + ") " + banda + " > " + (upmax * this.BandwidthProb[j]));
                if (banda > upmax * this.BandwidthProb[j]) {
//	    				System.out.println("\tUpMax="+this.UploadBandwidth[j+1]+ " UpMin="+((long)(this.UploadBandwidth[j+1]/4))+ " Upload="+this.UploadBandwidth[j+1]+
//	    						"\n\tDwMax="+this.DownloadBandwidth[j+1]+ " DwMin="+((long)(this.DownloadBandwidth[j+1]/4))+ " Download="+ (this.DownloadBandwidth[j+1]));
                    upload = this.UploadBandwidth[j + 1];
                    download = this.DownloadBandwidth[j + 1];
//	    				upload_min = (long)(upload/4);
//	    				download_min = (long)(download/4);

//	    				prot.setUploadMax(upload);
//	    				prot.setDownloadMax(download);
//	    				prot.setUploadMin(upload_min);
//	    				prot.setDownloadMin(download_min);
                } else {
                    upload = this.UploadBandwidth[j];
                    download = this.DownloadBandwidth[j];
                    j = this.BandwidthProb.length;
                }
            }
            bwa.setUpload(upload);
            bwa.setDownload(download);
            bwa.initialize();
//            System.out.println("\t\t>>> Upload = " + prot.getUpload() + "\n\tDownload = " + prot.getDownload());
        }
//      CHECK
//        double up[] = new double[3];
//        for(int j = 0 ; j < Network.size(); j++){
//            Node tmp = Network.get(j);
//            ChunkList prot = (ChunkList) tmp.getProtocol(pid);
//            if(prot.getUpload() <= UploadBandwidth[0])
//                up[0]++;
//            else if(prot.getUpload() <= UploadBandwidth[1])
//                up[1]++;
//            else
//                up[2]++;
//        }
//        up[0] /=Network.size();
//        up[1] /=Network.size();
//        up[1] += up[0];
//        up[2] /=Network.size();
//        up[2] +=up[1];
//        System.out.println("0 "+ up[0]+" 1 "+ up[1]+" 2 "+ up[2]);        
        this.UploadBandwidth = this.DownloadBandwidth = null;
        this.BandwidthProb = null;
        return false;
    }
}
