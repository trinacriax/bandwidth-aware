/*
 * Interface with methods used to initialize the data structure.
 */
package bandwidth.core;

/**
 *
 * @author ax
 */
import peersim.core.Node;

public interface BandwidthAwareSkeleton {

    /**
     * Set verbosity level.
     * @param debug Verbosity level.
     */
    public void setDebug(int debug);

    /**
     * Set current upload value.
     * @param upload
     */
    public void setUpload(long upload);

    /**
     * Set current download value.
     * @param download
     */
    public void setDownload(long download);

    /**
     * Set minimum upload available. This is a threshold under which no transmission are initialized, because should take more time.
     * @param upload_min Min upload
     */
    public void setUploadMin(long upload_min);

    /**
     * Set minimum download available. This is a threshold under which no transmission are initialized, because should take more time.
     * @param download_min Min download.
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
     * Method used for upload fluctuation.
     */
    public void fluctuationUpload();

    /**
     * Method used for download fluctuation.
     */
    public void fluctuationDownload();

    /**
     * Set number of active upload.
     * @param active_upload Active upload
     */
    public void setActiveUpload(int active_upload);

    /**
     * Set number of active download.
     * @param active_download Active download
     */
    public void setActiveDownload(int active_download);

    /**
     * Set number of passive upload.
     * @param passive_upload Passive upload.
     */
    public void setPassiveUpload(int passive_upload);

    /**
     * Set number of passive download.
     * @param passive_download Passive download.
     */
    public void setPassiveDownload(int passive_download);

    /**
     * Reset all values.
     */
    public void reset();

    /**
     * Initilize the object with corresponding data structure.
     */
    public void initialize();
}