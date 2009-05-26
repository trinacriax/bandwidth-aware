package bandwidth.core;

import bandwidth.test.BandwidthMessage;
import bandwidth.*;
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
        if (sender.getDebug() >= 4) {
            System.out.println(CommonState.getTime() + " BANDWIDTH  MANAGEMENT  MECHANISM " + bm.toString());
        }
        switch (bm.getMessage()) {
            case BandwidthMessage.UPD_UP:
                 {
                    if (sender.getUploadConnections().getSize() == 0) {
                        if (sender.getDebug() >= 6) {
                            System.out.println("\tIl nodo non ha connessioni attive in upload " + sender.getUploadConnections().getAll());
                        }
                        return;
                    }
                    if (bm.getBandwidth() < 0) {
                        sender.setUpdate(true);
                        if (sender.getDebug() >= 6) {
                            System.out.println("\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> AGGIORNAMENTO TABELLA BANDE Sender " + bm.sender.getID() + " Upload " + sender.getUpload());
                        }
                        long newUp = sender.getUpload() + bm.getBandwidth();
                        sender.setUpload(newUp);
                        if (sender.getDebug() >= 6) {
                            System.out.println("\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< FINE AGGIORNAMENTO TABELLA BANDE Sender " + bm.getSender().getID() + " Upload " + sender.getUpload());
                        }
                    } else {
                        sender.setUpdate(false);
                        BandwidthConnectionElement cet = new BandwidthConnectionElement(bm.sender, bm.receiver, bm.getBandwidth(), bm.getStart(), CommonState.getTime(), -1);
                        if (sender.getDebug() >= 6) {
                            System.out.println("\tTry to remove connection " + cet);
                        }
                        if (sender.getDebug() >= 6) {
                            System.out.println("\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> AGGIORNAMENTO TABELLA BANDE Sender " + bm.sender.getID() + " Upload " + sender.getUpload());
                        }
                        if (sender.getDebug() >= 6) {
//                            System.out.println("\t/ SENDER CONNECTION LIST --------------------------------");
                            for (int i = 0; i < sender.getUploadConnections().getSize(); i++) {
                                System.out.println("\t"+sender.getUploadConnections().getElement(i));
                            }
                            System.out.println("\t\\ ------------------------------------------------------------------------------------------------ /");
                        }

                        BandwidthConnectionElement det = sender.getUploadConnections().remConnection(cet);
                        if (det != null && cet.equals(det)) {
                            if (sender.getDebug() >= 6) {
                                System.out.println("\tRimossa connessione in Upload " + det.toString());
                            }
                            long newUp = sender.getUpload() + bm.getBandwidth();
                            sender.setUpload(newUp);
                        } else if (sender.getDebug() >= 6) {
                            System.out.println("\t\tNon  stata rimossa alcuna connessione " + cet);
                        }
                        if (sender.getDebug() >= 6) {
                            System.out.println("\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< FINE AGGIORNAMENTO TABELLA BANDE Sender " + bm.getSender().getID() + " Upload " + sender.getUpload());
                        }
                    }                
                if (sender.getUploadConnections().getRecord(bm.getSender(), bm.getReceiver()) == null && sender.getUpload() > sender.getUploadMax()) {
                    System.err.println(CommonState.getTime() + " error upload: " + bm.getSender().getID() + " up " + sender.getUpload() + "/" + sender.getUploadMax());
                }
                    return;
            }

            case BandwidthMessage.UPD_DOWN: {
                if (bm.getBandwidth() < 0) {
                    receiver.setUpdate(true);
                        if (sender.getDebug() >= 6) {
                            System.out.println("\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> AGGIORNAMENTO TABELLA BANDE Receiver " + bm.receiver.getID() + " Download " + receiver.getDownload());
                        }
                        long newDw = receiver.getDownload() + bm.getBandwidth();
                        receiver.setDownload(newDw);
                        if (sender.getDebug() >= 6) {
                            System.out.println("\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> AGGIORNAMENTO TABELLA BANDE Receiver " + bm.receiver.getID() + " Download " + receiver.getDownload());
                        }
                    }else {
                    receiver.setUpdate(false);
                    BandwidthConnectionElement cet = new BandwidthConnectionElement(bm.sender, bm.receiver, bm.getBandwidth(), bm.getStart(), CommonState.getTime(), -1);
                    if (sender.getDebug() >= 6) {
                        System.out.println("\tTry to remove connection " + cet);
                    }
                    if (receiver.getDownloadConnections().getSize() == 0) {
                        if (sender.getDebug() >= 6) {
                            System.out.println("\t\tIl nodo non ha connessioni attive in download " + receiver.getDownloadConnections().getAll());
                        }
                        return;
                    }
                    if (sender.getDebug() >= 6) {
                        System.out.println("\t>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> AGGIORNAMENTO TABELLA BANDE Receiver " + bm.receiver.getID() + " Download " + receiver.getDownload());
                    }
                    if (receiver.getDebug() > 6) {
//                        System.out.println("\t/ RECEIVER CONNECTION LIST  --------------------------------");
                        for (int i = 0; i < receiver.getDownloadConnections().getSize(); i++) {
                            System.out.println("\t"+receiver.getDownloadConnections().getElement(i));
                        }
                        System.out.println("\t\\ ------------------------------------------------------------------------------------------------ /");
                    }
                    BandwidthConnectionElement det = receiver.getDownloadConnections().remConnection(cet);
                    if (det != null && cet.equals(det)) {
                        if (sender.getDebug() >= 6) {
                            System.out.println("\tRimossa connessione in Download " + det);
                        }
                        long newDw = receiver.getDownload() + bm.getBandwidth();
                        receiver.setDownload(newDw);
                    } else if (sender.getDebug() >= 6) {
                        System.out.println("\t\tNon e stata rimossa alcuna connessione " + cet);
                    }
                    if (sender.getDebug() >= 6) {
                        System.out.println("\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< FINE AGGIORNAMENTO TABELLA BANDE Receiver " + bm.receiver.getID() + " Download " + receiver.getDownload());
                    }
                }

                if (receiver.getDownloadConnections().getRecord(bm.getSender(), bm.getReceiver()) == null && receiver.getDownload() > receiver.getDownloadMax()) {
                    System.err.println(CommonState.getTime() + " error download: " + bm.getReceiver().getID() + " down " + receiver.getDownload() + "/" + receiver.getDownloadMax());
                }
                return;
            }
        }
    }
}