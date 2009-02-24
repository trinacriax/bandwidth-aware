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
        switch (bm.getMessage()) {
            case BandwidthMessage.UPD_UP: {
                if (sender.getUploadConnections().getSize() == 0) {
                    if (sender.getDebug() >= 6) {
                        System.out.println("\t\tIl nodo non ha connessioni attive in upload " + sender.getUploadConnections().getAll());
                    }
                    return;
                }
                if (sender.getDebug() >= 5) {
                    System.out.println("\t\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> AGGIORNAMENTO TABELLA BANDE Sender " + bm.sender.getID());
                }
                BandwidthConnectionElement cet = new BandwidthConnectionElement(bm.sender.getID(), bm.receiver.getID(), Math.abs(bm.getBandwidth()), CommonState.getTime());
                BandwidthConnectionElement det = sender.getUploadConnections().remConnection(cet);
                if (det != null && cet.equals(det)) {
                    if (sender.getDebug() >= 5) {
                        System.out.println("\t\tRimossa connessione " + det);
                    }
                    long newUp = sender.getUpload() + bm.getBandwidth();
                    sender.setUpload(newUp);
                } else if (sender.getDebug() >= 5) {
                    System.out.println("\t\tNon  stata rimossa alcuna connessione " + cet);
                }
                if (sender.getDebug() >= 5) {
                    System.out.println("\t\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< FINE AGGIORNAMENTO TABELLA BANDE Sender");
                }
                
                if (receiver.getDownloadConnections().getSize() == 0) {
                    if (sender.getDebug() >= 6) {
                        System.out.println("\t\tIl nodo non ha connessioni attive in download " + receiver.getDownloadConnections().getAll());
                    }
                    return;
                }
                if (sender.getDebug() >= 5) {
                    System.out.println("\t\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> AGGIORNAMENTO TABELLA BANDE Receiver " + bm.receiver.getID());
                }
                cet = new BandwidthConnectionElement(bm.sender.getID(), bm.receiver.getID(), Math.abs(bm.getBandwidth()), CommonState.getTime());
                det = receiver.getDownloadConnections().remConnection(cet);
                if (det != null && cet.equals(det)) {
                    if (sender.getDebug() >= 5) {
                        System.out.println("\t\tRimossa connessione " + det);
                    }
                    long newDw = receiver.getDownload()+ bm.getBandwidth();
                    receiver.setDownload(newDw);
                } else if (sender.getDebug() >= 5) {
                    System.out.println("\t\tNon  stata rimossa alcuna connessione " + cet);
                }
                if (sender.getDebug() >= 5) {
                    System.out.println("\t\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< FINE AGGIORNAMENTO TABELLA BANDE Receiver");
                }
                return;
            }
        }


    }
}

