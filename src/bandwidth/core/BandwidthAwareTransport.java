/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bandwidth.core;

//import bandwidth.*;
import java.util.ArrayList;
import peersim.core.Protocol;
import peersim.core.Node;
import peersim.core.CommonState;
import peersim.edsim.EDSimulator;
import java.util.Vector;
//import java.util.ArrayList;

/**
 *
 * @author ax
 */
public class BandwidthAwareTransport implements Protocol, BandwidthAwareSkeleton {
    private double upload_sample;
    private int sample_counter_up;
    private double download_sample;
    private int sample_counter_dw;
    private long upload;
    private long upload_min;
    private long upload_max;
    private long upload_buf;
    private long download;
    private long download_min;
    private long download_max;
    private long download_buf;
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
    private ArrayList elements;

    public BandwidthAwareTransport(String prefix) {
        super();
    }

    public Object clone() {
        BandwidthAwareTransport bat = null;
        try {
            bat = (BandwidthAwareTransport) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        bat.upload_sample = new Double(0.0D);
        bat.sample_counter_up = new Integer(0);
        bat.sample_counter_dw = new Integer(0);
        bat.download_sample = new Double(0.0D);
        bat.upload = new Long(0);
        bat.upload_min = new Long(0);
        bat.upload_max = new Long(0);
        bat.upload_buf = new Long(0);

        bat.download = new Long(0);
        bat.download_min = new Long(0);
        bat.download_max = new Long(0);
        bat.download_buf = new Long(0);

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
        this.upload_sample = this.download_sample = 0.0D;
        this.sample_counter_up = this.sample_counter_dw = 0;
        this.upload = this.upload_max = this.upload_min = upload_buf = 0;
        this.download = this.download_max = this.download_min = download_buf = 0;
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

    public void sampleUpload(){
        long current_upload_used = this.upload_connection_list.getBandwidthUsage(CommonState.getTime());
        this.upload_sample += current_upload_used;
        this.sample_counter_up++;
    }

    public long getSampledUpload(){
        return Math.round(Math.ceil((this.upload_sample*1.0)/(this.sample_counter_up*1.0)));
    }


     public void sampleDownload(){
        long current_download_used = this.download_connection_list.getBandwidthUsage(CommonState.getTime());
        this.download_sample += current_download_used;
        this.sample_counter_dw++;
    }

    public long getSampledDownload(){
        return Math.round(Math.ceil((this.download_sample*1.0)/(this.sample_counter_dw*1.0)));
    }

    public void setUpload(long _upload) {
//        System.out.println("Update upload "+_upload);
        if (_upload < 0) {
            upload_buf += _upload;
//            System.out.println("Update buf "+this.upload_buf);
            this.upload = 0;
//            System.out.println("Update "+this.upload);
        } else {
            if (upload_buf < 0) {
//                System.out.println("Uploadbuf < 0 ");
                upload_buf = upload_buf + _upload;
                if(upload_buf>0){
                    this.upload += upload_buf;
                    upload_buf = 0;
                }
                
//                System.out.println("1UPLOAD  "+this.upload);
            } else {
                this.upload = _upload;
//                System.out.println("2UPLOAD  "+this.upload);
            }
        }
    }

    public void setUpload(long _upload, BandwidthConnectionElement bce) {
        bce = this.upload_connection_list.getRecord(bce.getSender(), bce.getReceiver(), bce.getTxId(), bce.getEnd());
        if (bce!= null && bce.getStart() == CommonState.getTime() && !bce.check) {            
//            System.out.print("UU Sto aggiungendo banda, quando tra poco me la tolgono: "+_upload);
            this.upload_buf += _upload;
//            System.out.println(" UBUF "+this.upload_buf);
        } else {
            this.setUpload(_upload);
        }
    }

    public long getUploadBUF() {
        return this.upload_buf;
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
//        System.out.println("Update download "+_download);
        if (_download < 0) {
//            if(this.download < _download){
            download_buf += _download;
//            System.out.println("Update buf "+this.download_buf);
            this.download = 0;
//            System.out.println("Update "+this.download);
//            }
//            else{
//                this.download -= _download;
//            }
        } else {
            if (download_buf < 0) {
//                System.out.println("downloadbuf < 0 ");
                download_buf = download_buf + _download;
                if(download_buf>0){
                    this.download += download_buf;
                    download_buf = 0;
                }

//                System.out.println("1download  "+this.download);
            } else {
                this.download = _download;
//                System.out.println("2download  "+this.download);
            }
        }
    }

    public void setDownload(long _download, BandwidthConnectionElement bce) {
        bce = this.download_connection_list.getRecord(bce.getSender(), bce.getReceiver(), bce.getTxId(),bce.getEnd());
        if (bce!=null && bce.getStart() == CommonState.getTime() && !bce.check) {
//            System.out.print("DD Sto aggiungendo banda, quando tra poco me la tolgono: "+_download);
            this.download_buf += _download;
//            System.out.println(" DBUF "+this.download_buf);
        } else {
            this.setDownload(_download);
        }
    }

    public long getDownloadBUF() {
        return this.download_buf;
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


    public long getEndUpload(){
        BandwidthConnectionElement bce = this.upload_connection_list.getFirstEnd();
        if(bce == null)
            return 0;
        else
            return bce.getEnd();
    }

    public long getEndDownload(){
        BandwidthConnectionElement bce = this.download_connection_list.getFirstEnd();
        if(bce == null)
            return 0;
        else
            return bce.getEnd();
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
        String result = "\n\t>> Upload: " + this.upload + " [" + this.upload_min + ":" + this.upload_max + "] - ";
        result += "Active " + this.getActiveUp() + "(" + this.getActiveUpload() + ")" + " Passive " + this.getPassiveUp() + "(" + this.getPassiveUpload() + ")";
        result += "\n\t<< Download: " + this.download + " [" + this.download_min + ":" + this.download_max + "] - ";
        result += "Active " + this.getActiveDw() + "(" + this.getActiveDownload() + ")" + " Passive " + this.getPassiveDw() + "(" + this.getPassiveDownload() + ")";
        return result;

    }

    //The control-message send should be implemented in this class.
    //public long sendControl(Node src, Node rcv,int pid)
    /**
     *  Metodo utilizzato per calcolare i tempi di trasmissione dei dati. Utilizza il priority sharing: la prima
     *  trasmissione che arriva prende tutto, la successiva prende il resto e cosi` via, con il limite sulla banda minima
     *  ossia una trasmissione non puo` andare con la velocita di banda minima (dovrebbe dipendere dalla mole di dati da trasmettere)
     *  Da rifinire il meccanismo di aggiornamento della banda quando mando un upload negativo e successivamente uno positivo x la stessa
     *  quantita` di banda (doppi download)
     */
    public long sendData(long data_bits, Node src, Node rcv, long eedelay, int pid) {
        long finish = -1;
        BandwidthAwareTransport sender = ((BandwidthAwareTransport) (src.getProtocol(pid)));
        BandwidthAwareTransport receiver = ((BandwidthAwareTransport) (rcv.getProtocol(pid)));
        long initupload = sender.getUpload();
        long initdownload = receiver.getDownload();
        if (sender.getUpload() < sender.getUploadMin()) {
            if (this.debug >= 4) {
                System.out.println("\t\t>> Node " + src.getID() + " has upload bw less than up_min that is " + sender.getUploadMin());
            }
            return BandwidthMessage.NO_UP;
        } else if (receiver.getDownload() < receiver.getDownloadMin()) {
            if (this.debug >= 3) {
                System.out.println("\t\t>> Node " + rcv.getID() + " has download bw less than down_min that is " + receiver.getDownloadMin());
            }
            return BandwidthMessage.NO_DOWN;
        }
        elements = new ArrayList();
        long bandwidth, delay;
        bandwidth = delay = 0;
        int up_i, dw_i;
        up_i = dw_i = 0;
//        ArrayList header = new ArrayList();
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
            if (this.debug >= 5) {
                System.out.println("UPLOAD CON: " + cupload);
            }
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
            if (this.debug >= 5) {
                System.out.println("DOWNLOAD CON: " + cdownload);
            }
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
            System.out.println("------------------------------------------------------ BANDWIDTH MANAGEMENT SYSTEM ------------------------------------------------------ ");
            System.out.println("Next sender upload ends at time: " + lastUpTime + "; Next receiver Download ends " + lastDwTime + " One-Way-Delay " + eedelay+" Bits to transmit are "+residuo);
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
        if(residuo == 0){
            if(this.debug >= 4)
                System.err.println("!!! The application layer request to send ZERO bits");
            return 1;
        }

        while (residuo > 0) {
            bandwidth = Math.min(banda_up, banda_dw);
            if (this.debug >= 5) {
                System.out.println("...Time " + baseTime + ". Bits to transmit: " + residuo + ". BW Sender : " + banda_up + ", BW Receiver : " + banda_dw + " TX Banda " + bandwidth);
            }
            //Da baseTime in poi hanno entrambi la tabella vuota, faccio tutto con un solo messaggio
            if ((uploadResidualTime == -1 && downloadResidualTime == -1)) {
                mexTime = Math.round((residuo / ((double) bandwidth)) * 1000);
                mexTime = (mexTime == 0 ? 1 : mexTime);
                if (this.debug >= 5) {
                    System.out.println("\tSender e Receiver hanno la tabella vuota, Trasmettono " + residuo + " con banda " + bandwidth + " durata trasmissione " + mexTime + " ms");
                }
                residuo = 0;
                tableTime = baseTime + mexTime;
                mexTime = tableTime - CommonState.getTime();
                finish = mexTime;
                BandwidthConnectionElement element = new BandwidthConnectionElement(src, rcv, bandwidth, baseTime, tableTime, 0);
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
//                    header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, (-1 * bandwidth), baseTime), mexTime));
                }
                mexTime = tableTime - CommonState.getTime();
                if (this.debug >= 5) {
                    System.out.println("\tSpedisco Update Upload a " + src.getID() + " per restituire " + bandwidth + " al tempo " + tableTime + " MexTime " + mexTime);
                }
                if (this.debug >= 5) {
                    System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per restituire " + bandwidth + " al tempo " + tableTime + " MexTime " + mexTime);
                }
//                header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, bandwidth, baseTime), mexTime));
            } // Il sender ha un upload in corso ed  ( il receive ha un download in corso che finisce dopo l'upload || non ha alcun download in corso)
            else if (uploadResidualTime != -1 && (uploadResidualTime < downloadResidualTime || downloadResidualTime == -1)) {
                if (this.debug >= 5) {
                    System.out.println("L'upload finisce prima del download :" + uploadResidualTime + " oppure il download è libero");
                }
                // controllo se la trasmissione può finire prima della fine dell'upload
                if (Math.round(((double) bandwidth) * uploadResidualTime / 1000.0D) >= residuo) {
                    long txTime = Math.round(residuo / ((double) bandwidth) * 1000);
                    txTime = (txTime == 0 ? 1 : txTime);
                    tableTime = baseTime + txTime;
                    BandwidthConnectionElement element = new BandwidthConnectionElement(src, rcv, bandwidth, baseTime, tableTime, CommonState.r.nextLong());
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
//                        header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, (-1 * bandwidth), baseTime), mexTime));
                    }
                    mexTime = tableTime - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Uplaod a " + src.getID() + " per " + bandwidth + " al tempo " + tableTime + " MexTime " + (tableTime - CommonState.getTime()));
                    }
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per " + bandwidth + " al tempo " + tableTime + " MexTime " + (tableTime - CommonState.getTime()));
                    }
//                    header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, bandwidth, baseTime), mexTime));
                    if (this.debug >= 5) {
                        System.out.println("La trasmissione di " + residuo + " finisce prima di " + uploadResidualTime + ", ci mette " + txTime + ", aggiungo elemento per il tempo " + tableTime);
                    }
                    finish = mexTime;
                    residuo = 0;
                } else {
                    //la trasmissione non finisce prima dell'upload pendente, quindi occorre spezzarla in almeno due parti
                    residuo = residuo - Math.round(bandwidth * ((double) uploadResidualTime) / 1000);
                    tableTime = uploadNextTime;
                    if (this.debug >= 5) {
                        System.out.println("Residuo " + residuo + " BaseTime " + baseTime + " TableTime " + tableTime);
                    }
                    BandwidthConnectionElement element = new BandwidthConnectionElement(src, rcv, bandwidth, baseTime, tableTime, CommonState.r.nextLong());
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
                        banda_up -= bandwidth;
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
//                        header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, (-1 * bandwidth), baseTime), mexTime));
                    }
                    mexTime = tableTime - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Upload a " + src.getID() + " per " + bandwidth + " ricezione " + tableTime + " MexTime " + mexTime);
                    }
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per " + bandwidth + " ricezione " + tableTime + " MexTime " + mexTime);
                    }
//                    header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, bandwidth, baseTime), mexTime));
                    baseTime = uploadNextTime;
                    if (this.debug >= 5) {
                        System.out.print("Aggiorno la banda up " + banda_up + " aggiungo " + uploadBusy);
                    }
                    banda_up += uploadBusy;
                    if (this.debug >= 5) {
                        System.out.println(" = banda up " + banda_up);
                    }
                    if (up_i < sender.upload_connection_list.getSize() - 1 && uploadNextTime < baseTime) {
                        do {
                            if (this.debug >= 5) {
                                System.out.println("Aggiorno upload " + baseTime);
                            }
                            if (this.debug >= 5) {
                                System.out.println(up_i + " - updating upload " + sender.upload_connection_list.getElement(up_i));
                            }
                            up_i++;
                            //banda usata nella prox trasmissione
                            cupload = sender.upload_connection_list.getElement(up_i);
                            uploadBusy = cupload.getBandwidth();
                            uploadStartTime = cupload.getStart();
                            uploadNextTime = cupload.getEnd();
                            uploadResidualTime = uploadNextTime - baseTime;
//                            if (uploadNextTime == baseTime) {
//                                if (this.debug >= 1) {
//                                    System.out.println("Aggiorno la banda " + banda_up + " aggiungo " + uploadBusy + " :: " + cupload);
//                                }
//                                banda_up += uploadBusy;
////                                baseTime =uploadNextTime;
//                            } else {
//                                if (this.debug >= 5) {
//                                    System.out.println("Aggiorno la banda " + banda_up + " tolgo " + uploadBusy);
//                                }
//                                banda_up -= uploadBusy;
//                            }
//                            if (this.debug >= 5) {
//                                System.out.println(" = " + banda_up);
//                            }
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
//                                header.clear();
//                                header = null;
                                elements = null;
                                return BandwidthMessage.NO_UP;
                            }
                        } while (up_i < sender.upload_connection_list.getSize() - 1 && uploadNextTime <= baseTime);
                    } else {
                        uploadResidualTime = -1;
                    }
                    if (this.debug >= 5) {
                        System.out.println(" a " + banda_up);
                    }
                }
            } //devo ricordare che lasttime è la base di tutti i tempi, devo sistemare tutto quello che ci sta sotto!
            else if (downloadResidualTime != -1 && (downloadResidualTime <= uploadResidualTime || uploadResidualTime == -1)) {
                if (this.debug >= 5) {
                    System.out.println("\til download finisce prima dell'upload :" + downloadResidualTime + " oppure l'upload è libero");
                }
                //controllo se la trasmissione può finire prima del download pendente
                if (Math.round(((double) bandwidth) * downloadResidualTime / 1000) >= residuo) {//riusciamo a trasmettere tutto prima che si liberi banda
                    long txTime = Math.round(residuo / ((double) bandwidth) * 1000);
                    txTime = (txTime == 0 ? 1 : txTime);
                    tableTime = baseTime + txTime;

                    BandwidthConnectionElement element = new BandwidthConnectionElement(src, rcv, bandwidth, baseTime, tableTime, CommonState.r.nextLong());
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
//                        header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, (-1 * bandwidth), baseTime), mexTime));
                    }
                    mexTime = tableTime - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Upload a " + src.getID() + " per " + bandwidth + " ricezione " + tableTime + " MexTime " + mexTime);
                    }
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per " + bandwidth + " al tempo " + tableTime);
                    }
//                    header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, bandwidth, baseTime), mexTime));
                    if (this.debug >= 5) {
                        System.out.println("La trasmissione di " + residuo + " finisce prima di " + downloadResidualTime + ", ci mette " + txTime + ", aggiungo elemento per il tempo " + baseTime);
                    }
                    finish = mexTime;
                    residuo = 0;
                } else {
                    //la trasmissione non può finire prima del download pendente, occorre spezzarla in almeno due parti
                    residuo = residuo - Math.round(((double) bandwidth) * downloadResidualTime / 1000);
                    tableTime = downloadNextTime;
                    if (this.debug >= 5) {
                        System.out.println("\tResiduo " + residuo + " tabletime " + tableTime);
                    }
                    BandwidthConnectionElement element = new BandwidthConnectionElement(src, rcv, bandwidth, baseTime, tableTime, CommonState.r.nextLong());
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
//                        banda_dw -= bandwidth;
                    } else {
                        mexTime = baseTime - CommonState.getTime();
                        if (this.debug >= 5) {
                            System.out.println("5 >>> Tolgo la banda con messaggio " + bandwidth + " al tempo " + baseTime + " per restituirla al tempo " + tableTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSpedisco Update Upload a " + src.getID() + " per " + (-1 * bandwidth) + " al tempo " + baseTime);
                        }
                        if (this.debug >= 5) {
                            System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per " + (-1 * bandwidth) + " al tempo " + baseTime);
                        }
//                        header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, (-1 * bandwidth), baseTime), mexTime));
                    }
                    mexTime = tableTime - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Upload a " + src.getID() + " per " + bandwidth + " al tempo " + tableTime);
                    }
                    if (this.debug >= 5) {
                        System.out.println("\tSpedisco Update Download a " + rcv.getID() + " per " + bandwidth + " al tempo " + tableTime);
                    }
//                    header.add(new BwEvent(src, src, new BandwidthMessage(src, rcv, BandwidthMessage.UPD_UP, bandwidth, baseTime), mexTime));
                    baseTime = downloadNextTime;
                    if (this.debug >= 5) {
                        System.out.print("Al tempo " + baseTime + " aggiorno download da " + banda_dw + " aggiungo " + downloadBusy);
                    }
                    banda_dw += downloadBusy;
                    if (this.debug >= 5) {
                        System.out.println("..." + banda_dw);
                    }
                    if (dw_i < receiver.getDownloadConnections().getSize() - 1 && downloadNextTime <= baseTime) {
                        do {
                            if (this.debug >= 5) {
                                System.out.println("Aggiorno download receiver " + rcv.getID());
                            }
                            dw_i++;
                            cdownload = receiver.getDownloadConnections().getElement(dw_i);
                            downloadBusy = cdownload.getBandwidth();
                            downloadStartTime = cdownload.getStart();
                            downloadNextTime = cdownload.getEnd();
                            downloadResidualTime = downloadNextTime - baseTime;
                            if (this.debug >= 5) {
                                System.out.println("DownStart " + downloadStartTime + " DownNext " + downloadNextTime + " currentTime " + CommonState.getTime());
                                System.out.println(dw_i + " - updating download " + cdownload);
                            }
//                            if (downloadNextTime == baseTime && downloadStartTime!=downloadNextTime) {
//                            if (downloadStartTime <= CommonState.getTime() && downloadStartTime < downloadNextTime) {//downloadStartTime == baseTime
//                                if (this.debug >= 5) {
//                                    System.out.println("Aggiorno download da " + banda_dw + " aggiungo " + downloadBusy);
//                                }
//                                banda_dw += downloadBusy;
////                                baseTime+=downloadResidualTime;
//                            } else {//else if(downloadStartTime> baseTime){
//                                if (this.debug >= 5) {
//                                    System.out.println("Aggiorno download da " + banda_dw + " tolgo " + downloadBusy);
//                                }
//                                banda_dw -= downloadBusy;
//                            }
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
                        } while ((dw_i < receiver.getDownloadConnections().getSize() - 1) && downloadNextTime <= baseTime);
                    } else {
                        downloadResidualTime = -1;
                    }
                }
            }
        }
        long txid = CommonState.r.nextLong();
        if (!elements.isEmpty()) {
            if (this.debug >= 5) {
                System.out.println("\tInserisco i seguenti elementi nelle tabelle di upload e download: ");
            }
            BandwidthConnectionElement cet = null;
            long olds, olde, oldb, cs, ce, cb; //
            olds = olde = oldb = cs = ce = cb = -1;
            ArrayList vsender = new ArrayList();
            for (int j = 0; j < elements.size(); j++) {
                if (this.debug >= 5) {
                    System.out.println("\tConnectionElement # " + j);
                }
                cet = ((BandwidthConnectionElement) elements.get(j));
                if (j == 0) {
                    olds = cet.getStart();
                    olde = cet.getEnd();
                    oldb = cet.getBandwidth();                    
                    if (this.debug >= 5) {
                        System.out.println("\tLa prima connessione ha i seguenti valori: Start " + olds + " End " + olde + " Bandwidth " + oldb);
                    }
                } else {
                    cs = cet.getStart();
                    ce = cet.getEnd();
                    cb = cet.getBandwidth();
                    if (this.debug >= 5) {
                        System.out.print("\tCerco di accorpare le due connessioni: " + olds + "--" + oldb + "-->" + olde + " con " + cs + "--" + cb + "-->" + ce + "......");
                    }
                    if (cs == olde && cb == oldb) {
                        olde = ce;
                        if (this.debug >= 5) {
                            System.out.println("Si possono accorpare :)");
                        }
                    } else {
                        if (this.debug >= 5) {
                            System.out.println("NON si possono accorpare :(");
                        }
                        BandwidthConnectionElement bce = new BandwidthConnectionElement(cet.getSender(), cet.getReceiver(), oldb, olds, olde, txid);
                        if (this.debug >= 5) {
                            System.out.println("\tCreating element  " + bce);
                        }
                        vsender.add(bce);
                        olds = cs;
                        olde = ce;
                        oldb = cb;
                        if (this.debug >= 5) {
                            System.out.println("\tUpdating values " + olds + " OldE " + olde + " OldB " + oldb + " TxID " + txid);
                        }
                    }
                }

            }
            BandwidthConnectionElement bce = new BandwidthConnectionElement(cet.getSender(), cet.getReceiver(), oldb, olds, olde, txid);
            vsender.add(bce);
            elements = vsender;
            vsender = null;
//            BwEvent bolds,boldr;
//            bolds=boldr=null;
            for (int j = 0; j < elements.size(); j++) {
                cet = ((BandwidthConnectionElement) elements.get(j));
                if (this.debug >= 5) {
                    System.out.println("\tSender Element " + cet + "; ");
                }
                sender.getUploadConnections().addConnection(cet);
                long mextime = cet.getEnd() - CommonState.getTime();
                BwEvent bs = new BwEvent(cet.getSender(), cet.getReceiver(), new BandwidthMessage(cet.getSender(), cet.getReceiver(), BandwidthMessage.UPD_UP, cet.getBandwidth(), cet.getStart()), mextime);
                if (this.debug >= 5) {
                    System.out.println("\t\tBandwidthEvent " + bs + "; ");
                }
                BandwidthConnectionElement bet = new BandwidthConnectionElement(cet.getSender(), cet.getReceiver(), cet.getBandwidth(), (cet.getStart() + eedelay), (cet.getEnd() + eedelay), cet.getTxId());
                if (this.debug >= 5) {
                    System.out.println("\tReceiver Element " + bet + "; ");
                }
                mextime = bet.getEnd() - CommonState.getTime();
                BwEvent br = new BwEvent(bet.getSender(), bet.getReceiver(), new BandwidthMessage(bet.getSender(), bet.getReceiver(), BandwidthMessage.UPD_DOWN, bet.getBandwidth(), bet.getStart()), mextime);
                if (this.debug >= 5) {
                    System.out.println("\t\tBandwidthEvent " + br + "; ");
                }
                receiver.getDownloadConnections().addConnection(bet);
                if (this.debug >= 5) {
                    System.out.println("\tIndice J " + j + "; ");
                }
                if (j == 0) {
                    olds = cet.getStart();
                    olde = cet.getEnd();
                    oldb = cet.getBandwidth();
                    mextime = olde - CommonState.getTime();
                    if (this.debug >= 5) {
                        System.out.print("\tFirst connection Start " + olds + " Finish " + olde + " Bandwidth " + oldb + " >> MexTime Sender " + mextime);
                    }
                    System.out.flush();
                    EDSimulator.add(bs.getTime(), bs.getBWM(), bs.getsrc(), pid);
//                    bolds = bs;
                    finish = mextime;
                    mextime = mextime + eedelay;
                    if (this.debug >= 5) {
                        System.out.println("; MexTime Receiver" + mextime);
                    }
//                    boldr = br;
                    EDSimulator.add(br.getTime(), br.getBWM(), br.getrcv(), pid);
                } else {
                    cs = cet.getStart() - CommonState.getTime();
                    ce = cet.getEnd() - CommonState.getTime();
                    cb = cet.getBandwidth();                    
                    BandwidthMessage less = new BandwidthMessage(bs.getsrc(), bs.getrcv(), BandwidthMessage.UPD_UP, (-1 * cb), cet.getStart());
                    if (this.debug >= 5) {
                        System.out.println("\tIn time " +cet.getStart()+ " I'll rem " + cb + " to  sender (" + cet.getSender().getID()+"\n\t\t"+less );
                    }
                    EDSimulator.add(cs, less, bs.getsrc(), pid);                    
                    less = new BandwidthMessage(bs.getsrc(), bs.getrcv(), BandwidthMessage.UPD_DOWN, (-1 * cb), (cet.getStart()+eedelay));
                    EDSimulator.add(cs, less, bs.getsrc(), pid);
                    if (this.debug >= 5) {
                        System.out.println("\tIn time " + cet.getStart() + "("+cet.getStart()+ " I'll rem " + cb + " to  sender (" + cet.getReceiver().getID()+"\n\t\t"+less );
                    }
                    finish = ce;
                    BandwidthMessage plus = new BandwidthMessage(bs.getsrc(), bs.getrcv(), BandwidthMessage.UPD_UP, cb, cet.getStart());
                    EDSimulator.add(ce, plus, bs.getsrc(), pid);
                    if (this.debug >= 5) {
                        System.out.println("\tIn time " + cet.getEnd()+ " I'll add " + cb + " to  sender (" + cet.getSender().getID()+"\n\t\t"+plus );
                    }
                    plus = new BandwidthMessage(bs.getsrc(), bs.getrcv(), BandwidthMessage.UPD_DOWN, cb, (cet.getStart()+eedelay));
                    EDSimulator.add((ce+eedelay), plus, bs.getsrc(), pid);
                    if (this.debug >= 5) {
                        System.out.println("\tIn time " + (cet.getEnd()+eedelay)+ " I'll add " + cb + " to  sender (" + cet.getReceiver().getID()+"\n\t\t"+plus );
                    }
                }
            }
            elements.clear();
        }
        //        header.clear();
//        header = null;
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
        return "[ Source " + src.getID() + " | Receiver " + dest.getID() + " | Mex " + bwm.getClass() + " | Time " + time + " ]";
    }
}


