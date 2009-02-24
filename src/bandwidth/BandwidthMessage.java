/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bandwidth;

/**
 *
 * @author ax
 */

import peersim.core.Node;

public class BandwidthMessage {

	protected final Node sender;
    protected final Node receiver;
	protected final int MessageID;
	protected final long bandwidth;

    protected final static byte UPD_UP = 10;
    protected final static byte UPD_DOWN = 100;
    public final static byte NO_UP = -1;
    public final static byte NO_DOWN = -2;

    public BandwidthMessage(Node sender, int MessageID, long bandwidth)
	{
		this.sender = sender;
        this.receiver = sender;
		this.MessageID = MessageID;
		this.bandwidth = bandwidth;
	}

    public BandwidthMessage(Node sender, Node receiver, int MessageID, long bandwidth)
	{
		this.sender = sender;
        this.receiver = receiver;
		this.MessageID = MessageID;
		this.bandwidth = bandwidth;
	}

	public BandwidthMessage(Node sender, int MessageID)
	{
        this(sender, MessageID, 0);

	}

    public BandwidthMessage(Node sender, Node receiver, int MessageID)
	{
        this(sender, receiver, MessageID, 0);
	}

	

    public Node getSender(){
        return this.sender;
    }

    public Node getReceiver(){
        return this.receiver;
    }

    public int getMessage(){
        return this.MessageID;
    }

    public long getBandwidth(){
        return this.bandwidth;
    }

	public String toString(){
		String result = "Sender " + this.sender.getID() + " | Receiver " + this.receiver.getID()+  " | Message "+ this.MessageID +" | Banda "+ this.bandwidth+".";
		return result;
	}

}