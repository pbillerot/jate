package atelier;
import java.io.*;

/**
 * <pre>
 * Thread de lecture du flot de sortie du shell de commande.
 * Soit le flot de sortie ou le flot des erreurs
 * </pre>
 * @author  : Philippe.BILLEROT@laposte.net
 * @version : 1.0 du 9 novembre 2000
 */
public class JaDosOutput implements Runnable {
private final static String nomClasse = "JaDosOutput";
JaFrame         jaf = null;
Thread          thread = null;
BufferedReader  in = null;

    public JaDosOutput(JaFrame pjaf, BufferedReader pin) {
        jaf = pjaf;
        in = pin;
        try {
            thread = new Thread(this);
            thread.start();
        } catch ( Exception e ) {
        	jaf.log(e); 
            thread = null;
        } // end try
    } // end constructor

    /**
     * EXECUTION DU SCRIPT DOS
     */
    public void run() {
        try {
            String str = "";
            while ( (str = in.readLine()) != null ) {
                if ( str.length() > 0 ) jaf.log(str);
            } // end while
            in.close();
        } catch ( Exception e ) {
        	jaf.log(e); 
        } // end try
    }
} // end class

