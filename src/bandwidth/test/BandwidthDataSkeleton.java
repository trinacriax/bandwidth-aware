package bandwidth.test;

/**
 * TEST CLASS
 * @author Alessandro Russo
 * @version 1.0
 */
public interface BandwidthDataSkeleton {

    public void Initialize(int n);

    public void resetAll();

    public void setDebug(int val);

    public void setBandwidth(int bandiwdthp);

    public void setChunkSize(long chunk_size);
}
