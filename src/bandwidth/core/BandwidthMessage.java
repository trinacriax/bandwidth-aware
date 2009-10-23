package bandwidth.core;

import peersim.core.Node;

/**
 * Cointains the messages used in the bandwidth management protocol and
 * the error codes for data trasnfer.
 *
 * @author Alessandro Russo
 * @version $Revision: 0.02$
 */


public class BandwidthMessage {

    /**
     * Sender node
     */
    protected final Node sender;
    /**
     * Receiver node
     */
    protected final Node receiver;
    /**
     * Type of message. See constants.
     */
    protected final int MessageID;
    /**
     * Bandwidth used.
     */
    protected final long bandwidth;
    /**
     * Start time for bandwidth in according to message type.
     */
    protected long start;
    /**
     * Message for updating upload, when pending upload is present
     */
    protected final static byte UPD_UP = 10;
    /**
     * Message for updating download, when pending download is present
     */
    protected final static byte UPD_DOWN = 100;
    /**
     * Message used to notificate no upload bandwidth
     */
    public final static byte NO_UP = -1;
    /**
     * Message used to notificate no download bandwidth
     */
    public final static byte NO_DOWN = -2;

    /**
     * Constructor for creating a new entity of bandwidth message.
     * @param sender Sender node. Sender and receiver are the same.
     * @param MessageID Message identifier.
     */
    public BandwidthMessage(Node sender, int MessageID) {
        this(sender, MessageID, 0);

    }

    /**
     * Constructor for creating a new entity of bandwidth message.
     * @param sender Sender node.
     * @param receiver Receiver node.
     * @param MessageID Message identifier.
     */
    public BandwidthMessage(Node sender, Node receiver, int MessageID) {
        this(sender, receiver, MessageID, 0);
    }

    /**
     * Constructor for creating a new entity of bandwidth message.
     * @param sender Sender node. Sender and receiver are the same.
     * @param MessageID Message identifier.
     * @param bandwidth Bandwidth used.
     */
    public BandwidthMessage(Node sender, int MessageID, long bandwidth) {
        this(sender, sender, MessageID, bandwidth, 0);

    }

    /**
     * Constructor for creating a new entity of bandwidth message.
     * @param sender Sender node.
     * @param receiver Receiver node.
     * @param MessageID Message identifier.
     * @param bandwidth Bandwidth used.
     */
    public BandwidthMessage(Node sender, Node receiver, int MessageID, long bandwidth) {
        this(sender, receiver, MessageID, bandwidth, 0);
    }

    /**
     * Constructor for creating a new entity of bandwidth message.
     * @param sender Sender node.
     * @param receiver Receiver node.
     * @param MessageID Message identifier.
     * @param bandwidth Bandwidth used.
     * @param start Start time
     */
    public BandwidthMessage(Node sender, Node receiver, int MessageID, long bandwidth, long start) {
        this.sender = sender;
        this.receiver = receiver;
        this.MessageID = MessageID;
        this.bandwidth = bandwidth;
        this.start = start;
    }

    /**
     * Get the sender node.
     * @return Sender node.
     */
    public Node getSender() {
        return this.sender;
    }

    /**
     * Get the receiver node.
     * @return Receiver node.
     */
    public Node getReceiver() {
        return this.receiver;
    }

    /**
     * Get the type of messagge.
     * @return Type of message.
     */
    public int getMessage() {
        return this.MessageID;
    }

    /**
     * Get the bandwidth used.
     * @return bandwidth used as long.
     */
    public long getBandwidth() {
        return this.bandwidth;
    }

    /**
     * Get the start time.
     * @return start time of the messagge.
     */
    public long getStart() {
        return this.start;
    }

    /**
     * Set the start time
     * @param value Start time to assign to the current messagge.
     */
    public void setStart(long value) {
        this.start = value;
    }

    /**
     *Printable version of bandwidth message.
     * @return String containing labels and values of current bandwidth message.
     */
    public String toString() {
        return "Sender " + this.sender.getID() + " | Receiver " + this.receiver.getID() + " | Message " + this.MessageID + " | Bandwidth " + this.bandwidth + " | Start " + this.start + ".";
    }
}
