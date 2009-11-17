package bandwidth.core;

import peersim.core.*;
import peersim.cdsim.*;
import peersim.edsim.*;

/**
 * Core protocol for bandwidth management system. <p>
 * This class implements a priority sharing bandwidth mechanism, 
 * which leads to give as much resource as possible to first 
 * transmission, then to the second and so on.<p>
 * The protocol provides a method which computes
 * the time needed to perform the transfer or an error code
 * if either the upload or download bandwidth is not available.
 *
 *
 * @author Alessandro Russo
 * @version $Revision: 0.02$
 */
public class BandwidthAwareProtocol extends BandwidthAwareTransport implements CDProtocol, EDProtocol {

    /**
     * Main constructor
     * @param prefix String prefix in the config file.
     */
    public BandwidthAwareProtocol(String prefix) {
        super(prefix);
    }

    /**
     * Method used for cycle based simulations.
     * @param node Node invoked
     * @param pid Protocol identifier
     */
    public void nextCycle(Node node, int pid) {
    }

    /**
     * Method invoked to perform downlink update.<p>
     * This method returns the bandwidth used for a past connection
     * or reserve the bandiwdth for a transfer computed in the past.
     * @param node Node that will update its upload.
     * @param pid Protocol identifier
     * @param bm BandwidthMessage entity which cointains the message to execute.
     */
    public void exUpdateDownload(Node node, int pid, BandwidthMessage bm) {
        BandwidthAwareProtocol sender, receiver;
        sender = (BandwidthAwareProtocol) bm.sender.getProtocol(pid);
        receiver = (BandwidthAwareProtocol) bm.receiver.getProtocol(pid);
        if (sender.getDebug() >= 6) {
            System.out.println("\n\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
        if (bm.getBandwidth() < 0) {
            if (sender.getDebug() >= 6) {
                System.out.println("\t>>>>> TIME " + CommonState.getTime() + " >>>>>>>>>   UPDATING DOWNLOAD (REMOVING) > RECEIVER " + bm.receiver.getID() + " Download " + receiver.getDownload() + " (" + receiver.getDownloadBUF() + ")");
            }
            BandwidthConnectionElement cet = new BandwidthConnectionElement(bm.sender, bm.receiver, bm.getBandwidth(), bm.getStart(), CommonState.getTime(), -1);
            cet = receiver.getDownloadConnections().getRecordE(bm.sender, bm.receiver, bm.getStart(), Math.abs(bm.getBandwidth()));
            if (receiver.getDebug() >= 6) {
                System.out.println("\tMessage " + bm.toString() + "\n\tElement " + cet);
            }
            if (cet != null) {
                cet.setCheck();
            }
            long newDw = receiver.getDownload() + bm.getBandwidth();
            receiver.setDownload(newDw);
            if (sender.getDebug() >= 6) {
                System.out.println("\t>>>>> TIME " + CommonState.getTime() + " >>>>>>>>>   UPDATING DONE < RECEIVER " + bm.receiver.getID() + " Download " + receiver.getDownload() + " (" + receiver.getDownloadBUF() + ")");
            }
        } else {
            if (sender.getDebug() >= 6) {
                System.out.println("\t>>>>> TIME " + CommonState.getTime() + " >>>>>>>>>   UPDATING DOWNLOAD (ADDING) > RECEIVER " + bm.receiver.getID() + " Download " + receiver.getDownload() + " (" + receiver.getDownloadBUF() + ")");
            }
            BandwidthConnectionElement cet = new BandwidthConnectionElement(bm.sender, bm.receiver, bm.getBandwidth(), bm.getStart(), CommonState.getTime(), -1);
            if (sender.getDebug() >= 6) {
                System.out.println("\tTry to remove connection " + cet);
            }
            if (receiver.getDownloadConnections().getSize() == 0) {
                if (sender.getDebug() >= 6) {
                    System.out.println("\t\tThe node does not have any active connections in download " + receiver.getDownloadConnections().getAll());
                }
                return;
            }
            if (receiver.getDebug() > 6) {
                for (int i = 0; i < receiver.getDownloadConnections().getSize(); i++) {
                    System.out.println("\t" + i + " - " + receiver.getDownloadConnections().getElement(i));
                }
                System.out.println("\t\\ ------------------------------------------------------------------------------------------------ /");
            }
            BandwidthConnectionElement det = receiver.getDownloadConnections().remConnection(cet);
            if (det != null && cet.equals(det)) {
                if (sender.getDebug() >= 6) {
                    System.out.println("\tRemoving download connections: " + det);
                }
                long newDw = receiver.getDownload() + bm.getBandwidth();
                receiver.setDownload(newDw, det);
            }
            if (det == null && sender.getDebug() >= 6) {
                System.out.println("\t\tNo connections have been removed: " + cet);
            }
            det = receiver.getDownloadConnections().getRecordT(det.getSender(), det.getReceiver(), det.getTxId());
            if (receiver.getDebug() > 6) {
                for (int i = 0; i < receiver.getDownloadConnections().getSize(); i++) {
                    System.out.println("\t" + i + " - " + receiver.getDownloadConnections().getElement(i));
                }
                System.out.println("\t\\ ------------------------------------------------------------------------------------------------ /");
            }
            if (sender.getDebug() >= 6) {
                System.out.println("\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  UPDATING DONE > RECEIVER " + bm.receiver.getID() + " Download " + receiver.getDownload() + " (" + receiver.getDownloadBUF() + ")");
            }
        }
        if (receiver.getDownload() < 0 || receiver.getDownload() > receiver.getDownloadMax()) {
            System.err.println(CommonState.getTime() + " error download: ID " + bm.getReceiver().getID() + ", Download " + receiver.getDownload() + " (" + receiver.getDownloadMax() + ")");
        }
        if (sender.getDebug() >= 6) {
            System.out.println("\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
        return;

    }

    /**
     * Method invoked to perform uplink update.<p>
     * This method returns the bandwidth used for a past connection
     * or reserve the bandiwdth for a transfer computed in the past.
     * @param node Node that will update its upload.
     * @param pid Protocol identifier
     * @param bm BandwidthMessage entity which cointains the message to execute.
     */
    public void exUpdateUpload(Node node, int pid, BandwidthMessage bm) {
        BandwidthAwareProtocol sender, receiver;
        sender = (BandwidthAwareProtocol) bm.sender.getProtocol(pid);
        receiver = (BandwidthAwareProtocol) bm.receiver.getProtocol(pid);
        if (sender.getDebug() >= 6) {
            System.out.println("\n\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
        if (bm.getBandwidth() < 0) {
            if (sender.getDebug() >= 6) {
                System.out.println("\t>>>>> TIME " + CommonState.getTime() + " >>>>>>>>>  UPDATING UPLOAD (REMOVING) > SENDER " + bm.sender.getID() + " Upload " + sender.getUpload() + " (" + sender.getUploadBUF() + ")");
            }
            BandwidthConnectionElement cet = new BandwidthConnectionElement(bm.sender, bm.receiver, bm.getBandwidth(), bm.getStart(), CommonState.getTime(), -1);
            cet = receiver.getUploadConnections().getRecordE(bm.sender, bm.receiver, bm.getStart(), Math.abs(bm.getBandwidth()));
            if (receiver.getDebug() >= 6) {
                System.out.println("\tMessage " + bm.toString() + "\n\tElement " + cet);
            }
            if (cet != null) {
                cet.setCheck();
            }
            long newUp = sender.getUpload() + bm.getBandwidth();
            sender.setUpload(newUp);
            if (sender.getDebug() >= 6) {
                System.out.println("\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< UPDATING DONE < SENDER " + bm.getSender().getID() + " Upload " + sender.getUpload() + " (" + sender.getUploadBUF() + ")");
            }
        } else {
            if (sender.getDebug() >= 6) {
                System.out.println("\t>>>>> TIME " + CommonState.getTime() + " >>>>>>>>>   UPDATING UPLOAD (ADDING) > SENDER " + bm.sender.getID() + " Upload " + sender.getUpload() + " (" + sender.getUploadBUF() + ")");
            }
            if (sender.getUploadConnections().getSize() == 0) {
                if (sender.getDebug() >= 6) {
                    System.out.println("\t\tThe node does not have any active connections in download " + sender.getUploadConnections().getAll());
                }
                return;
            }
            BandwidthConnectionElement cet = new BandwidthConnectionElement(bm.sender, bm.receiver, bm.getBandwidth(), bm.getStart(), CommonState.getTime(), -1);
            if (sender.getDebug() >= 6) {
                System.out.println("\t>>>>Try to remove connection " + cet);
            }
            if (sender.getDebug() >= 6) {
                for (int i = 0; i < sender.getUploadConnections().getSize(); i++) {
                    System.out.println("\t" + sender.getUploadConnections().getElement(i));
                }
                System.out.println("\t\\ ------------------------------------------------------------------------------------------------ /");
            }

            BandwidthConnectionElement det = sender.getUploadConnections().remConnection(cet);
            if (det != null && cet.equals(det)) {
                if (sender.getDebug() >= 6) {
                    System.out.println("\tRemoving upload connections: " + det.toString());
                }
                long newUp = sender.getUpload() + bm.getBandwidth();
                sender.setUpload(newUp, det);

            } else if (sender.getDebug() >= 6) {
                System.out.println("\t\tNo connections have been removed: " + cet);
            }
            if (sender.getDebug() >= 6) {
                System.out.println("\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  UPDATING DONE < SENDER " + bm.getSender().getID() + " Upload " + sender.getUpload() + " (" + sender.getUploadBUF() + ")");
            }
        }
        if (sender.getUpload() < 0 || sender.getUpload() > sender.getUploadMax()) {
            System.err.println(CommonState.getTime() + " error upload: ID " + bm.getSender().getID() + ", upload " + sender.getUpload() + " (" + sender.getUploadMax() + ")");
        }
        if (sender.getDebug() >= 6) {
            System.out.println("\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
        return;
    }

    /**
     * Schedule the events, delivering the object cointaing the event to the correct protocol instance PID in the node NODE.
     * @param node Node to invoce.
     * @param pid Protocol which message refers to.
     * @param event event threat.
     */
    public void processEvent(Node node, int pid, Object event) {
        BandwidthMessage bm = (BandwidthMessage) event;
        if (bm.getSender() == null) {
            System.err.println("--- Time " + CommonState.getTime() + ": Node " + node.getID() + " receives a message with NULL sender; It will be discarded.");
            return;
        }
        switch (bm.getMessage()) {
            case BandwidthMessage.UPD_UP: {
                this.exUpdateUpload(node, pid, bm);
                break;
            }
            case BandwidthMessage.UPD_DOWN: {
                this.exUpdateDownload(node, pid, bm);
                break;
            }
        }
    }
}
