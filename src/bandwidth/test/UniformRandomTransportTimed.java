package bandwidth.test;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;

/**
 * TEST CLASS
 * @author Alessandro Russo
 * @version 1.0
 */
public class UniformRandomTransportTimed implements TransportBandwidthTimed {

//---------------------------------------------------------------------
//Parameters
//---------------------------------------------------------------------
    private static final String PAR_MINDELAY = "mindelay";
    private static final String PAR_MAXDELAY = "maxdelay";
///**
//---------------------------------------------------------------------
//Fields
//---------------------------------------------------------------------
    /** Minimum delay for message sending */
    private final long min;
    /** Difference between the max and min delay plus one. That is, max delay is
     * min+range-1.
     */
    private final long range;
    private final long max;

//---------------------------------------------------------------------
//Initialization
//---------------------------------------------------------------------
    /**
     * Constructor read configuration parameters.
     * @param prefix
     */
    public UniformRandomTransportTimed(String prefix) {

        min = Configuration.getLong(prefix + "." + PAR_MINDELAY, 0);
        max = Configuration.getLong(prefix + "." + PAR_MAXDELAY, 0);
        if (max < min) {
            throw new IllegalParameterException(prefix + "." + PAR_MAXDELAY,
                    "The maximum latency cannot be smaller than the minimum latency");
        }
        range = max - min + 1;
    }

//---------------------------------------------------------------------
    public Object clone() {
        return this;
    }

//---------------------------------------------------------------------
//Methods
//---------------------------------------------------------------------
    public long sendControl(Node src, Node dest, Object msg, int pid) {
        long delay = getLatency(src, dest);
        EDSimulator.add(delay, msg, dest, pid);
        return delay;
    }

    public void sendControl(Node src, Node dest, Object msg, long delay, int pid) {
        EDSimulator.add(delay, msg, dest, pid);
    }

    public long getLatency(Node src, Node dest) {
        return (range == 1 ? min : min + CommonState.r.nextLong(range));
    }
}
