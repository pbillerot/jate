package atelier;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

/**
 * @author  : Philippe.BILLEROT@laposte.net
 * @version : 1.0 du 22 mai 2000
 */
public class JaListPane extends JPanel implements ActionListener, ListSelectionListener, MouseListener {
private final static String nomClasse = "JaListPane";
private boolean bModif = false;
private String  fichier = "";
private File  repertoire = null;
private JaFrame      jaf = null;
private JaProperties jac = null;
private JaProperties jap = new JaProperties();
// MENU POPUP
private JPopupMenu  menuPopup;
//private JMenuItem   menuNew;
//private JMenuItem   menuOpen;
//private JMenuItem   menuSave;
//private JMenuItem   menuSaveAs;
private JMenuItem   menuDel;
private JMenuItem   menuEdit;
private JMenuItem   menuEditNew;
// BARRE BOUTONS
private JButton buttonSave;
// LISTE
private JList       liste;
private DefaultListModel listeModele;
private JTextField  texteListe;
private JButton     buttonMaj;
private JButton     buttonDel;
private JButton     buttonEdit;
private JButton     buttonEditNew;

    public JaListPane(JaFrame pjaf, JaProperties pjac) {
        super(new BorderLayout());
        this.jaf = pjaf;
        this.jac = pjac;
        initComponents();
    	initSelection();        
        saveEnabled(false);
        texteListe.requestFocus();
    } // end init

    public void load() {
    	this.jac.keysToList(this.listeModele);
    	initSelection();
    	saveEnabled(false);
    	this.texteListe.requestFocus();
    	setTitre();
    }
    public void save() {
    	this.jac.saveFromListe("fichier", this.liste);
    	saveEnabled(false);
    }

    /**
     * Création de l'IHM (Interface Homme Machine)
     */
    private void initComponents() {
        String methode = "initComponents";

        // MENU POPUP
        menuPopup = new JPopupMenu();

        menuEdit = new JMenuItem("Ouvrir le fichier");
        menuEdit.addActionListener(this);
        menuPopup.add(menuEdit);

        menuEditNew = new JMenuItem("Ouvrir le fichier dans une nouvelle fenêtre");
        menuEditNew.addActionListener(this);
        menuPopup.add(menuEditNew);

        //menuDel = new JMenuItem("enlever la ligne et supprimer le fichier");
        menuDel = new JMenuItem("enlever la ligne");
        menuDel.addActionListener(this);
        menuPopup.add(menuDel);

//        menuPopup.add(new JSeparator() );
//
//        menuNew = new JMenuItem("nouvelle liste");
//        menuNew.addActionListener(this);
//        menuPopup.add(menuNew);
//
//        menuOpen = new JMenuItem("Ouvrir un autre fichier liste...");
//        menuOpen.addActionListener(this);
//        menuPopup.add(menuOpen);
//
//        menuSave = new JMenuItem("Enregistrer le fichier");
//        menuSave.addActionListener(this);
//        menuSave.setAccelerator(KeyStroke.getKeyStroke('S', Event.CTRL_MASK));
//        menuPopup.add(menuSave);
//
//        menuSaveAs = new JMenuItem("Enregistrer le fichier sous...");
//        menuSaveAs.addActionListener(this);
//        menuPopup.add(menuSaveAs);

        // PANNEAU LISTE
        // la barre d'outils
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        buttonSave = new JButton(Ja.getIcon("/images/save.gif"));
        buttonSave.setToolTipText("enregistrer la liste des fichiers");
        buttonSave.addActionListener(this);
        toolbar.add(buttonSave);

        // la liste listeModele liste
        listeModele = new DefaultListModel();
        jac.groupeToList("fichier", this.listeModele);
        liste = new JList(listeModele);
        liste.setFont(Ja.getFont());
        liste.setForeground((Color.blue).darker());
        liste.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        liste.addListSelectionListener(this);
        liste.addMouseListener(this);
        liste.addKeyListener(new ListKeyListener());
        JScrollPane scrollPaneListe = new JScrollPane(liste);

        // le champ d'édition texteListe
        texteListe = new JTextField();
        texteListe.setFont(Ja.getFont());
        texteListe.setForeground((Color.blue).darker());
        texteListe.addActionListener(this);

        // la barre de commandes
        JToolBar toolbarListe = new JToolBar();
        toolbarListe.setFloatable(false);
        buttonMaj = new JButton("", Ja.getIcon("/images/listeAjout.gif"));
        buttonMaj.setToolTipText("ajouter une ligne");
        buttonMaj.addActionListener(this);
        toolbarListe.add(buttonMaj);

        buttonDel = new JButton("", Ja.getIcon("/images/del.gif"));
        buttonDel.setToolTipText("enlever la ligne");
        buttonDel.addActionListener(this);
        toolbarListe.add(buttonDel);

        buttonEdit = new JButton("", Ja.getIcon("/images/document.gif"));
        buttonEdit.setToolTipText("ouvrir le fichier");
        buttonEdit.addActionListener(this);
        toolbarListe.add(buttonEdit);

        buttonEditNew = new JButton("", Ja.getIcon("/images/new.gif"));
        buttonEditNew.setToolTipText("ouvrir le fichier dans une nouvelle fenêtre");
        buttonEditNew.addActionListener(this);
        toolbarListe.add(buttonEditNew);

        // regroupement de texteListe et toolbarListe
        JPanel editButton = new JPanel(new BorderLayout());
        editButton.add(texteListe, BorderLayout.NORTH);
        editButton.add(toolbarListe, BorderLayout.SOUTH);

        // regroupement
        add(toolbar, BorderLayout.NORTH);
        add(scrollPaneListe, BorderLayout.CENTER);
        add(editButton, BorderLayout.SOUTH);

    } // end initComponents

    /**
     * terminaison : demande de sauvegarde des modifs
     */
    public void sauveQuiPeut() {
        if ( buttonSave.isEnabled() ) {
            int iret = JOptionPane.showConfirmDialog(this,
            "Le fichier " + fichier + " a été modifié, voulez-vous l'enregistrer ?",
            "Sauvegarde", JOptionPane.YES_NO_OPTION);
            if ( iret == JOptionPane.OK_OPTION ) buttonSave.doClick();
        } // endif
    } // end sauveQuiPeut()

    /**
     *  initialisation de la sélection
     */
    private void initSelection() {
        if ( listeModele.getSize() > 0 ) {
            liste.setSelectedIndex(0);
            texteListe.setText(liste.getSelectedValue().toString());
            //buttonDel.setEnabled(true);
            //buttonEdit.setEnabled(true);
        } else {
            buttonDel.setEnabled(false);
            buttonEdit.setEnabled(false);
        } // endif
    }

    /**
     * gestion des évenements de la liste
     */
    public void valueChanged(ListSelectionEvent e) {
        Object objet = e.getSource();
        if ( listeModele.getSize() > 0 && liste.getSelectedIndex() != -1 ) {
            buttonDel.setEnabled(true);
            String fic = liste.getSelectedValue().toString();
            int posPoint = fic.lastIndexOf(".");
            if ( posPoint != -1 ) {
                String ext = Ja.mid(fic, posPoint + 2);
                String app = jac.getProperty("extension." + ext);
                if ( app != null && app.length() > 0 ) {
                    if ( app.startsWith("nul") ) {
                        buttonEdit.setEnabled(false);
                        buttonEditNew.setEnabled(false);
                    } else {
                        buttonEdit.setEnabled(true);
                        buttonEditNew.setEnabled(false);
                    } // endif
                } // endif
            } else {
                buttonEdit.setEnabled(true);
                buttonEditNew.setEnabled(true);
            } // endif
            texteListe.setText(liste.getSelectedValue().toString());
        } else {
            texteListe.setText("");
        } // endif
    }

    /**
     * enabled disabled du bouton et menu Sauvegarder
     */
    private void saveEnabled(boolean b) {
        buttonSave.setEnabled(b);
        //menuSave.setEnabled(b);
    } // endif

    /**
     * gestion des ACTIONS
     */
    public void actionPerformed(ActionEvent e) {
        Object objet = e.getSource();
//        if ((objet == this.menuSave) || (objet == this.buttonSave)) {
//            this.jac.saveFromListe("fichier", this.liste);
//            saveEnabled(false);
//        }
        if (objet == this.buttonSave) {
            this.jac.saveFromListe("fichier", this.liste);
            saveEnabled(false);
        }
        if ( objet == buttonMaj || objet == texteListe ) {
            int index = liste.getSelectedIndex();
            String texte = texteListe.getText();
            if ( listeModele.getSize() > 0 && liste.getSelectedValue().toString().equals(texte) ) {
                // maj
            } else {
                // ajout
                listeModele.addElement(texte);
                //index = listeModele.getSize() - 1;
                initSelection();
            } // endif
            Ja.sortList(listeModele);
            liste.setSelectedValue(texte, true);
            saveEnabled(true);
            return;
        }
        if ( objet == buttonDel ) {
            int index = liste.getSelectedIndex();
            listeModele.removeElementAt(index);
            if ( listeModele.getSize() < (index + 1) ) {
                if ( index > 0 ) {
                    index--;
                } // endif
            } // endif
            if ( listeModele.getSize() > 0 ) {
                liste.setSelectedIndex(index);
                liste.ensureIndexIsVisible(index);
            } // endif
            initSelection();
            saveEnabled(true);
            return;
        }
        if ( objet == menuDel ) {
            // = buttonDel et suppression du fichier
            try {
//                File file = new File(liste.getSelectedValue().toString());
//                file.delete();
//                file = null;
                buttonDel.doClick();
            }catch (Exception exc ) {
                Ja.debug(290, nomClasse, exc);
            } // end try
        } // endif
//        if ( objet == menuNew ) {
//            listeModele.clear();
//            texteListe.setText("");
//            fichier = "";
//            setTitre();
//        } // endif

        if ( objet == buttonEdit || objet == menuEdit ) {
            fichierEdit(liste.getSelectedValue().toString());
        } // endif
        if ( objet == buttonEditNew || objet == menuEditNew ) {
            fichierNewEdit(liste.getSelectedValue().toString());
        } // endif
    } // end actionPerformed

    /**
     * Mouse Listener
     */
    public void mousePressed(MouseEvent e) {
    }
    public void mouseReleased(MouseEvent e) {
        if ( e.getClickCount() > 0 ) {
            this.fichierEdit(liste.getSelectedValue().toString());
        } // endif
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }
    public void mouseClicked(MouseEvent e) {
        if ( SwingUtilities.isRightMouseButton(e) ) {
            menuPopup.show(e.getComponent(), e.getX(), e.getY());
        } // endif
    }

    /**
     * gestion du listener
     */
    protected Vector listeners = new Vector();
    public void addJaPaneListener(JaPaneListener listener) {
        listeners.addElement(listener);
    }
    public void removeJaPaneListener(JaPaneListener listener) {
        listeners.remove(listener);
    }
    protected void fireJaPaneEventTitle(String mess ) {
        JaPaneEvent e = new JaPaneEvent(this, mess);
        for (int i=0, n = listeners.size(); i < n; i++ ) {
            JaPaneListener listener = (JaPaneListener)listeners.elementAt(i);
            listener.jaPaneEventTitle(e);
        } // end for
    }
    protected void fireJaPaneEventEdit(String mess) {
        JaPaneEvent e = new JaPaneEvent(this, mess);
        for (int i=0, n = listeners.size(); i < n; i++ ) {
            JaPaneListener listener = (JaPaneListener)listeners.elementAt(i);
            listener.jaPaneEventEdit(e, false);
        } // end for
    }
    protected void fireJaPaneEventNewEdit(String mess) {
        JaPaneEvent e = new JaPaneEvent(this, mess);
        for (int i=0, n = listeners.size(); i < n; i++ ) {
            JaPaneListener listener = (JaPaneListener)listeners.elementAt(i);
            listener.jaPaneEventEdit(e, true);
        } // end for
    }
    protected void fireJaPaneEventAction(String objetCommande, String texteCommande, 
    		String titreCommande, String ligneCommande ) {
    	JaPaneEvent e = new JaPaneEvent(this, objetCommande, texteCommande, titreCommande, 
    			ligneCommande);
    	for (int i=0, n = listeners.size(); i < n; i++ ) {
    		JaPaneListener listener = (JaPaneListener)listeners.elementAt(i);
    		listener.jaPaneEventAction(e);
    	} // end for
    }
    private void setTitre() {
        fireJaPaneEventTitle(fichier);
    } // end setTitre

    private void fichierEdit(String fic) {
        // ajout du préfixe répertoire
        String path;
        if ( fic.charAt(1) != ':' && fic.charAt(0) != '/' ) {
            String dir = new File("").getAbsolutePath();
            if ( dir.endsWith(System.getProperty("file.separator")) ) {
                path = dir + fic;
            } else {
                path = dir + System.getProperty("file.separator") + fic;
            } // endif
        } else {
            path = fic;
        } // endif
        // recherche de l'extension du fichier fic
        int posPoint = path.lastIndexOf(".");
        if ( posPoint != -1 ) {
            String extension = Ja.mid(path, posPoint + 2);
            for (int i = 1; ; i++ ) {
                String titre = jac.getProperty("extension." + i + "." + JaFrame.CLEVAR);
                if ( titre != null ) {
                	if ( titre.equalsIgnoreCase(extension) ) {
                        String commande = jac.getProperty("extension." + i + "." + JaFrame.CLEVAL, "");
                        if ( commande != null && commande.length() > 0 ) {
                            String cmd = Ja.remplace(commande, "%1", path);
                            if ( ! cmd.startsWith("nul") ) {
                            	fireJaPaneEventAction("Lanceur", "", "Lanceur", cmd);
                            } // endif
                        } else {
                            fireJaPaneEventEdit(liste.getSelectedValue().toString());
                        } // endif
                	} else {
                        fireJaPaneEventEdit(liste.getSelectedValue().toString());
                	} // endif
                } else {
                    fireJaPaneEventEdit(liste.getSelectedValue().toString());
                    break;
                } // endif
            } // end for
        } else {
            fireJaPaneEventEdit(liste.getSelectedValue().toString());
        } // endif
    } // endif
    private void fichierNewEdit(String fic) {
        // ajout du préfixe répertoire
        String path;
        if ( fic.charAt(1) != ':' && fic.charAt(0) != '/' ) {
            String dir = new File("").getAbsolutePath();
            if ( dir.endsWith(System.getProperty("file.separator")) ) {
                path = dir + fic;
            } else {
                path = dir + System.getProperty("file.separator") + fic;
            } // endif
        } else {
            path = fic;
        } // endif
        // recherche de l'extension du fichier fic
        int posPoint = path.lastIndexOf(".");
        if ( posPoint != -1 ) {
            String extension = Ja.mid(path, posPoint + 2);
            for (int i = 1; ; i++ ) {
                String titre = jac.getProperty("extension." + i + "." + JaFrame.CLEVAR);
                if ( titre != null ) {
                	if ( titre.equalsIgnoreCase(extension) ) {
                        String commande = jac.getProperty("extension." + i + "." + JaFrame.CLEVAL, "");
                        if ( commande != null && commande.length() > 0 ) {
                            String cmd = Ja.remplace(commande, "%1", path);
                            if ( ! cmd.startsWith("nul") ) {
                            	fireJaPaneEventAction("Lanceur", "", "Lanceur", cmd);
                            } // endif
                        } else {
                            fireJaPaneEventNewEdit(liste.getSelectedValue().toString());
                        } // endif
                	} else {
                        fireJaPaneEventNewEdit(liste.getSelectedValue().toString());
                	} // endif
                } else {
                	// 
                    break;
                } // endif
            } // end for
        } else {
            fireJaPaneEventNewEdit(liste.getSelectedValue().toString());
        } // endif
    } // endif

    /**
     * gestion du filtre dans le gestionnaire de fichier
     */
    protected static class FiltreFichier extends javax.swing.filechooser.FileFilter {

        public boolean accept(File f) {
            String extension = getExtension(f);
            if ( extension != null ) {
                if (extension.equals("lst") ) {
                    return true;
                } else {
                    return false;
                } // endif
            } // endif
            return false;
        }

        public String getDescription() {
            return "fichiers liste d'objets (*.lst)";
        }

        public String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 &&  i < s.length() - 1) {
                ext = s.substring(i+1).toLowerCase();
            }
            return ext;
        }
    } // end class FiltreFichier

	public class ListKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
		// Check for key characters.
		if ( KeyEvent.getKeyText(e.getKeyCode()).equalsIgnoreCase("s") && e.isControlDown() ) {
			buttonSave.doClick();
		} // endif
	} // end class
}
} // end JaListPane

