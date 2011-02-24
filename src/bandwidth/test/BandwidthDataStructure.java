package bandwidth.test;

import bandwidth.core.BandwidthAwareProtocol;
import peersim.core.*;

/**
 * TEST CLASS
 * @author Alessandro Russo
 * @version 1.0
 */
public class BandwidthDataStructure implements BandwidthDataSkeleton, Protocol {

    /**Array that contains the time in which the node receive the i-th chunk*/
    protected long[] chunk_list = null;
    /**Total number of chunks that the node should receive*/
    protected int number_of_chunks;
    /**Debug level */
    protected int debug;
    /**# of chunks transmitted correctly via push*/
    protected int success_upload;
    /**Protocol ID for bandwidth mechanism*/
    protected int bandwidth;
    /**#of chunks received in push*/
    protected int chunkpush;
    /**Chunk's size in bits*/
    protected long chunk_size;
    /**Time in which node completes its chunk-list*/
    protected long completed;
    /**Max # of push attempts */
    protected int max_push_attempts;
    /**Current push attempts */
    protected int push_attempts;
    /**Time spent in push*/
    protected long time_in_push;
    /**Time needed to change state*/
    protected long switchtime;

    public BandwidthDataStructure(String prefix) {
        super();
    }

    /**
     * Clone method
     */
    @Override
    public Object clone() {
        BandwidthDataStructure clh = null;
        try {
            clh = (BandwidthDataStructure) super.clone();
        } catch (CloneNotSupportedException e) {
        } // never happens
        clh.chunk_list = null;
        clh.bandwidth = 0;
        clh.number_of_chunks = 0;
        clh.completed = 0L;
        clh.debug = 0;
        clh.chunk_size = 0;
        clh.success_upload = 0;
        clh.chunkpush = 0;
        clh.max_push_attempts = 0;
        clh.push_attempts = 0;
        clh.time_in_push = 0L;
        clh.switchtime = 0L;
        return clh;
    }

    /**
     * 
     * Reset protocol's data structure
     * Invoked in the Initializer
     * 
     */
    @Override
    public void resetAll() {
        this.chunk_list = null;
        this.chunk_size = 0;
        this.completed = 0;
        this.debug = 0;
        this.bandwidth = 0;
        this.number_of_chunks = 0;
        this.chunkpush = 0;
        this.max_push_attempts = 0;
        this.push_attempts = 0;
        this.time_in_push = 0;
        this.switchtime = 0;
        this.success_upload = 0;
    }

    /**
     * This method is invoked in the Initialized, after the reset one.
     * @param items The number of chunks that will be distributed
     *
     * */
    @Override
    public void Initialize(int items) {
        this.resetAll();
        this.chunk_list = new long[items];
        for (int i = 0; i < items; i++) {
            this.chunk_list[i] = BandwidthInfo.NOT_OWNED;
        }
    }

    /**
     *
     * Set bandwidth protocol
     * @param bw the protocol identifier (PID) of the protocol that
     * implements the bandwidth mechanism
     *
     */
    @Override
    public void setBandwidth(int bw) {
        this.bandwidth = bw;
    }

    /**
     *
     * Return he PID of the protocol that identify the bandwidth mechanism.
     * @return PID of the bandwidth protocol
     *
     */
    public int getBandwidth() {
        return this.bandwidth;
    }

    /**
     *
     * Time needs by the node to switch its state,
     * @param time in ms
     *
     */
    public void setSwitchTime(long time) {
        this.switchtime = time;
    }

    /**
     *
     * Return the time needs by the node to switch its state
     * @return the time in ms
     *
     */
    public long getSwitchTime() {
        return this.switchtime;
    }

    /**
     * Set the debug level
     * @param value the level of verbosity 0 up to 10
     */
    @Override
    public void setDebug(int value) {
        this.debug = value;
    }

    /**
     * Get the debug level
     * @return value the level of verbosity 0 up to 10
     */
    public int getDebug() {
        return this.debug;
    }

    /**
     * 
     * @param chunk_size 
     */
    @Override
    public void setChunkSize(long chunk_size) {
        this.chunk_size = chunk_size;
    }

    /**
     * 
     * @return 
     */
    public long getChunkSize() {
        return this.chunk_size;
    }

    /**
     * 
     * @param chunk 
     */
    public void setInDown(String chunk) {
        long index = Long.parseLong(chunk.substring(chunk.indexOf(":") + 1,
                chunk.length()));
        this.chunk_list[(int) (index)] = BandwidthInfo.IN_DOWNLOAD;
    }

    /**
     * 
     * @param index 
     */
    public void setInDown(long index) {
        if (this.chunk_list[(int) (index)] == BandwidthInfo.NOT_OWNED) {
            this.chunk_list[(int) (index)] = BandwidthInfo.IN_DOWNLOAD;
        }
    }

    /**
     * 
     * @param index 
     */
    public void resetInDown(long index) {
        if (this.chunk_list[(int) (index)] == BandwidthInfo.IN_DOWNLOAD) {
            this.chunk_list[(int) (index)] = BandwidthInfo.NOT_OWNED;
        }
    }

    /**
     * 
     * @return 
     */
    public String bitmap() {
        String res = "";
        for (int i = 0; i < this.chunk_list.length; i++) {
            res += (this.normalize(this.chunk_list[i]) > BandwidthInfo.OWNED ? "1" : (this.chunk_list[i] == BandwidthInfo.IN_DOWNLOAD) ? "!" : "0") + (i % 10 == 9 ? "," : "");
        }
        return res;
    }

    /**
     * 
     * @return 
     */
    public int getLast() {
        int last = -1;
        for (int i = this.chunk_list.length - 1; i >= 0; i--) {
            if (normalize(this.chunk_list[i]) > BandwidthInfo.OWNED) {
                return i;
            }
        }
        return last;
    }

    /**
     * 
     * @param chunktime
     * @return 
     */
    public long normalize(long chunktime) {
        if (chunktime != BandwidthInfo.OWNED && chunktime != BandwidthInfo.NOT_OWNED && chunktime != BandwidthInfo.IN_DOWNLOAD) {
            return Math.abs(chunktime);
        } else {
            return chunktime;
        }
    }

    /**
     * 
     * @param index
     * @return 
     */
    public long getChunk(int index) {
        return this.chunk_list[index];
    }

    /**
     * 
     * @param chunk
     * @param method
     * @return 
     */
    public boolean addChunk(int chunk, int method) {
        if ((this.chunk_list[chunk] == BandwidthInfo.NOT_OWNED) || (this.chunk_list[chunk] == BandwidthInfo.IN_DOWNLOAD)) {
            this.chunk_list[chunk] = CommonState.getTime();
        }
        return true;
    }

    /**
     * 
     * @return 
     */
    public int getSize() {
        int size = 0;
        for (int i = 0; i < this.chunk_list.length; i++) {
            if (normalize(this.chunk_list[i]) > BandwidthInfo.OWNED) {
                size++;
            }
        }
        return size;
    }

    /**
     * 
     * @param node
     * @return 
     */
    public String toString(Node node) {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("Node ");
        sbuf.append(node.getID());
        sbuf.append(", Time ");
        sbuf.append(CommonState.getTime());
        sbuf.append(", List ");
        sbuf.append(this.getSize());
        if (this.getSize() == this.getNumberOfChunks()) {
            sbuf.append(" >>> has all chunks.");
        } else {
            sbuf.append(".");
        }
        return sbuf.toString();
    }

    /**
     * 
     * @param id
     * @return 
     */
    public long getChunkInfo(long id) {
        int idi = (int) id;
        return this.chunk_list[idi];
    }

    /**
     * Add one to success uploads
     * */
    public void addSuccessUpload() {
        this.success_upload++;
    }

    /**
     * Add one to success uploads
     * */
    public int getSuccessUpload() {
        return this.success_upload;
    }

    /**
     * Reset the number of success upload
     * */
    public void resetSuccessUpload() {
        this.success_upload = 0;
    }

    /**
     * Set the number of chunks to distribute.
     * @param _number_of_chunks chunks to distribute.
     */
    public void setNumberOfChunks(int _number_of_chunks) {
        this.number_of_chunks = _number_of_chunks;
    }

    /**
     * Get the number of chunks to distribute.
     * @return chunks to distribute.
     */
    public int getNumberOfChunks() {
        return this.number_of_chunks;
    }

    /**
     * Return the number of active uploads
     * @return the number of active uploads
     **/
    public int getActiveUpload(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        return bap.getActiveUpload();
    }

    public int getActiveUp(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        return bap.getActiveUp();
    }

    public void addActiveUp(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        bap.addActiveUp();
    }

    public void remActiveUp(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        bap.remActiveUp();
    }

    public void resetActiveUp(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        bap.resetActiveUp();
    }

    public int getActiveDownload(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        return bap.getActiveDownload();
    }

    public int getActiveDw(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        return bap.getActiveDw();
    }

    public void addActiveDw(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        bap.addActiveDw();
    }

    public void remActiveDw(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        bap.remActiveDw();
    }

    public void resetActiveDw(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        bap.resetActiveDw();
    }

    public int getPassiveUpload(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        return bap.getPassiveUpload();
    }

    public int getPassiveUp(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        return bap.getPassiveUp();
    }

    public void addPassiveUp(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        bap.addPassiveUp();
    }

    public void remPassiveUp(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        bap.remPassiveUp();
    }

    public void resetPassiveUp(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        bap.resetPassiveUp();
    }

    public int getPassiveDownload(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        return bap.getPassiveDownload();
    }

    public int getPassiveDw(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        return bap.getPassiveDw();
    }

    public void addPassiveDw(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        bap.addPassiveDw();
    }

    public void remPassiveDw(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        bap.remPassiveDw();
    }

    public void resetPassiveDw(Node node) {
        BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(this.bandwidth);
        bap.resetPassiveDw();
    }

    /**
     * 
     * @param node
     * @return 
     */
    public long getUploadMin(Node node) {
        return ((BandwidthAwareProtocol) node.getProtocol(this.getBandwidth())).getUploadMin();
    }

    /** 
     * 
     * @param node
     * @return 
     */
    public long getUploadMax(Node node) {
        return ((BandwidthAwareProtocol) node.getProtocol(this.getBandwidth())).getUploadMax();
    }

    /**
     * 
     * @param node
     * @return 
     */
    public long getDownloadMin(Node node) {
        return ((BandwidthAwareProtocol) node.getProtocol(this.getBandwidth())).getUploadMin();
    }

    /**
     * 
     * @param node
     * @return 
     */
    public long getDownloadMax(Node node) {
        return ((BandwidthAwareProtocol) node.getProtocol(this.getBandwidth())).getUploadMax();
    }

    /**
     * 
     * @param node
     * @return 
     */
    public long getUpload(Node node) {
        return ((BandwidthAwareProtocol) node.getProtocol(this.getBandwidth())).getUpload();
    }

    /**
     * 
     * @param node
     * @return 
     */
    public long getDownload(Node node) {
        return ((BandwidthAwareProtocol) node.getProtocol(this.getBandwidth())).getDownload();
    }

    /**
     * 
     * @param node
     * @return 
     */
    public String getBwInfo(Node node) {
        return ((BandwidthAwareProtocol) node.getProtocol(this.getBandwidth())).toString();
    }
}
