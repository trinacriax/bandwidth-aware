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
     * @param prefix string prefix for config properties
     */
    public BandwidthTester(String prefix) {
        super(prefix);
    }

    /**
     * This is the standard method the define periodic activity.
     * The frequency of execution of this method is defined by a
     * {@link peersim.edsim.CDScheduler} component in the configuration.
     */
    public void nextCycle(Node node, int pid) {
    }

    /**
     * This method simulates a message from a {@peersim.core.Node} source to a receiver {@peersim.core.Node}
     * @param Node src Sender node
     * @param Node dest Receiver node
     * @param Object msg Message to deliver
     * @param int pid Protocol identifier
     */
    /**
     * It is simply a shortcut to add event with a given delay in the queue of the simulator.
     */
    public long send(Node src, Node dest, Object msg, int pid) {
        long delay = ((UniformRandomTransportTimed) src.getProtocol(FastConfig.getTransport(pid))).sendControl(src, dest, msg, pid);
        return delay;
    }

    public void send(Node src, Node dest, Object msg, long delay, int pid) {
        ((UniformRandomTransportTimed) src.getProtocol(FastConfig.getTransport(pid))).sendControl(src, dest, msg, delay, pid);
    }

    /**
     *
     * This is the main method that implements the asynchronous ALTERNATE.
     * Each "case" corresponds to a state of the protocol where the node performs some operations.
     *
     */
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
                if (im.getSender().getID() != node.getID()) {
                    if (sender.getDebug() >= 0) {
                        System.err.println("!!! Time: " + CommonState.getTime() + " Node " + im.getSender().getID() + " tries to change the state of Node " + node.getID() + " in PUSH: NOT ALLOW");
                    }
                    sender = receiver = null;
                    return;
                }
                if (sender.getDebug() >= 1) {
                    System.out.print(CommonState.getTime() + "\tNode " + node.getID() + " PUSH CYCLE");
                }
                if (sender.getDebug() >= 8) {
                    System.out.print(" (" + sender.getPushAttempt() + "/" + sender.getPushRetry() + ")");
                }
                if (sender.getDebug() >= 6) {
                    System.out.print(" #Chunks " + sender.getSize() + " " + sender.getBwInfo(node));
                }
                if (sender.getDebug() >= 1) {
                    System.out.print("\n");
                }
                if (sender.getUpload(node) > sender.getUploadMax(node)) {
                    System.err.println(CommonState.getTime() + " errore " + sender.getUpload(node) + " > " + sender.getUploadMax(node));
                }
                Node peer = null;
                long delay = 0;
                int chunktopush = sender.getLast();//source select latest chunk
                peer = Network.get(0);
                receiver = ((BandwidthTester) (peer.getProtocol(pid)));
                BandwidthTesterMessage imm = new BandwidthTesterMessage(chunktopush, node, BandwidthInfo.PUSH);
                this.send(node, peer, imm, pid);
                if (sender.getDebug() >= 2) {
                    System.out.println(CommonState.getTime() + "\tNode " + node.getID() + " PUSHes chunk " + imm.getChunkids() + " to Node " + peer.getID() +
                            " MexRX " + (delay + CommonState.getTime()));
                }
                sender = receiver = null;
                return;
            }
            case BandwidthInfo.PUSH: //************************************* N O D O   R E C E I V E    P U S H   ******************************************
            {
                receiver = (BandwidthTester) (node.getProtocol(pid));
                if (receiver.getDebug() >= 2) {
                    System.out.println(CommonState.getTime() + "\tNode " + node.getID() + " receives " + im.getMessageID() + " from " + im.getSender().getID() + " for chunk(s) " + im.getChunkids() +
                            ((receiver.getDebug() >= 6) ? " " + receiver.getBwInfo(node) + " " + receiver.getConnections() : "") + ".");

                }
                int chunktopush = -1;
                long response = BandwidthInfo.OWNED; //nella proposta di push i chunk sono ordinati in modo descrescente
                for (int i = 0; i < im.getChunks().length && response != BandwidthInfo.NOT_OWNED; i++) {//recupera il chunk con id più alto che manca al nodo target tra quelli proposti dal sender
                    chunktopush = im.getChunks()[i];
                    response = receiver.getChunk(chunktopush);
                }
                //************************** NODO NON HA BANDA ****************************
                if (receiver.getPassiveDw(node) >= receiver.getPassiveDownload(node) || receiver.getDownload(node) < receiver.getDownloadMin(node)) {//numero massimo di download passivi raggiunto
                    if (receiver.getDebug() >= 3) {
                        System.out.println("\tREFUSE - it has either reached the max number of passive downloads " + receiver.getPassiveDw(node) + "/" + receiver.getPassiveDownload(node) + " no more bandwidth in download");
                    }
                    BandwidthTesterMessage imm = new BandwidthTesterMessage(chunktopush, node, BandwidthInfo.NO_DOWNLOAD_BANDWIDTH_PUSH);
                    long delay = this.send(node, im.getSender(), imm, pid);
                    if (receiver.getDebug() >= 4) {
                        System.out.println("\tNode " + node.getID() + " sends " + imm.getMessageID() + " to " + im.getSender().getID() + " MexRx " + (CommonState.getTime() + delay));
                    }
                } //************************** NODO POSSIEDE IL CHUNK OPPURE E' IN DOWNLOAD DA UN ALTRO NODO*****************************
                else if (response != BandwidthInfo.NOT_OWNED || response == BandwidthInfo.IN_DOWNLOAD) {
                    //il Nodo ha quel chunk
                    if (receiver.getDebug() >= 3) {
                        System.out.println("\tREFUSE - chunk owned or in download (" + response + "). #Chunks " + receiver.getSize());
                    }
                    //applicare tecniche di completamento: es il receiver propone il chunk nel caso in cui abbia il chunk che sender vuole push			
                    BandwidthTesterMessage imm = new BandwidthTesterMessage(chunktopush, node, BandwidthInfo.NO_CHUNK_OWNED);
                    long delay = this.send(node, im.getSender(), imm, pid);
                    if (receiver.getDebug() >= 4) {
                        System.out.println("\tNode " + node.getID() + " sends " + imm.getMessageID() + " to " + im.getSender().getID() + " MexRx " + (CommonState.getTime() + delay));
                    }
                } //************************** NODO NON HA IL CHUNK ED ACCETTA IL PUSH ****************************
                else if (response == BandwidthInfo.NOT_OWNED) {
                    if (receiver.getDebug() >= 3) {
                        System.out.println("\tNode " + node.getID() + " accepts chunk " + chunktopush + " from " + im.getSender().getID());
                    }
                    BandwidthTesterMessage imm = new BandwidthTesterMessage(chunktopush, node, BandwidthInfo.OK_PUSH);
                    long delay = this.send(node, im.getSender(), imm, pid);
                    receiver.addPassiveDw(node);
                    receiver.setInDown(chunktopush);
                    if (receiver.getDebug() >= 4) {
                        System.out.println("\tNode " + node.getID() + " sends OK_PUSH to " + im.getSender().getID() +
                                " for chunks m:" + chunktopush + " MexRx " + (CommonState.getTime() + delay) + " " + (receiver.getDebug() >= 6 ? receiver.getBwInfo(node) + " " + receiver.getConnections() + " " : " "));
                    }
                } else {
//                    if (receiver.getDebug() >= 0) {
                    System.err.println("::: ATTENTION - case not threated in PUSH " + CommonState.getTime());
//                    }
                    System.exit(1);
                }
                sender = receiver = null;
                return;
            }
            //************************************* N O D E   R I C E V E    O K    P E R   I L   P U S H ******************************************
            case BandwidthInfo.OK_PUSH: {	//Receiver ha accetta il PUSH di sender sul chunk
                sender = ((BandwidthTester) (node.getProtocol(pid)));
                receiver = ((BandwidthTester) (im.getSender().getProtocol(pid)));
                int chunktopush = im.getChunks()[0];
                long response = sender.getChunk(chunktopush);
                if (sender.getDebug() >= 1) {
                    System.out.println(CommonState.getTime() + "\tNode " + node.getID() + " PUSH CYCLE (" + sender.getPushAttempt() +
                            "/" + sender.getPushRetry() + ") " + ((sender.getDebug() >= 4) ? " #Chunks " + sender.getSize() + " " + sender.getBwInfo(node) : "") +
                            " rec-OK_PUSH from " + im.getSender().getID() + " for chunk " + chunktopush + "(" + response + ")");
                }
                //***********************************SENDER HA IL CHUNK RICHIESTO *************************************			
                if (response != BandwidthInfo.NOT_OWNED && response != BandwidthInfo.IN_DOWNLOAD) {
                    BandwidthAwareProtocol bap = (BandwidthAwareProtocol) node.getProtocol(sender.getBandwidth());
                    long eedelay = 10;
                    long result = bap.sendData(sender.getChunkSize(), node, im.getSender(), eedelay, sender.getBandwidth());
                    if (result == BandwidthMessage.NO_UP || result == BandwidthMessage.NO_DOWN || result == -1) {//|| receiver.getChunk(chunktopush)!= Message.NOT_OWNED) {
                        receiver.resetInDown(chunktopush);
                        if (sender.getDebug() >= 4 && result == BandwidthMessage.NO_UP) {
                            System.out.println("\tNode " + node.getID() + " has no more upload bandwidth for transmission with Node " + im.getSender().getID() + ", upload " + sender.getUpload(node));
                        } else if (sender.getDebug() >= 4 && result == BandwidthMessage.NO_DOWN) {
                            System.out.println("\tNode " + im.getSender().getID() + " has no more download bandwidth for receiving chunks from Node " + node.getID() + ", download " + receiver.getDownload(im.getSender()));
                        }
                        if (sender.getDebug() >= 4) {
                            System.out.print("\tSender Active up from " + sender.getActiveUp(node));
                        }
                        sender.remActiveUp(node);
                        if (receiver.getDebug() >= 4) {
                            System.out.println(" to " + sender.getActiveUp(node));
                        }
                        if (receiver.getDebug() >= 4) {
                            System.out.print("\tReceiver Passive down from " + receiver.getPassiveDw(im.getSender()));
                        }
                        receiver.remPassiveDw(im.getSender());
                        if (receiver.getDebug() >= 4) {
                            System.out.println(" to " + receiver.getPassiveDw(im.getSender()));
                        }
                        long delay = sender.getSwitchTime();
                        if (sender.getDebug() >= 4) {
                            System.out.println("\tNode " + node.getID() + " SWITCH to PUSHa (" + sender.getPushAttempt() + "/" + sender.getPushRetry() + ") at time " + CommonState.getTime() + " MexRX " + (CommonState.getTime() + delay));
                        }

                        this.send(node, node, new BandwidthTesterMessage(null, node, BandwidthInfo.SWITCH_PUSH), delay, pid);
                        sender.addFailPush();
                    } else {
                        long delay = eedelay;
                        this.send(node, im.getSender(), new BandwidthTesterMessage(chunktopush, node, BandwidthInfo.START_PUSH), pid);
                        if (sender.getDebug() >= 4) {
                            System.out.println("\tNode " + node.getID() + " sends START_PUSH m:" + chunktopush + " to " + im.getSender().getID() + " MexRx " + (CommonState.getTime() + delay));
                        }
                        delay += result;
                        if (sender.getDebug() >= 4) {
                            System.out.println("\tNode " + node.getID() + " sends FINISH_PUSH m:" + chunktopush + " to " + im.getSender().getID() + " MexRx " + (CommonState.getTime() + delay));
                        }
                        this.send(node, im.getSender(), new BandwidthTesterMessage(chunktopush, node, BandwidthInfo.FINISH_PUSH), delay, pid);
                        delay += sender.getSwitchTime();
                        if (sender.getDebug() >= 4) {
                            System.out.println("\tNode " + node.getID() + " will SWITCH to PUSHb (" + sender.getPushAttempt() + "/" + sender.getPushRetry() + ") al tempo " + (CommonState.getTime() + result + sender.getSwitchTime()));
                        }
                        this.send(node, node, new BandwidthTesterMessage(null, node, BandwidthInfo.SWITCH_PUSH), delay, pid);

                    }
                } else {
//                    if (sender.getDebug() >= 1) {
                    System.err.println("::: ATTENTION - case not threated in OK_PUSH " + CommonState.getTime() +
                            " Receiver " + im.getSender().getID() + " has proposed a chunks that the sender " + node.getID() + " does not own: chunk " + chunktopush + " ( " + sender.getChunk(chunktopush) + ")");
//                    }
                    System.exit(-10);
                }
                sender = receiver = null;
                return;
            }
            case BandwidthInfo.START_PUSH: {
                receiver = ((BandwidthTester) (node.getProtocol(pid)));
                long chunktopush = im.getChunks()[0];
                if (receiver.getDebug() >= 1) {
                    System.out.println(CommonState.getTime() + "\tNode " + node.getID() + " PUSH CYCLE (" + receiver.getPushAttempt() +
                            "/" + receiver.getPushRetry() + ") " + ((receiver.getDebug() >= 4) ? " #Chunks " + receiver.getSize() + " " + receiver.getBwInfo(node) : "") +
                            " recSTART_PUSH " + chunktopush + " from " + im.getSender().getID());
                }
                sender = receiver = null;
                return;
            }
            case BandwidthInfo.FINISH_PUSH://il receiver riceve il messaggio di fine PUSH
            {
                sender = ((BandwidthTester) (im.getSender().getProtocol(pid)));
                receiver = ((BandwidthTester) (node.getProtocol(pid)));
                int chunktopush = (int) im.getChunks()[0];
                if (receiver.getDebug() >= 2) {
                    System.out.println(CommonState.getTime() + "\tNode " + node.getID() + " recFINISH_PUSH " + im.getChunkids() + " from " + im.getSender().getID());
                }
                sender.remActiveUp(im.getSender());
                sender.addSuccessUpload();
                if (receiver.getSize()==0) {//e` la prima attivazione.
                    this.send(node, node, new BandwidthTesterMessage(null, node, BandwidthInfo.SWITCH_PUSH), receiver.getSwitchTime(), pid);
                    if (sender.getDebug() >= 4) {
                        System.out.println("\t>>Node " + node.getID() + " has just been ACTIVATED!!! SWITCH PUSH at time " + (CommonState.getTime() + receiver.getSwitchTime()));
                    }
                }
                if (sender.getSource() == im.getSender().getIndex()) {
                    if (sender.getDebug() >= 4) {
                        System.out.print("\t>>Source " + im.getSender().getID() + " add last src " + chunktopush);
                    }
                    sender.addLastsrc();
                    if (sender.getDebug() >= 4) {
                        System.out.println(" >> " + sender.getLast());
                    }
                }
                receiver.remPassiveDw(node);
                receiver.addChunk(chunktopush, BandwidthInfo.PUSH_CYCLE);
                if (receiver.getDebug() >= 6) {
                    System.out.println("\tSender " + im.getSender().getID() + " " + sender.getConnections() + "\n\t---  Receiver " + node.getID() + " " + receiver.getBwInfo(node) + " " + receiver.getConnections());
                }
                sender = receiver = null;
                return;
            }
            case BandwidthInfo.NO_CHUNK_OWNED: {                //Il Nodo possiede già il chunk che si vuole pushare
                sender = (BandwidthTester) (node.getProtocol(pid));
                long chunktopush = im.getChunks()[0];
                if (sender.getDebug() >= 3) {
                    System.out.print(CommonState.getTime() + "\tNode " + node.getID() + " " + sender.getBwInfo(node) + " " + sender.getSize() + " receives " + im.getMessageID() + " for chunk " + chunktopush + " from node " + im.getSender().getID());
                }
                sender.addFailPush();
                sender.remActiveUp(node);
                if (sender.getDebug() >= 4) {
                    System.out.println("...updating" + sender.getConnections());
                }
                long delay = sender.getSwitchTime();
                if (sender.getDebug() >= 4) {
                    System.out.println("\tNode " + node.getID() + " will SWITCH to PUSHc (" + sender.getPushAttempt() + "/" + sender.getPushRetry() + ") at time " + CommonState.getTime() + " MexRX " + (CommonState.getTime() + delay));
                }
                this.send(node, node, new BandwidthTesterMessage(null, node, BandwidthInfo.SWITCH_PUSH), delay, pid);
                sender = receiver = null;
                return;
            }
            case BandwidthInfo.NO_DOWNLOAD_BANDWIDTH_PUSH: {
                sender = ((BandwidthTester) (node.getProtocol(pid)));
                if (sender.getDebug() >= 3) {
                    System.out.println(CommonState.getTime() + "\tNode " + node.getID() + " receives a message from Node " + im.getSender().getID() + " that does not have more download bandwidth");
                }
                sender.addFailPush();
                sender.remActiveUp(node);
                long delay = sender.getSwitchTime();
                if (sender.getDebug() >= 4) {
                    System.out.println("\tNode " + node.getID() + " will SWITCH to PUSHd (" + sender.getPushAttempt() + "/" + sender.getPushRetry() + ") at time " + CommonState.getTime() + " MexRX " + (CommonState.getTime() + delay));
                }
                this.send(node, node, new BandwidthTesterMessage(null, node, BandwidthInfo.SWITCH_PUSH), delay, pid);
                sender = receiver = null;
                return;
            }
        }
    }
}
