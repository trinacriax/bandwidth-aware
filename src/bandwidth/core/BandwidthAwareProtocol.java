package bandwidth.core;

import peersim.core.*;
import peersim.cdsim.*;
import peersim.edsim.*;

/**
 * Core protocol for bandwidth management system. 
 * This class implements a priority sharing bandwidth mechanism, 
 * which leads to give as much resource as possible to first transmission, 
 * then to the second and so on.
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
                if (bm.getBandwidth() < 0) {
                    if (sender.getDebug() >= 6) {
                        System.out.println("\t>>>>>>>>> " + CommonState.getTime() + " >>>>>>>>>  UPDATING UPLOAD (REM) > SENDER " + bm.sender.getID() + " Upload " + sender.getUpload() + " (" + sender.getUploadBUF() + ")");
                    }
                    BandwidthConnectionElement cet = new BandwidthConnectionElement(bm.sender, bm.receiver, bm.getBandwidth(), bm.getStart(), CommonState.getTime(), -1);
                    cet = receiver.getUploadConnections().getRecordE(bm.sender,bm.receiver,bm.getStart(),Math.abs(bm.getBandwidth()));
                    if (receiver.getDebug() >= 6) {
                        System.out.println("\tMessage " + bm.toString()+"\n\tElement "+cet);
                    }
                    if(cet!=null)
                        cet.setCheck();
                    long newUp = sender.getUpload() + bm.getBandwidth();
                    if (sender.getDebug() >= 6) {
                        System.out.println("\tNew up is " + newUp);
                    }
                    sender.setUpload(newUp);
                    if (sender.getDebug() >= 6) {
                        System.out.println("\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< UPDATING DONE < SENDER " + bm.getSender().getID() + " Upload " + sender.getUpload() + " (" + sender.getUploadBUF() + ")");
                    }
                } else {
//                        sender.setUpdate(false);                        
                    if (sender.getDebug() >= 6) {
                        System.out.println("\t>>>>>>>>> " + CommonState.getTime() + " >>>>>>>>>   UPDATING UPLOAD (ADD) > SENDER " + bm.sender.getID() + " Upload " + sender.getUpload() + " (" + sender.getUploadBUF() + ")");
                    }
                    if (sender.getUploadConnections().getSize() == 0) {
                        if (sender.getDebug() >= 6) {
                            System.out.println("\tIl nodo non ha connessioni attive in upload " + sender.getUploadConnections().getAll());
                        }
                        return;
                    }
                    BandwidthConnectionElement cet = new BandwidthConnectionElement(bm.sender, bm.receiver, bm.getBandwidth(), bm.getStart(), CommonState.getTime(), -1);
                    if (sender.getDebug() >= 6) {
                        System.out.println("\t>>>>Try to remove connection " + cet);
                    }
                    if (sender.getDebug() >= 6) {
//                            System.out.println("\t/ SENDER CONNECTION LIST --------------------------------");
                        for (int i = 0; i < sender.getUploadConnections().getSize(); i++) {
                            System.out.println("\t" + sender.getUploadConnections().getElement(i));
                        }
                        System.out.println("\t\\ ------------------------------------------------------------------------------------------------ /");
                    }

                    BandwidthConnectionElement det = sender.getUploadConnections().remConnection(cet);
                    if (det != null && cet.equals(det)) {
                        if (sender.getDebug() >= 6) {
                            System.out.println("\tRimossa connessione in Upload " + det.toString());
                        }
                        long newUp = sender.getUpload() + bm.getBandwidth();
                        sender.setUpload(newUp, det);

                    } else if (sender.getDebug() >= 6) {
                        System.out.println("\t\tNon  stata rimossa alcuna connessione " + cet);
                    }

//                    det = sender.getUploadConnections().getRecordT(det.getSender(), det.getReceiver(), det.getTxId());
//                    if (det == null) {
//                        if (sender.getDebug() >= 6) {
//                            System.out.println("\t\tNo more connection between " + bm.getSender().getIndex() + " and " + bm.getReceiver().getIndex() + ".");
//                        }
//                        sender.remActiveUp();
//                    }
                    if (sender.getDebug() >= 6) {
                        System.out.println("\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  UPDATING DONE < SENDER " + bm.getSender().getID() + " Upload " + sender.getUpload() + " (" + sender.getUploadBUF() + ")");
                    }
                }
                if (sender.getUpload() < 0 || sender.getUpload() > sender.getUploadMax()) {
                    System.err.println(CommonState.getTime() + " error upload: " + bm.getSender().getID() + " up " + sender.getUpload() + "/" + sender.getUploadMax());
                }
                if (sender.getDebug() >= 6) {
                    System.out.println();
                }
                return;
            }

            case BandwidthMessage.UPD_DOWN: {
                if (bm.getBandwidth() < 0) {
//                    receiver.setUpdate(true);
                    if (sender.getDebug() >= 6) {
                        System.out.println("\t>>>>>>>>> " + CommonState.getTime() + " >>>>>>>>>   UPDATING DOWNLOAD (REM) > RECEIVER " + bm.receiver.getID() + " Download " + receiver.getDownload() + " (" + receiver.getDownloadBUF() + ")");
                    }
                    BandwidthConnectionElement cet = new BandwidthConnectionElement(bm.sender, bm.receiver, bm.getBandwidth(), bm.getStart(), CommonState.getTime(), -1);
                    cet = receiver.getDownloadConnections().getRecordE(bm.sender,bm.receiver,bm.getStart(),Math.abs(bm.getBandwidth()));
                    if (receiver.getDebug() >= 6) {
                        System.out.println("\tMessage " + bm.toString()+"\n\tElement "+cet);
                    }
                    if(cet!=null)
                        cet.setCheck();
                    long newDw = receiver.getDownload() + bm.getBandwidth();
                    receiver.setDownload(newDw);
                    if (sender.getDebug() >= 6) {
                        System.out.println("\t>>>>>>>>> " + CommonState.getTime() + " >>>>>>>>>   UPDATING DONE < RECEIVER " + bm.receiver.getID() + " Download " + receiver.getDownload() + " (" + receiver.getDownloadBUF() + ")");
                    }
                } else {
//                    receiver.setUpdate(false);

                    if (sender.getDebug() >= 6) {
                        System.out.println("\t>>>>>>>>> " + CommonState.getTime() + " >>>>>>>>>   UPDATING DOWNLOAD (ADD) > RECEIVER " + bm.receiver.getID() + " Download " + receiver.getDownload() + " (" + receiver.getDownloadBUF() + ")");
                    }
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
                    if (receiver.getDebug() > 6) {
//                        System.out.println("\t/ RECEIVER CONNECTION LIST  --------------------------------");
                        for (int i = 0; i < receiver.getDownloadConnections().getSize(); i++) {
                            System.out.println("\t"+i+" - " + receiver.getDownloadConnections().getElement(i));
                        }
                        System.out.println("\t\\ ------------------------------------------------------------------------------------------------ /");
                    }
                    BandwidthConnectionElement det = receiver.getDownloadConnections().remConnection(cet);
                    if (det != null && cet.equals(det)) {
                        if (sender.getDebug() >= 6) {
                            System.out.println("\tRimossa connessione in Download " + det);
                        }
                        long newDw = receiver.getDownload() + bm.getBandwidth();
                        receiver.setDownload(newDw,det);
                    } else if (sender.getDebug() >= 6) {
                        System.out.println("\t\tNon e stata rimossa alcuna connessione " + cet);
                    }
                    det = receiver.getDownloadConnections().getRecordT(det.getSender(), det.getReceiver(), det.getTxId());
//                    if (det == null) {
//                        if (sender.getDebug() >= 6) {
//                            System.out.println("\t\tNo more connection between " + bm.getSender().getIndex() + " and " + bm.getReceiver().getIndex() + ".");
//                        }
//                        receiver.rem
//                    }
                                        if (receiver.getDebug() > 6) {
//                        System.out.println("\t/ RECEIVER CONNECTION LIST  --------------------------------");
                        for (int i = 0; i < receiver.getDownloadConnections().getSize(); i++) {
                            System.out.println("\t"+i+" - " + receiver.getDownloadConnections().getElement(i));
                        }
                        System.out.println("\t\\ ------------------------------------------------------------------------------------------------ /");
                    }
                    if (sender.getDebug() >= 6) {
                        System.out.println("\t<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  UPDATING DONE > RECEIVER " + bm.receiver.getID() + " Download " + receiver.getDownload() + " (" + receiver.getDownloadBUF() + ")");
                    }
                }
                if (receiver.getDownload() < 0 || receiver.getDownload() > receiver.getDownloadMax()) {
                    System.err.println(CommonState.getTime() + " error download: " + bm.getReceiver().getID() + " down " + receiver.getDownload() + "/" + receiver.getDownloadMax());
                }
                if (sender.getDebug() >= 6) {
                    System.out.println();
                }
                return;
            }
        }
    }
}