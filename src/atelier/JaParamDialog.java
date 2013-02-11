package atelier;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

/**
 * @author  : Philippe.BILLEROT@laposte.net
 * @version : 1.0 du 23 juillet 2000
 */
public class JaParamDialog extends JDialog implements ActionListener, ListSelectionListener {
private final static String nomClasse = "JaParamDialog";
private JaProperties    jac = null;
private JaFrame         jaf = null;
private Set             set = new TreeSet();
// MENU
private JMenuItem   menuPosition;
private JMenuItem   menuClose;
// BARRE BOUTONS
private JButton buttonSave;
// LISTE
private JList listeKeys, listeElements;
private DefaultListModel listeModeleKeys, listeModeleElements;
private JSplitPane splitPane;
private JTextField texteKey, texteElement;
// BARRE BOUTONS DE COMMANDE
private JButton buttonMaj;
private JButton buttonDel;
private JButton buttonClose;

    /**
     * Contruction de la classe
     */
    public JaParamDialog(JaFrame jaf, String titre) {
        super(jaf.getFrame(), titre, true);
        this.jaf = jaf;
        this.jac = jaf.getJaProperties();
        initComponents();
        pack();
        jac.getPosition(this, nomClasse);
        saveEnabled(false);
    } // end init

    /**
     *  Chargement des clé dans un vector dans un 1er temps
     *  la suite est dans show()
     */
    public void add(String cle) {
        if ( cle.endsWith("*") ) {
            cle = Ja.left(cle, cle.length() - 1);
            for (Enumeration ee = jac.keys(); ee.hasMoreElements(); ) {
                String key = (String)ee.nextElement();
                if ( key.startsWith(cle) ) {
                    set.add(key);
                } // endif
            } // end for
        } else {
            set.add(cle);
        } // endif
    } // end add()

    /**
     *  Tri des cles, insertion dans le tableau, affichage
     */
    public void showDialog() {
        for(Iterator it = set.iterator(); it.hasNext(); ) {
            String cle = (String)it.next();
            listeModeleKeys.addElement(cle);
            listeModeleElements.addElement(jac.getProperty(cle, ""));
        } // endif
        initSelection();
        this.setVisible(true);
    } // end show()

    /**
     * Création de l'IHM (Interface Homme Machine)
     */
    private void initComponents() {
        String methode = "initComponents";

        // MENUS
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("Fichier");

        menuPosition = new JMenuItem("Mémoriser la position et la taille de la fenêtre");
        menuPosition.addActionListener(this);
        menuFile.add(menuPosition);

        menuFile.add(new JSeparator() );

        menuClose = new JMenuItem("Fermer");
        menuClose.addActionListener(this);
        menuFile.add(menuClose);

        menuBar.add(menuFile);
        setJMenuBar(menuBar);

        // barre d'outils
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        buttonClose = new JButton(Ja.getIcon("/images/close.gif"));
        buttonClose.setToolTipText("pour fermer la fenêtre");
        buttonClose.addActionListener(this);
        toolbar.add(buttonClose);

        buttonSave = new JButton(Ja.getIcon("/images/save.gif"));
        buttonSave.setToolTipText("pour enregistrer le fichier");
        buttonSave.addActionListener(this);
        toolbar.add(buttonSave);

        // création de la zone LISTE
        listeModeleKeys = new DefaultListModel();
        listeKeys = new JList(listeModeleKeys);
        listeKeys.setFixedCellHeight(18);
        listeKeys.setFont(Ja.getFont());
        listeKeys.setForeground((Color.blue).darker());
        listeKeys.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listeKeys.setSelectedIndex(0);
        listeKeys.addListSelectionListener(this);
        JScrollPane scrollPane1 = new JScrollPane(listeKeys);

        listeModeleElements = new DefaultListModel();
        listeElements = new JList(listeModeleElements);
        listeElements.setFixedCellHeight(18);
        listeElements.setFont(Ja.getFont());
        listeElements.setForeground((Color.blue).darker());
        listeElements.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listeElements.setSelectedIndex(0);
        listeElements.addListSelectionListener(this);
        JScrollPane scrollPane2 = new JScrollPane(listeElements);

        texteKey = new JTextField();
        texteKey.setFont(Ja.getFont());
        texteKey.setForeground((Color.blue).darker());
        texteKey.addActionListener(this);

        texteElement = new JTextField();
        texteElement.setFont(Ja.getFont());
        texteElement.setForeground((Color.blue).darker());
        texteElement.addActionListener(this);

        JPanel panel1 = new JPanel(new BorderLayout());
        JPanel panel2 = new JPanel(new BorderLayout());
        panel1.add(scrollPane1, BorderLayout.CENTER);
        panel1.add(texteKey, BorderLayout.SOUTH);
        panel2.add(scrollPane2, BorderLayout.CENTER);
        panel2.add(texteElement, BorderLayout.SOUTH);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   panel1, panel2);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(Integer.parseInt(jac.getProperty(nomClasse + ".divider", "150")));

        // barre d'outils de commande
        JToolBar toolbar2 = new JToolBar();
        toolbar2.setFloatable(false);

        buttonMaj = new JButton("ajouter", Ja.getIcon("/images/listeAjout.gif"));
        buttonMaj.setToolTipText("pour mettre à jour ou ajouter une nouvelle ligne");
        buttonMaj.addActionListener(this);
        toolbar2.add(buttonMaj);

        buttonDel = new JButton("Enlever", Ja.getIcon("/images/del.gif"));
        buttonDel.setToolTipText("pour enlever la ligne");
        buttonDel.addActionListener(this);
        toolbar2.add(buttonDel);

        initSelection();

        getContentPane().add(toolbar, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);
        getContentPane().add(toolbar2, BorderLayout.SOUTH);

        // gestion de la demande d'arrét
        //setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // Finish setting up the frame, and show it.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                sauveQuiPeut();
            }
        });
    } // end initComponents

    /**
     *  initialisation de la sélection
     */
    private void initSelection() {
        if ( listeModeleKeys.getSize() > 0 ) {
            buttonDel.setEnabled(true);
            texteKey.setText(listeKeys.getSelectedValue().toString());
            texteElement.setText(listeElements.getSelectedValue().toString());
        } else {
            texteKey.setText("");
            texteElement.setText("");
            buttonDel.setEnabled(false);
        } // endif
    }

    /**
     * terminaison : demande de sauvegarde des modifs
     */
    public void sauveQuiPeut() {
        if ( buttonSave.isEnabled() ) {
            int iret = JOptionPane.showConfirmDialog(this,
            "Les paramétres ont été modifiés, voulez-vous les enregistrer ?",
            "Sauvegarde", JOptionPane.YES_NO_OPTION);
            if ( iret == JOptionPane.OK_OPTION ) buttonSave.doClick();
        } // endif
        dispose();
    } // end sauveQuiPeut()

    /**
     * gestion des ACTIONS
     */
    public void actionPerformed(ActionEvent e) {
        Object objet = e.getSource();
        if ( objet == menuPosition ) {
            savePosition();
            return;
        }
        if ( objet == menuClose || objet == buttonClose ) {
            sauveQuiPeut();
        }
        if ( objet == buttonSave ) {
            // on delete avant les clés chargés au départ dans set
            for(Iterator it = set.iterator(); it.hasNext(); ) {
                String cle = (String)it.next();
                jac.remove(cle);
            } // end for
            // création des clés qui rentent dans la liste
            int imax = listeKeys.getModel().getSize();
            for (int i=0; i < imax; i++ ) {
                listeKeys.setSelectedIndex(i);
                listeElements.setSelectedIndex(i);
                jac.put(listeKeys.getSelectedValue(), listeElements.getSelectedValue());
            } // endfor
            jac.save();
            saveEnabled(false);
        }
        if ( objet == buttonMaj || objet == texteKey || objet == texteElement ) {
            int index = listeKeys.getSelectedIndex();
            if ( listeModeleKeys.getSize() > 0 && listeKeys.getSelectedValue().toString().equals(texteKey.getText()) ) {
                // maj
                listeModeleElements.setElementAt(texteElement.getText(), index);
            } else {
                // ajout
                listeModeleKeys.addElement(texteKey.getText());
                listeModeleElements.addElement(texteElement.getText());
                index = listeModeleKeys.getSize() - 1;
            } // endif
            listeKeys.setSelectedIndex(index);
            listeElements.setSelectedIndex(index);
            listeKeys.ensureIndexIsVisible(index);
            listeElements.ensureIndexIsVisible(index);
            saveEnabled(true);
            return;
        }
        if ( objet == buttonDel ) {
            int index = listeKeys.getSelectedIndex();
            listeModeleKeys.removeElementAt(index);
            listeModeleElements.removeElementAt(index);
            if ( listeModeleKeys.getSize() < (index + 1) ) {
                if ( index > 0 ) {
                    index--;
                } // endif
            } // endif
            if ( listeModeleKeys.getSize() > 0 ) {
                listeKeys.setSelectedIndex(index);
                listeElements.setSelectedIndex(index);
                listeKeys.ensureIndexIsVisible(index);
                listeElements.ensureIndexIsVisible(index);
            } else {
                buttonDel.setEnabled(false);
            } // endif
            saveEnabled(true);
            return;
        }
    } // end actionPerformed

    /**
     * gestion des évenements du text area
     */
    public void valueChanged(ListSelectionEvent e) {
        Object objet = e.getSource();
        if ( objet == listeKeys ) {
            listeElements.setSelectedIndex(listeKeys.getSelectedIndex());
            listeElements.ensureIndexIsVisible(listeKeys.getSelectedIndex());
            texteElement.setText(listeElements.getSelectedValue().toString());
        }
        if ( objet == listeElements ) {
            listeKeys.setSelectedIndex(listeElements.getSelectedIndex());
            listeKeys.ensureIndexIsVisible(listeElements.getSelectedIndex());
            texteKey.setText(listeKeys.getSelectedValue().toString());
        }
    }

    /**
     * enabled disabled du bouton et menu Sauvegarder
     */
    private void saveEnabled(boolean b) {
        buttonSave.setEnabled(b);
    } // endif

    /**
     * demande de sauvegarde de la position du séparateur
     */
    public void savePosition() {
        jac.setProperty(nomClasse + ".divider", "" + splitPane.getDividerLocation());
        jac.savePosition(this, nomClasse);
    } // endif

} // end class

