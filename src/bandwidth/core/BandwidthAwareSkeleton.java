package bandwidth.core;

/**
 * Skeleton to initialize the data structure of the protocol.
 *
 * @author Alessandro Russo
 * @version $Revision: 0.02$
 */
public interface BandwidthAwareSkeleton {

    /**
     * Set verbosity level.
     * @param debug Verbosity level.
     */
    public void setDebug(int debug);

    /**
     * Set current upload value.
     * @param upload Upload bandwidth of the node.
     */
    public void setUpload(long upload);

    /**
     * Initialize all the upload resources. It also initializes the download bandwidth invoking initDownload.
     * @param upload Upload bandwidth of the node.
     */
    public void initUpload(long upload);

    /**
     * Set current download value.
     * @param download The download bandwidth of the node.
     */
    public void setDownload(long download);

    /**
     * Set minimum upload available. This is a threshold under which no transmission are initialized, because should take more time.
     * @param upload_min Minimum upload.
     */
    public void setUploadMin(long upload_min);

    /**
     * Set minimum download available. This is a threshold under which no transmission are initialized, because should take more time.
     * @param download_min Minimum download.
     */
    public void setDownloadMin(long download_min);

    /**
     * Maximum upload available.
     * @param upload_max Maximum upload.
     */
    public void setUploadMax(long upload_max);

    /**
     * Maximum download available.
     * @param download_max Maximum download.
     */
    public void setDownloadMax(long download_max);

    /**
     * Initialize download resources in the node. This method is invoking by initUpload and the download is set as ten passiveDownload times the upload.
     * @param download The download bandwidth of the node.
     */
    public void initDownload(long download);

    /**
     * Method used for upload fluctuation. It will be implemented.
     */
    public void fluctuationUpload();

    /**
     * Method used for download fluctuation. It will be implemented.
     */
    public void fluctuationDownload();

    /**
     * Set maximum number of active upload.
     * @param active_upload Active upload
     */
    public void setActiveUpload(int active_upload);

    /**
     * Set maximum  number of active download.
     * @param active_download Active download
     */
    public void setActiveDownload(int active_download);

    /**
     * Set maximum  number of passive upload.
     * @param passive_upload Passive upload.
     */
    public void setPassiveUpload(int passive_upload);

    /**
     * Set maximum  number of passive download.
     * @param passive_download Passive download.
     */
    public void setPassiveDownload(int passive_download);

    /**
     * Reset all fields in the protocol data structure.
     */
    public void reset();

    /**
     * Initilize the object with corresponding data structure.
     */
    public void initialize();
}
