/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bandwidth;

import peersim.core.Protocol;
import peersim.core.Node;
import peersim.core.CommonState;
import peersim.edsim.EDSimulator;
import java.util.Vector;
import java.util.ArrayList;

/**
 *
 * @author ax
 */
public class BandwidthAwareTransport implements Protocol, BandwidthAwareSkeleton {

    private long upload;
    private long upload_min;
    private long upload_max;
    private long download;
    private long download_min;
    private long download_max;
    private int debug;
    private int active_upload;
    private int active_download;
    private int passive_upload;
    private int passive_download;
    private int active_up;
    private int active_dw;
    private int passive_up;
    private int passive_dw;
    private BandwidthConnectionList upload_connection_list;
    private BandwidthConnectionList download_connection_list;

    public BandwidthAwareTransport(String prefix) {
        super();
    }

    public Object clone() {
        BandwidthAwareTransport bat = null;
        try {
            bat = (BandwidthAwareTransport) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        bat.upload = new Long(0);
        bat.upload_min = new Long(0);
        bat.upload_max = new Long(0);

        bat.download = new Long(0);
        bat.download_min = new Long(0);
        bat.download_max = new Long(0);

        bat.debug = new Integer(0);

        bat.active_download = new Integer(0);
        bat.active_dw = new Integer(0);
        bat.active_upload = new Integer(0);
        bat.active_up = new Integer(0);
        bat.passive_download = new Integer(0);
        bat.passive_dw = new Integer(0);
        bat.passive_upload = new Integer(0);
        bat.passive_up = new Integer(0);


        bat.upload_connection_list = new BandwidthConnectionList();
        bat.download_connection_list = new BandwidthConnectionList();
        return bat;
    }

    public void reset() {
        this.upload = this.upload_max = this.upload_min = 0;
        this.download = this.download_max = this.download_min = 0;
        this.active_up = this.active_upload = this.passive_up = this.passive_upload;
        this.active_dw = this.active_download = this.passive_dw = this.passive_download;
        this.debug = 0;
        this.upload_connection_list = this.download_connection_list = null;
    }

    public void initialize() {
        if (this.upload_connection_list == this.download_connection_list && this.upload_connection_list == null) {
            this.upload_connection_list = new BandwidthConnectionList();
            this.download_connection_list = new BandwidthConnectionList();
        }
    }

    public int getDebug() {
        return this.debug;
    }

    public void setDebug(int debug) {
        this.debug = debug;
    }

    public void setUpload(long _upload) {
        this.upload = _upload;
    }

    public long getUpload() {
        return this.upload;
    }

    public void setUploadMin(long _upload_min) {
        this.upload_min = _upload_min;
    }

    public long getUploadMin() {
        return this.upload_min;
    }

    public void setUploadMax(long _upload_max) {
        this.upload_max = _upload_max;
    }

    public long getUploadMax() {
        return this.upload_max;
    }

    public void setDownload(long _download) {
        this.download = _download;
    }

    public long getDownload() {
        return this.download;
    }

    public void setDownloadMin(long _download_min) {
        this.download_min = _download_min;
    }

    public long getDownloadMin() {
        return this.download_min;
    }

    public void setDownloadMax(long _download_max) {
        this.download_max = _download_max;
    }

    public long getDownloadMax() {
        return this.download_max;
    }

    /**
     * Numero di trasmissioni in upload attualmente attive nello stato attivo
     * del nodo
     */
    public int getActiveUp() {
        return this.active_up;
    }

    /**
     * Aggiunge una trasmissione in upload a quelle attive stato attivo del nodo
     */
    public void addActiveUp() {
        this.active_up++;
    }

    /**
     * Rimuove una trasmissione in upload a quelle attive stato attivo del nodo
     */
    public void remActiveUp() {
        this.active_up--;
    }

    /**
     * Reset trasmissioni in upload attive
     */
    public void resetActiveUp() {
        this.active_up = 0;
    }

    /**
     * Numero di trasmissioni in download attualmente attive stato attivo del
     * nodo
     */
    public int getActiveDw() {
        return this.active_dw;
    }

    /**
     * Numero di trasmissioni in download attualmente attive stato attivo del
     * nodo
     */
    public void addActiveDw() {
        this.active_dw++;
    }

    /**
     * Rimuove una trasmissione in download a quelle attive stato attivo del
     * nodo
     */
    public void remActiveDw() {
        this.active_dw--;
    }

    /**
     * Reset trasmissione in download attive
     */
    public void resetActiveDw() {
        this.active_dw = 0;
    }

    /**
     * Numero di trasmissioni in upload attualmente attive stato passivo del
     * nodo
     */
    public int getPassiveUp() {
        return this.passive_up;
    }

    /**
     * Aggiunge una trasmissione in upload attualmente attive stato passivo del
     * nodo
     */
    public void addPassiveUp() {
        this.passive_up++;
    }

    /**
     * Rimuove una trasmissioni in upload attualmente attive stato passivo del
     * nodo
     */
    public void remPassiveUp() {
        this.passive_up--;
    }

    /**
     * Reset trasmissione in upload passive
     */
    public void resetPassiveUp() {
        this.passive_up = 0;
    }

    /**
     * Numero di trasmissioni in download attualmente attive stato passivo del
     * nodo
     */
    public int getPassiveDw() {
        return this.passive_dw;
    }

    /**
     * Aggiunge uno al numero di trasmissioni in download attualmente attive
     * stato passivo del nodo
     */
    public void addPassiveDw() {
        this.passive_dw++;
    }

    /**
     * Rimuove una trasmissioni in download attualmente attive stato passivo del
     * nodo
     */
    public void remPassiveDw() {
        this.passive_dw--;
    }

    /**
     * Reset trasmissione in download passive
     */
    public void resetPassiveDw() {
        this.passive_dw = 0;
    }

    /**
     * Imposta il numero massimo di trasmissioni in upload attive con stato del
     * nodo attivo
     */
    public void setActiveUpload(int active_upload) {
        this.active_upload = active_upload;
    }

    /**
     * Restituisce il numero massimo di trasmissioni in upload attive con stato
     * del nodo attivo
     */
    public int getActiveUpload() {
        return this.active_upload;
    }

    /**
     * Imposta il numero massimo di trasmissioni in download attive con stato
     * del nodo attivo
     */
    public void setActiveDownload(int active_download) {
        this.active_download = active_download;
    }

    /**
     * Restituisce il numero massimo di trasmissioni in download attive con
     * stato del nodo attivo
     */
    public int getActiveDownload() {
        return this.active_download;
    }

    /**
     * Imposta il numero massimo di trasmissioni in upload attive con stato del
     * nodo passivo
     */
    public void setPassiveUpload(int passive_upload) {
        this.passive_upload = passive_upload;
    }

    /**
     * Restituisce il numero massimo di trasmissioni in upload attive con stato
     * del nodo passivo
     */
    public int getPassiveUpload() {
        return this.passive_upload;
    }

    /**
     * Imposta il numero massimo di trasmissioni in download attive con stato
     * del nodo passivo
     */
    public void setPassiveDownload(int passive_download) {
        this.passive_download = passive_download;
    }

    /**
     * Restituisce il numero massimo di trasmissioni in download attive con
     * stato del nodo passivo
     */
    public int getPassiveDownload() {
        return this.passive_download;
    }

    public BandwidthConnectionList getUploadConnections() {
        return this.upload_connection_list;
    }

    public BandwidthConnectionList getDownloadConnections() {
        return this.download_connection_list;
    }

    public void fluctuationUpload() {
    }

    public void fluctuationDownload() {
    }

    public String toString() {
        String result = "\n\t>> Upload: " + this.upload + " [" + this.upload_min + ":" + this.upload_max + "]; Download " + this.download + " [" + this.download_min + ":" + this.download_max + "] ";
        result += "\n\t>>>ActUp " + this.getActiveUp() + "/" + this.getActiveUpload() + ", ActDw " + this.getActiveDw() + "/" + this.getActiveDownload() + ", ";
        result += "\n\t>>>PasUp " + this.getPassiveUp() + "/" + this.getPassiveUpload() + ", PasDw " + this.getPassiveDw() + "/" + this.getPassiveDownload();
        return result;

    }
    /**
     *  Metodo utilizzato per calcolare i tempi di trasmissione dei dati. Utilizza il priority sharing: la prima
     *  trasmissione che arriva prende tutto, la successiva prende il resto e cosi` via, con il limite sulla banda minima
     *  ossia una trasmissione non puo` andare con la velocita di banda minima (dovrebbe dipendere dalla mole di dati da trasmettere)
     *  Da rifinire il meccanismo di aggiornamento della banda quando mando un upload negativo e successivamente uno positivo x la stessa
     *  quantita` di banda (doppi download)
     */
    public long send(long data_bits, Node src, Node rcv, int pid) {
        long finish = -1;
        BandwidthAwareTransport sender = ((BandwidthAwareTransport) (src.getProtocol(pid)));
        BandwidthAwareTransport receiver = ((BandwidthAwareTransport) (rcv.getProtocol(pid)));
        long initupload = sender.getUpload();
        long initdownload = receiver.getDownload();
        if (sender.getUpload() < sender.getUploadMin()) {
            if (this.debug >= 4) {
                System.out.println("Attention!! Node " + src.getID() + " has upload bw less than up_min that is " + sender.getUploadMin());
            }
            return BandwidthMessage.NO_UP;
        } else if (receiver.getDownload() < receiver.getDownloadMin()) {
            if (this.debug >= 3) {
                System.out.println("Attention!! Node " + rcv.getID() + " has download bw less than down_min that is " + receiver.getDownloadMin());
            }
            return BandwidthMessage.NO_DOWN;
        }
        Vector elements = new Vector();
        long bandwidth, delay;
        bandwidth = delay = 0;
        int up_i, dw_i;
        up_i = dw_i = 0;
        ArrayList header = new ArrayList();
        BandwidthConnectionElement cupload, cdownload;
        long uploadBusy, uploadStartTime, uploadNextTime, uploadResidualTime;
        uploadBusy = uploadStartTime = uploadNextTime = uploadResidualTime = 0;
        long downloadBusy, downloadStartTime, downloadNextTime, downloadResidualTime;
        downloadBusy = downloadNextTime = downloadResidualTime = 0;
        long residuo = data_bits;
        long banda_up, banda_dw, old_up, old_dw;
        old_up = old_dw = 0;
        banda_up = sender.getUpload();
        if (sender.getUploadConnections().getSize() == 0) {
            uploadResidualTime = -1;
        } else {
            cupload = sender.upload_connection_list.getElement(up_i);
            uploadBusy = cupload.getBandwidth();
            uploadStartTime = cupload.getStart();
            uploadNextTime = cupload.getEnd();
            uploadResidualTime = uploadNextTime - CommonState.getTime();
        }
        banda_dw = receiver.getDownload();
        if (receiver.getDownloadConnections().getSize() == 0) {
            downloadResidualTime = -1;
        } else {
            cdownload = (receiver.getDownloadConnections().getElement(dw_i));
            downloadBusy = cdownload.getBandwidth();
            downloadStartTime = cdownload.getStart();
            downloadNextTime = cdownload.getEnd();
            downloadResidualTime = downloadNextTime - CommonState.getTime();
        }
        long baseTime = CommonState.getTime();
        long tableTime, mexTime;
        tableTime = mexTime = 0;
        int size = sender.upload_connection_list.getSize();
        long lastUpTime = 0;
        if (size > 0) {
            BandwidthConnectionElement cent = sender.upload_connection_list.getElement(size - 1);
            lastUpTime = cent.getEnd();
        }
        size = receiver.getDownloadConnections().getSize();
        long lastDwTime = 0;
        if (size > 0) {
            BandwidthConnectionElement cent = receiver.getDownloadConnections().getElement(size - 1);
            lastDwTime = cent.getEnd();
        }
        if (this.debug >= 5) {
            System.out.println("------------------------------------------------------ INIZIO GESTIONE BANDE PUSH ------------------------------------------------------ ");
            System.out.println("Tempo fine ultimo Upload: " + lastUpTime + " e Download " + lastDwTime);
        }
        if (this.debug >= 10) {
            if (sender.upload_connection_list.getSize() > 0) {
                System.out.println(">>> Sender ");
                BandwidthConnectionList cl = sender.getUploadConnections();
                for (int i = 0; i < cl.getSize(); i++) {
                    System.out.println(cl.getElement(i));
                }
                System.out.println("<<<< Sender ");
            }
            if (receiver.getDownloadConnections().getSize() > 0) {
                System.out.println(">>>> Receiver ");
                BandwidthConnectionList cl = receiver.getDownloadConnections();
                for (int i = 0; i < cl.getSize(); i++) {
                    System.out.println(cl.getElement(i));
                }
                System.out.println("<<<< Receiver ");
            }
        }
        boolean flag = true;
        while (residuo > 0) {
            bandwidth = Math.min(banda_up, banda_dw);
            if (this.debug >= 5) {
                System.out.println("...Time " + baseTime + ". Bits to transmit: " + residuo + ". BW Sender : " + banda_up + ", BW Receiver : " + banda_dw + " TX Banda " + bandwidth);
            }
            //Hanno entrambi la tabella vuota.
            if ((uploadResidualTime == -1 && downloadResidualTime == -1)) {
                mexTime = Math.round((residuo / ((double) bandwidth)) * 1000);
                if (this.debug >= 5) {
                    System.out.println("\tSender e Receiver hanno la tabella vuota, Trasmettono " + residuo + " con banda " + bandwidth + " durata trasmissione " + mexTime + " ms");
                }
                residuo = 0;
                tableTime = baseTime + mexTime;
                mexTime = tableTime - CommonState.getTime();
                finish = mexTime;
                long txtoken = CommonState.r.nextLong();
                BandwidthConnectionElement element = new BandwidthConnectionElement(src.getID(), rcv.getID(), bandwidth, baseTime, tableTime, txtoken);
                elements.add(element);
                if (this.debug >= 5) {
                    System.out.println("\t1 Aggiungo in upload e download l'elemento " + element.toString());
                }
                element = null;
                if (flag) {
                    if (this.debug >= 5) {
                        System.out.println("1 >>> Tolgo direttamente la banda " + bandwidth + " al tempo " + baseTime + " per restituirla al tempo " + tableTime);
                    }
                    old_up = sender.getUpload();
                    old_dw = receiver.getDownload();
                    sender.setUpload(sender.getUpload() - bandwidth);
                    receiver.setDownload(receiver.getDownload() - bandwidth);
                    flag = false;
                } else {
                    if (this.debug >= 5) {
                        System.out.println("1 >>> Tolgo la banda con messaggio " + bandwidth + " al tempo " + baseTime);
                    }
                    mexTime = baseTime - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Upload a " + src.getID() + " per " + (-1 * bandwidth) + " riceve al tempo " + baseTime + " MexTime " + mexTime);
                    }
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per " + (-1 * bandwidth) + " riceve al tempo " + baseTime + " MexTime " + mexTime);
                    }
                    header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, (-1 * bandwidth), baseTime), mexTime));
                }
                mexTime = tableTime - CommonState.getTime();
                if (this.debug >= 5) {
                    System.out.println("\tSpedisco Update Upload a " + src.getID() + " per restituire " + bandwidth + " al tempo " + tableTime + " MexTime " + mexTime);
                }
                if (this.debug >= 5) {
                    System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per restituire " + bandwidth + " al tempo " + tableTime + " MexTime " + mexTime);
                }
                header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, bandwidth, baseTime), mexTime));
            } // Il sender ha un upload in corso ed  ( il receive ha un download in corso che finisce dopo l'upload || non ha alcun download in corso)
            else if (uploadResidualTime != -1 && (uploadResidualTime < downloadResidualTime || downloadResidualTime == -1)) {
                if (this.debug >= 5) {
                    System.out.println("L'upload finisce prima del download :" + uploadResidualTime + " oppure il download è libero");
                }
                // controllo se la trasmissione può finire prima della fine dell'upload
                if (Math.round(((double) bandwidth) * uploadResidualTime / 1000.0D) >= residuo) {
                    long txTime = Math.round(residuo / ((double) bandwidth) * 1000);
                    tableTime = baseTime + txTime;
                    BandwidthConnectionElement element = new BandwidthConnectionElement(src.getID(), rcv.getID(), bandwidth, baseTime, tableTime, CommonState.r.nextLong());
                    elements.add(element);
                    if (this.debug >= 5) {
                        System.out.println("\t2 Aggiungo l'elemento " + element.toString());
                    }
                    element = null;

                    if (flag) {
                        old_up = sender.getUpload();
                        old_dw = receiver.getDownload();

                        sender.setUpload(sender.getUpload() - bandwidth);
                        receiver.setDownload(receiver.getDownload() - bandwidth);
                        flag = false;
                        if (this.debug >= 5) {
                            System.out.println("2 >>> Tolgo direttamente la banda " + bandwidth + " al tempo " + baseTime + " per restituirla al tempo " + tableTime);
                        }
                    } else {
                        mexTime = baseTime - CommonState.getTime();
                        if (this.debug >= 5) {
                            System.out.println("2 >>> Tolgo la banda con messaggio " + bandwidth + " al tempo " + baseTime + " per restituirla al tempo " + tableTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSpedisco Update Upload a " + src.getID() + " per " + (-1 * bandwidth) + " ricezione " + baseTime + " MexTime " + mexTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per " + (-1 * bandwidth) + " ricezione " + baseTime + " MexTime " + mexTime);
                        }
                        header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, (-1 * bandwidth), baseTime), mexTime));
                    }
                    mexTime = tableTime - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Uplaod a " + src.getID() + " per " + bandwidth + " al tempo " + tableTime + " MexTime " + (tableTime - CommonState.getTime()));
                    }
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per " + bandwidth + " al tempo " + tableTime + " MexTime " + (tableTime - CommonState.getTime()));
                    }
                    header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, bandwidth, baseTime), mexTime));
                    if (this.debug >= 5) {
                        System.out.println("La trasmissione di " + residuo + " finisce prima di " + uploadResidualTime + ", ci mette " + txTime + ", aggiungo elemento per il tempo " + tableTime);
                    }
                    finish = mexTime;
                    residuo = 0;
                } else {
                    //la trasmissione non finisce prima dell'upload pendente, quindi occorre spezzarla in almeno due parti
                    residuo = residuo - Math.round(bandwidth * ((double) uploadResidualTime) / 1000);
                    tableTime = uploadNextTime;

                    BandwidthConnectionElement element = new BandwidthConnectionElement(src.getID(), rcv.getID(), bandwidth, baseTime, tableTime, CommonState.r.nextLong());
                    elements.add(element);
                    if (this.debug >= 5) {
                        System.out.println("\t3 Aggiungo l'elemento " + element.toString());
                    }
                    element = null;

                    if (flag) {
                        old_up = sender.getUpload();
                        old_dw = receiver.getDownload();

                        sender.setUpload(sender.getUpload() - bandwidth);
                        receiver.setDownload(receiver.getDownload() - bandwidth);
                        flag = false;
                        if (this.debug >= 5) {
                            System.out.println("3 >>> Tolgo direttamente la banda " + bandwidth + " al tempo " + baseTime + " per restituirla al tempo " + tableTime);
                        }
                    } else {
                        mexTime = baseTime - CommonState.getTime();
                        if (this.debug >= 5) {
                            System.out.println("3 >>> Tolgo la banda con messaggio " + bandwidth + " dal tempo " + baseTime + " per restituirla al tempo " + tableTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSpedisco Update Upload a " + src.getID() + " per " + (-1 * bandwidth) + " ricezione " + baseTime + " MexTime " + mexTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per " + (-1 * bandwidth) + " ricezione " + baseTime + " MexTime " + mexTime);
                        }
                        header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, (-1 * bandwidth), baseTime), mexTime));
                    }
                    mexTime = tableTime - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Upload a " + src.getID() + " per " + bandwidth + " ricezione " + tableTime + " MexTime " + mexTime);
                    }
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per " + bandwidth + " ricezione " + tableTime + " MexTime " + (tableTime - CommonState.getTime()));
                    }
                    header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, bandwidth, baseTime), mexTime));
                    baseTime = uploadNextTime;
                    if (this.debug >= 5) {
                        System.out.println("Aggiorno la banda " + banda_up + " aggiungo " + uploadBusy);
                    }
                    banda_up += uploadBusy;
                    if (up_i < sender.upload_connection_list.getSize() - 1) {
                        do {
                            if (this.debug >= 5) {
                                System.out.println("Aggiorno download push");
                            }
                            up_i++;
                            cupload = sender.upload_connection_list.getElement(up_i);
                            uploadBusy = cupload.getBandwidth();
                            uploadStartTime = cupload.getStart();
                            uploadNextTime = cupload.getEnd();
                            uploadResidualTime = uploadNextTime - CommonState.getTime();
                            if (uploadNextTime == baseTime) {
                                if (this.debug >= 1) {
                                    System.out.println("Aggiorno la banda " + banda_up + " aggiungo " + uploadBusy);
                                }
                                banda_up += uploadBusy;
                            } else {
                                if (this.debug >= 5) {
                                    System.out.println("Aggiorno la banda " + banda_up + " tolgo " + uploadBusy);
                                }
                                banda_up -= uploadBusy;
                            }
                            if (this.debug >= 5) {
                                System.out.print(" = " + banda_up);
                            }
                            if (banda_up <= 0) {
                                if (this.debug >= 5) {
                                    System.out.println("Non va bene, quando aggiorno la banda questa va in negativo - upload, vuol dire che in futuro ho tutta la banda occupata");
                                }
                                if (this.debug >= 5) {
                                    System.out.println("::: Nodo " + src.getID() + " in futuro ha la banda in upload tutta occupata. Non puo` proseguire con l'upload dei  dati al Nodo " +
                                            rcv.getID());
                                }
                                if (this.debug >= 5) {
                                    System.out.println("--> Nodo " + src.getID() + " notifica a " + rcv.getID() + " che non ha più banda in Upload per fare il PUSH, MexTX " + CommonState.getTime() + " MexRX " + (CommonState.getTime() + delay));
                                }
                                if (this.debug >= 5) {
                                    System.out.println("--- Nodo " + src.getID() + " SWITCH to PUSHi2 al tempo " + (CommonState.getTime() + delay));
                                }
                                sender.setUpload(initupload);
                                receiver.setDownload(initdownload);
                                header.clear();
                                header = null;
                                elements = null;
                                return BandwidthMessage.NO_UP;
                            }
                        } while ((uploadNextTime != baseTime) && (up_i < sender.upload_connection_list.getSize() - 1));
                    } else {
                        uploadResidualTime = -1;
                    }
                    if (this.debug >= 5) {
                        System.out.println(" a " + banda_up);
                    }
                }
            } /*devo ricordare che lasttime è la base di tutti i tempi, devo sistemare tutto quello che ci sta sotto!*/ else if (downloadResidualTime != -1 && (downloadResidualTime <= uploadResidualTime || uploadResidualTime == -1)) {
                if (this.debug >= 5) {
                    System.out.println("\til download finisce prima dell'upload :" + downloadResidualTime + " oppure l'upload è libero");
                }
                //controllo se la trasmissione può finire prima del download pendente
                if (Math.round(((double) bandwidth) * downloadResidualTime / 1000) >= residuo) {//riusciamo a trasmettere tutto prima che si liberi banda
                    long txTime = Math.round(residuo / ((double) bandwidth) * 1000);
                    tableTime = baseTime + txTime;

                    BandwidthConnectionElement element = new BandwidthConnectionElement(src.getID(), rcv.getID(), bandwidth, baseTime, tableTime, CommonState.r.nextLong());
                    elements.add(element);
                    if (this.debug >= 5) {
                        System.out.println("\t4 Aggiungo l'elemento " + element.toString());
                    }
                    element = null;
                    if (flag) {
                        old_up = sender.getUpload();
                        old_dw = receiver.getDownload();
                        sender.setUpload(sender.getUpload() - bandwidth);
                        receiver.setDownload(receiver.getDownload() - bandwidth);
                        flag = false;
                        if (this.debug >= 5) {
                            System.out.println("4 >>> Tolgo direttamente la banda " + bandwidth + " al tempo " + baseTime + " per restituirla al tempo " + tableTime);
                        }
                    } else {
                        mexTime = baseTime - CommonState.getTime();
                        if (this.debug >= 5) {
                            System.out.println("4 >>> Tolgo la banda con messaggio " + bandwidth + " al tempo " + baseTime + " per restituirla al tempo " + tableTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSpedisco Update Upload a " + src.getID() + " per " + (-1 * bandwidth) + " ricezione " + baseTime + " MexTime " + mexTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per " + (-1 * bandwidth) + " ricezione " + baseTime + " MexTime " + mexTime);
                        }
                        header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, (-1 * bandwidth), baseTime), mexTime));
                    }
                    mexTime = tableTime - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Upload a " + src.getID() + " per " + bandwidth + " ricezione " + tableTime + " MexTime " + mexTime);
                    }
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per " + bandwidth + " al tempo " + tableTime);
                    }
                    header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, bandwidth, baseTime), mexTime));
                    if (this.debug >= 5) {
                        System.out.println("La trasmissione di " + residuo + " finisce prima di " + downloadResidualTime + ", ci mette " + txTime + ", aggiungo elemento per il tempo " + baseTime);
                    }
                    finish = mexTime;
                    residuo = 0;
                } else {
                    //la trasmissione non può finire prima del download pendente, occorre spezzarla in almeno due parti
                    residuo = residuo - Math.round(((double) bandwidth) * downloadResidualTime / 1000);
                    tableTime = downloadNextTime;
                    BandwidthConnectionElement element = new BandwidthConnectionElement(src.getID(), rcv.getID(), bandwidth, baseTime, tableTime, CommonState.r.nextLong());
                    elements.add(element);
                    if (this.debug >= 5) {
                        System.out.println("\t5 Aggiungo l'elemento " + element.toString());
                    }
                    element = null;
                    if (flag) {
                        old_up = sender.getUpload();
                        old_dw = receiver.getDownload();

                        sender.setUpload(sender.getUpload() - bandwidth);
                        receiver.setDownload(receiver.getDownload() - bandwidth);
                        flag = false;
                        if (this.debug >= 5) {
                            System.out.println("5 >>> Tolgo direttamente la banda " + bandwidth + " al tempo " + baseTime + " per restituirla al tempo " + tableTime);
                        }
                    } else {
                        mexTime = baseTime - CommonState.getTime();
                        if (this.debug >= 5) {
                            System.out.println("5 >>> Tolgo la banda con messaggio " + bandwidth + " al tempo " + baseTime + " per restituirla al tempo " + tableTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSpedisco Update Upload a " + src.getID() + " per " + (-1 * bandwidth) + " al tempo " + mexTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per " + (-1 * bandwidth) + " al tempo " + mexTime);
                        }
                        header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, (-1 * bandwidth), baseTime), mexTime));
                    }
                    mexTime = tableTime - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Upload a " + src.getID() + " per " + bandwidth + " al tempo " + tableTime);
                    }
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per " + bandwidth + " al tempo " + tableTime);
                    }
                    header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, bandwidth, baseTime), mexTime));
                    baseTime = downloadNextTime;
                    if (this.debug >= 5) {
                        System.out.println("Aggiorno download da " + banda_dw + " aggiungo " + downloadBusy);
                    }
                    banda_dw += downloadBusy;
                    if (dw_i < receiver.getDownloadConnections().getSize() - 1) {
                        do {
                            if (this.debug >= 5) {
                                System.out.println("Aggiorno download push");
                            }
                            dw_i++;
                            cdownload = receiver.getDownloadConnections().getElement(dw_i);
                            downloadBusy = cdownload.getBandwidth();
                            downloadStartTime = cdownload.getStart();
                            downloadNextTime = cdownload.getEnd();
                            downloadResidualTime = downloadNextTime - CommonState.getTime();
                            if (downloadNextTime == baseTime) {
                                if (this.debug >= 5) {
                                    System.out.println("Aggiorno download da " + banda_dw + " aggiungo " + downloadBusy);
                                }
                                banda_dw += downloadBusy;
                            } else {
                                if (this.debug >= 5) {
                                    System.out.println("Aggiorno download da " + banda_dw + " tolgo " + downloadBusy);
                                }
                                banda_dw -= downloadBusy;
                            }
                            if (this.debug >= 5) {
                                System.out.println(" = " + banda_dw);
                            }
                            if (banda_dw <= 0) {
                                if (this.debug >= 5) {
                                    System.out.println("Non va bene, quando aggiorno la banda questa va in negativo - download, vuol dire che in futuro ho tutta la banda occupata");
                                }
                                if (this.debug >= 5) {
                                    System.out.println("::: Receiver " + rcv.getID() + " in futuro non ha banda in download per accettare il push. Spedisce NO_DOWNLOAD_BW_PUSH al Sender " + src.getID() + " mex rivuto al tempo " +
                                            (CommonState.getTime() + delay));
                                }
                                sender.setUpload(initupload);
                                receiver.setDownload(initdownload);
                                return BandwidthMessage.NO_DOWN;
                            }
                        } while ((downloadNextTime != baseTime) && (dw_i < receiver.getDownloadConnections().getSize() - 1));
                    } else {
                        downloadResidualTime = -1;
                    }
                }
            }
        }
        if (!elements.isEmpty()) {
            if (this.debug >= 5) {
                System.out.println("Inserisco i seguenti elementi nelle tabelle di upload e download: ");
            }
            BandwidthConnectionElement cet = null;
            for (int j = 0; j < elements.size(); j++) {
                cet = ((BandwidthConnectionElement) elements.get(j));
                if (this.debug >= 5) {
                    System.out.println("\t" + cet + "; ");
                }
                sender.getUploadConnections().addConnection(cet);
                receiver.getDownloadConnections().addConnection(cet);
            }
        }
        elements.clear();
        Node rr = null;
        long mmtt;
        mmtt = 0;
        if (this.debug >= 10) {
            System.out.println("Aggiungo " + header.size() + " eventi in coda:");
        }
        while (!header.isEmpty()) {
            BwEvent ee = (BwEvent) header.remove(0);
            rr = ee.getrcv();
            mmtt = ee.getTime();
            if (mmtt < 0) {
                System.err.println("NEGATIVE TIME!! " + ee);
            }
            BandwidthMessage bme = (BandwidthMessage) ee.getBWM();
            if (this.debug >= 5) {
                System.out.println(ee.toString() + " >> " + bme.toString());
            }

            EDSimulator.add(mmtt, ee.getBWM(), rr, pid);
        }
        header.clear();
        header = null;
        if (this.debug >= 5) {
            System.out.println(">>>>>>>>>>>> Tabella Sender <<<<<<<<<<< ");
            System.out.println(sender.getUploadConnections().getAll());
            System.out.println(">>>>>>>>>>>> Tabella Receiver <<<<<<<<<<< ");
            System.out.println(receiver.getDownloadConnections().getAll());
        }
        if (this.debug >= 5) {
            System.out.println("Sender " + src.getID() + " >> " + sender.toString());
            System.out.println("Receiver " + rcv.getID() + " >> " + receiver.toString());
            System.out.println("---------------------------------------------------------- FINE GESTIONE BANDE ---------------------------------------------------------- ");
        }
        return finish;
    }
}

class BwEvent {

    private Node src;
    private Node dest;
    private Object bwm;
    private long time;

    public BwEvent(Node src, Node rcv, Object mex, long time) {
        this.src = src;
        this.dest = rcv;
        this.bwm = mex;
        this.time = time;
    }

    public Node getsrc() {
        return this.src;
    }

    public Node getrcv() {
        return this.dest;
    }

    public Object getBWM() {
        return this.bwm;
    }

    public long getTime() {
        return time;
    }

    public String toString() {
        return "Source " + src.getID() + " Receiver " + dest.getID() + " Mex " + bwm.getClass() + " Time " + time;
    }
}


