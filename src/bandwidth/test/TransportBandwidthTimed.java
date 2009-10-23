package bandwidth.test;

import peersim.core.*;

/**
 * TEST CLASS
 * @author Alessandro Russo
 * @version 1.0
 */
public interface TransportBandwidthTimed extends Protocol {

//---------------------------------------------------------------------
//Methods
//---------------------------------------------------------------------
    /**
     * Send a message with a random delay.
     * @param src Source node.
     * @param dest Destination node.
     * @param msg Message to send.
     * @param pid Corresponding protocol identifier.
     * @return Time need to perform the transfer.
     */
    public long sendControl(Node src, Node dest, Object msg, int pid);

    /**
     * Send a message with a random delay.
     * @param src Source node.
     * @param dest Destination node.
     * @param msg Message to send.
     * @param pid Corresponding protocol identifier.
     * @param delay Time needed to perform the transfer.
     */
    public void sendControl(Node src, Node dest, Object msg, long delay, int pid);

    /**
     * Get the latency between two nodes.
     * @param src sender node
     * @param dest destination node.
     * @return latency between nodes.
     */
    public long getLatency(Node src, Node dest);
}
