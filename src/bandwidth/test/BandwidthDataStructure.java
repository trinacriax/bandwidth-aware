package bandwidth.test;

import bandwidth.core.BandwidthAwareProtocol;
import peersim.config.FastConfig;
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
    /**Source identifier*/
    protected int source;
    /**Debug level */
    protected int debug;
    /**# of chunks transmitted correctly via push*/
    protected int success_upload;
    /**# of chunks offered in push*/
    protected int push_window;
    /**Protocol ID for bandwidth mechanism*/
    protected int bandwidth;
    /**#of chunks received in push*/
    protected int chunkpush;
    /**Chunk's size in bits*/
    protected long chunk_size;
    /**# of failed push*/
    protected int fail_push;
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
    /**Total neighbor knowledge*/
    private int nk;

    public BandwidthDataStructure(String prefix) {
        super();
    }

    /**
     * 
     * Clone method
     * 
     */
    @Override
    public Object clone() {
        BandwidthDataStructure clh = null;
        try {
            clh = (BandwidthDataStructure) super.clone();
        } catch (CloneNotSupportedException e) {
        } // never happens
        clh.chunk_list = null;// new long[1];
        clh.bandwidth = 0;
        clh.number_of_chunks = 0;
        clh.completed = 0L;
        clh.debug = 0;
        clh.fail_push = 0;
        clh.chunk_size = 0;
        clh.push_window = 0;
        clh.source = 0;
        clh.success_upload = 0;
        clh.chunkpush = 0;
        clh.max_push_attempts = 0;
        clh.push_attempts = 0;
        clh.time_in_push = 0L;
        clh.switchtime = 0L;
        clh.nk = 0;
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
        this.push_window = 0;
        this.debug = 0;
        this.bandwidth = 0;
        this.fail_push = 0;
        this.number_of_chunks = 0;
        this.source = 0;
        this.chunkpush = 0;
        this.max_push_attempts = 0;
        this.push_attempts = 0;
        this.time_in_push = 0;
        this.switchtime = 0;
        this.success_upload = 0;
        this.nk = 0;
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
            this.chunk_list[i] = -1;
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
     * Set the max number of push retries
     * @param push_retries max number of push attempts
     */
    public void setPushRetry(int push_retries) {
        this.max_push_attempts = push_retries;
    }

    /**
     * Get the current number of push attempts
     * @return the number of push attempts
     */
    public int getPushRetry() {
        return this.max_push_attempts;
    }

    /**
     * Add one to the number of push attempts
     */
    public void addPushAttempt() {
        this.push_attempts++;
    }

    /**
     * Add one to the number of push attempts
     */
    public int getPushAttempt() {
        return this.push_attempts;
    }

    /**
     * Remove one to the number of push attempts
     */
    public void remPushAttempt() {
        this.push_attempts--;
    }

    /**
     * Reset the number of push attempts
     */
    public void resetPushAttempt() {
        this.push_attempts = 0;
    }

    /**
     * Return the time spent in push transmission by the node
     * @return the time spent in push
     * */
    public long getTimePush() {
        return this.time_in_push;
    }

    /**
     * Set the number of chunks the node proposes in push
     * @param window size for the number of chunks proposed in push
     * */
    public void setPushWindow(int window) {
        this.push_window = window;
    }

    /**
     * Return the number of chunks the node proposes in push
     * @return the window size for the number of chunks proposed in push
     **/
    public int getPushWindow() {
        return this.push_window;
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
     * add the number of chunk in push
     * */
    public void addChunkInPush() {
        this.chunkpush++;
    }

    /**
     * Get the number of chunks in push.
     * @return chunk in push.
     */
    public int getChunkInPush() {
        return this.chunkpush;
    }

    /**
     * Set the source node.
     * @param _source source node.
     */
    public void setSource(int _source) {
        this.source = _source;
    }

    /**
     * Get the source.
     * @return source node for testing protocol.
     */
    public int getSource() {
        return this.source;
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
     * set the node as completed
     * @param value time in which the node completes the reception of chunks.
     */
    public void setCompleted(long value) {
        this.completed = value;
    }

    /**
     * get the time in which the node has completed to receive chunks.
     * @return completion time.
     */
    public long getCompleted() {
        return this.completed;
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

    /**
     * 
     * @param chunk_size 
     */
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
     */
    public void addFailPush() {
        this.fail_push++;
    }

    /**
     * 
     * @return 
     */
    public int getFailPush() {
        return this.fail_push;
    }

    /**
     * 
     * @param timeinpush 
     */
    public void addTimeInPush(long timeinpush) {
        this.time_in_push += timeinpush;
    }

    /**
     * 
     * @return 
     */
    public String getConnections() {
        String result = "]] " + this.getSize();// + " : " + this.bitmap();
        return result;
    }

    /**
     * 
     * @return 
     */
    public int getLastsrc() {
        return this.nk;
    }

    /**
     * 
     */
    public void addLastsrc() {
        this.nk++;
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
    public boolean produce() {
        int index = 0;
        if (this.getSize() == 0) {
            index = 0;
        } else if (this.getSize() == this.getNumberOfChunks()) {
            return false;
        } else {
            index = this.getLast() + 1;
        }
        this.addChunk(index, BandwidthInfo.PUSH_CYCLE);

        return true;
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
     * @return 
     */
    public int getLastSRC() {
        int index = this.getLast();
        return index;
    }

    /**
     * 
     * @param elements
     * @return 
     */
    public int[] getLast(int elements) {
        if (this.getSize() < elements) {
            elements = this.getSize();
        }
        int result[] = new int[elements];
        int index = 0;
        int count = 0;
        while (elements > 0 && count < this.chunk_list.length) {
            int id = (this.chunk_list.length - count - 1);
            if (this.chunk_list[id] != BandwidthInfo.IN_DOWNLOAD && this.chunk_list[id] != BandwidthInfo.NOT_OWNED) {
                result[index++] = id;
                elements--;
            }
            count++;
        }
        if (elements > 0) {
            int temp[] = new int[result.length - elements];
            System.arraycopy(result, 0, temp, 0, temp.length);
            result = temp;
        }
        return result;
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
     * @param index
     * @return 
     */
    public int getChunks(int[] index) {
        int owned = 0;
        for (int i = 0; i < index.length; i++) {
            if (this.getChunk(index[i]) > BandwidthInfo.OWNED) {
                owned++;
            }
        }
        return owned;
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
        sbuf.append("Nodo ");
        sbuf.append(node.getID());
        sbuf.append(", Time ");
        sbuf.append(CommonState.getTime());
        sbuf.append(" , Fail Push ");
        sbuf.append(this.fail_push);
        sbuf.append(", Lista ");
        sbuf.append(this.getSize());
        if (this.getSize() == this.getNumberOfChunks()) {
            sbuf.append(" >>> ha tutti i chunks.");
        } else {
            sbuf.append(".");
        }
        return sbuf.toString();
    }

    /**
     * 
     * @param node
     * @param pid
     * @return 
     */
    public String getNeighborhood(Node node, int pid) {
        Linkable linkable = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
        String results = "Node " + node.getID() + ": " + linkable.degree() + " [ ";
        for (int i = 0; i < linkable.degree(); i++) {
            results += linkable.getNeighbor(i).getID() + ", ";
        }
        results += " ]";
        return results;
    }

    /**
     * 
     * @param node
     * @param pid
     * @return 
     */
    public Node getNeighbor(Node node, int pid) {
        Linkable linkable = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
        return linkable.getNeighbor(CommonState.r.nextInt(linkable.degree()));
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
}
