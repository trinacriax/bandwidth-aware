/*
 * Copyright (c) 2003-2005 The BISON Project
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package bandwidth.core;


import peersim.core.*;
import peersim.vector.*;

/**
 * Initializes the values drawing uniform random samples from the range
 * [{@value #PAR_MIN}, {@value #PAR_MAX}[.
 * @see VectControl
 * @see peersim.vector
 */
public class BandwidthAwareInitializer2 extends VectControl
{

    /**
     * Initialize the Bandwidth Aware protocol.
     * This protocol is useful for having a network layer where peers have
     * different resources in term of both up-/down-load bandwidth.
     * You have to provide the CDF of the bandwidth.
     * It uses the methods defined in {@link bandwidth.BandwidthAwareSkeleton}.
     *
     * @author Alessandro Russo
     * @version $Revision: 0.01$
     */
    // ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------
    private static final String PAR_PROT = "protocol";
    private static final String PAR_UP_BAND = "uplink";
    private static final String PAR_DOWN_BAND = "downlink";
    private static final String PAR_BW_PROB = "bdist";
    private static final String PAR_ACTIVE_UPLOAD = "active_upload";
    private static final String PAR_ACTIVE_DOWNLOAD = "active_download";
    private static final String PAR_PASSIVE_UPLOAD = "passive_upload";
    private static final String PAR_PASSIVE_DOWNLOAD = "passive_download";
    private static final String PAR_DEBUG = "debug";
    private static final String PAR_BMP = "bmp";//peers' bandwidth multiplicator
    private static final String PAR_BMS = "bms";//source's bandwidth multiplicaotr
    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
//    /**Protocol Identifier */
//    private final int pid;
//    /**Value uses for debugging*/
//    private final int debug;
//    /**Matrtix for base-upload bandwidth distribution*/
//    private int UploadBandwidth[];
//    /**Matrtix for base-download bandwidth distribution*/
//    private int DownloadBandwidth[];
//    /**Matrtix for bandwidth distribution*/
//    private double BandwidthProb[];
//    /**Active upload*/
//    private int active_upload;
//    /**Active download*/
//    private int active_download;
//    /**Passive upload*/
//    private int passive_upload;
//    /**Passive download*/
//    private int passive_download;
//    /**Source upload bandwidth*/
//    private int srcup;
//    /**Source download bandwidth*/
//    private int srcdw;
//    /**Peers' bandwidth multiplicator*/
//    private double bmp[];
//    /**Source bandwidth multiplicator*/
//    private double bms;

    //
    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------
    /**
     * Creates a new instance and read parameters from the config file.
     */
    public BandwidthAwareInitializer2(String prefix) {
	super(prefix);
//	// Read parameters based on type
//            active_upload = Integer.valueOf(Configuration.getInt(prefix + "." + PAR_ACTIVE_UPLOAD, 1));
//            active_download = Integer.valueOf(Configuration.getInt(prefix + "." + PAR_ACTIVE_DOWNLOAD, 1));
//            passive_upload = Integer.valueOf(Configuration.getInt(prefix + "." + PAR_PASSIVE_UPLOAD, 1));
//            passive_download = Integer.valueOf(Configuration.getInt(prefix + "." + PAR_PASSIVE_DOWNLOAD, 1));
//            //pid = Configuration.getPid(prefix + "." + PAR_PROT);
//            debug = Configuration.getInt(prefix + "." + PAR_DEBUG, 0);
//            String _bmp[] = Configuration.getString(prefix + "." + PAR_BMP, "1").split(" ");
//            String _bprob[] = Configuration.getString(prefix + "." + PAR_BW_PROB, "1").split(" ");
//            System.err.println("Init Bandwidth. Debug " + debug);
//            bms = Configuration.getDouble(prefix + "." + PAR_BMS,0);
//            if (_bmp.length == 1) {//BANDWIDTH HOMOGENEOUS NETWORK
//                this.UploadBandwidth = new int[_bmp.length];
//                this.DownloadBandwidth = new int[_bmp.length];
//                this.BandwidthProb = new double[_bmp.length];
//                bmp = new double[1];
//                bmp[0] = Configuration.getDouble(prefix + "." + PAR_BMP, 1);
//                System.err.print("Bmp[0] >" + bmp[0] + ">");
//                double _upload = Configuration.getDouble(prefix + "." + PAR_UP_BAND, -1);
//                if(bms == 0)//source has the same bandwidth of peers
//                    bms = bmp[0];
//                srcup = (int) Math.ceil(bms * _upload);
//                _upload = Math.round(_upload * bmp[0]);
//                this.UploadBandwidth[0] = (int) _upload;
//                System.err.print("UP " + this.UploadBandwidth[0] + "; ");
//                double _download = Configuration.getDouble(prefix + "." + PAR_DOWN_BAND, -1);
//                srcdw = (int) Math.ceil(bms * _download);
//                _download = Math.round(_download * bmp[0]);
//                this.DownloadBandwidth[0] = (int) _download;
//                System.err.print("DW " + this.DownloadBandwidth[0] + "; ");
//                this.BandwidthProb[0] = 1;
//                System.err.print("Prob " + this.BandwidthProb[0] + ".\n");
//
//            } else {//BANDWIDTH HETEROGENEOUS NETWORK
//                this.UploadBandwidth = new int[_bmp.length];
//                this.DownloadBandwidth = new int[_bmp.length];
//                this.BandwidthProb = new double[_bmp.length];
//                System.err.println("Init Bandwidth  " + _bmp.length);
//                double _upload = Configuration.getDouble(prefix + "." + PAR_UP_BAND, -1);
//                double _download = Configuration.getDouble(prefix + "." + PAR_DOWN_BAND, -1);
//                if(bms == 0)//source has the highest bandwidth available
//                    bms = bmp[bmp.length-1];
//                srcup = (int) Math.ceil(bms * _upload);
//                srcdw = (int) Math.ceil(bms * _download);
//                for (int i = 0; i < _bmp.length; i++) {
//                    bmp[i] = Configuration.getDouble(_bmp[i], 1);
//                    System.err.print("Bmp[" + i + "] >" + bmp[i] + ">");
//                    this.UploadBandwidth[i] = (int) Math.round(_upload * bmp[i]);
//                    System.err.print("UPBW [" + i + "] =" + this.UploadBandwidth[i] + "; ");
//                    this.DownloadBandwidth[0] = (int) Math.round(_download * bmp[i]);
//                    System.err.print("DWBW [" + i + "] =" + this.DownloadBandwidth[i] + "\n");
//                    this.BandwidthProb[i] = Double.parseDouble(_bprob[i]);
//                    System.err.print("\tBWPROB [" + i + "] =" + this.BandwidthProb[i] + "\n");
//                }
//            }
//            System.err.print("#Bandwidth init done\n");
    }

// --------------------------------------------------------------------------
// Methods
// --------------------------------------------------------------------------

/**
 * Initializes the values drawing uniform random samples from the range
 * [{@value #PAR_MIN}, {@value #PAR_MAX}[.
 * @return always false
 */
public boolean execute() {

//	if(setter.isInteger())
//	{
//		long d = max.longValue() - min.longValue();
//		for (int i = 0; i < Network.size(); ++i)
//		{
//			setter.set(i,CommonState.r.nextLong(d)+min.longValue());
//		}
//	}
//	else
//	{
//		double d = max.doubleValue() - min.doubleValue();
//		for (int i = 0; i < Network.size(); ++i)
//		{
//			setter.set(i,CommonState.r.nextDouble()*d+
//			min.doubleValue());
//		}
//	}
//
	return false;
}

// --------------------------------------------------------------------------

/**
 * Initializes the value drawing a uniform random sample from the range
 * [{@value #PAR_MIN}, {@value #PAR_MAX}[.
 * @param n the node to initialize
 */
public void initialize(Node n) {

//	if( setter.isInteger() )
//	{
//		long d = max.longValue() - min.longValue();
//		setter.set(n,CommonState.r.nextLong(d) + min.longValue());
//	}
//	else
//	{
//		double d = max.doubleValue() - min.doubleValue();
//		setter.set(n,CommonState.r.nextDouble()*d);
//	}
}

// --------------------------------------------------------------------------

}
