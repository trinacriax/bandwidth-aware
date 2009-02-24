/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bandwidth;

/**
 *
 * @author ax
 */
import peersim.core.Node;

public interface BandwidthAwareSkeleton {

    public void setDebug(int debug);
    
    public void setUpload(long upload);

    public void setDownload(long download);

    public void setUploadMin(long upload_min);

    public void setDownloadMin(long download_min);

    public void setUploadMax(long upload_max);

    public void setDownloadMax(long download_max);

    public void fluctuationUpload();

    public void fluctuationDownload();

    public void reset();

    public void initialize();

}
