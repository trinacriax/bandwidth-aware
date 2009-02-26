/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bandwidth;

import peersim.config.*;
import peersim.core.*;
import peersim.cdsim.*;
import peersim.edsim.*;

/**
 *
 * @author ax
 */
public class BandwidthAwareProtocol extends BandwidthAwareTransport implements CDProtocol, EDProtocol {

    public BandwidthAwareProtocol(String prefix) {
        super(prefix);
    }

    public void nextCycle(Node node, int pid) {
    }

    public void processEvent(Node node, int pid, Object event) {
        BandwidthMessage bm = (BandwidthMessage) event;
        BandwidthAwareProtocol sender, receiver;
        if (bm.getSender() == null) {
            System.err.println("--- Time " + CommonState.getTime() + " Messaggio al Nodo " + node.getID() + " con sender NULL non verrà processato.");
            return;
        }
        sender = (BandwidthAwareProtocol) bm.sender.getProtocol(pid);
        receiver = (BandwidthAwareProtocol) bm.receiver.getProtocol(pid);
        if (sender.getDebug() >= 1) {
                System.out.println(CommonState.getTime()+" BANDWIDTH  MANAGEMENT  MECHANISM "+bm.toString());
        }
        switch (bm.getMessage()) {
            case BandwidthMessage.UPD_UP: {
                if (sender.getUploadConnections().getSize() == 0) {
                    if (sender.getDebug() >= 1) {
                        System.out.println("\tIl nodo non ha connessioni attive in upload " + sender.getUploadConnections().getAll());
                    }
                    return;
                }
                if (bm.getBandwidth() < 0) {
                    if (sender.getDebug() >= 1) {
                        System.out.println("\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> AGGIORNAMENTO TABELLA BANDE Sender " + bm.sender.getID() + " Upload " + sender.getUpload());
                    }
                    long newUp = sender.getUpload() + bm.getBandwidth();
                    sender.setUpload(newUp);
                     if (sender.getDebug() >= 1) {
                        System.out.println("\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< FINE AGGIORNAMENTO TABELLA BANDE Sender " + bm.getSender().getID() + " Upload " + sender.getUpload());
                    }
                     if (sender.getDebug() >= 1) {
                        System.out.println("\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> AGGIORNAMENTO TABELLA BANDE Receiver " + bm.receiver.getID() + " Download " + receiver.getDownload());
                    }
                    long newDw = receiver.getDownload() + bm.getBandwidth();
                    receiver.setDownload(newDw);
                       if (sender.getDebug() >= 1) {
                        System.out.println("\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> AGGIORNAMENTO TABELLA BANDE Receiver " + bm.receiver.getID() + " Download " + receiver.getDownload());
                    }
                } else {
                    BandwidthConnectionElement cet = new BandwidthConnectionElement(bm.sender.getID(), bm.receiver.getID(), bm.getBandwidth(), bm.getStart(), CommonState.getTime(), -1);
                    if (sender.getDebug() >= 2) {
                        System.out.println("\tTry to remove connection " + cet);
                    }
                    if (sender.getDebug() >= 1) {
                        System.out.println("\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> AGGIORNAMENTO TABELLA BANDE Sender " + bm.sender.getID() + " Upload " + sender.getUpload());
                    }
                    if (sender.getDebug() > 4) {
                        System.out.println("SENDER CONNECTION LIST \\\\\\\\\\\\\\\\\\");
                        for (int i = 0; i < sender.getUploadConnections().getSize(); i++) {
                            System.out.println(sender.getUploadConnections().getElement(i));
                        }
                        System.out.println("/////////////////////////////////");
                    }

                    BandwidthConnectionElement det = sender.getUploadConnections().remConnection(cet);
                    if (det != null && cet.equals(det)) {
                        if (sender.getDebug() >= 2) {
                            System.out.println("\t\tRimossa connessione " + det);
                        }
                        long newUp = sender.getUpload() + bm.getBandwidth();
                        sender.setUpload(newUp);
                    } else if (sender.getDebug() >= 1) {
                        System.out.println("\t\tNon  stata rimossa alcuna connessione " + cet);
                    }
                    if (sender.getDebug() >= 1) {
                        System.out.println("\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< FINE AGGIORNAMENTO TABELLA BANDE Sender " + bm.getSender().getID() + " Upload " + sender.getUpload());
                    }

                    if (receiver.getDownloadConnections().getSize() == 0) {
                        if (sender.getDebug() >= 6) {
                            System.out.println("\t\tIl nodo non ha connessioni attive in download " + receiver.getDownloadConnections().getAll());
                        }
                        return;
                    }
                    if (sender.getDebug() >= 1) {
                        System.out.println("\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> AGGIORNAMENTO TABELLA BANDE Receiver " + bm.receiver.getID() + " Download " + receiver.getDownload());
                    }
//                    cet = new BandwidthConnectionElement(bm.sender.getID(), bm.receiver.getID(), Math.abs(bm.getBandwidth()), bm.getStart(), CommonState.getTime(), -1);
                    if (receiver.getDebug() > 4) {
                        System.out.println("RECEIVER CONNECTION LIST \\\\\\\\\\\\\\\\\\ ");
                        for (int i = 0; i < receiver.getDownloadConnections().getSize(); i++) {
                            System.out.println(receiver.getDownloadConnections().getElement(i));
                        }
                        System.out.println("/////////////////////////////////");
                    }

                    det = receiver.getDownloadConnections().remConnection(cet);
                    if (det != null && cet.equals(det)) {
                        if (sender.getDebug() >= 2) {
                            System.out.println("\t\tRimossa connessione " + det);
                        }
                        long newDw = receiver.getDownload() + bm.getBandwidth();
                        receiver.setDownload(newDw);
                    } else if (sender.getDebug() >= 1) {
                        System.out.println("\t\tNon  stata rimossa alcuna connessione " + cet);
                    }
                    if (sender.getDebug() >= 1) {
                        System.out.println("\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< FINE AGGIORNAMENTO TABELLA BANDE Receiver " + bm.receiver.getID() + " Download " + receiver.getDownload());
                    }
                    return;
                }
            }
        }

        if (sender.getDebug() >= 1) {
            System.out.println("Bandwidth Management Protocol Ends.....");
        }
    }
}

