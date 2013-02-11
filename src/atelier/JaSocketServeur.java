package atelier;
import java.io.*;
import java.net.*;

/**
 * <pre>
 *      Thread de lecture du flot de sortie du socket
 * </pre>
 * @author  : Philippe.BILLEROT@laposte.net
 * @version : 1.0 du 9 novembre 2000
 */
public class JaSocketServeur implements Runnable {
private final static String nomClasse = "JaSocketServeur";
JaFrame         jaf = null;
Thread          thread = null;
int             port;

    public JaSocketServeur(JaFrame pjaf, int portSocket) {
        jaf = pjaf;
        port = portSocket;
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
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            jaf.log("serveur à l'écoute sur le port " + port + "...");
            while ( true ) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "Cp863"));
                    // lecture du message du client
                    jaf.log("j'ai un client");
                    String mess;
                    while ( (mess = in.readLine()) != null ) {
                        jaf.log(mess);
                        mess = null;
                    } // end while
                    in.close();
                    socket.close();
                    socket = null;
                    in = null;
                    mess = null;
                } catch ( Exception e ) {
                    try {
                        socket.close();
                    } catch ( Exception ee ) {
                        jaf.log(50, nomClasse, ee);
                    } // end try
                    jaf.log(60, nomClasse, e);
                } // end try
            } // end while
        } catch ( Exception e ) {
            try {
                serverSocket.close();
            } catch ( Exception ee ) {
                jaf.log(70, nomClasse, ee);
            } // end try
            jaf.log(80, nomClasse, e);
        } // end try
    }
} // end class

