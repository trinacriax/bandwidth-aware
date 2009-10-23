package bandwidth.test;

/**
 * TEST CLASS
 * @author Alessandro Russo
 * @version 1.0
 */
public interface BandwidthInfo {

    public final int PUSH_CYCLE = 0;
    public final int PUSH = 3;
    public final int START_PUSH = 6;
    public final int FINISH_PUSH = 8;
    public final int OK_PUSH = 12;
    public final int NO_CHUNK_OWNED = 18;
    public final int NO_UPLOAD_BANDWIDTH_PUSH = 26;
    public final int NO_DOWNLOAD_BANDWIDTH_PUSH = 30;
    public final int SWITCH_PUSH = 80;
    public final int MILLISECONDI = 1000;
    public final long OWNED = -1;
    public final long IN_DOWNLOAD = -2;
    public final long NOT_OWNED = -3;
}
