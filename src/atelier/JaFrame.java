package atelier;
import javax.swing.*;
import java.util.*;

/**
 * Interface entre la frame principale et les panneaux de cette frame
 * @author  : Philippe.BILLEROT@laposte.net
 * @version : 1.0 du 12 novembre 2000
 */
public interface JaFrame {
public final static String CLEVAR = "var"; 
public final static String CLEVAL = "val"; 

    public JFrame getFrame();
    public JaProperties getJaProperties();
    public Hashtable getContexte();
    public void log(Exception e);
    public void log(String message);
    public void log(String var, String val);
    public void log(int lig, String var, Exception e);

} // end JaFrame
