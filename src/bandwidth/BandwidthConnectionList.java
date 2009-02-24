package bandwidth;

import java.util.LinkedList;

public class BandwidthConnectionList {

    protected LinkedList connection_list;

    public BandwidthConnectionList() {
        connection_list = new LinkedList();
    }

    public void addConnection(BandwidthConnectionElement ce) {
//		System.out.print("Adding "+ce.toString()+" oldsize "+this.connection_list.size()+" ");
        if (this.connection_list.isEmpty()) {
            this.connection_list.add(ce);
        } else {
            BandwidthConnectionElement tmp;
            long finish_ce = ce.getEnd();
            long actual_ce;
            this.connection_list.addLast(ce);
            for (int i = 0; i < this.connection_list.size(); i++) {
                actual_ce = ((BandwidthConnectionElement) this.connection_list.get(i)).getEnd();
                if (finish_ce <= actual_ce) {
                    for (int j = this.connection_list.size() - 1; j > i; j--) {
                        tmp = ((BandwidthConnectionElement) this.connection_list.get(j - 1));
                        this.connection_list.set(j, tmp);
                    }
                    this.connection_list.set(i, ce);
                    return;
                }
            }
        }
    }
    
    

    public BandwidthConnectionElement remConnection(BandwidthConnectionElement ce) {
//        System.out.println("Looking for connection "+ce);
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

    public BandwidthConnectionElement getFirstEnd() {
        BandwidthConnectionElement tmp;
        if (this.connection_list.isEmpty()) {
            return null;
        } else {
            tmp = ((BandwidthConnectionElement) this.connection_list.getFirst());
            return tmp;
        }
    }

    public BandwidthConnectionElement getElement(int i) {
        if (this.connection_list.isEmpty()) {
            return null;
        }
        if (this.connection_list.size() < i) {
            return null;
        }
        BandwidthConnectionElement ce = (BandwidthConnectionElement) this.connection_list.get(i);
        return ce;
    }

    public int getSize() {
        return this.connection_list.size();
    }

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
}
