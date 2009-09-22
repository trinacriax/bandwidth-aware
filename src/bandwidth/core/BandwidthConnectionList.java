package bandwidth.core;

/**
 * This class implements a list of connection elements which represent
 * the currrent active connections of current node.
 */
import java.util.LinkedList;
import peersim.core.Node;

public class BandwidthConnectionList {

    /**
     * Collects a set of connection elements
     */
    protected LinkedList connection_list;

    /**
     * Constructor method to initialize the list of connection elements.
     */
    public BandwidthConnectionList() {
        connection_list = new LinkedList();
    }

    /**
     * This methos is used to add a connection element to the current list of connections.
     * Connection elemenets are sorted first for start time and then for end time.
     *
     * @param ce Connection element to be added with all paramenters.
     */
    public void addConnection(BandwidthConnectionElement ce) {
        if (this.connection_list.isEmpty()) {
            this.connection_list.add(ce);
        } else {
            BandwidthConnectionElement tmp;
            long finish_ce = ce.getEnd();
            long start_ce = ce.getStart();
            long actual_fe, actual_se;
            this.connection_list.addLast(ce);
            for (int i = 0; i < this.connection_list.size(); i++) {
                actual_fe = ((BandwidthConnectionElement) this.connection_list.get(i)).getEnd();
                actual_se = ((BandwidthConnectionElement) this.connection_list.get(i)).getStart();
                if (finish_ce < actual_fe) {
                    for (int j = this.connection_list.size() - 1; j > i; j--) {
                        tmp = ((BandwidthConnectionElement) this.connection_list.get(j - 1));
                        this.connection_list.set(j, tmp);
                    }
                    this.connection_list.set(i, ce);
                    return;
                } else if (finish_ce == actual_fe) {
                    for (; start_ce > actual_se && i < this.connection_list.size(); i++) {
                        actual_fe = ((BandwidthConnectionElement) this.connection_list.get(i)).getEnd();
                        actual_se = ((BandwidthConnectionElement) this.connection_list.get(i)).getStart();
                    }
                    for (int j = this.connection_list.size() - 1; j > i; j--) {
                        tmp = ((BandwidthConnectionElement) this.connection_list.get(j - 1));
                        this.connection_list.set(j, tmp);
                    }
                    if (i >= this.connection_list.size()) {
                        i--;
                    }
                    this.connection_list.set(i, ce);
                    return;
                }
            }

        }
    }

    /**
     * Remove a connection element from the list of elements.
     * @param ce ConnectionElement to be removed. The criteria used to identify the ConnectionElement to remove is the
     *      start time, the end time and the node.
     * @return Bandwidth connection element just removed, null if no elements with this criteria was found.
     */
    public BandwidthConnectionElement remConnection(BandwidthConnectionElement ce) {
        if (this.connection_list.isEmpty()) {
            return null;
        } else {
            BandwidthConnectionElement actual;
            for (int i = 0; i < this.connection_list.size(); i++) {
                actual = ((BandwidthConnectionElement) this.connection_list.get(i));
                if (ce.equals(actual)) {
                    actual = ((BandwidthConnectionElement) this.connection_list.remove(i));
                    return actual;
                }
            }
        }
        return null;
    }

    /**
     * Return the first connection element with a given Current node and target node.
     * @param s Current node
     * @param r Target node.
     * @return The corresponding connection element, null otherwise.
     */
    public BandwidthConnectionElement getRecord(Node s, Node r) {
        BandwidthConnectionElement bce = null;
        if (connection_list.isEmpty()) {
            return bce;
        }
        for (int i = 0; i < this.connection_list.size(); i++) {
            bce = (BandwidthConnectionElement) this.connection_list.get(i);
            if (bce.getSender() == s && bce.getReceiver() == r) {
                return bce;
            }
        }
        return null;
    }

    /**
     * Return the first connection element with a given Current node, target node and transaction identifier.
     * @param s Current node.
     * @param r Target node.
     * @param txid long value which identify the transaction.
     * @return The corresponding connection element, null otherwise.
     */
    public BandwidthConnectionElement getRecordT(Node s, Node r, long txid) {
        BandwidthConnectionElement bce = null;
        if (connection_list.isEmpty()) {
            return bce;
        }
        for (int i = 0; i < this.connection_list.size(); i++) {
            bce = (BandwidthConnectionElement) this.connection_list.get(i);
            if (bce.getSender() == s && bce.getReceiver() == r && bce.getTxId() == txid) {
                return bce;
            }
        }
        return null;
    }

    /**
     * Return the first connection element with a given Current node, target node, transaction identifier and the end time.
     * @param s Current node.
     * @param r Target node.
     * @param txid long value which identify the transaction.
     * @param end end time of the connection queried
     * @return The corresponding connection element, null otherwise.
     */
    public BandwidthConnectionElement getRecord(Node s, Node r, long txid, long end) {
        BandwidthConnectionElement bce = null;
        if (connection_list.isEmpty()) {
            return bce;
        }
        for (int i = 0; i < this.connection_list.size(); i++) {
            bce = (BandwidthConnectionElement) this.connection_list.get(i);
            if (bce.getSender() == s && bce.getReceiver() == r && bce.getTxId() == txid && bce.getStart() == end) {
                return bce;
            }
        }
        return null;
    }

    /**
     * Return the first connection element with a given Current node, target node, start time and bandwidth used.
     * @param s Current node.
     * @param r Target node.
     * @param starTime Time in which the connection element started.
     * @param bandwidth Bandwidth used in the connection element queried.
     * @return The corresponding connection element, null otherwise.
     */
    public BandwidthConnectionElement getRecordE(Node s, Node r, long startTime, long bandwidth) {
        BandwidthConnectionElement bce = null;
        if (connection_list.isEmpty()) {
            return bce;
        }
        for (int i = 0; i < this.connection_list.size(); i++) {
            bce = (BandwidthConnectionElement) this.connection_list.get(i);
            if (bce.getSender() == s && bce.getReceiver() == r && bce.getStart() == startTime && bce.getBandwidth() == bandwidth) {
                return bce;
            }
        }
        return null;
    }

    /**
     * This method looks for the first connection element in the list.
     * @return The first {@see #BandwidthConnectionElement} in the list of connection elements.
     */
    public BandwidthConnectionElement getFirstEnd() {
        BandwidthConnectionElement tmp;
        if (this.connection_list.isEmpty()) {
            return null;
        } else {
            tmp = ((BandwidthConnectionElement) this.connection_list.getFirst());
            return tmp;
        }
    }

    /**
     * Returns the i-th element in the list of connections.
     * @param i Position in the list of the element.
     * @return BandwidthConnectionElement in the i-th posisition, null otherwise.
     */
    public BandwidthConnectionElement getElement(int i) {
        if (this.connection_list.isEmpty()) {
            return null;
        } else if (this.connection_list.size() < i) {
            return null;
        } else {
            BandwidthConnectionElement ce = (BandwidthConnectionElement) this.connection_list.get(i);

            return ce;
        }
    }

    /**
     * Get the number of elements in the list of connections.
     * @return The number of elements in the list.
     */
    public int getSize() {
        return this.connection_list.size();
    }

    /**
     * Gives a printable version of the connection list.
     * @return String containing all the elements in the connection list.
     */
    public String getAll() {
        String result = "";
        BandwidthConnectionElement ce = null;
        for (int i = 0; i < this.connection_list.size(); i++) {
            ce = (BandwidthConnectionElement) this.connection_list.get(i);
            if (i == 0) {
                result += ce.getLabels() + "\n";
            }
            result += ce.getValues() + "\n";
        }
        return result;
    }

    /**
     * This method is used to sample and get the bandwidth usage in the given time.
     * It looks for all connection with (startTime <= time <= endTime), and return the bandwidth usage within these times.
     * @param time Time to sample.
     * @return Bandwidth usage.
     */
    public long getBandwidthUsage(long time) {
        long band_use = 0;
        for (int i = 0; i < this.getSize(); i++) {
            BandwidthConnectionElement bce = (BandwidthConnectionElement) this.getElement(i);
            if (bce.getStart() <= time && bce.getEnd() <= time) {
                band_use += bce.bandwidth;
            }
        }
        return band_use;
    }
}
