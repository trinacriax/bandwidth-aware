package bandwidth.test;



import peersim.core.*;

public interface TransportBandwidthTimed extends Protocol
{
//---------------------------------------------------------------------
//Methods
//---------------------------------------------------------------------

/**
 * Delivers the message with either a uniform random delay or a given delay.
*/
public long sendControl(Node src, Node dest, Object msg, int pid);
public void sendControl(Node src, Node dest, Object msg, long delay, int pid);

/**
 * Returns a random
 * delay, that is drawn from the configured interval according to the uniform
 * distribution.
*/
public long getLatency(Node src, Node dest);


}
