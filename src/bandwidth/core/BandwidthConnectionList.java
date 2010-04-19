package bandwidth.core;

import java.lang.reflect.Array;
import peersim.core.Node;

/**
 * This class implements a collection of connection elements.<p>
 * Such a collection reflects all connections in the current node with its neighbors.
 *
 * @author Alessandro Russo <russo@disi.unitn.it> <p> DISI - University of Trento (Italy) <p> Napa-Wine <www.napa-wine.eu>.
 * @version $Revision: 0.2$
 *
 */
public class BandwidthConnectionList {

  /**
   * This array cointains all the connection element in this protocol instance.<p>
   * Such an array is increased and reduced in runtime to collect all the connections of the node.
   */
  protected BandwidthConnectionElement[] connection_list;
  private final static int INITIAL_CAPACITY = 16;

  /**
   * Constructor method to initialize the list of connection elements.
   */
  public BandwidthConnectionList() {
    connection_list = new BandwidthConnectionElement[INITIAL_CAPACITY];
  }

  /**
   * Get the number of elements in the list of connections.
   * @return The number of elements in the list.
   */
  public int getSize() {
    int size = 0;
    for (int i = 0, len = this.connection_list.length; i < len; i++) {
      if (this.connection_list[i] != null) {
        size++;
      }
    }
    return size;
  }

  /**
   * Checks whether the list is empty or not.
   * @return True if the list is empty, false otherwise.
   */
  public boolean isEmpty() {
    return (this.getSize() == 0);
  }

  /**
   * This methos is used to add a connection element to the current list of connections.<p>
   * Connection elemenets are sorted first for start time and then for end time.
   *
   * @param ce Connection element to be added with all paramenters.
   */
  public void addConnection(BandwidthConnectionElement ce) {
    if (this.isEmpty()) {
      this.connection_list[this.getSize()] = ce;
    } else {
      int size = this.getSize();
      if (this.connection_list.length == size) {
        //double the array size
        int new_size = (int) Math.round(connection_list.length * 2.0);
        BandwidthConnectionElement[] _connection_list = new BandwidthConnectionElement[new_size];
        System.arraycopy(connection_list, 0, _connection_list, 0, size);
        connection_list = _connection_list;
        _connection_list = null;
      }
      BandwidthConnectionElement tmp;
      long new_end = ce.getEnd();
      long new_start = ce.getStart();
      long current_end, current_start;
      this.connection_list[this.getSize()] = (ce);
      for (int i = 0, len = this.getSize(); i < len; i++) {
        current_end = (this.connection_list[i]).getEnd();
        current_start = (this.connection_list[i]).getStart();
        if (new_end < current_end) {
          for (int j = this.getSize() - 1; j > i; j--) {
            tmp = (this.connection_list[(j - 1)]);
            this.connection_list[j] = tmp;
          }
          this.connection_list[i] = ce;
          return;
        } else if (new_end == current_end) {
          for (int len1 = this.getSize(); new_start > current_start && i < len1; i++) {
            current_end = (this.connection_list[(i)]).getEnd();
            current_start = (this.connection_list[(i)]).getStart();
          }
          for (int j = this.getSize() - 1; j > i; j--) {
            tmp = (this.connection_list[(j - 1)]);
            this.connection_list[j] = tmp;
          }
          if (i >= this.getSize()) {
            i--;
          }
          this.connection_list[i] = ce;
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
    if (this.isEmpty()) {
      return null;
    } else {
      BandwidthConnectionElement actual;
      for (int i = 0, len = this.getSize(); i < len; i++) {
        actual = (this.connection_list[(i)]);
        if (ce.equals(actual)) {
          actual = (this.connection_list[(i)]);
          this.connection_list[i] = null;
          this.cleanList();
          return actual;
        }
      }
    }
    return null;
  }

  /**
   * Remove all the null element in the connection list.
   */
  public void cleanList() {
    int current = 0;
    int tmp = 0;
    int len = this.getSize();
    while (current <= len) {
      if (this.connection_list[tmp] != null) {
        tmp++;
      } else if (this.connection_list[current] != null) {
        this.connection_list[tmp] = this.connection_list[current];
        this.connection_list[current] = null;
        tmp++;
      }
      current++;
    }
  }

  /**
   * Return the first connection element with a given Current node and target node.
   * @param s Current node
   * @param r Target node.
   * @return The corresponding {@link BandwidthConnectionElement}, null otherwise.
   */
  public BandwidthConnectionElement getRecord(Node s, Node r) {
    BandwidthConnectionElement bce = null;
    if (this.isEmpty()) {
      return bce;
    }
    for (int i = 0, len = this.getSize(); i < len; i++) {
      bce = this.connection_list[(i)];
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
   * @return The corresponding {@link BandwidthConnectionElement}, null otherwise.
   */
  public BandwidthConnectionElement getRecordT(Node s, Node r, long txid) {
    BandwidthConnectionElement bce = null;
    if (this.isEmpty()) {
      return bce;
    }
    for (int i = 0, len = this.getSize(); i < len; i++) {
      bce = this.connection_list[(i)];
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
   * @return The corresponding {@link BandwidthConnectionElement}, null otherwise.
   */
  public BandwidthConnectionElement getRecord(Node s, Node r, long txid, long end) {
    BandwidthConnectionElement bce = null;
    if (this.isEmpty()) {
      return bce;
    }
    for (int i = 0, len = this.getSize(); i < len; i++) {
      bce = this.connection_list[(i)];
      this.cleanList();
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
   * @param startTime Time in which the connection element started.
   * @param bandwidth Bandwidth used in the connection element queried.
   * @return The corresponding {@link BandwidthConnectionElement}, null otherwise.
   */
  public BandwidthConnectionElement getRecordE(Node s, Node r, long startTime, long bandwidth) {
    BandwidthConnectionElement bce = null;
    if (this.isEmpty()) {
      return bce;
    }
    for (int i = 0, len = this.getSize(); i < len; i++) {
      bce = this.connection_list[(i)];
      if (bce.getSender() == s && bce.getReceiver() == r && bce.getStart() == startTime && bce.getBandwidth() == bandwidth) {
        return bce;
      }
    }
    return null;
  }

  /**
   * This method looks for the first connection element in the list.
   * @return The first {@link BandwidthConnectionElement} in the list of connection elements.
   */
  public BandwidthConnectionElement getFirstEnd() {
    BandwidthConnectionElement tmp;
    if (this.isEmpty()) {
      return null;
    } else {
      tmp = (this.connection_list[0]);
      return tmp;
    }
  }

  /**
   * Returns the i-th element in the list of connections.
   * @param i Position in the list of the element.
   * @return BandwidthConnectionElement in the i-th posisition, null otherwise.
   */
  public BandwidthConnectionElement getElement(int i) {
    if (this.isEmpty()) {
      return null;
    } else if (this.getSize() < i) {
      return null;
    } else {
      BandwidthConnectionElement ce = this.connection_list[(i)];

      return ce;
    }
  }

  /**
   * Gives a printable version of the connection list.
   * @return String containing all the elements in the connection list.
   */
  public String getAll() {
    String result = "";
    BandwidthConnectionElement ce = null;
    for (int i = 0, len = this.getSize(); i < len; i++) {
      ce = this.connection_list[(i)];
      if (i == 0) {
        result += ce.getLabels() + "\n";
      }
      result += ce.getValues() + "\n";
    }
    return result;
  }

  /**
   * This method is used to sample and get the bandwidth usage in the given time.<p>
   * It looks for all connection with (startTime <= time <= endTime), and return the bandwidth usage within these times.
   * @param time Time to sample.
   * @return Bandwidth usage.
   */
  public long getBandwidthUsage(long time) {
    long band_use = 0;
    for (int i = 0, len = this.getSize(); i < len; i++) {
      BandwidthConnectionElement bce = this.getElement(i);
      if (bce.getStart() <= time && bce.getEnd() <= time) {
        band_use += bce.bandwidth;
      }
    }
    return band_use;
  }
}
