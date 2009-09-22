package bandwidth.core;

import peersim.core.Node;

public class BandwidthConnectionElement {

    /**
     * Sender node.
     */
    Node sender;
    /**
     * Receiver node.
     */
    Node receiver;
    /**
     * Bandwidth used.
     */
    long bandwidth;
    /**
     * Start time for bandwidth usage.
     */
    long start_time;
    /**
     * End time for bandwidth usage.
     */
    long end_time;
    /**
     * Transmission identifier.
     */
    long txid;
    /**
     * Check whether the node has pending bandwidth to upload or not.
     */
    boolean check;

    /**
     * Constructor method.
     * @param sender Sender node, i.e. the current node.
     * @param receiver Receiver node.
     * @param band Bandwidth used.
     * @param end End time for this connection.
     * @param txid Transmission identifier.
     */
    public BandwidthConnectionElement(Node sender, Node receiver, long band, long end, long txid) {
        this(sender, receiver, band, 0, end, txid);
    }

    /**
     * Constructor method.
     * @param sender Sender node, i.e. the current node.
     * @param receiver Receiver node.
     * @param band Bandwidth used.
     * @param end End time for this connection.
     */
    public BandwidthConnectionElement(Node sender, Node receiver, long band, long end) {
        this(sender, receiver, band, 0, end, 0);
    }

    /**
     * Constructor method.
     * @param sender Sender node, i.e. the current node.
     * @param receiver Receiver node.
     * @param band Bandwidth used.
     * @param start Start time for this connection.
     * @param end End time for this connection.
     * @param txid Transmission identifier.
     */
    public BandwidthConnectionElement(Node sender, Node receiver, long band, long start, long end, long txid) {
        this.sender = sender;
        this.receiver = receiver;
        this.bandwidth = band;
        this.start_time = start;
        this.end_time = end;
        this.txid = txid;
        this.check = false;
    }

    /**
     * Get the sender node.
     * @return Node sender.
     */
    public Node getSender() {
        return this.sender;
    }

    /**
     * Receiver node.
     * @return Node receiver.
     */
    public Node getReceiver() {
        return this.receiver;
    }

    /**
     * Get the bandwidth used in this connection element.
     * @return bandwidth used.
     */
    public long getBandwidth() {
        return this.bandwidth;
    }

    /**
     * Get the start time for the connection element.
     * @return start time.
     */
    public long getStart() {
        return this.start_time;
    }

    /**
     * Get the end time for this connection.
     * @return End time for this connection.
     */
    public long getEnd() {
        return this.end_time;
    }

    /**
     * Get the transaction identifier for the connection element.
     * @return
     */
    public long getTxId() {
        return this.txid;
    }

    /**
     * Set the start time for the connection element.
     * @param value Start time
     */
    public void setStarttime(long value) {
        this.start_time = value;
    }

    /**
     * Set the end time for the connection element.
     * @param value End time
     */
    public void setEndtime(long value) {
        this.end_time = value;
    }

    /**
     * Set check value, i.e. there is a pending upload of bandwidth.
     */
    public void setCheck() {
        this.check = !check;
    }

    /**
     * Get the check value for checking whether a pending upload exists or not.
     * @return True if there is a pending upload of bandwidth, false otherwise.
     */
    public boolean getCheck() {
        return this.check;
    }

    /**
     * Check the connection element is the same of the one give.
     * @param ce Connection element to compare.
     * @return True if thery are the same, false otherwise.
     */
    public boolean equals(BandwidthConnectionElement ce) {
        if ((ce.sender.getIndex() == this.sender.getIndex()) && (ce.receiver.getIndex() == this.receiver.getIndex()) && (ce.start_time == this.start_time) &&
                (ce.bandwidth == this.bandwidth) && (ce.end_time == this.end_time)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Printable versione of connection element.
     * @return String containing information on current connection element.
     */
    public String toString() {
        return "| Src " + this.sender.getIndex() + " | Rec " + this.receiver.getIndex() + " | TxID  " + this.txid +
                " | Bwd " + this.bandwidth + " | Start " + this.start_time + " | End " + this.end_time + " | " + this.check + " |";
    }

    /**
     * Prints the valuse of the connection element.
     * @return String containing the values of current connection element.
     */
    public String getValues() {
        return this.sender.getIndex() + "\t" + this.receiver.getIndex() + "\t" + this.txid +
                "\t" + this.bandwidth + "\t\t" + this.start_time + "\t" + this.end_time + "; ";
    }

    /**
     * Print the labels of the current connection element.
     * @return String with the labels of connection element.
     */
    public String getLabels() {
        return "SRC\tDEST\tTxID\tBandwidth\tStart\tEnd\t";
    }
}
