package bandwidth.test;

import bandwidth.core.BandwidthMessage;
import bandwidth.core.BandwidthAwareProtocol;
import peersim.config.*;
import peersim.core.*;
import peersim.cdsim.*;
import peersim.edsim.*;

/**
 * TEST CLASS
 * @author Alessandro Russo
 * @version 1.0
 */
public class BandwidthTester extends BandwidthDataStructure implements CDProtocol, EDProtocol {
//--------------------------------------------------------------------------
// Initialization
//--------------------------------------------------------------------------

    /**
     * @param prefix string prefix for configuration properties
     */
    public BandwidthTester(String prefix) {
        super(prefix);
    }

    /**
     * This is the standard method the define periodic activity.
     * The frequency of execution of this method is defined by a
     * {@link peersim.edsim.CDScheduler} component in the configuration.
     */
    @Override
    public void nextCycle(Node node, int pid) {
    }

    /**
     * This method simulates a message from a {@peersim.core.Node} source to a receiver {@peersim.core.Node}
     * @param Sender node
     * @param Receiver node
     * @param Message to deliver
     * @param Protocol identifier
     */
    /**
     * It is simply a shortcut to add event with a given delay in the queue of the simulator.
     */
    public long send(Node src, Node dest, Object msg, int pid) {
        long delay = ((UniformRandomTransportTimed) src.getProtocol(FastConfig.getTransport(pid))).sendControl(src, dest, msg, pid);
        return delay;
    }

    /**
     * Send a message from the sender to the receiver with a given delay.
     * @param src sender node.
     * @param dest destination node.
     * @param msg message to deliver.
     * @param delay message delay.
     * @param pid protocol identifier.
     */
    public void send(Node src, Node dest, Object msg, long delay, int pid) {
        ((UniformRandomTransportTimed) src.getProtocol(FastConfig.getTransport(pid))).sendControl(src, dest, msg, delay, pid);
    }

    /**
     * Asynchronous simulator.
     * @param node
     * @param pid
     * @param event 
     */
    @Override
    public void processEvent(Node node, int pid, Object event) {
        BandwidthTesterMessage im = (BandwidthTesterMessage) event;
        BandwidthTester sender;
        BandwidthTester receiver;
        if (im.getSender() == null) {
            System.err.println("--- Time " + CommonState.getTime() + " Node " + node.getID() + " receives a message with NULL sender, this will be skipped.");
            return;
        }
        switch (im.getMessage()) {
            case BandwidthInfo.SWITCH_PUSH: {
                //**************************** PUSH STATE ****************************\\
                sender = ((BandwidthTester) (node.getProtocol(pid)));
                if (sender.getDebug() >= 1) {
                    System.out.print(CommonState.getTime() + "\tNode " + node.getID() + " PUSH CYCLE. ChunksBitmap " + sender.bitmap() + " ");
                    System.out.println(" #Chunks " + sender.getSize() + " " + sender.getBwInfo(node));
                }
                Node peer = null;
                long delay = 0;
                int chunktopush = sender.getLast();//sender select latest chunk
                peer = Network.get(0);//target peer is always 0
                //IdleProtocol neighbors = (IdleProtocol) node.getProtocol(FastConfig.getLinkable(pid));//Select the target node among neighbors
                //peer = neighbors.getNeighbor(CommonState.r.nextInt(neighbors.degree()));
                sender.addActiveUp(node);
                receiver = ((BandwidthTester) (peer.getProtocol(pid)));
                BandwidthTesterMessage imm = new BandwidthTesterMessage(chunktopush, node, BandwidthInfo.PUSH);
                this.send(node, peer, imm, pid);
                if (sender.getDebug() >= 1) {
                    System.out.println(CommonState.getTime() + "\tNode " + node.getID() + " PUSHes chunk " + imm.getChunkids() + " to Node " + peer.getID()
                            + " MexRX " + (delay + CommonState.getTime()));
                }
                sender = null;
                break;
            }
            case BandwidthInfo.PUSH: //************************************* N O D O   R E C E I V E    P U S H   ******************************************
            {
                receiver = (BandwidthTester) (node.getProtocol(pid));
                if (receiver.getDebug() >= 1) {
                    System.out.println(CommonState.getTime() + "\tNode " + node.getID() + " ChunksBitmap |" + receiver.bitmap() + "| receives " + im.getMessageID()
                            + " from " + im.getSender().getID() + " for chunk(s) " + im.getChunkids() + receiver.getBwInfo(node) + ".");
                }
                int chunktopush = im.getChunk();
                if (receiver.getPassiveDw(node) >= receiver.getPassiveDownload(node) || receiver.getDownload(node) < receiver.getDownloadMin(node)) {//numero massimo di download passivi raggiunto
                    if (receiver.getDebug() >= 1) {
                        System.out.println("\tREFUSE - it has either reached the max number of passive downloads " + receiver.getPassiveDw(node) + "/" + receiver.getPassiveDownload(node) + " no more bandwidth in download");
                    }
                    BandwidthTesterMessage imm = new BandwidthTesterMessage(chunktopush, node, BandwidthInfo.NO_DOWNLOAD_BANDWIDTH_PUSH);
                    long delay = this.send(node, im.getSender(), imm, pid);
                    if (receiver.getDebug() >= 1) {
                        System.out.println("\tNode " + node.getID() + " sends " + imm.getMessageID() + " to " + im.getSender().getID() + " MexRx " + (CommonState.getTime() + delay));
                    }
                } else {
                    if (receiver.getDebug() >= 1) {
                        System.out.println("\tNode " + node.getID() + " accepts chunk " + chunktopush + " from " + im.getSender().getID());
                    }
                    BandwidthTesterMessage imm = new BandwidthTesterMessage(chunktopush, node, BandwidthInfo.OK_PUSH);
                    long delay = this.send(node, im.getSender(), imm, pid);
                    receiver.addPassiveDw(node);
                    receiver.setInDown(chunktopush);
                    if (receiver.getDebug() >= 1) {
                        System.out.println("\tNode " + node.getID() + " sends OK_PUSH to " + im.getSender().getID()
                                + " for chunks m:" + chunktopush + " MexRx " + (CommonState.getTime() + delay) + receiver.getBwInfo(node) + ".");
                    }
                }
                receiver = null;
                break;
            }
            //************************************* N O D E   R I C E V E    O K    P E R   I L   P U S H ******************************************
            case BandwidthInfo.OK_PUSH: {
                sender = ((BandwidthTester) (node.getProtocol(pid)));
                receiver = ((BandwidthTester) (im.getSender().getProtocol(pid)));
                int chunktopush = im.getChunk();
                long response = sender.getChunk(chunktopush);
                if (sender.getDebug() >= 1) {
                    System.out.println(CommonState.getTime() + "\tNode " + node.getID() + " PUSH CYCLE " + " #Chunks " + sender.getSize() + " " + sender.getBwInfo(node)
                            + " rec-OK_PUSH from " + im.getSender().getID() + " for chunk " + chunktopush + "(" + response + ")");
                }
                if (response != BandwidthInfo.NOT_OWNED && response != BandwidthInfo.IN_DOWNLOAD) {
                    BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(sender.getBandwidth());
                    long eedelay = 10;
                    long result = bap.sendData(sender.getChunkSize(), node, im.getSender(), eedelay, sender.getBandwidth());
                    if (result == BandwidthMessage.NO_UP || result == BandwidthMessage.NO_DOWN || result == -1) {
                        receiver.resetInDown(chunktopush);
                        if (sender.getDebug() >= 1 && result == BandwidthMessage.NO_UP) {
                            System.out.println("\tNode " + node.getID() + " has no more upload bandwidth for transmission with Node " + im.getSender().getID() + ", upload " + sender.getUpload(node));
                        } else if (sender.getDebug() >= 1 && result == BandwidthMessage.NO_DOWN) {
                            System.out.println("\tNode " + im.getSender().getID() + " has no more download bandwidth for receiving chunks from Node " + node.getID() + ", download " + receiver.getDownload(im.getSender()));
                        }
                        if (sender.getDebug() >= 1) {
                            System.out.print("\tSender Active up from " + sender.getActiveUp(node));
                        }
                        sender.remActiveUp(node);
                        if (receiver.getDebug() >= 1) {
                            System.out.println(" to " + sender.getActiveUp(node));
                            System.out.print("\tReceiver Passive down from " + receiver.getPassiveDw(im.getSender()));
                        }
                        receiver.remPassiveDw(im.getSender());
                        if (receiver.getDebug() >= 1) {
                            System.out.println(" to " + receiver.getPassiveDw(im.getSender()));
                        }
                        long delay = sender.getSwitchTime();
                        if (sender.getDebug() >= 1) {
                            System.out.println("\tNode " + node.getID() + " SWITCH to PUSHa at " + CommonState.getTime() + " MexRX " + (CommonState.getTime() + delay));
                        }
                        this.send(node, node, new BandwidthTesterMessage(0, node, BandwidthInfo.SWITCH_PUSH), delay, pid);
                    } else {
                        long delay = eedelay;
                        this.send(node, im.getSender(), new BandwidthTesterMessage(chunktopush, node, BandwidthInfo.START_PUSH), pid);
                        if (sender.getDebug() >= 1) {
                            System.out.println("\tNode " + node.getID() + " sends START_PUSH m:" + chunktopush + " to " + im.getSender().getID() + " MexRx " + (CommonState.getTime() + delay));
                        }
                        delay += result;
                        if (sender.getDebug() >= 1) {
                            System.out.println("\tNode " + node.getID() + " sends FINISH_PUSH m:" + chunktopush + " to " + im.getSender().getID() + " MexRx " + (CommonState.getTime() + delay));
                        }
                        this.send(node, im.getSender(), new BandwidthTesterMessage(chunktopush, node, BandwidthInfo.FINISH_PUSH), delay, pid);
                        delay += sender.getSwitchTime();
                        if (sender.getDebug() >= 1) {
                            System.out.println("\tNode " + node.getID() + " will SWITCH to PUSHb at " + (CommonState.getTime() + result + sender.getSwitchTime()));
                        }
                        this.send(node, node, new BandwidthTesterMessage(0, node, BandwidthInfo.SWITCH_PUSH), delay, pid);

                    }
                } else {
                    System.err.println("::: ATTENTION - case not threated in OK_PUSH " + CommonState.getTime()
                            + " Receiver " + im.getSender().getID() + " has proposed a chunks that the sender " + node.getID() + " does not own: chunk " + chunktopush + " ( " + sender.getChunk(chunktopush) + ")");
                    System.exit(-10);
                }
                sender = receiver = null;
                break;
            }
            case BandwidthInfo.START_PUSH: {
                receiver = ((BandwidthTester) (node.getProtocol(pid)));
                long chunktopush = im.getChunk();
                if (receiver.getDebug() >= 1) {
                    System.out.println(CommonState.getTime() + "\tNode " + node.getID() + " PUSH CYCLE "
                            + " #Chunks " + receiver.getSize() + " " + receiver.getBwInfo(node)
                            + " recSTART_PUSH " + chunktopush + " from " + im.getSender().getID());
                }
                sender = receiver = null;
                break;
            }
            case BandwidthInfo.FINISH_PUSH: {
                sender = ((BandwidthTester) (im.getSender().getProtocol(pid)));
                receiver = ((BandwidthTester) (node.getProtocol(pid)));
                int chunktopush = im.getChunk();
                if (receiver.getDebug() >= 1) {
                    System.out.println(CommonState.getTime() + "\tNode " + node.getID() + " recFINISH_PUSH " + im.getChunkids() + " from " + im.getSender().getID());
                }
                sender.remActiveUp(im.getSender());
                sender.addSuccessUpload();
                if (receiver.getSize() == 0) {
                    this.send(node, node, new BandwidthTesterMessage(0, node, BandwidthInfo.SWITCH_PUSH), receiver.getSwitchTime(), pid);
                    if (sender.getDebug() >= 1) {
                        System.out.println("\t>>Node " + node.getID() + " has just been ACTIVATED!!! SWITCH PUSH at time " + (CommonState.getTime() + receiver.getSwitchTime()));
                    }
                }
                receiver.remPassiveDw(node);
                receiver.addChunk(chunktopush, BandwidthInfo.PUSH_CYCLE);
                sender = receiver = null;
                break;
            }
            case BandwidthInfo.NO_DOWNLOAD_BANDWIDTH_PUSH: {
                sender = ((BandwidthTester) (node.getProtocol(pid)));
                if (sender.getDebug() >= 1) {
                    System.out.println(CommonState.getTime() + "\tNode " + node.getID() + " receives a message from Node " + im.getSender().getID() + " that does not have more download bandwidth");
                }
            }
            sender.remActiveUp(node);
            long delay = sender.getSwitchTime();
            if (sender.getDebug() >= 1) {
                System.out.println("\tNode " + node.getID() + " will SWITCH to PUSHd at time " + CommonState.getTime() + " MexRX " + (CommonState.getTime() + delay));
            }
            this.send(node, node, new BandwidthTesterMessage(0, node, BandwidthInfo.SWITCH_PUSH), delay, pid);
            sender = receiver = null;
            break;
        }
    }
}
