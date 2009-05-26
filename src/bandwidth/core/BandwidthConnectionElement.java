package bandwidth.core;

import peersim.core.Node;

public class BandwidthConnectionElement {

    Node sender;
    Node receiver;
    long bandwidth;
    long start_time;
    long end_time;
    long txid;

    public BandwidthConnectionElement(Node sender, Node receiver, long band, long end, long txid) {
        this(sender, receiver, band, 0, end, txid);
    }

    public BandwidthConnectionElement(Node sender, Node receiver, long band, long end) {
        this(sender, receiver, band, 0, end, 0);
    }

    public BandwidthConnectionElement(Node sender, Node receiver, long band, long start, long end, long txid) {
        this.sender = sender;
        this.receiver = receiver;
        this.bandwidth = band;
        this.start_time = start;
        this.end_time = end;
        this.txid = txid;
    }

    public Node getSender() {
        return this.sender;
    }

    public Node getReceiver() {
        return this.receiver;
    }

    public long getBandwidth() {
        return this.bandwidth;
    }

    public long getStart() {
        return this.start_time;
    }

    public long getEnd() {
        return this.end_time;
    }

    public long getTxId(){
        return this.txid;
    }
    public void setStarttime(long value)
    {this.start_time = value; }
    
    public void setEndtime(long value)
    {this.end_time = value; }
    
    public boolean equals(BandwidthConnectionElement ce) {
        if ((ce.sender.getIndex() == this.sender.getIndex()) && (ce.receiver.getIndex() == this.receiver.getIndex()) && (ce.start_time== this.start_time) &&
                (ce.bandwidth == this.bandwidth) && (ce.end_time == this.end_time)) {
            return true;
        } else {
            return false;
        }

    }

    public String toString() {
        return "| Src " + this.sender.getIndex() + " | Rec " + this.receiver.getIndex() + " | TxID  " + this.txid+
                " | Bwd " + this.bandwidth + " | Start " + this.start_time + " | End " + this.end_time+"|";
    }

    public String getValues() {
        return this.sender.getIndex() + "\t" + this.receiver.getIndex() + "\t" + this.txid +
                "\t" + this.bandwidth + "\t\t" + this.start_time + "\t" + this.end_time + "; ";
    }

    public String getLabels() {
        return "SRC\tDEST\tTxID\tBandwidth\tStart\tEnd\t";
    }
}
