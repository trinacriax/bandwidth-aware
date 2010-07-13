package bandwidth.core;

import java.util.Iterator;
import java.util.LinkedList;
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
 * @author Alessandro Russo <russo@disi.unitn.it> <p> DISI - University of Trento (Italy) <p> Napa-Wine <www.napa-wine.eu>.
 * @version $Revision: 0.2$
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
  private static int debug;
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
  @Override
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
   * Reset the main fields of this class.
   */
  @Override
  public void reset() {
    this.upload = this.upload_max = this.upload_min = upload_buf = 0;
    this.download = this.download_max = this.download_min = download_buf = 0;
    this.active_up = this.active_upload = this.passive_up = this.passive_upload;
    this.active_dw = this.active_download = this.passive_dw = this.passive_download;
    debug = 0;
    this.upload_connection_list = this.download_connection_list = null;
  }

  /**
   * Initialize data structures.
   */
  @Override
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
    return debug;
  }

  /**
   * Set the verbosity level.
   * @param debug Verbosity level.
   */
  @Override
  public void setDebug(int debug) {
    debug = debug;
  }

  public void logOutln(int _debug, String message) {
    if (this.getDebug() >= _debug) {
      System.out.println(message);
    }
  }

  public void logOut(
          int _debug, String message) {
    if (this.getDebug() >= _debug) {
      System.out.print(message);
    }
  }

  public void logErr(int _debug, String message) {
    if (this.getDebug() >= _debug) {
      System.err.print(message);
    }
  }

  /**
   * Initilize the current upload, the minimum and the maxium values,
   * it also executes the initialization of download.
   * @param _upload Maximum upload bandiwdth at the current node.
   */
  @Override
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
  @Override
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
  public final long getUploadBUF() {
    return this.upload_buf;
  }

  /**
   * Return the current upload bandwidth.
   * @return Current upload bandwidth.
   */
  public final long getUpload() {
    return this.upload;
  }

  /**
   * Set the minimum upload.
   * @param _upload_min Minimum upload.
   */
  @Override
  public void setUploadMin(long _upload_min) {
    this.upload_min = _upload_min;
  }

  /**
   * Get the minimum upload.
   * @return Minimum upload.
   */
  public final long getUploadMin() {
    return this.upload_min;
  }

  /**
   * Set the maximum upload.
   * @param _upload_max Maximum upload.
   */
  @Override
  public void setUploadMax(long _upload_max) {
    this.upload_max = _upload_max;
  }

  /**
   * Get teh maximum upload.
   * @return Maximum upload.
   */
  public  final long getUploadMax() {
    return this.upload_max;
  }

  /**
   * Initilize the download bandwidth, the minimum and the maximum values.
   * @param _download Download bandwidth.
   */
  @Override
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
  @Override
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
  public final long getDownloadBUF() {
    return this.download_buf;
  }

  /**
   * Return the current download bandwidth.
   * @return Current download bandwidth.
   */
  public final long getDownload() {
    return this.download;
  }

  /**
   * Set the minimum download.
   * @param _download_min Minimum download.
   */
  @Override
  public void setDownloadMin(long _download_min) {
    this.download_min = _download_min;
  }

  /**
   * Get the minimum download.
   * @return Minimum download.
   */
  public final long getDownloadMin() {
    return this.download_min;
  }

  /**
   * Set the maximum download.
   * @param _download_max Maximum download.
   */
  @Override
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
  public final int getActiveUp() {
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
  public final int getActiveDw() {
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
  public final int getPassiveUp() {
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
  public final int getPassiveDw() {
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
  @Override
  public void setActiveUpload(int active_upload) {
    this.active_upload = active_upload;
  }

  /**
   * Get the  maximum number of active upload.
   * @return maximum number of active download.
   */
  public final int getActiveUpload() {
    return this.active_upload;
  }

  /**
   * Set the maximum number of active download.
   * @param active_download max number of active dowload.
   */
  @Override
  public void setActiveDownload(int active_download) {
    this.active_download = active_download;
  }

  /**
   * Get the  maximum number of active download.
   * @return Max number of active download.
   */
  public final int getActiveDownload() {
    return this.active_download;
  }

  /**
   * Set the maximum number of passive upload.
   * @param passive_upload Maximum number of passive upload.
   */
  @Override
  public void setPassiveUpload(int passive_upload) {
    this.passive_upload = passive_upload;
  }

  /**
   * Get the  maximum number of passive upload.
   * @return maximum number of passive upload.
   */
  public final int getPassiveUpload() {
    return this.passive_upload;
  }

  /**
   * Set the  maximum number of passive download.
   * @param passive_download maximum number of passive download.
   */
  @Override
  public void setPassiveDownload(int passive_download) {
    this.passive_download = passive_download;
  }

  /**
   * Get the  maximum number of passive download.
   * @return maximum number of passive download.
   */
  public final int getPassiveDownload() {
    return this.passive_download;
  }

  /**
   * Get the next time the upload bandiwdth will be available.
   * @return time in which the upload bandwidth will be available.
   */
  public final long getEndUpload() {
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
  public final long getEndDownload() {
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

  //TODO Bandwidth fluctuation to implement
  /**
   * Provide bandwidth fluctuation during the simulation. To implement!
   */
  @Override
  public void fluctuationUpload() {
  }

  /**
   * Provide bandwidth fluctuation during the simulation. To implement!
   */
  @Override
  public void fluctuationDownload() {
  }

  /**
   * Printable version of the bandwidth instance.
   * @return A string representing the "bandwidth" status inside the current node.
   */
  @Override
  public String toString() {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("\n\t>> Upload: ");
    sbuf.append(this.upload);
    sbuf.append(" [");
    sbuf.append(this.upload_min);
    sbuf.append(":");
    sbuf.append(this.upload_max);
    sbuf.append("] - ");
    sbuf.append("Active ");
    sbuf.append(this.getActiveUp());
    sbuf.append("(");
    sbuf.append(this.getActiveUpload());
    sbuf.append(")");
    sbuf.append(" Passive ");
    sbuf.append(this.getPassiveUp());
    sbuf.append("(");
    sbuf.append(this.getPassiveUpload());
    sbuf.append(")");
    sbuf.append("\n\t<< Download: ");
    sbuf.append(this.download);
    sbuf.append(" [");
    sbuf.append(this.download_min);
    sbuf.append(":");
    sbuf.append(this.download_max);
    sbuf.append("] - ");
    sbuf.append("Active ");
    sbuf.append(this.getActiveDw());
    sbuf.append("(");
    sbuf.append(this.getActiveDownload());
    sbuf.append(")");
    sbuf.append(" Passive ");
    sbuf.append(this.getPassiveDw());
    sbuf.append("(");
    sbuf.append(this.getPassiveDownload() + ")");
    return sbuf.toString();
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
      //logOutln(4, "The application layer request to send ZERO bits");
      return 1;
    }else if (data_to_send_bits < 0) {
      logErr(0, "The application layer request to send negative bits " + data_to_send_bits+"\n");
      return -1;
    }
    BandwidthAwareTransport sender = ((BandwidthAwareTransport) (src.getProtocol(pid)));
    BandwidthAwareTransport receiver = ((BandwidthAwareTransport) (rcv.getProtocol(pid)));
    long initupload = sender.getUpload();
    long initdownload = receiver.getDownload();
    if (sender.getUpload() < sender.getUploadMin()) {
      //logOutln(4, "\t\t>> Node " + src.getID() + " has upload bw (" + initupload + ") less than minimum upload (" + sender.getUploadMin() + ").\nReturning code NO_UP: " + BandwidthMessage.NO_UP);
      return BandwidthMessage.NO_UP;
    } else if (receiver.getDownload() < receiver.getDownloadMin()) {
      //logOutln(4, "\t\t>> Node " + rcv.getID() + " has download bw (" + initdownload + ") less than minimum download (" + receiver.getDownloadMin() + ").\nReturning code NO_DOWN: " + BandwidthMessage.NO_DOWN);
      return BandwidthMessage.NO_DOWN;
    }
    LinkedList<BandwidthConnectionElement> elements = new LinkedList<BandwidthConnectionElement>();
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
    long current_time = CommonState.getTime();
    if (sender.getUploadConnections().getSize() == 0) {
      uploadResidualTime = -1;
    } else {
      cupload = sender.upload_connection_list.getElement(up_i);
      //logOutln(5, "First upload connection: " + cupload);
      uploadBusy = cupload.getBandwidth();
      uploadStartTime = cupload.getStart();
      uploadNextTime = cupload.getEnd();
      uploadResidualTime = uploadNextTime - current_time;
    }
    banda_dw = receiver.getDownload();
    if (receiver.getDownloadConnections().getSize() == 0) {
      downloadResidualTime = -1;
    } else {
      cdownload = (receiver.getDownloadConnections().getElement(dw_i));
      //logOutln(5, "First download connection: " + cdownload);
      downloadBusy = cdownload.getBandwidth();
      downloadStartTime = cdownload.getStart();
      downloadNextTime = cdownload.getEnd();
      downloadResidualTime = downloadNextTime - current_time;
    }
    
    long baseTime = current_time;
    long tableTime, mexTime;
    tableTime = mexTime = 0;
    boolean flag = true;
    
    while (data_to_send_bits > 0) {
      bandwidth = Math.min(banda_up, banda_dw);
      //logOutln(5, "Time " + baseTime + ". Bits to transmit: " + data_to_send_bits + ". BW Sender: " + banda_up + ", BW Receiver: " + banda_dw + ", Bandwidth for transmission " + bandwidth);
      //From baseTime both sendere and receiver have all the bandwidth available.
      if ((uploadResidualTime == -1 && downloadResidualTime == -1)) {
        mexTime = Math.round((data_to_send_bits / ((double) bandwidth)) * 1000);
        mexTime = (mexTime == 0 ? 1 : mexTime);
        //logOutln(5, "\tSender and Receiver do not have any connection. They are transmissing " + data_to_send_bits + " with bandwidth " + bandwidth + " and the transmission takes " + mexTime + " ms");
        data_to_send_bits = 0;
        tableTime = baseTime + mexTime;
        mexTime = tableTime - current_time;
        finish = mexTime;
        BandwidthConnectionElement element = new BandwidthConnectionElement(src, rcv, bandwidth, baseTime, tableTime, 0);
        elements.add(element);
        //logOutln(5, "\t1 Adding element " + element.toString() + " both upload and download connections list.");
        element = null;
        if (flag) {
          //logOutln(5, "1 >>> Removing bandwidth " + bandwidth + " at time " + baseTime + ". This will be returned at time " + tableTime);
          old_up = sender.getUpload();
          old_dw = receiver.getDownload();
          sender.setUpload(sender.getUpload() - bandwidth);
          receiver.setDownload(receiver.getDownload() - bandwidth);
          flag = false;
        } else {
          //logOutln(5, "1 >>> Removing bandwidth with message " + bandwidth + " at time " + baseTime);
          mexTime = baseTime - current_time;
          //logOutln(5, "\tSending Update Upload to " + src.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
          //logOutln(5, "\tSending Update Download to " + rcv.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
        }
        mexTime = tableTime - current_time;
        //logOutln(5, "\tSending Update Upload to " + src.getID() + " for giving back " + bandwidth + " at time " + tableTime + " MexTime " + mexTime);
        //logOutln(5, "\tSending Update Download to " + rcv.getID() + " for giving back " + bandwidth + " at time " + tableTime + " MexTime " + mexTime);
      } // The sender is executing an upload and the receiver either is exectuing a download which finishes after the upload OR does not have any download.
      else if (uploadResidualTime != -1 && (uploadResidualTime < downloadResidualTime || downloadResidualTime == -1)) {
        //logOutln(5, "The upload either finishes before the download (" + uploadResidualTime + ") or the download is idle.");
        // Check whether the transmission could finish before the end of the active upload or not.
        if (Math.round(((double) bandwidth) * uploadResidualTime / 1000.0D) >= data_to_send_bits) {
          long txTime = Math.round(data_to_send_bits / ((double) bandwidth) * 1000);
          txTime = (txTime == 0 ? 1 : txTime);
          tableTime = baseTime + txTime;
          BandwidthConnectionElement element = new BandwidthConnectionElement(src, rcv, bandwidth, baseTime, tableTime, CommonState.r.nextLong());
          elements.add(element);
          //logOutln(5, "\t2 Adding element " + element.toString());
          element = null;
          if (flag) {
            old_up = sender.getUpload();
            old_dw = receiver.getDownload();

            sender.setUpload(sender.getUpload() - bandwidth);
            receiver.setDownload(receiver.getDownload() - bandwidth);
            flag = false;
            //logOutln(5, "2 >>> Removing bandwidth " + bandwidth + " at time " + baseTime + " , it will be gave back at time " + tableTime);
          } else {
            mexTime = baseTime - current_time;
            //logOutln(5, "2 >>> Removing bandwidth with message " + bandwidth + " at time " + baseTime + " , it will be gave back at time " + tableTime);
            //logOutln(5, "\tSending Update Upload to " + src.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
            //logOutln(5, "\tSending Update Download to " + rcv.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
          }
          mexTime = tableTime - current_time;
          //logOutln(5, "\tSending Update Uplaod to " + src.getID() + " for " + bandwidth + " at time " + tableTime + " MexTime " + (tableTime - current_time));
          //logOutln(5, "\tSending Update Download to " + rcv.getID() + " for " + bandwidth + " at time " + tableTime + " MexTime " + (tableTime - current_time));
          //logOutln(5, "The transmission of data (" + data_to_send_bits + ") finishes before " + uploadResidualTime + ", it will take " + txTime + " ms, Adding element at time " + tableTime);
          finish = mexTime;
          data_to_send_bits = 0;
        } else {
          //The transmission does not finish before the current upload: it has to be divided in two or more parts.
          data_to_send_bits = data_to_send_bits - Math.round(bandwidth * ((double) uploadResidualTime) / 1000);
          tableTime = uploadNextTime;
          //logOutln(5, "Data to transmit " + data_to_send_bits + " BaseTime " + baseTime + " TableTime " + tableTime);
          BandwidthConnectionElement element = new BandwidthConnectionElement(src, rcv, bandwidth, baseTime, tableTime, CommonState.r.nextLong());
          elements.add(element);
          //logOutln(5, "\t3 Adding element " + element.toString());
          element = null;
          if (flag) {
            old_up = sender.getUpload();
            old_dw = receiver.getDownload();
            sender.setUpload(sender.getUpload() - bandwidth);
            receiver.setDownload(receiver.getDownload() - bandwidth);
            banda_up -= bandwidth;
            flag = false;
            //logOutln(5, "3 >>> Removing bandwidth " + bandwidth + " at time " + baseTime + " , it will be gave back at time " + tableTime);
          } else {
            mexTime = baseTime - current_time;
            //logOutln(5, "3 >>> Removing bandwidth with message " + bandwidth + " dat time " + baseTime + " , it will be gave back at time " + tableTime);
            //logOutln(5, "\tSending Update Upload to " + src.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
            //logOutln(5, "\tSending Update Download to " + rcv.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
          }
          mexTime = tableTime - current_time;
          //logOutln(5, "\tSending Update Upload to " + src.getID() + " for " + bandwidth + " at time " + tableTime + " MexTime " + mexTime);
          //logOutln(5, "\tSending Update Download to " + rcv.getID() + " for " + bandwidth + " at time " + tableTime + " MexTime " + mexTime);
          baseTime = uploadNextTime;
          //logOut(5, "Updating upload bandwidth " + banda_up + ", adding " + uploadBusy);
          banda_up += uploadBusy;
          //logOutln(5, " = upload becomes " + banda_up);
          int list_size = sender.upload_connection_list.getSize();
          if (up_i < list_size - 1 && uploadNextTime < baseTime) {
            do {
              //logOutln(5, "Updating upload " + baseTime);
              //logOutln(5, up_i + " - updating upload " + sender.upload_connection_list.getElement(up_i));
              up_i++;
              //banda usata nella prox trasmissione
              cupload = sender.upload_connection_list.getElement(up_i);
              uploadBusy = cupload.getBandwidth();
              uploadStartTime = cupload.getStart();
              uploadNextTime = cupload.getEnd();
              uploadResidualTime = uploadNextTime - baseTime;
              //logOutln(5, "upload Start " + uploadStartTime + " UpNext " + uploadNextTime + " currentTime " + current_time + " " + up_i + " - updating upload " + cupload);
              if (banda_up <= 0) {
                //logOutln(5, "Warning! Updating upload - in the next future I'll not have upload bandwidth: it will be used for other transmission(s).");
                //logOutln(5, "\tNode " + src.getID() + " has its upload bandwidth busy ion the future, it cannot perform the transfer to Nodo " + rcv.getID());
                sender.setUpload(initupload);
                receiver.setDownload(initdownload);
                elements.clear();
                elements = null;
                //Notify no bandwidth in upload
                return BandwidthMessage.NO_UP;
              }
            } while (up_i < list_size - 1 && uploadNextTime <= baseTime);
            //check all connections
          } else {
            uploadResidualTime = -1;
          }
          //logOutln(5, " to " + banda_up);
        }
      }//There is a connection in download and the current download finished before the upload or no upload are active.
      else if (downloadResidualTime != -1 && (downloadResidualTime <= uploadResidualTime || uploadResidualTime == -1)) {
        //logOutln(5, "\tThe download finished before the upload:" + downloadResidualTime + " or the upload is idle.");
        //Checking if the transmission finishes before the current download
        if (Math.round(((double) bandwidth) * downloadResidualTime / 1000) >= data_to_send_bits) {
          //The node is able to transmit the whole data before the ends of the first connection
          long txTime = Math.round(data_to_send_bits / ((double) bandwidth) * 1000);
          txTime = (txTime == 0 ? 1 : txTime);
          tableTime = baseTime + txTime;

          BandwidthConnectionElement element = new BandwidthConnectionElement(src, rcv, bandwidth, baseTime, tableTime, CommonState.r.nextLong());
          elements.add(element);
          //logOutln(5, "\t4 Adding element " + element.toString());
          element = null;
          if (flag) {
            old_up = sender.getUpload();
            old_dw = receiver.getDownload();
            sender.setUpload(sender.getUpload() - bandwidth);
            receiver.setDownload(receiver.getDownload() - bandwidth);
            flag = false;
            //logOutln(5, "4 >>> Removing bandwidth " + bandwidth + " at time " + baseTime + " , it will be gave back at time " + tableTime);
          } else {
            mexTime = baseTime - current_time;
            //logOutln(5, "4 >>> Removing bandwidth with message " + bandwidth + " at time " + baseTime + " , it will be gave back at time " + tableTime);
            //logOutln(5, "\tSending Update Upload to " + src.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
            //logOutln(5, "\tSending Update Download to " + rcv.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime + " MexTime " + mexTime);
          }
          mexTime = tableTime - current_time;
          //logOutln(5, "\tSending Update Upload to " + src.getID() + " for " + bandwidth + " at time " + tableTime + " MexTime " + mexTime);
          //logOutln(5, "\tSending Update Download to " + rcv.getID() + " for " + bandwidth + " at time " + tableTime);
          //logOutln(5, "The transmission of data (" + data_to_send_bits + ") finishes before " + downloadResidualTime + ", it will take " + txTime + " ms: adding element at time " + baseTime);
          finish = mexTime;
          data_to_send_bits = 0;
        } else {
          //The transmission cannot finished before the curret download: it will be divided in two or more parts.
          data_to_send_bits = data_to_send_bits - Math.round(((double) bandwidth) * downloadResidualTime / 1000);
          tableTime = downloadNextTime;
          //logOutln(5, "\tResiduo " + data_to_send_bits + " tabletime " + tableTime);
          BandwidthConnectionElement element = new BandwidthConnectionElement(src, rcv, bandwidth, baseTime, tableTime, CommonState.r.nextLong());
          elements.add(element);
          //logOutln(5, "\t5 Adding element " + element.toString());
          element = null;
          if (flag) {
            old_up = sender.getUpload();
            old_dw = receiver.getDownload();
            sender.setUpload(sender.getUpload() - bandwidth);
            receiver.setDownload(receiver.getDownload() - bandwidth);
            flag = false;
            //logOutln(5, "5 >>> Removing bandwidth " + bandwidth + " at time " + baseTime + " , it will be gave back at time " + tableTime);
          } else {
            mexTime = baseTime - current_time;
            //logOutln(5, "5 >>> Removing bandwidth with message " + bandwidth + " at time " + baseTime + " , it will be gave back at time " + tableTime);
            //logOutln(5, "\tSending Update Upload to " + src.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime);
            //logOutln(5, "\tSending Update Download to " + rcv.getID() + " for " + (-1 * bandwidth) + " at time " + baseTime);
          }
          mexTime = tableTime - current_time;
          //logOutln(5, "\tSending Update Upload to " + src.getID() + " for " + bandwidth + " at time " + tableTime);
          //logOutln(5, "\tSending Update Download to " + rcv.getID() + " for " + bandwidth + " at time " + tableTime);
          baseTime = downloadNextTime;
          //logOut(5, "At time " + baseTime + " the download bandwidth will be updated from " + banda_dw + " adding " + downloadBusy);
          banda_dw += downloadBusy;
          //logOutln(5, "..." + banda_dw);
          int list_size = receiver.getDownloadConnections().getSize();
          if (dw_i < list_size - 1 && downloadNextTime <= baseTime) {
            do {
              //logOutln(5, "Updating the download bandwidth of the receiver " + rcv.getID());
              dw_i++;
              cdownload = receiver.getDownloadConnections().getElement(dw_i);
              downloadBusy = cdownload.getBandwidth();
              downloadStartTime = cdownload.getStart();
              downloadNextTime = cdownload.getEnd();
              downloadResidualTime = downloadNextTime - baseTime;
              //logOutln(5, "Download Start " + downloadStartTime + " DownNext " + downloadNextTime + " currentTime " + current_time);
              //logOutln(5, dw_i + " - updating download " + cdownload);
              //logOutln(5, " = download becomes " + banda_dw);
              if (banda_dw <= 0) {
                //logOutln(5, "Warning! Updating download - in the next future I'll not have download bandwidth: it will be used for other transmission(s).");
                //logOutln(5, "\tNode " + rcv.getID() + " has its download bandwidth busy ion the future, it cannot receive data from Nodo " + src.getID());
                sender.setUpload(initupload);
                receiver.setDownload(initdownload);
                return BandwidthMessage.NO_DOWN;
              }
            } while ((dw_i < list_size - 1) && downloadNextTime <= baseTime);
          } else {
            downloadResidualTime = -1;
          }
        }
      }
    }
    long txid = CommonState.r.nextLong();
//    if (!elements.isEmpty()) {
//      //logOutln(5,"\tThere are "  + elements.size() + " elements to add in the connections tables of both sender and receiver.");
//    }
    BandwidthConnectionElement cet = null;
    long olds, olde, oldb, cs, ce, cb;
    olds = olde = oldb = cs = ce = cb = -1;
    LinkedList<BandwidthConnectionElement> vsender = new LinkedList<BandwidthConnectionElement>();
    Iterator<BandwidthConnectionElement> bce_it = elements.iterator();
    int j = 0;
    while(bce_it.hasNext()){
    //for (int j = 0, len = elements.size(); j < len; j++) {
      //logOut(5, "\tConnectionElement # " + j);
      cet = bce_it.next();
      if (j == 0) {
        olds = cet.getStart();
        olde = cet.getEnd();
        oldb = cet.getBandwidth();
        //logOutln(5, ": Start " + olds + " End " + olde + " Bandwidth " + oldb);
      } else {
        cs = cet.getStart();
        ce = cet.getEnd();
        cb = cet.getBandwidth();
        //logOutln(5, ": Start " + cs + " End " + ce + " Bandwidth " + cb);
        //logOut(5, "\tTrying to pack two connections: Start " + olds + " -- Bandwidth " + oldb + " -- Finishes " + olde + " (?pack?) Start " + cs + " -- Bandwidth " + cb + " -- End " + ce + "...");
        if (cs == olde && cb == oldb) {
          olde = ce;
          //logOut(5, "They CAN be packed: ");
        } else {
          //logOut(5, "They CANNOT be packed: ");
          BandwidthConnectionElement bce = new BandwidthConnectionElement(cet.getSender(), cet.getReceiver(), oldb, olds, olde, txid);
          //logOutln(5, "the new element is " + bce);
          vsender.add(bce);
          olds = cs;
          olde = ce;
          oldb = cb;
          //logOutln(5, "\tUpdating values " + olds + " OldE " + olde + " OldB " + oldb + " TxID " + txid);
        }
      }
      j++;
    }
    //logOutln(5, "");
    BandwidthConnectionElement bce = new BandwidthConnectionElement(cet.getSender(), cet.getReceiver(), oldb, olds, olde, txid);
    vsender.add(bce);
    bce_it = vsender.iterator();
    //for (int j = 0, len = elements.size(); j < len; j++) {
    j =0;
    while(bce_it.hasNext()){
      Node bs_src, br_src;
      bs_src = br_src = null;
      Node bs_dest, br_dest;
      bs_dest = br_dest = null;
      BandwidthMessage bs_bwm, br_bwm;
      bs_bwm = br_bwm = null;
      long bs_time, br_time;
      bs_time = br_time = 0;
      //cet = elements.get(j);
      cet = bce_it.next();
      //logOutln(5, "\n\tSender Element " + cet + "; ");
      sender.getUploadConnections().addConnection(cet);
      long mextime = cet.getEnd() - current_time;
      bs_src = cet.getSender();
      bs_dest = cet.getReceiver();
      bs_bwm = new BandwidthMessage(cet.getSender(), cet.getReceiver(), BandwidthMessage.UPD_UP, cet.getBandwidth(), cet.getStart());
      bs_time = mextime;
      //logOutln(5, "\t\tBandwidthEvent SRC " + bs_src.getID() + " | RCV " + bs_dest.getID() + " | Mex " + bs_bwm.toString() + " | " + bs_time + ".");
      BandwidthConnectionElement bet = new BandwidthConnectionElement(cet.getSender(), cet.getReceiver(), cet.getBandwidth(), (cet.getStart() + eedelay), (cet.getEnd() + eedelay), cet.getTxId());
      //logOutln(5, "\n\tReceiver Element " + bet + "; ");
      mextime = bet.getEnd() - current_time;
      //br_src = bet.getSender();
      br_dest = bet.getReceiver();
      br_bwm = new BandwidthMessage(bet.getSender(), bet.getReceiver(), BandwidthMessage.UPD_DOWN, bet.getBandwidth(), bet.getStart());
      br_time = mextime;
      ////logOutln(5, "\t\tBandwidthEvent SRC " + br_src.getID() + " | RCV " + br_dest.getID() + " | Mex " + br_bwm.toString() + " | " + br_time + ".");
      receiver.getDownloadConnections().addConnection(bet);
      //logOutln(5, "\tIndex J " + j + "; ");
      if (j == 0) {
        olds = cet.getStart();
        olde = cet.getEnd();
        oldb = cet.getBandwidth();
        mextime = olde - current_time;
        //logOut(5, "\tFirst connection Start " + olds + " Finish " + olde + " Bandwidth " + oldb + " >> MexTime Sender " + mextime);
        //System.out.flush();
        EDSimulator.add(bs_time, bs_bwm, bs_src, pid);
        finish = mextime;
        mextime = mextime + eedelay;
        //logOutln(5, "; MexTime Receiver " + mextime);
        EDSimulator.add(br_time, br_bwm, br_dest, pid);
      } else {
        cs = cet.getStart() - current_time;
        ce = cet.getEnd() - current_time;
        cb = cet.getBandwidth();
        BandwidthMessage up_to_remove = new BandwidthMessage(bs_src, bs_dest, BandwidthMessage.UPD_UP, (-1 * cb), cet.getStart());
        //logOutln(5, "\tIn time " + cet.getStart() + " bandwidth " + cb + " will be removed at the sender (" + cet.getSender().getID() + "\n\t\t" + up_to_remove);
        EDSimulator.add(cs, up_to_remove, bs_src, pid);
        up_to_remove = new BandwidthMessage(bs_src, bs_dest, BandwidthMessage.UPD_DOWN, (-1 * cb), (cet.getStart() + eedelay));
        EDSimulator.add(cs, up_to_remove, bs_src, pid);
        //logOutln(5, "\tAt time " + cet.getStart() + "(" + cet.getStart() + " bandwidth " + cb + " will be removed at the receiver (" + cet.getReceiver().getID() + "\n\t\t" + up_to_remove);
        finish = ce;
        BandwidthMessage plus = new BandwidthMessage(bs_src, bs_dest, BandwidthMessage.UPD_UP, cb, cet.getStart());
        EDSimulator.add(ce, plus, bs_src, pid);
        //logOutln(5, "\tAt time " + cet.getEnd() + " bandwidth " + cb + " will be added at the sender (" + cet.getSender().getID() + "\n\t\t" + plus);
        plus = new BandwidthMessage(bs_src, bs_dest, BandwidthMessage.UPD_DOWN, cb, (cet.getStart() + eedelay));
        EDSimulator.add((ce + eedelay), plus, bs_src, pid);
        //logOutln(5, "\tAt time " + (cet.getEnd() + eedelay) + " bandwidth " + cb + " will be added at the receiver (" + cet.getReceiver().getID() + "\n\t\t" + plus);
      }
      j++;
//      elements.clear();
    }
    //logOutln(5, ">>>>>>>>>>>> Sender Connections Table <<<<<<<<<<< " + sender.getUploadConnections().getSize());
    //logOutln(5, sender.getUploadConnections().getAll());
    //logOutln(5, ">>>>>>>>>>>> Receive Conenctions Table <<<<<<<<<<< " + receiver.getDownloadConnections().getSize());
    //logOutln(5, receiver.getDownloadConnections().getAll());
    //logOutln(5, "Sender " + src.getID() + " >> " + sender.toString());
    //logOutln(5, "Receiver " + rcv.getID() + " >> " + receiver.toString());
    //logOutln(5, "---------------------------------------------------------- BANDWIDTH MANAGEMENT ENDS ---------------------------------------------------------- ");
    cet = null;
    bce = null;
    vsender.clear();
    elements.clear();
    src = null;
    rcv = null;
    bce_it = null;
    return finish;
  }
}
