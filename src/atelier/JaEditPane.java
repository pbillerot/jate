package atelier;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import java.util.*;

/**
 * Panneau Editeur, une classe majeure dans l'atelier.
 * @author  : Philippe.BILLEROT@laposte.net
 * @version : 1.0 du 22 mai 2000
 */
public class JaEditPane extends JPanel implements Runnable, ActionListener, DocumentListener, ItemListener, MouseListener, UndoableEditListener, KeyListener {
private final static String nomClasse = "JaEditPane";
private JaFrame         jaf = null;
private JaProperties    jac;
private boolean bModif = false;
private boolean AVEC_FTP = false;
private final static int     FTP_DIR = 1;
private final static int     FTP_SUB = 2;
private int     FTP_TYPE_COMMANDE;
private Thread  thread = null;
private String  fichier = "";
static final JTextComponent.KeyBinding[] defaultBindings = {
     new JTextComponent.KeyBinding(
       KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK),
       DefaultEditorKit.copyAction),
     new JTextComponent.KeyBinding(
       KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.CTRL_MASK),
       DefaultEditorKit.copyAction),
     new JTextComponent.KeyBinding(
       KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK),
       DefaultEditorKit.pasteAction),
     new JTextComponent.KeyBinding(
       KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.SHIFT_MASK),
       DefaultEditorKit.pasteAction),
     new JTextComponent.KeyBinding(
       KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK),
       DefaultEditorKit.cutAction),
     new JTextComponent.KeyBinding(
       KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_MASK),
       DefaultEditorKit.cutAction),
   };
protected UndoManager undo = new UndoManager();

// MENU POPUP
private JPopupMenu  menuPopup;
private JMenuItem   menuCut;
private JMenuItem   menuCopy;
private JMenuItem   menuPaste;
private JMenuItem   menuDel;
private JMenuItem   menuClose;
private JMenuItem   menuNew;
private JMenuItem   menuOpen;
private JMenuItem   menuPrint;
private JMenuItem   menuSave;
private JMenuItem   menuSaveAs;
private JMenuItem   menuSelectAll;
private JMenuItem   menuAnnuler;
private JMenuItem   menuRepeter;
// BARRE BOUTONS
private JButton     buttonSave;
private JButton     buttonPrint;
private JButton     buttonCut;
private JButton     buttonCopy;
private JButton     buttonPaste;
private JButton     buttonSelectAll;
private JButton     buttonDel;
private JButton     buttonClose;
private JButton     buttonAnnuler;
private JButton     buttonRepeter;
private JCheckBox   checkRetourLigne;
private JButton     buttonDebut;
private JButton     buttonFin;

// EDITEUR
private JTextArea   textArea;
private JScrollPane scrollPane;
private JViewport   viewPort;
CaretListenerLabel  caretListenerLabel;
// BARRE RECHERCHE
private JCheckBox   checkCasse;
private JButton     buttonSearch;
private JButton     buttonReplace;
private JButton     buttonReplaceEnd;
private JTextField  textSearch;
private JTextField  textReplace;

    public JaEditPane(JaFrame pjaf) {
        super(new BorderLayout());
        this.jaf = pjaf;
        this.jac = jaf.getJaProperties();
        if ( ! jac.getParam("jate","ftpOpen", "").equals("") ) AVEC_FTP = true;
        initComponents();
        this.jac.getPosition(this, nomClasse);
        buttonEnabled();
        textArea.requestFocus();
        super.setVisible(true);
    } // end init

    public void load(String fichier) {
        try {
            sauveQuiPeut();
            this.fichier = fichier;
            textArea.setText("");
            if ( ! fichier.equals("") ) Ja.loadTextArea(textArea, fichier, jaf);
            undo.discardAllEdits();
            buttonEnabled();
            textArea.setCaretPosition(0); // positionnement au début du texte
            textArea.requestFocus();
            setTitre();
        } catch (Exception e) {
            jaf.log(110, nomClasse, e);
        } // end try
    }

    public void setTexte(String texte) {
        try {
            sauveQuiPeut();
            textArea.setText(texte);
            //undo.discardAllEdits();
            buttonEnabled();
            textArea.setCaretPosition(0); // positionnement au début du texte
            textArea.requestFocus();
        } catch (Exception e) {
            jaf.log(120, nomClasse, e);
        } // end try
    }

    /**
     * rendre le texte
     */
    public String getTexte() {
        return textArea.getText();
    }

    public void saveAs(String fichier) {
        this.fichier = fichier;
        Ja.saveTextArea(textArea, fichier, jaf);
        undo.discardAllEdits();
        buttonEnabled();
        setTitre();
    }

    public boolean saveAs() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(fichier));
        chooser.setDialogTitle("Enregistrer sous...");
        chooser.setCurrentDirectory(new File("."));
        int iret = chooser.showSaveDialog(this);
        if ( iret == JFileChooser.CANCEL_OPTION ) {
            return false;
        } // endif
        if ( iret == JFileChooser.APPROVE_OPTION ) {
            fichier = chooser.getSelectedFile().getAbsolutePath();
            saveAs(fichier);
        } // endif
        return true;
    }

    public String getNomFichier() {
        return fichier;
    } // end

    /**
     * terminaison : demande de sauvegarde des modifs
     */
    public boolean sauveQuiPeut() {
        if ( buttonSave.isEnabled() ) {
            int iret = JOptionPane.showConfirmDialog(this,
            "Le fichier " + fichier + " a été modifié, voulez-vous l'enregistrer ?",
            "Sauvegarde", JOptionPane.YES_NO_OPTION);
            if ( fichier.equals("") ) {
                if ( iret == JOptionPane.OK_OPTION ) return saveAs();
            } else {
                if ( iret == JOptionPane.OK_OPTION ) buttonSave.doClick();
            } // endif
        } // endif
        return true;
    } // end sauveQuiPeut()

    /**
     * Création de l'IHM (Interface Homme Machine)
     */
    private void initComponents() {
        String methode = "initComponents";

        // MENU POPUP
        menuPopup = new JPopupMenu();

        menuCut = new JMenuItem("couper");
        menuCut.addActionListener(this);
        menuPopup.add(menuCut);

        menuCopy = new JMenuItem("copier");
        menuCopy.addActionListener(this);
        menuPopup.add(menuCopy);

        menuPaste = new JMenuItem("coller");
        menuPaste.addActionListener(this);
        menuPopup.add(menuPaste);

        menuSelectAll = new JMenuItem("tout sélectionner");
        menuSelectAll.addActionListener(this);
        menuPopup.add(menuSelectAll);

        menuDel = new JMenuItem("effacer la sélection");
        menuDel.addActionListener(this);
        menuPopup.add(menuDel);

        menuPopup.add(new JSeparator() );

        menuAnnuler = new JMenuItem("Annuler");
        menuAnnuler.addActionListener(this);
        menuPopup.add(menuAnnuler);

        menuRepeter = new JMenuItem("Répéter");
        menuRepeter.addActionListener(this);
        menuPopup.add(menuRepeter);

        menuPopup.add(new JSeparator() );

        boolean actionTrouvee = false;
        for (int i = 1; ; i++ ) {
            String titre = jac.getProperty("action." + i + "." + JaFrame.CLEVAR);
            if ( titre != null ) {
                actionTrouvee = true;
                if ( titre.startsWith("-") ) {
                    menuPopup.add(new JSeparator() );
                } else {
                    JMenuItem menu = new JMenuItem(titre, Ja.getIcon("/images/execute.gif"));
                    menu.setActionCommand(jac.getProperty("action." + i + "." + JaFrame.CLEVAL, ""));
                    menu.addActionListener(this);
                    menuPopup.add(menu);
                } // endif
            } else {
                break;
            } // endif
        } // end for
        if ( actionTrouvee ) menuPopup.add(new JSeparator() );

        menuClose = new JMenuItem("Fermer la fenêtre");
        menuClose.addActionListener(this);
        menuPopup.add(menuClose);

        menuNew = new JMenuItem("Nouveau");
        menuNew.addActionListener(this);
        menuPopup.add(menuNew);

        menuOpen = new JMenuItem("Ouvrir un fichier...");
        menuOpen.addActionListener(this);
        menuPopup.add(menuOpen);

        //if ( ! jac.getProperty("path.acrobat", "").equals("")) {
            menuPrint = new JMenuItem("Imprimer le fichier");
            menuPrint.addActionListener(this);
            menuPopup.add(menuPrint);
        //} // endif

        menuSave = new JMenuItem("Enregistrer le fichier");
        menuSave.addActionListener(this);
        menuSave.setAccelerator(KeyStroke.getKeyStroke('S', Event.CTRL_MASK));
        menuPopup.add(menuSave);

        menuSaveAs = new JMenuItem("Enregistrer le fichier sous...");
        menuSaveAs.addActionListener(this);
        menuPopup.add(menuSaveAs);

        // barre d'outils
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        buttonClose = new JButton(Ja.getIcon("/images/close.gif"));
        buttonClose.setToolTipText("fermer la fenétre");
        buttonClose.addActionListener(this);
        toolbar.add(buttonClose);

        buttonSave = new JButton(Ja.getIcon("/images/save.gif"));
        buttonSave.setToolTipText("enregistrer le fichier (ctrl + s)");
        buttonSave.addActionListener(this);
        toolbar.add(buttonSave);

        //if ( ! jac.getProperty("print.titre", "").equals("") || ! jac.getProperty("path.acrobat", "").equals("")) {
            buttonPrint = new JButton(Ja.getIcon("/images/print.gif"));
            buttonPrint.setToolTipText(jac.getProperty("print.titre", ""));
            buttonPrint.addActionListener(this);
            toolbar.add(buttonPrint);
            toolbar.add(new JToolBar.Separator());
        //} // endif

        buttonAnnuler = new JButton(Ja.getIcon("/images/undo.gif"));
        buttonAnnuler.setToolTipText("défaire (ctrl + z)");
        buttonAnnuler.addActionListener(this);
        toolbar.add(buttonAnnuler);

        buttonRepeter = new JButton(Ja.getIcon("/images/redo.gif"));
        buttonRepeter.setToolTipText("refaire  (ctrl + y)");
        buttonRepeter.addActionListener(this);
        toolbar.add(buttonRepeter);

        toolbar.add(new JToolBar.Separator());

        buttonCut = new JButton(Ja.getIcon("/images/cut.gif"));
        buttonCut.setToolTipText("couper");
        buttonCut.addActionListener(this);
        toolbar.add(buttonCut);

        buttonCopy = new JButton(Ja.getIcon("/images/copy.gif"));
        buttonCopy.setToolTipText("copier");
        buttonCopy.addActionListener(this);
        toolbar.add(buttonCopy);

        buttonPaste = new JButton(Ja.getIcon("/images/paste.gif"));
        buttonPaste.setToolTipText("coller");
        buttonPaste.addActionListener(this);
        toolbar.add(buttonPaste);
        toolbar.add(new JToolBar.Separator());

        buttonSelectAll = new JButton(Ja.getIcon("/images/selectAll.gif"));
        buttonSelectAll.setToolTipText("tout sélectionner");
        buttonSelectAll.addActionListener(this);
        toolbar.add(buttonSelectAll);

        buttonDel = new JButton(Ja.getIcon("/images/del.gif"));
        buttonDel.setToolTipText("effacer le texte sélectionné");
        buttonDel.addActionListener(this);
        toolbar.add(buttonDel);

        toolbar.add(new JToolBar.Separator());

        checkRetourLigne = new JCheckBox("retour ligne", false);
        checkRetourLigne.setFont(Ja.getFontBouton());
        checkRetourLigne.addItemListener(this);
        toolbar.add(checkRetourLigne);

        caretListenerLabel = new CaretListenerLabel("lig:col[0:0]");
        caretListenerLabel.setFont(Ja.getFont());
        caretListenerLabel.setForeground(Color.black);
        toolbar.add(caretListenerLabel);

        toolbar.addMouseListener(this);

        this.add(toolbar, BorderLayout.NORTH);

        // création de la zone EDITEUR
        textArea = new JTextArea();
        textArea.setFont(Ja.getFont());
        textArea.setLineWrap(false);
        textArea.setWrapStyleWord(true);
        textArea.setForeground((Color.blue).darker());
        textArea.getDocument().addDocumentListener(this);
        textArea.getDocument().addUndoableEditListener(this);
        textArea.addMouseListener(this);
        textArea.addKeyListener(this);
        textArea.addCaretListener(caretListenerLabel);
        JTextComponent.loadKeymap(textArea.getKeymap(), defaultBindings, textArea.getActions());        

        JScrollPane scrollPane = new JScrollPane();
        JViewport viewPort = scrollPane.getViewport();
        viewPort.add(textArea);
        this.add(scrollPane, BorderLayout.CENTER);

        // création de la barre SEARCH
        JToolBar barreSearch = new JToolBar();
        barreSearch.setFloatable(false);

        checkCasse = new JCheckBox("respecter la casse ", false);
        checkCasse.addItemListener(this);
        checkCasse.setFont(Ja.getFontBouton());
        barreSearch.add(checkCasse);
        barreSearch.addMouseListener(this);

        textSearch = new JTextField(15);
        textSearch.setFont(Ja.getFont());
        textSearch.setForeground((Color.blue).darker());
        barreSearch.add(textSearch);
        barreSearch.add(new JToolBar.Separator());
        JTextComponent.loadKeymap(textSearch.getKeymap(), defaultBindings, textSearch.getActions());

        buttonDebut = new JButton(Ja.getIcon("/images/haut.gif"));
        buttonDebut.setToolTipText("se positionner au début du texte");
        buttonDebut.addActionListener(this);
        barreSearch.add(buttonDebut);

        buttonFin = new JButton(Ja.getIcon("/images/bas.gif"));
        buttonFin.setToolTipText("se positionner à la fin du texte");
        buttonFin.addActionListener(this);
        barreSearch.add(buttonFin);

        buttonSearch = new JButton("Rechercher", Ja.getIcon("/images/find.gif"));
        buttonSearch.setToolTipText("rechercher le texte");
        buttonSearch.setFont(Ja.getFontBouton());
        buttonSearch.addActionListener(this);
        barreSearch.add(buttonSearch);
        barreSearch.add(new JToolBar.Separator());

        textReplace = new JTextField(15);
        textReplace.setFont(Ja.getFont());
        textReplace.setForeground((Color.blue).darker());
        barreSearch.add(textReplace);
        barreSearch.add(new JToolBar.Separator());

        buttonReplace = new JButton("Remplacer", Ja.getIcon("/images/replace.gif"));
        buttonReplace.setFont(Ja.getFontBouton());
        buttonReplace.setToolTipText("remplacer le texte et rechercher le suivant");
        buttonReplace.addActionListener(this);
        barreSearch.add(buttonReplace);
        barreSearch.add(new JToolBar.Separator());

        buttonReplaceEnd = new JButton("Fin", Ja.getIcon("/images/replace.gif"));
        buttonReplaceEnd.setFont(Ja.getFontBouton());
        buttonReplaceEnd.setToolTipText("remplacer jusqu'à la fin du texte");
        buttonReplaceEnd.addActionListener(this);
        barreSearch.add(buttonReplaceEnd);

        this.add(barreSearch, BorderLayout.SOUTH);

    } // end initComponents

    public void itemStateChanged(ItemEvent e) {
        Object objet = e.getItemSelectable();
        if ( objet == checkRetourLigne) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                textArea.setLineWrap(false);
            } else {
                textArea.setLineWrap(true);
            } // endif
            textArea.requestFocus();
        } // endif
    } // end itemStateChanged

    /**
     * gestion des ACTIONS
     */
    public void actionPerformed(ActionEvent e) {
        Object objet = e.getSource();
        if ( objet == buttonClose || objet == menuClose ) {
            if ( sauveQuiPeut() ) {
                fireJaPaneEventRemove(fichier);
            } // endif
            return;
        } // endif
        if ( objet == buttonAnnuler || objet == menuAnnuler ) {
            undo.undo();
            textArea.requestFocus();
            return;
        } // endif
        if ( objet == buttonRepeter || objet == menuRepeter ) {
            undo.redo();
            textArea.requestFocus();
            return;
        } // endif
        if ( objet == buttonCut || objet == menuCut ) {
            textArea.cut();
            textArea.requestFocus();
            return;
        } // endif
        if ( objet == buttonCopy || objet == menuCopy ) {
            textArea.copy();
            textArea.requestFocus();
            return;
        } // endif
        if ( objet == buttonPaste || objet == menuPaste ) {
            textArea.paste();
            textArea.requestFocus();
            return;
        } // endif
        if ( objet == buttonSelectAll || objet == menuSelectAll ) {
            textArea.selectAll();
            textArea.requestFocus();
            return;
        } // endif
        if ( objet == buttonDel || objet == menuDel ) {
            textArea.replaceSelection("");
            textArea.requestFocus();
            return;
        } // endif
        if ( objet == buttonSave || objet == menuSave ) {
            if ( fichier.equals("") ) {
                saveAs();
            } else {
                saveAs(fichier);
            } // endif
            textArea.requestFocus();
            return;
        } // endif
        if ( objet == buttonPrint || objet == menuPrint ) {
            new JaPrintToPdf(fichier, jaf);
            return;
        } // endif
        if ( objet == menuOpen ) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Ouvrir un fichier...");
            chooser.setCurrentDirectory(new File("."));
            int iret = chooser.showOpenDialog(this);
            if ( iret == JFileChooser.APPROVE_OPTION ) {
                fichier = chooser.getSelectedFile().getAbsolutePath();
                load(fichier);
                setTitre();
            } // endif
            return;
        } // endif
        if ( objet == menuNew ) {
            if ( sauveQuiPeut() ) {
                fichier = "";
                textArea.setText("");
                undo.discardAllEdits();
                buttonEnabled();
                setTitre();
            } // endif
            return;
        } // endif
        if ( objet == menuSaveAs ) {
            saveAs();
            return;
        } // endif
        if ( objet == buttonDebut ) {
            textArea.setCaretPosition(0);
            textArea.requestFocus();
            return;
        } // endif
        if ( objet == buttonFin ) {
            int len = textArea.getDocument().getLength();
            if ( len > 0 ) textArea.setCaretPosition(len - 1);
            textArea.requestFocus();
            return;
        } // endif

        if ( objet == buttonSearch ) {
            search();
            return;
        } // endif
        if ( objet == buttonReplace ) {
            replaceSearch();
            return;
        } // endif
        if ( objet == buttonReplaceEnd ) {
            replaceEnd();
            return;
        } // endif
        fireJaPaneEventAction(fichier, textArea.getText(), ((AbstractButton)objet).getText(),
           ((AbstractButton)objet).getActionCommand());
    } // end actionPerformed

    /**
     * gestion des évenements du text area DocumentListener
     */
    public void changedUpdate(DocumentEvent e) {
        buttonEnabled();
    }
    public void insertUpdate(DocumentEvent e) {
        buttonEnabled();
    }
    public void removeUpdate(DocumentEvent e) {
        buttonEnabled();
    }

    /**
     * Mouse Listener
     */
    public void mousePressed(MouseEvent e) {
    }
    public void mouseReleased(MouseEvent e) {
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
     * gestion des évenements UndoableEditListener
     */
    public void undoableEditHappened(UndoableEditEvent e) {
        UndoableEdit edit = e.getEdit();
        undo.addEdit(edit);
        buttonEnabled();
    }

    /**
     * enabled disabled des boutons undo redo
     */
    private void buttonEnabled() {
        if ( undo.canUndo() ) {
            buttonAnnuler.setEnabled(true);
            menuAnnuler.setEnabled(true);
            buttonSave.setEnabled(true);
            menuSave.setEnabled(true);
        } else {
            buttonAnnuler.setEnabled(false);
            menuAnnuler.setEnabled(false);
            buttonSave.setEnabled(false);
            menuSave.setEnabled(false);
        } // endif
        if ( undo.canRedo() ) {
            buttonRepeter.setEnabled(true);
            menuRepeter.setEnabled(true);
        } else {
            buttonRepeter.setEnabled(false);
            menuRepeter.setEnabled(false);
        } // endif
    } // end buttonEnabled

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
    protected void fireJaPaneEventRemove(String mess ) {
        JaPaneEvent e = new JaPaneEvent(this, mess);
        for (int i=0, n = listeners.size(); i < n; i++ ) {
            JaPaneListener listener = (JaPaneListener)listeners.elementAt(i);
            listener.jaPaneEventRemove(e);
        } // end for
    }
    protected void fireJaPaneEventAction(String objetCommande, String texteCommande, 
    										String titreCommande, String ligneCommande ) {
		JaPaneEvent e = null;
    	if ( ligneCommande.indexOf("%2") != -1 ) {
    		// recup de la ligne courante de l'éditeur
    		try {
	            int pos = textArea.getCaretPosition();
	            int lig = textArea.getLineOfOffset(pos);
	            int start = textArea.getLineStartOffset(lig);
	            int end = textArea.getLineEndOffset(lig);
	            String text = textArea.getText(start, end - start);
	            String commande = Ja.remplace(Ja.remplace(ligneCommande, "%2", text), "\n", "");
		        e = new JaPaneEvent(this, objetCommande, texteCommande, titreCommande, commande);
    		} catch ( Exception ex) {
    			jaf.log(e.toString());
    		} // end try
    	} // endif
    	if ( ligneCommande.indexOf("%0") != -1 ) {
	        e = new JaPaneEvent(this, objetCommande, texteCommande, titreCommande, 
	        	Ja.remplace(ligneCommande, "%0", fichier));
    	} // endif
    	if ( ligneCommande.indexOf("%1") != -1 ) {
	        e = new JaPaneEvent(this, objetCommande, texteCommande, titreCommande, 
	        	Ja.remplace(ligneCommande, "%1", textArea.getText()));
    	} // endif
    	if ( e == null ) {
	        e = new JaPaneEvent(this, objetCommande, texteCommande, titreCommande, 
	        	ligneCommande);
    	} // endif
        for (int i=0, n = listeners.size(); i < n; i++ ) {
            JaPaneListener listener = (JaPaneListener)listeners.elementAt(i);
            listener.jaPaneEventAction(e);
        } // end for
    }
    private void setTitre() {
        fireJaPaneEventTitle(fichier);
    } // end setTitre

    /**
     * gestion de la recherche
     */
    private boolean search() {
        boolean bret = false;
        int posSearch;
        if ( checkCasse.isSelected() ) {
            posSearch = (textArea.getText()).indexOf(textSearch.getText(), textArea.getCaretPosition());
        } else {
            posSearch = (textArea.getText().toLowerCase()).indexOf(textSearch.getText().toLowerCase(), textArea.getCaretPosition());
        } // endif
        if ( posSearch != -1 ) {
            textArea.select(posSearch, posSearch + textSearch.getDocument().getLength());
            bret = true;
        } // endif
        textArea.requestFocus();
        return bret;
    }
    private boolean replaceSearch() {
        if ( textArea.getSelectedText() != null ) {
            textArea.replaceSelection(textReplace.getText());
        } // endif
        return search();
    }
    private void replaceEnd() {
        boolean bret = search();
        while ( bret == true ) {
            bret = replaceSearch();
        } // endWhile
    }

    // Ecouteur du curseur
    protected class CaretListenerLabel extends JLabel implements CaretListener {
        public CaretListenerLabel (String label) {
            super(label);
            setFont(Ja.getFont());
        }
        public void caretUpdate(CaretEvent e) {
            try {
                int dot = e.getDot();
                int lig = textArea.getLineOfOffset(dot);
                int col = dot - textArea.getLineStartOffset(lig);
                setText("lig:col[" + (lig + 1) + ":" + col + "] (" + dot + " car)");
            } catch (BadLocationException ble) {
                setText("lig:col[erreur]");
            } // end try
        }
    } // end class CaretListenerLabel

    /**
     * Ecouteur du clavier
     */
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
		//jaf.log(KeyEvent.getKeyText(e.getKeyCode()) + ":" + e.getKeyCode());
		if ( KeyEvent.getKeyText(e.getKeyCode()).equalsIgnoreCase("s") && e.isControlDown() ) {
			buttonSave.doClick();
		} // endif
		if ( KeyEvent.getKeyText(e.getKeyCode()).equalsIgnoreCase("z") && e.isControlDown() ) {
			buttonAnnuler.doClick();
		} // endif
		if ( KeyEvent.getKeyText(e.getKeyCode()).equalsIgnoreCase("y") && e.isControlDown() ) {
			buttonRepeter.doClick();
		} // endif
        if ( e.getKeyCode() == KeyEvent.VK_TAB ) {
            try {
                int dot = textArea.getCaretPosition();
                int lig = textArea.getLineOfOffset(dot);
                int col = dot - textArea.getLineStartOffset(lig);
                // insertion du nombre de blanc pour arriver é la tabultaion suivante
                StringBuffer buf = new StringBuffer();
                buf.append(" ");
                while ( (++col % 4) != 0 ) {
                    buf.append(" ");
                } // endwhile
                textArea.insert(buf.toString(), dot);
                buf = null;
                e.consume();
            } catch (Exception ex ) {
                Ja.debug(670, nomClasse, ex);
            } // end try
        } // endif
        if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
            try {
                int pos = textArea.getCaretPosition();
                int lig = textArea.getLineOfOffset(pos);
                int start = textArea.getLineStartOffset(lig);
                int end = textArea.getLineEndOffset(lig);
                String text = textArea.getText(start, end - start);
                // retour à la ligne suivante méme colonne que la ligne précédente
                // recherche du 1er caractére sur la ligne courante
                StringBuffer buf = new StringBuffer();
                buf.append("\r\n");
                for (int i=0, len=text.length(); i < len && text.charAt(i) == ' '; i++ ) {
                    buf.append(" ");
                } // endfor
                textArea.insert(buf.toString(), pos);
                buf = null;
                e.consume();
            } catch (Exception ex ) {
                Ja.debug(670, nomClasse, ex);
            } // end try
        } // endif
        if ( e.getKeyCode() == KeyEvent.VK_F5 ) {
            // exécution de la ligne de commande courante
        	fireJaPaneEventAction("Paramètres", "", "Paramètres", "%2");
        	} // endif
    }

    /**
     *  Démarrage du THREAD
     */
    private void startThread(int typeCommande) {
      try {
        if ( thread != null ) {
          jaf.log("REFUS", "commande en cours");
          return;
        } // endif
        thread = new Thread(this, "" + typeCommande);
        thread.start();
      } catch ( Exception e ) {
        e.printStackTrace();
        thread = null;
      } // end try
    } // end run
    /**
     *  Exécution du THREAD
     */
    public void run() {
      Thread      myThread = Thread.currentThread();
      Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
      try {
        thread = null;
      } catch (Exception e) {
        e.printStackTrace();
        thread = null;
      } // end try
    } // end run

} // end class
