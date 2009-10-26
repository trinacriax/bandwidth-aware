package bandwidth.core;

import peersim.config.*;
import peersim.core.*;
import peersim.vector.VectControl;

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
public class CDFDistribution extends VectControl {

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
    private double value_multipliers[];

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
        System.err.println("on " + _val_multi.length + " elements: ");
        //get base value.
        if (setter.isInteger()) {
            this.base_value = Integer.valueOf(Configuration.getInt(prefix + "." + PAR_BASE_VAL, -1));
        } else {
            this.base_value = new Double(Configuration.getDouble(prefix + "." + PAR_BASE_VAL, -1));
        }
        //check: base value has to eb positive.
        if (base_value.doubleValue() < 0.0) {
            System.err.println("Error. The base value is negative; it has to be positive.");
            System.exit(1);
        }
        System.err.println("Base value: " + base_value);
        this.values_distribution = new double[_val_dist.length];
        this.value_multipliers = new double[_val_multi.length];
        double last_distribution = 0;
        System.err.print("i-th [ Distribution - Multiplier ]\n");
        for (int i = 0; i < value_multipliers.length; i++) {
            values_distribution[i] = Double.parseDouble(_val_dist[i]);
            value_multipliers[i] = Double.parseDouble(_val_multi[i]);
            System.err.print(i + "-th [" + values_distribution[i] + " - " + value_multipliers[i] + "]\n");
            //Check multipliers > 0
            if (value_multipliers[i] <= 0) {
                System.err.println("Error. Multipliers has be positive while the " + i +"-th is negative (" + value_multipliers[i] + ").");
                 System.exit(1);
            }
            if (values_distribution[i] < 0) {
                System.err.println("Error. Distribution has be positive while the " + i +"-th is negative (" + values_distribution[i] + ").");
                System.exit(1);
            }
            if(last_distribution == 0)
                last_distribution = values_distribution[i];
            if(last_distribution >values_distribution[i]){
                System.err.println("You have to defined the distribution in increasing order (e.g. 0.3,0.4,0.89,0.94,1.0) as a CDF where the last value is 1.0.");
                System.exit(1);
            }
            else if(values_distribution[i]>1){
                System.err.println("You have defined a value in the distribution greater than 1.0, it could not be possible because the maximum value in a CDF is 1.0");
                System.exit(1);
            }
        }
        System.err.print("\n");
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
                long val_max = (int) Math.round(this.base_value.intValue() * this.value_multipliers[this.value_multipliers.length - 1]);
                long _value = CommonState.r.nextLong(((long) (val_max)));
                for (int j = 0; j < this.values_distribution.length - 1 || (j == 0 && this.values_distribution.length == 1); j++) {
                    if (_value > val_max * this.values_distribution[j]) {
                        set_value = (int) Math.round(base_value.intValue() * value_multipliers[j + 1]);
                    } else {
                        set_value = (int) Math.round(base_value.intValue() * value_multipliers[j]);
                        j = this.values_distribution.length;
                    }
                }
                if (debug >= 6) {
                    System.out.println("Setting value > " + set_value + " in node " + i + ";");
                }
                setter.set(i, set_value);

            }
        } else {
            double set_value = 0;
            for (int i = 0; i < Network.size(); ++i) {
                double val_max = this.base_value.intValue() * this.value_multipliers[this.value_multipliers.length - 1];
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
