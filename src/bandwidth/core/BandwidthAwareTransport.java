package bandwidth.core;

import java.util.ArrayList;
import peersim.core.Protocol;
import peersim.core.Node;
import peersim.core.CommonState;
import peersim.edsim.EDSimulator;

/**
 * The class implements the data structure for the bandiwdth protocol.<p>
 * The data structure and the main methods used to provide the bandwdith mechanism
 * are in this class. <p> In particular the method for computing the transfer time:
 * it computes the time needed to delivery to set of data from the sender to the
 * receiver, otherwise it returns an error code which reflects the needed of up/down-link bandwidth.
 *
 * @author Alessandro Russo
 * @version $Revision: 0.02$
 */
public class BandwidthAwareTransport implements Protocol, BandwidthAwareSkeleton {

    private long upload;
    /**
     * Minimum upload bandiwdth.
     */
    private long upload_min;
    /**
     * Maximum upload bandwidth.
     */
    private long upload_max;
    /**
     * Upload bandwidth which will be removed in the next future.
     */
    private long upload_buf;
    /**
     * Current download bandwidth.
     */
    private long download;
    /**
     * Minimum download bandwidth.
     */
    private long download_min;
    /**
     * Maximum donwload bandwidth.
     */
    private long download_max;
    /**
     * Download bandwidth which will be removed in the next future.
     */
    private long download_buf;
    /**
     * Verbosity level.
     */
    private int debug;
    /**
     * Maximum number of connection started by the node that involves the uplink.
     * (e.g., node sends data)
     */
    private int active_upload;
    /**
     * Maximum number of connection started by the node that involves the downlink.
     * (e.g., node request data)
     */
    private int active_download;
    /**
     * Maximum number of connection received by the node that involves the uplink.
     * (e.g., node receives to request to send data)
     */
    private int passive_upload;
    /**
     * Maximum number of connection received by the node that involves the downlink.
     * (e.g., node receives to propose to receive data)
     */
    private int passive_download;
    /**
     * Current number of active upload.
     */
    private int active_up;
    /**
     * Current number of active download.
     */
    private int active_dw;
    /**
     * Current number of passive upload.
     */
    private int passive_up;
    /**
     * Current number of passive download.
     */
    private int passive_dw;
    /**
     * Data structure which collec connection elements which involve the uplink
     */
    private BandwidthConnectionList upload_connection_list;
    /**
     * Data structure which collec connection elements which involve the downlink
     */
    private BandwidthConnectionList download_connection_list;
    /**
     * Data structure used to collect elements produced during the process of
     * time delivery computation.
     */
    private ArrayList<BandwidthConnectionElement> elements;

    /**
     * Constructore is emepty
     * @param prefix
     */
    public BandwidthAwareTransport(String prefix) {
        super();
    }

    /**
     * Clone method implemented for Protocol class.
     * @return An object which is the clone of the current on.
     */
    public Object clone() {
        BandwidthAwareTransport bat = null;
        try {
            bat = (BandwidthAwareTransport) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        bat.upload = new Long(0);
        bat.upload_min = new Long(0);
        bat.upload_max = new Long(0);
        bat.upload_buf = new Long(0);

        bat.download = new Long(0);
        bat.download_min = new Long(0);
        bat.download_max = new Long(0);
        bat.download_buf = new Long(0);

        bat.debug = new Integer(0);

        bat.active_download = new Integer(0);
        bat.active_dw = new Integer(0);
        bat.active_upload = new Integer(0);
        bat.active_up = new Integer(0);
        bat.passive_download = new Integer(0);
        bat.passive_dw = new Integer(0);
        bat.passive_upload = new Integer(0);
        bat.passive_up = new Integer(0);
        bat.upload_connection_list = new BandwidthConnectionList();
        bat.download_connection_list = new BandwidthConnectionList();
        return bat;
    }

    /**
     * reset the main fields of this class.
     */
    public void reset() {
        this.upload = this.upload_max = this.upload_min = upload_buf = 0;
        this.download = this.download_max = this.download_min = download_buf = 0;
        this.active_up = this.active_upload = this.passive_up = this.passive_upload;
        this.active_dw = this.active_download = this.passive_dw = this.passive_download;
        this.debug = 0;
        this.upload_connection_list = this.download_connection_list = null;
    }

    /**
     * Initialize data structures.
     */
    public void initialize() {
        if (this.upload_connection_list == this.download_connection_list && this.upload_connection_list == null) {
            this.upload_connection_list = new BandwidthConnectionList();
            this.download_connection_list = new BandwidthConnectionList();
        }
    }

    /**
     * Gives the debug level.
     * @return the verbosity leve.
     */
    public int getDebug() {
        return this.debug;
    }

    /**
     * Set the verbosity level.
     * @param debug Verbosity level.
     */
    public void setDebug(int debug) {
        this.debug = debug;
    }

    /**
     * Initilize the current upload, the minimum and the maxium values,
     * it also executes the initialization of download.
     * @param _upload Maximum upload bandiwdth at the current node.
     */
    public void initUpload(long _upload) {
        if (this.getUploadMax() == 0) {
            this.setUpload(_upload);
            this.setUploadMax(_upload);
            this.setUploadMin((long) Math.ceil(_upload * .15));
            this.initDownload(_upload);
        }
    }

    /**
     * Set the current upload bandwidth
     * @param _upload Current upload bandwidth.
     */
    public void setUpload(long _upload) {      
        if (_upload < 0) {
            upload_buf += _upload;
            this.upload = 0;
        } else {
            if (upload_buf < 0) {
                upload_buf = upload_buf + _upload;
                if (upload_buf > 0) {
                    this.upload += upload_buf;
                    upload_buf = 0;
                }
            } else {
                this.upload = _upload;
            }
        }
    }

    /**
     * Updates the current available upload bandwidth.<p>
     * It adds the resources give back from to (part of) connection which
     * will be used in the next future from the same tranmission.
     * @param _upload Banwidth to update
     * @param bce BandwidthConnectionElement to check.
     */
    public void setUpload(long _upload, BandwidthConnectionElement bce) {
        bce = this.upload_connection_list.getRecord(bce.getSender(), bce.getReceiver(), bce.getTxId(), bce.getEnd());
        if (bce != null && bce.getStart() == CommonState.getTime() && !bce.check) {
            this.upload_buf += _upload;
        } else {
            this.setUpload(_upload);
        }
    }

    /**
     * Return the upload buffer which cointains the bandwidth to use in the next future.
     * @return Upload reserved for the next connection.
     */
    public long getUploadBUF() {
        return this.upload_buf;
    }

    /**
     * Return the current upload bandwidth.
     * @return Current upload bandwidth.
     */
    public long getUpload() {
        return this.upload;
    }

    /**
     * Set the minimum upload.
     * @param _upload_min Minimum upload.
     */
    public void setUploadMin(long _upload_min) {
        this.upload_min = _upload_min;
    }

    /**
     * Get the minimum upload.
     * @return Minimum upload.
     */
    public long getUploadMin() {
        return this.upload_min;
    }

    /**
     * Set the maximum upload.
     * @param _upload_max Maximum upload.
     */
    public void setUploadMax(long _upload_max) {
        this.upload_max = _upload_max;
    }

    /**
     * Get teh maximum upload.
     * @return Maximum upload.
     */
    public long getUploadMax() {
        return this.upload_max;
    }

    /**
     * Initilize the download bandwidth, the minimum and the maximum values.
     * @param _download Download bandwidth.
     */
    public void initDownload(long _download) {
        if (this.getDownloadMax() == 0) {
            if (_download == this.getUploadMax()) {
                _download = Math.round(this.getDownload() * _download);
            }
            this.setDownload(_download);
            this.setDownloadMax(_download);
            this.setDownloadMin((long) Math.ceil(_download * .15));
        }
    }

    /**
     * Set the current download bandwidth.
     * @param _download Current download bandwidth.
     */
    public void setDownload(long _download) {
        if (_download < 0) {
            download_buf += _download;
            this.download = 0;
        } else {
            if (download_buf < 0) {
                download_buf = download_buf + _download;
                if (download_buf > 0) {
                    this.download += download_buf;
                    download_buf = 0;
                }
            } else {
                this.download = _download;
            }
        }
    }

    /**
     * Updates the current available download bandwidth.<p>
     * It adds the resources give back from to (part of) connection which
     * will be used in the next future from the same tranmission.
     * @param _download Banwidth to update.
     * @param bce BandwidthConnectionElement to check.
     */
    public void setDownload(long _download, BandwidthConnectionElement bce) {
        bce = this.download_connection_list.getRecord(bce.getSender(), bce.getReceiver(), bce.getTxId(), bce.getEnd());
        if (bce != null && bce.getStart() == CommonState.getTime() && !bce.check) {
            this.download_buf += _download;
        } else {
            this.setDownload(_download);
        }
    }

    /**
     * Return the download buffer which cointains the bandwidth to use in the next future.
     * @return Download reserved for the next connection.
     */
    public long getDownloadBUF() {
        return this.download_buf;
    }

    /**
     * Return the current download bandwidth.
     * @return Current download bandwidth.
     */
    public long getDownload() {
        return this.download;
    }

    /**
     * Set the minimum download.
     * @param _download_min Minimum download.
     */
    public void setDownloadMin(long _download_min) {
        this.download_min = _download_min;
    }

    /**
     * Get the minimum download.
     * @return Minimum download.
     */
    public long getDownloadMin() {
        return this.download_min;
    }

    /**
     * Set the maximum download.
     * @param _download_max Maximum download.
     */
    public void setDownloadMax(long _download_max) {
        this.download_max = _download_max;
    }

    /**
     * Get teh maximum download.
     * @return Maximum download.
     */
    public long getDownloadMax() {
        return this.download_max;
    }

    /**
     * Get the number of connections issues by the node that involves the upload.
     * @return number of connections in upload.
     */
    public int getActiveUp() {
        return this.active_up;
    }

    /**
     * Add one to the number of active upload.
     */
    public void addActiveUp() {
        this.active_up++;
    }

    /**
     * Remove an active upload.
     */
    public void remActiveUp() {
        this.active_up--;
    }

    /**
     * Reset the active upload.
     */
    public void resetActiveUp() {
        this.active_up = 0;
    }

    /**
     * Get the number of connections issues by the node that involves the dwonload.
     * @return number of connections in download.
     */
    public int getActiveDw() {
        return this.active_dw;
    }

    /**
     * Add one ot number of active download.
     */
    public void addActiveDw() {
        this.active_dw++;
    }

    /**
     * Remove an active download.
     */
    public void remActiveDw() {
        this.active_dw--;
    }

    /**
     * Reset the active download.
     */
    public void resetActiveDw() {
        this.active_dw = 0;
    }

    /**
     * Get the number of connections received by the node that involves the upload.
     * @return number of connections received in upload.
     */
    public int getPassiveUp() {
        return this.passive_up;
    }

    /**
     * Add one ot number of passive download.
     */
    public void addPassiveUp() {
        this.passive_up++;
    }

    /**
     * Remove to passive upload.
     */
    public void remPassiveUp() {
        this.passive_up--;
    }

    /**
     * Reset trasmissione in passive upload.
     */
    public void resetPassiveUp() {
        this.passive_up = 0;
    }

    /**
     * Get the number of connections received by the node that involves the download.
     * @return number of connections received for the download.
     */
    public int getPassiveDw() {
        return this.passive_dw;
    }

    /**
     * Add one to number of passive download.
     */
    public void addPassiveDw() {
        this.passive_dw++;
    }

    /**
     * Remove to passive download.
     */
    public void remPassiveDw() {
        this.passive_dw--;
    }

    /**
     * Reset trasmissione in passive download.
     */
    public void resetPassiveDw() {
        this.passive_dw = 0;
    }

    /**
     * Set the  maximum number of  active upload.
     * @param active_upload max active uploads.
     */
    public void setActiveUpload(int active_upload) {
        this.active_upload = active_upload;
    }

    /**
     * Get the  maximum number of active upload.
     * @return maximum number of active download.
     */
    public int getActiveUpload() {
        return this.active_upload;
    }

    /**
     * Set the maximum number of active download.
     * @param active_download max number of active dowload.
     */
    public void setActiveDownload(int active_download) {
        this.active_download = active_download;
    }

    /**
     * Get the  maximum number of active download.
     * @return Max number of active download.
     */
    public int getActiveDownload() {
        return this.active_download;
    }

    /**
     * Set the maximum number of passive upload.
     * @param passive_upload Maximum number of passive upload.
     */
    public void setPassiveUpload(int passive_upload) {
        this.passive_upload = passive_upload;
    }

    /**
     * Get the  maximum number of passive upload.
     * @return maximum number of passive upload.
     */
    public int getPassiveUpload() {
        return this.passive_upload;
    }

    /**
     * Set the  maximum number of passive download.
     * @param passive_download maximum number of passive download.
     */
    public void setPassiveDownload(int passive_download) {
        this.passive_download = passive_download;
    }

    /**
     * Get the  maximum number of passive download.
     * @return maximum number of passive download.
     */
    public int getPassiveDownload() {
        return this.passive_download;
    }

    /**
     * Get the next time the upload bandiwdth will be available.
     * @return time in which the upload bandwidth will be available.
     */
    public long getEndUpload() {
        BandwidthConnectionElement bce = this.upload_connection_list.getFirstEnd();
        if (bce == null) {
            return 0;
        } else {
            return bce.getEnd();
        }
    }

    /**
     * Get the next time the upload bandiwdth will be available.
     * @return time in which the download bandwidth will be available.
     */
    public long getEndDownload() {
        BandwidthConnectionElement bce = this.download_connection_list.getFirstEnd();
        if (bce == null) {
            return 0;
        } else {
            return bce.getEnd();
        }
    }

    /**
     * Get the whole list of current connections which involve the upload.
     * @return A list of connections in upload.
     */
    public BandwidthConnectionList getUploadConnections() {
        return this.upload_connection_list;
    }

    /**
     * Get the whole list of current connections which involve the download.
     * @return A list of connections in download.
     */
    public BandwidthConnectionList getDownloadConnections() {
        return this.download_connection_list;
    }

    //XXX Bandwidth fluctuation to implement
    /**
     * Provide bandwidth fluctuation during the simulation. To implement!
     */
    public void fluctuationUpload() {
    }

    /**
     * Provide bandwidth fluctuation during the simulation. To implement!
     */
    public void fluctuationDownload() {
    }

    public String toString() {
        String result = "\n\t>> Upload: " + this.upload + " [" + this.upload_min + ":" + this.upload_max + "] - ";
        result += "Active " + this.getActiveUp() + "(" + this.getActiveUpload() + ")" + " Passive " + this.getPassiveUp() + "(" + this.getPassiveUpload() + ")";
        result += "\n\t<< Download: " + this.download + " [" + this.download_min + ":" + this.download_max + "] - ";
        result += "Active " + this.getActiveDw() + "(" + this.getActiveDownload() + ")" + " Passive " + this.getPassiveDw() + "(" + this.getPassiveDownload() + ")";
        return result;

    }

    /**
     *
     * This method compute that time needed to transfer to given amount of data in bits, from one sender to one receiver with to given end-to-end delay.<p>
     * This mechanism uses the priority sharingUtilizza il priority sharing, i.e. the first transmission takes as much resources as possibile,
     * the next one takes also as much resources as possible and so on with the same policy.<p>
     * A transmission and the corresponding allocation will be placed only when enough bandwidth is available and is greater than the minimum,
     * otherwise the transmission could take long time to end. The time is expresse in milliseconds.
     *
     * @param data_to_send_bits Amount of data to transmit in bits.
     * @param src Sender node.
     * @param rcv Receiver node.
     * @param eedelay End-to-end delay
     * @param pid Bandwidth protocol id
     * @return Time needed to perform the trasmission, an erro code otherwise.
     * 
     */
    public long sendData(long data_to_send_bits, Node src, Node rcv, long eedelay, int pid) {
        long finish = -1;
        if (data_to_send_bits == 0) {
            if (this.debug >= 4) {
                System.err.println("The application layer request to send ZERO bits");
            }
            return 1;
        }
        BandwidthAwareTransport sender = ((BandwidthAwareTransport) (src.getProtocol(pid)));
        BandwidthAwareTransport receiver = ((BandwidthAwareTransport) (rcv.getProtocol(pid)));
        long initupload = sender.getUpload();
        long initdownload = receiver.getDownload();
        if (sender.getUpload() < sender.getUploadMin()) {
            if (this.debug >= 4) {
                System.out.println("\t\t>> Node " + src.getID() + " has upload bw (" + initupload + ") less than minimum upload (" + sender.getUploadMin() + ").\nReturning code NO_UP: " + BandwidthMessage.NO_UP);
            }
            return BandwidthMessage.NO_UP;
        } else if (receiver.getDownload() < receiver.getDownloadMin()) {
            if (this.debug >= 3) {
                System.out.println("\t\t>> Node " + rcv.getID() + " has download bw (" + initdownload + ") less than minimum download (" + receiver.getDownloadMin() + ").\nReturning code NO_DOWN: " + BandwidthMessage.NO_DOWN);
            }
            return BandwidthMessage.NO_DOWN;
        }
        elements = new ArrayList<BandwidthConnectionElement>();
        long bandwidth;
        bandwidth = 0;
        int up_i, dw_i;
        up_i = dw_i = 0;
        BandwidthConnectionElement cupload, cdownload;
        long uploadBusy, uploadStartTime, uploadNextTime, uploadResidualTime;
        uploadBusy = uploadStartTime = uploadNextTime = uploadResidualTime = 0;
        long downloadBusy, downloadStartTime, downloadNextTime, downloadResidualTime;
        downloadBusy = downloadNextTime = downloadResidualTime = 0;
        long banda_up, banda_dw, old_up, old_dw;
        old_up = old_dw = 0;
        banda_up = sender.getUpload();
        if (sender.getUploadConnections().getSize() == 0) {
            uploadResidualTime = -1;
        } else {
            cupload = sender.upload_connection_list.getElement(up_i);
            if (this.debug >= 5) {
                System.out.println("First upload connection: " + cupload);
            }
            uploadBusy = cupload.getBandwidth();
            uploadStartTime = cupload.getStart();
            uploadNextTime = cupload.getEnd();
            uploadResidualTime = uploadNextTime - CommonState.getTime();
        }
        banda_dw = receiver.getDownload();
        if (receiver.getDownloadConnections().getSize() == 0) {
            downloadResidualTime = -1;
        } else {
            cdownload = (receiver.getDownloadConnections().getElement(dw_i));
            if (this.debug >= 5) {
                System.out.println("First download connection: " + cdownload);
            }
            downloadBusy = cdownload.getBandwidth();
            downloadStartTime = cdownload.getStart();
            downloadNextTime = cdownload.getEnd();
            downloadResidualTime = downloadNextTime - CommonState.getTime();
        }
        long baseTime = CommonState.getTime();
        long tableTime, mexTime;
        tableTime = mexTime = 0;
        int size = sender.upload_connection_list.getSize();
        long lastUpTime = 0;
        if (size > 0) {
            BandwidthConnectionElement cent = sender.upload_connection_list.getElement(size - 1);
            lastUpTime = cent.getEnd();
        }
        size = receiver.getDownloadConnections().getSize();
        long lastDwTime = 0;
        if (size > 0) {
            BandwidthConnectionElement cent = receiver.getDownloadConnections().getElement(size - 1);
            lastDwTime = cent.getEnd();
        }
        if (this.debug >= 5) {
            System.out.println("------------------------------------------------------ BANDWIDTH MANAGEMENT SYSTEM ------------------------------------------------------ ");
            System.out.println("Next sender upload ends at time: " + lastUpTime + "; Next receiver Download ends " + lastDwTime + "; One-Way-Delay " + eedelay + "; Bits to transmit are " + data_to_send_bits);
        }
        //Print connection list of the sender
        if (this.debug >= 10) {
            if (sender.upload_connection_list.getSize() > 0) {
                System.out.println(">>> Sender ");
                BandwidthConnectionList cl = sender.getUploadConnections();
                for (int i = 0; i < cl.getSize(); i++) {
                    System.out.println(cl.getElement(i));
                }
                System.out.println("<<<< Sender ");
            }
            //Print connection list of the receiver
            if (receiver.getDownloadConnections().getSize() > 0) {
                System.out.println(">>>> Receiver ");
                BandwidthConnectionList cl = receiver.getDownloadConnections();
                for (int i = 0; i < cl.getSize(); i++) {
                    System.out.println(cl.getElement(i));
                }
                System.out.println("<<<< Receiver ");
            }
        }
        boolean flag = true;
        while (data_to_send_bits > 0) {
            bandwidth = Math.min(banda_up, banda_dw);
            if (this.debug >= 5) {
                System.out.println("Time " + baseTime + ". Bits to transmit: " + data_to_send_bits + ". BW Sender: " + banda_up + ", BW Receiver: " + banda_dw + ", Bandwidth for transmission " + bandwidth);
            }
            //From baseTime both sendere and receiver have all the bandwidth available.
            if ((uploadResidualTime == -1 && downloadResidualTime == -1)) {
                mexTime = Math.round((data_to_send_bits / ((double) bandwidth)) * 1000);
                mexTime = (mexTime == 0 ? 1 : mexTime);
                if (this.debug >= 5) {
                    System.out.println("\tSender and Receiver do not have any connection. They are transmissing " + data_to_send_bits + " with bandwidth " + bandwidth + " and the transmission takes " + mexTime + " ms");
                }
                data_to_send_bits = 0;
                tableTime = baseTime + mexTime;
                mexTime = tableTime - CommonState.getTime();
                finish = mexTime;
                BandwidthConnectionElement element = new BandwidthConnectionElement(src, rcv, bandwidth, baseTime, tableTime, 0);
                elements.add(element);
                if (this.debug >= 5) {
                    System.out.println("\t1 Adding element " + element.toString() + " both upload and download connections list.");
                }
                element = null;
                if (flag) {
                    if (this.debug >= 5) {
                        System.out.println("1 >>> Removing bandwidth " + bandwidth + " at time " + baseTime + ". This will be returned at time " + tableTime);
                    }
                    old_up = sender.getUpload();
                    old_dw = receiver.getDownload();
                    sender.setUpload(sender.getUpload() - bandwidth);
                    receiver.setDownload(receiver.getDownload() - bandwidth);
                    flag = false;
                } else {
                    if (this.debug >= 5) {
                        System.out.println("1 >>> Removing bandwidth with message " + bandwidth + " at time " + baseTime);
                    }
                    mexTime = baseTime - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.println("\tSending Update Upload to " + src.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
                    }
                    if (this.debug >= 5) {
                        System.out.println("\tSending Update Download to " + rcv.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
                    }
                }
                mexTime = tableTime - CommonState.getTime();
                if (this.debug >= 5) {
                    System.out.println("\tSending Update Upload to " + src.getID() + " for giving back " + bandwidth + " at time " + tableTime + " MexTime " + mexTime);
                }
                if (this.debug >= 5) {
                    System.out.println("\tSending Update Download to " + rcv.getID() + " for giving back " + bandwidth + " at time " + tableTime + " MexTime " + mexTime);
                }
            } // The sender is executing an upload and the receiver either is exectuing a download which finishes after the upload OR does not have any download.
            else if (uploadResidualTime != -1 && (uploadResidualTime < downloadResidualTime || downloadResidualTime == -1)) {
                if (this.debug >= 5) {
                    System.out.println("The upload either finishes before the download (" + uploadResidualTime + ") or the download is idle.");
                }
                // Check whether the transmission could finish before the end of the active upload or not.
                if (Math.round(((double) bandwidth) * uploadResidualTime / 1000.0D) >= data_to_send_bits) {
                    long txTime = Math.round(data_to_send_bits / ((double) bandwidth) * 1000);
                    txTime = (txTime == 0 ? 1 : txTime);
                    tableTime = baseTime + txTime;
                    BandwidthConnectionElement element = new BandwidthConnectionElement(src, rcv, bandwidth, baseTime, tableTime, CommonState.r.nextLong());
                    elements.add(element);
                    if (this.debug >= 5) {
                        System.out.println("\t2 Adding element " + element.toString());
                    }
                    element = null;

                    if (flag) {
                        old_up = sender.getUpload();
                        old_dw = receiver.getDownload();

                        sender.setUpload(sender.getUpload() - bandwidth);
                        receiver.setDownload(receiver.getDownload() - bandwidth);
                        flag = false;
                        if (this.debug >= 5) {
                            System.out.println("2 >>> Removing bandwidth " + bandwidth + " at time " + baseTime + " , it will be gave back at time " + tableTime);
                        }
                    } else {
                        mexTime = baseTime - CommonState.getTime();
                        if (this.debug >= 5) {
                            System.out.println("2 >>> Removing bandwidth with message " + bandwidth + " at time " + baseTime + " , it will be gave back at time " + tableTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSending Update Upload to " + src.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSending Update Download to " + rcv.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
                        }
                    }
                    mexTime = tableTime - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.println("\tSending Update Uplaod to " + src.getID() + " for " + bandwidth + " at time " + tableTime + " MexTime " + (tableTime - CommonState.getTime()));
                    }
                    if (this.debug >= 5) {
                        System.out.println("\tSending Update Download to " + rcv.getID() + " for " + bandwidth + " at time " + tableTime + " MexTime " + (tableTime - CommonState.getTime()));
                    }
                    if (this.debug >= 5) {
                        System.out.println("The transmission of data (" + data_to_send_bits + ") finishes before " + uploadResidualTime + ", it will take " + txTime + " ms, Adding element at time " + tableTime);
                    }
                    finish = mexTime;
                    data_to_send_bits = 0;
                } else {
                    //The transmission does not finish before the current upload: it has to be divided in two or more parts.
                    data_to_send_bits = data_to_send_bits - Math.round(bandwidth * ((double) uploadResidualTime) / 1000);
                    tableTime = uploadNextTime;
                    if (this.debug >= 5) {
                        System.out.println("Data to transmit " + data_to_send_bits + " BaseTime " + baseTime + " TableTime " + tableTime);
                    }
                    BandwidthConnectionElement element = new BandwidthConnectionElement(src, rcv, bandwidth, baseTime, tableTime, CommonState.r.nextLong());
                    elements.add(element);
                    if (this.debug >= 5) {
                        System.out.println("\t3 Adding element " + element.toString());
                    }
                    element = null;
                    if (flag) {
                        old_up = sender.getUpload();
                        old_dw = receiver.getDownload();
                        sender.setUpload(sender.getUpload() - bandwidth);
                        receiver.setDownload(receiver.getDownload() - bandwidth);
                        banda_up -= bandwidth;
                        flag = false;
                        if (this.debug >= 5) {
                            System.out.println("3 >>> Removing bandwidth " + bandwidth + " at time " + baseTime + " , it will be gave back at time " + tableTime);
                        }
                    } else {
                        mexTime = baseTime - CommonState.getTime();
                        if (this.debug >= 5) {
                            System.out.println("3 >>> Removing bandwidth with message " + bandwidth + " dat time " + baseTime + " , it will be gave back at time " + tableTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSending Update Upload to " + src.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSending Update Download to " + rcv.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
                        }
                    }
                    mexTime = tableTime - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.println("\tSending Update Upload to " + src.getID() + " for " + bandwidth + " at time " + tableTime + " MexTime " + mexTime);
                    }
                    if (this.debug >= 5) {
                        System.out.println("\tSending Update Download to " + rcv.getID() + " for " + bandwidth + " at time " + tableTime + " MexTime " + mexTime);
                    }
                    baseTime = uploadNextTime;
                    if (this.debug >= 5) {
                        System.out.print("Updating upload bandwidth " + banda_up + ", adding " + uploadBusy);
                    }
                    banda_up += uploadBusy;
                    if (this.debug >= 5) {
                        System.out.println(" = upload becomes " + banda_up);
                    }
                    if (up_i < sender.upload_connection_list.getSize() - 1 && uploadNextTime < baseTime) {
                        do {
                            if (this.debug >= 5) {
                                System.out.println("Updating upload " + baseTime);
                            }
                            if (this.debug >= 5) {
                                System.out.println(up_i + " - updating upload " + sender.upload_connection_list.getElement(up_i));
                            }
                            up_i++;
                            //banda usata nella prox trasmissione
                            cupload = sender.upload_connection_list.getElement(up_i);
                            uploadBusy = cupload.getBandwidth();
                            uploadStartTime = cupload.getStart();
                            uploadNextTime = cupload.getEnd();
                            uploadResidualTime = uploadNextTime - baseTime;
                            if (this.debug >= 5) {
                                System.out.println("upload Start " + uploadStartTime + " UpNext " + uploadNextTime + " currentTime " + CommonState.getTime() + " " + up_i + " - updating upload " + cupload);
                            }
                            if (banda_up <= 0) {
                                if (this.debug >= 5) {
                                    System.out.println("Warning! Updating upload - in the next future I'll not have upload bandwidth: it will be used for other transmission(s).");
                                }
                                if (this.debug >= 5) {
                                    System.out.println("\tNode " + src.getID() + " has its upload bandwidth busy ion the future, it cannot perform the transfer to Nodo " + rcv.getID());
                                }
                                sender.setUpload(initupload);
                                receiver.setDownload(initdownload);
                                elements = null;
                                //Notify no bandwidth in upload
                                return BandwidthMessage.NO_UP;
                            }
                        } while (up_i < sender.upload_connection_list.getSize() - 1 && uploadNextTime <= baseTime);
                        //check all connections
                    } else {
                        uploadResidualTime = -1;
                    }
                    if (this.debug >= 5) {
                        System.out.println(" to " + banda_up);
                    }
                }
            }//There is a connection in download and the current download finished before the upload or no upload are active.
            else if (downloadResidualTime != -1 && (downloadResidualTime <= uploadResidualTime || uploadResidualTime == -1)) {
                if (this.debug >= 5) {
                    System.out.println("\tThe download finished before the upload:" + downloadResidualTime + " or the upload is idle.");
                }
                //Checking if the transmission finishes before the current download
                if (Math.round(((double) bandwidth) * downloadResidualTime / 1000) >= data_to_send_bits) {
                    //The node is able to transmit the whole data before the ends of the first connection
                    long txTime = Math.round(data_to_send_bits / ((double) bandwidth) * 1000);
                    txTime = (txTime == 0 ? 1 : txTime);
                    tableTime = baseTime + txTime;

                    BandwidthConnectionElement element = new BandwidthConnectionElement(src, rcv, bandwidth, baseTime, tableTime, CommonState.r.nextLong());
                    elements.add(element);
                    if (this.debug >= 5) {
                        System.out.println("\t4 Adding element " + element.toString());
                    }
                    element = null;
                    if (flag) {
                        old_up = sender.getUpload();
                        old_dw = receiver.getDownload();
                        sender.setUpload(sender.getUpload() - bandwidth);
                        receiver.setDownload(receiver.getDownload() - bandwidth);
                        flag = false;
                        if (this.debug >= 5) {
                            System.out.println("4 >>> Removing bandwidth " + bandwidth + " at time " + baseTime + " , it will be gave back at time " + tableTime);
                        }
                    } else {
                        mexTime = baseTime - CommonState.getTime();
                        if (this.debug >= 5) {
                            System.out.println("4 >>> Removing bandwidth with message " + bandwidth + " at time " + baseTime + " , it will be gave back at time " + tableTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSending Update Upload to " + src.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSending Update Download to " + rcv.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
                        }
                    }
                    mexTime = tableTime - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.println("\tSending Update Upload to " + src.getID() + " for " + bandwidth + " at time " + tableTime + " MexTime " + mexTime);
                    }
                    if (this.debug >= 5) {
                        System.out.println("\tSending Update Download to " + rcv.getID() + " for " + bandwidth + " at time " + tableTime);
                    }
                    if (this.debug >= 5) {
                        System.out.println("The transmission of data (" + data_to_send_bits + ") finishes before " + downloadResidualTime + ", it will take " + txTime + " ms: adding element at time " + baseTime);
                    }
                    finish = mexTime;
                    data_to_send_bits = 0;
                } else {
                    //The transmission cannot finished before the curret download: it will be divided in two or more parts.
                    data_to_send_bits = data_to_send_bits - Math.round(((double) bandwidth) * downloadResidualTime / 1000);
                    tableTime = downloadNextTime;
                    if (this.debug >= 5) {
                        System.out.println("\tResiduo " + data_to_send_bits + " tabletime " + tableTime);
                    }
                    BandwidthConnectionElement element = new BandwidthConnectionElement(src, rcv, bandwidth, baseTime, tableTime, CommonState.r.nextLong());
                    elements.add(element);
                    if (this.debug >= 5) {
                        System.out.println("\t5 Adding element " + element.toString());
                    }
                    element = null;
                    if (flag) {
                        old_up = sender.getUpload();
                        old_dw = receiver.getDownload();
                        sender.setUpload(sender.getUpload() - bandwidth);
                        receiver.setDownload(receiver.getDownload() - bandwidth);
                        flag = false;
                        if (this.debug >= 5) {
                            System.out.println("5 >>> Removing bandwidth " + bandwidth + " at time " + baseTime + " , it will be gave back at time " + tableTime);
                        }
                    } else {
                        mexTime = baseTime - CommonState.getTime();
                        if (this.debug >= 5) {
                            System.out.println("5 >>> Removing bandwidth with message " + bandwidth + " at time " + baseTime + " , it will be gave back at time " + tableTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSending Update Upload to " + src.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSending Update Download to " + rcv.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime);
                        }
                    }
                    mexTime = tableTime - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.println("\tSending Update Upload to " + src.getID() + " for " + bandwidth + " at time " + tableTime);
                    }
                    if (this.debug >= 5) {
                        System.out.println("\tSending Update Download to " + rcv.getID() + " for " + bandwidth + " at time " + tableTime);
                    }
                    baseTime = downloadNextTime;
                    if (this.debug >= 5) {
                        System.out.print("At time " + baseTime + " the download bandwidth will be updated from " + banda_dw + " adding " + downloadBusy);
                    }
                    banda_dw += downloadBusy;
                    if (this.debug >= 5) {
                        System.out.println("..." + banda_dw);
                    }
                    if (dw_i < receiver.getDownloadConnections().getSize() - 1 && downloadNextTime <= baseTime) {
                        do {
                            if (this.debug >= 5) {
                                System.out.println("Updating the download bandwidth of the receiver " + rcv.getID());
                            }
                            dw_i++;
                            cdownload = receiver.getDownloadConnections().getElement(dw_i);
                            downloadBusy = cdownload.getBandwidth();
                            downloadStartTime = cdownload.getStart();
                            downloadNextTime = cdownload.getEnd();
                            downloadResidualTime = downloadNextTime - baseTime;
                            if (this.debug >= 5) {
                                System.out.println("Download Start " + downloadStartTime + " DownNext " + downloadNextTime + " currentTime " + CommonState.getTime());
                                System.out.println(dw_i + " - updating download " + cdownload);
                            }
                            if (this.debug >= 5) {
                                System.out.println(" = download becomes " + banda_dw);
                            }
                            if (banda_dw <= 0) {
                                if (this.debug >= 5) {
                                    System.out.println("Warning! Updating download - in the next future I'll not have download bandwidth: it will be used for other transmission(s).");
                                }
                                if (this.debug >= 5) {
                                    System.out.println("\tNode " + rcv.getID() + " has its download bandwidth busy ion the future, it cannot receive data from Nodo " + src.getID());
                                }
                                sender.setUpload(initupload);
                                receiver.setDownload(initdownload);
                                return BandwidthMessage.NO_DOWN;
                            }
                        } while ((dw_i < receiver.getDownloadConnections().getSize() - 1) && downloadNextTime <= baseTime);
                    } else {
                        downloadResidualTime = -1;
                    }
                }
            }
        }
        long txid = CommonState.r.nextLong();
        if (!elements.isEmpty()) {
            if (this.debug >= 5) {
                System.out.println("\tThere are " + elements.size() + " elements to add in the connections tables of both sender and receiver.");
            }
            BandwidthConnectionElement cet = null;
            long olds, olde, oldb, cs, ce, cb;
            olds = olde = oldb = cs = ce = cb = -1;
            ArrayList<BandwidthConnectionElement> vsender = new ArrayList<BandwidthConnectionElement>();
            for (int j = 0; j < elements.size(); j++) {
                if (this.debug >= 5) {
                    System.out.println("\tConnectionElement # " + j);
                }
                cet =  elements.get(j);
                if (j == 0) {
                    olds = cet.getStart();
                    olde = cet.getEnd();
                    oldb = cet.getBandwidth();
                    if (this.debug >= 5) {
                        System.out.println("\tConnectionElement # " + j + ": Start " + olds + " End " + olde + " Bandwidth " + oldb);
                    }
                } else {
                    cs = cet.getStart();
                    ce = cet.getEnd();
                    cb = cet.getBandwidth();
                    if (this.debug >= 5) {
                        System.out.print("\tTrying to pack two connections: Start " + olds + " -- Bandwidth " + oldb + " -- Finishes " + olde + " (?pack?) Start " + cs + " -- Bandwidth " + cb + " -- End " + ce + "...");
                    }
                    if (cs == olde && cb == oldb) {
                        olde = ce;
                        if (this.debug >= 5) {
                            System.out.print("They CAN be packed: ");
                        }
                    } else {
                        if (this.debug >= 5) {
                            System.out.print("They CANNOT be packed: ");
                        }
                        BandwidthConnectionElement bce = new BandwidthConnectionElement(cet.getSender(), cet.getReceiver(), oldb, olds, olde, txid);
                        if (this.debug >= 5) {
                            System.out.println("the new element is " + bce);
                        }
                        vsender.add(bce);
                        olds = cs;
                        olde = ce;
                        oldb = cb;
                        if (this.debug >= 5) {
                            System.out.println("\tUpdating values " + olds + " OldE " + olde + " OldB " + oldb + " TxID " + txid);
                        }
                    }
                }
            }
             if (this.debug >= 5) {
                            System.out.println();
                        }
            BandwidthConnectionElement bce = new BandwidthConnectionElement(cet.getSender(), cet.getReceiver(), oldb, olds, olde, txid);
            vsender.add(bce);
            elements = vsender;
            vsender = null;
            for (int j = 0; j < elements.size(); j++) {
                Node bs_src, br_src;
                bs_src = br_src = null;
                Node bs_dest, br_dest;
                bs_dest = br_dest = null;
                BandwidthMessage bs_bwm, br_bwm;
                bs_bwm = br_bwm = null;
                long bs_time, br_time;
                bs_time = br_time = 0;
                cet =  elements.get(j);
                if (this.debug >= 5) {
                    System.out.println("\n\tSender Element " + cet + "; ");
                }
                sender.getUploadConnections().addConnection(cet);
                long mextime = cet.getEnd() - CommonState.getTime();
                bs_src = cet.getSender();
                bs_dest = cet.getReceiver();
                bs_bwm = new BandwidthMessage(cet.getSender(), cet.getReceiver(), BandwidthMessage.UPD_UP, cet.getBandwidth(), cet.getStart());
                bs_time = mextime;
                if (this.debug >= 5) {
                    System.out.println("\t\tBandwidthEvent SRC " + bs_src.getID() + " | RCV " + bs_dest.getID() + " | Mex " + bs_bwm.toString() + " | " + bs_time + ".");
                }
                BandwidthConnectionElement bet = new BandwidthConnectionElement(cet.getSender(), cet.getReceiver(), cet.getBandwidth(), (cet.getStart() + eedelay), (cet.getEnd() + eedelay), cet.getTxId());
                if (this.debug >= 5) {
                    System.out.println("\n\tReceiver Element " + bet + "; ");
                }
                mextime = bet.getEnd() - CommonState.getTime();
                br_src = bet.getSender();
                br_dest = bet.getReceiver();
                br_bwm = new BandwidthMessage(bet.getSender(), bet.getReceiver(), BandwidthMessage.UPD_DOWN, bet.getBandwidth(), bet.getStart());
                br_time = mextime;
                if (this.debug >= 5) {
                    System.out.println("\t\tBandwidthEvent SRC " + br_src.getID() + " | RCV " + br_dest.getID() + " | Mex " + br_bwm.toString() + " | " + br_time + ".");
                }
                receiver.getDownloadConnections().addConnection(bet);
                if (this.debug >= 5) {
                    System.out.println("\tIndex J " + j + "; ");
                }
                if (j == 0) {
                    olds = cet.getStart();
                    olde = cet.getEnd();
                    oldb = cet.getBandwidth();
                    mextime = olde - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.print("\tFirst connection Start " + olds + " Finish " + olde + " Bandwidth " + oldb + " >> MexTime Sender " + mextime);
                    }
                    System.out.flush();
                    EDSimulator.add(bs_time, bs_bwm, bs_src, pid);
                    finish = mextime;
                    mextime = mextime + eedelay;
                    if (this.debug >= 5) {
                        System.out.println("; MexTime Receiver " + mextime);
                    }
                    EDSimulator.add(br_time, br_bwm, br_dest, pid);
                } else {
                    cs = cet.getStart() - CommonState.getTime();
                    ce = cet.getEnd() - CommonState.getTime();
                    cb = cet.getBandwidth();
                    BandwidthMessage up_to_remove = new BandwidthMessage(bs_src, bs_dest, BandwidthMessage.UPD_UP, (-1 * cb), cet.getStart());
                    if (this.debug >= 5) {
                        System.out.println("\tIn time " + cet.getStart() + " bandwidth " + cb + " will be removed at the sender (" + cet.getSender().getID() + "\n\t\t" + up_to_remove);
                    }
                    EDSimulator.add(cs, up_to_remove, bs_src, pid);
                    up_to_remove = new BandwidthMessage(bs_src, bs_dest, BandwidthMessage.UPD_DOWN, (-1 * cb), (cet.getStart() + eedelay));
                    EDSimulator.add(cs, up_to_remove, bs_src, pid);
                    if (this.debug >= 5) {
                        System.out.println("\tAt time " + cet.getStart() + "(" + cet.getStart() + " bandwidth " + cb + " will be removed at the receiver (" + cet.getReceiver().getID() + "\n\t\t" + up_to_remove);
                    }
                    finish = ce;
                    BandwidthMessage plus = new BandwidthMessage(bs_src, bs_dest, BandwidthMessage.UPD_UP, cb, cet.getStart());
                    EDSimulator.add(ce, plus, bs_src, pid);
                    if (this.debug >= 5) {
                        System.out.println("\tAt time " + cet.getEnd() + " bandwidth " + cb + " will be added at the sender (" + cet.getSender().getID() + "\n\t\t" + plus);
                    }
                    plus = new BandwidthMessage(bs_src, bs_dest, BandwidthMessage.UPD_DOWN, cb, (cet.getStart() + eedelay));
                    EDSimulator.add((ce + eedelay), plus, bs_src, pid);
                    if (this.debug >= 5) {
                        System.out.println("\tAt time " + (cet.getEnd() + eedelay) + " bandwidth " + cb + " will be added at the receiver (" + cet.getReceiver().getID() + "\n\t\t" + plus);
                    }
                }
            }
            elements.clear();
        }
        if (this.debug >= 5) {
            System.out.println(">>>>>>>>>>>> Sender Connections Table <<<<<<<<<<< "+ sender.getUploadConnections().getSize());
            System.out.println(sender.getUploadConnections().getAll());
            System.out.println(">>>>>>>>>>>> Receive Conenctions Table <<<<<<<<<<< "+receiver.getDownloadConnections().getSize());
            System.out.println(receiver.getDownloadConnections().getAll());
        }
        if (this.debug >= 5) {
            System.out.println("Sender " + src.getID() + " >> " + sender.toString());
            System.out.println("Receiver " + rcv.getID() + " >> " + receiver.toString());
            System.out.println("---------------------------------------------------------- BANDWIDTH MANAGEMENT ENDS ---------------------------------------------------------- ");
        }
        return finish;
    }
}
