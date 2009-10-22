package bandwidth.core;

/**
 *
 * @author ax
 */
import peersim.config.*;
import peersim.core.*;
import peersim.vector.VectControl;

public class CDFDistribution extends VectControl {

    /**
     * Initialize the value from a distribution given in the config file.
     * This initializer is useful for to assign different values
     * from a given ditribution using the setter method.
     * You have to provide the base value, the multipliers separated 
     * by a comma, and the CDF of the values.
     *
     * @author Alessandro Russo
     * @version $Revision: 0.01$
     */
    // ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------    
    private static final String PAR_BASE_VAL = "base_value";//uplink resources
    private static final String PAR_VAL_MUL = "value_multiplier";//peers' bandwidth multiplicator
    private static final String PAR_VAL_DIS = "cdf_distribution";//bandiwdth distribution
    private static final String PAR_DEBUG = "debug";//debug level    
    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------    
    /**Level of verbosity */
    private final int debug;
    /**Base value*/
    private final Number base_value;
    /**Value Distribution*/
    private final double values_distribution[];
    /**Base_value multipliers*/
    private double values_multiplier[];

    // 
    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------
    /**
     * This constructor creates a new instance of the class,
     * invoking super and then reading parameters from the config file.
     */
    public CDFDistribution(String prefix) {
        super(prefix);
        System.err.print("Init. Distribution by CDF ");
        debug = Integer.valueOf(Configuration.getInt(prefix + "." + PAR_DEBUG, 0));
        String _val_multi[] = Configuration.getString(prefix + "." + PAR_VAL_MUL, "1").split(",");
        String _val_dist[] = Configuration.getString(prefix + "." + PAR_VAL_DIS, "1").split(",");
        System.err.println("on " + _val_multi.length + " elements");
        if (setter.isInteger()) {
            this.base_value = Integer.valueOf(Configuration.getInt(prefix + "." + PAR_BASE_VAL, -1));
        } else {
            this.base_value = new Double(Configuration.getDouble(prefix + "." + PAR_BASE_VAL, -1));
        }
        if (base_value.doubleValue() < 0.0) {
            System.err.println("Warning: the base value is negative .");
        }
        System.err.println("Current base value is " + base_value);
        this.values_distribution = new double[_val_dist.length];
        this.values_multiplier = new double[_val_multi.length];
        int total = 0;
        System.err.print("i-th [ Distribution - Multiplier ]\n");
        for (int i = 0; i < values_multiplier.length; i++) {
            values_distribution[i] = Double.parseDouble(_val_dist[i]);
            values_multiplier[i] = Double.parseDouble(_val_multi[i]);
            System.err.print(i+"-th [" + values_distribution[i]+" - "+ values_multiplier[i]+"]\n");
            if(values_multiplier[i] <0){
                System.err.println("Multipliers should be only positive! Current is " + values_multiplier[i] +" which is negative.");
            }
            if(values_distribution[i] <0){
                System.err.println("Distribution have to be positive! Current is " + values_multiplier[i] +" which is negative..cast to positive.");
            }
            total +=Math.abs(values_distribution[i]);

            if(total>1){
                total -=values_distribution[i];
                values_distribution[i]=1.0-total;
                total +=values_distribution[i];
            }
            if(total==1 && i+1<values_multiplier.length)
                System.err.println("You have to defined a CDF greater than 1. Current is " + total +" till element "+ i);
        }
        System.err.print("\n");
        System.err.print("#Bandwidth init done\n");
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    /**
     * Initialize peers' fields.
     * @return Always return false.
     */
    public boolean execute() {
        if (debug >= 6) {
                    System.out.println("Executing the setter CDF Distribution.");
                }
        if (setter.isInteger()) {
            long set_value = 0;
            for (int i = 0; i < Network.size(); ++i) {
                long val_max = (int) Math.round(this.base_value.intValue() * this.values_multiplier[this.values_multiplier.length - 1]);
                long _value = CommonState.r.nextLong(((long) (val_max)));
                for (int j = 0; j < this.values_distribution.length - 1 || (j==0 && this.values_distribution.length==1); j++) {
                    if (_value > val_max * this.values_distribution[j]) {
                        set_value = (int) Math.round(base_value.intValue() * values_multiplier[j + 1]);
                    } else {
                        set_value = (int) Math.round(base_value.intValue() * values_multiplier[j]);
                        j = this.values_distribution.length;
                    }
                }
                if (debug >= 6) {
                    System.out.println("Setting value > " + set_value +" in node "  +i+ ";");
                }
                setter.set(i, set_value);

            }
        } else {
            double set_value = 0;
            for (int i = 0; i < Network.size(); ++i) {
                double val_max = this.base_value.intValue() * this.values_multiplier[this.values_multiplier.length - 1];
                double _value = CommonState.r.nextLong(((long) (val_max)));

                for (int j = 0; j < this.values_distribution.length - 1; j++) {
                    if (debug >= 6) {
                        System.out.println("\t" + j + ") " + _value + " > " + (val_max * this.values_distribution[j]));
                    }
                    if (_value > val_max * this.values_distribution[j]) {
                        set_value = base_value.intValue() * values_distribution[j + 1];

                    } else {
                        set_value = base_value.intValue() * values_distribution[j];
                        j = this.values_distribution.length;
                    }
                }
                setter.set(i, set_value);
            }
        }
        return false;
    }
}
