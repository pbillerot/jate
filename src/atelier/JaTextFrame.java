package atelier;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * @author  : Philippe.BILLEROT@laposte.net
 * @version : 1.0 du 23 novembre 2000
 */
public class JaTextFrame extends JFrame implements ActionListener, JaPaneListener, JaFrame {
private final static String nomClasse = "JaTextFrame";
private String       fichier = "";
private JaFrame      jaf = null;
private JaProperties jac = null;
private Hashtable    contexte = new Hashtable();
// MENU
private JMenuItem   menuPosition;
private JMenuItem   menuFermer;

private JaTextPane  textPane;

    public JaTextFrame(JaFrame jaf, String fichier) {
        this.jaf = jaf;
        this.jac = jaf.getJaProperties();
        this.fichier = fichier;
        initComponents();
        this.pack();
        initPosition();
        this.setVisible(true);
        textPane.load(fichier);
    } // end init

    /**
     * Interface JaFrame
     */
    public JFrame getFrame() {
        return this;
    }
    public JaProperties getJaProperties() {
        return jac;
    }
    public Hashtable getContexte() {
        return contexte;
    }
    public void log(Exception e) {
    	
    }
    public void log(String message) {
    }
    public void log(String var, String val) {
    }
    public void log(int lig, String var, Exception e) {
    }
    /*****************************/

    private void initPosition() {
        jac.getPosition(this, nomClasse);
    }

    /**
     * Création de l'IHM (Interface Homme Machine)
     */
    private void initComponents() {
        String methode = "initComponents";
        setIconImage(Ja.getImage("/images/jate_icon.gif"));
        // MENUS
        JMenuBar menuBar = new JMenuBar();
        // menu FICHIER
        JMenu menuFile = new JMenu("Fichier");

        menuPosition = new JMenuItem("mémoriser la position et la taille de la fenêtre");
        menuPosition.addActionListener(this);
        menuFile.add(menuPosition);

        menuFile.add(new JSeparator() );

        menuFermer = new JMenuItem("Fermer");
        menuFermer.addActionListener(this);
        menuFile.add(menuFermer);

        menuBar.add(menuFile);
        setJMenuBar(menuBar);

        // PANNEAU EDIT
        textPane = new JaTextPane(jaf);
        textPane.addJaPaneListener(this);

        // disposition des PANELS
        getContentPane().add(textPane, BorderLayout.CENTER);

        // gestion de la demande d'arrét
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                sauveQuiPeut();
            }
        });

    } // end initComponents

    /**
     * terminaison : demande de sauvegarde des modifs
     */
    public void sauveQuiPeut() {
        textPane.sauveQuiPeut();
        dispose();
    } // end sauveQuiPeut()

    public void setTitre(String titre) {
        this.setTitle(titre);
    } // end setTitre

    /**
     * gestion des ACTIONS
     */
    public void actionPerformed(ActionEvent e) {
        Object objet = e.getSource();
        if ( objet == menuPosition ) {
            jac.savePosition(this, nomClasse);
            return;
        }
        if ( objet == menuFermer ) {
            sauveQuiPeut();
            return;
        }
    } // end actionPerformed

    /**
     * événement de JaPaneListener
     */
    public void jaPaneEventTitle(JaPaneEvent e) {
        Object objet = e.getSource();
        String text = e.getMessage();
        setTitre(text);
    }
    public void jaPaneEventEdit(JaPaneEvent e, boolean dansNouvelleFenetre) {
        textPane.load(e.getMessage());
    }
    public void jaPaneEventSetTexte(JaPaneEvent e) {
        textPane.setTexte(e.getMessage());
    }
    public void jaPaneEventRemove(JaPaneEvent e) {
        dispose();
    }
    public void jaPaneEventAction(JaPaneEvent e) {
    } // end

} // end class
