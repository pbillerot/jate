package atelier;
import java.io.*;
import java.net.*;

/**
 * <pre>
 * Thread de lecture du flot de sortie du shell de commande.
 * Soit le flot de sortie ou le flot des erreurs
 * </pre>
 * @author  : Philippe.BILLEROT@laposte.net
 * @version : 1.0 du 9 novembre 2000
 */
public class JaSocketClient implements Runnable {
private final static String nomClasse = "JaSocketClient";
JaFrame         jaf = null;
Thread          thread = null;
String          adresse;
int             port;
String          mess;

    public JaSocketClient(JaFrame pjaf, String addressServer, int portSocket, String message) {
        jaf = pjaf;
        adresse = addressServer;
        port = portSocket;
        mess = message;
        try {
            thread = new Thread(this);
            thread.start();
        } catch ( Exception e ) {
            jaf.log(10, nomClasse, e);
            thread = null;
        } // end try
    } // end constructor

    /**
     * EXECUTION DU THREAD
     */
    public void run() {
        try {
            InetAddress addr = InetAddress.getByName(adresse);
            Socket socket = new Socket(addr, port);
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            wr.write(mess);
            wr.close();
            socket.close();
        } catch ( Exception e ) {
            jaf.log(20, nomClasse, e);
        } // end try
    }
} // end class

