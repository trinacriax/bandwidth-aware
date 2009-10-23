package bandwidth.test;

import java.io.*;
import java.util.zip.GZIPOutputStream;
import peersim.core.*;

/**
 * TEST CLASS
 * @author Alessandro Russo
 * @version 1.0
 */
public class PrintLogs extends PrintStream {

    private String dirname;

    public PrintLogs(String prefix) {
        super(System.out);
        dirname = "logs";
        try {
            if (!new File(dirname).exists()) {
                new File(dirname).mkdir();
            }
            String name = CommonState.getConfig() + "-" + CommonState.r.getLastSeed() + ".gz";
            this.out = new GZIPOutputStream(new FileOutputStream(new File(dirname, name)));
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public void setOut(String name) {
        try {
            this.out = new GZIPOutputStream(new FileOutputStream(new File(dirname, name)));
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
