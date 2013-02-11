package atelier;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * <pre>
 * Fenétre de saisie des paramètres d'une commande ou d'un fichier scripte
 * {$fichier} {$repertoireAtelier} {paramètre1} {paramètre2} etc...
 *
 * <img src=JaInputDialog.gif>
 * <pre>
 * @author  : Philippe.BILLEROT@laposte.net
 * @version : 1.0 du 23 juillet 2000
 */
public class JaInputDialog extends JDialog implements ActionListener {
private final static String nomClasse = "JaInputDialog";
private JaProperties    jac = null;
private JaFrame         jaf = null;
private String          paragraphe = null;
// MENU
private JMenuItem   menuPosition;
private JMenuItem   menuFermer;
// BARRE BOUTONS
private JButton         buttonOK;
private JButton         buttonCancel;
// Panneau des champs de saisie
private JPanel          panel;
private Vector          vParametres = new Vector();
private Hashtable       hParametres = new Hashtable();

    /**
     * Contruction de la classe
     */
    public JaInputDialog(JaFrame jaf, String titre, String paragraphe) {
        super(jaf.getFrame(), titre, true);
        this.jaf = jaf;
        this.jac = jaf.getJaProperties();
        this.paragraphe = paragraphe;
        initComponents();
    } // end init

    public void add(String parametre) {
    	vParametres.addElement(parametre);
        hParametres.put(parametre, "");
    } // end add()
	/**
	 * Method add.
	 * @param parametre
	 * @param items liste des item séparés par une virgule
	 */
    public void add(String parametre, String items) {
    	vParametres.addElement(parametre);
        hParametres.put(parametre, items);
    } // end add()

    public void showInputDialog() {
        GridBagLayout gridbag = new GridBagLayout();
        JPanel textPane = new JPanel(gridbag);

        for (Enumeration ee = vParametres.elements(); ee.hasMoreElements(); ) {
            String parametre = (String)ee.nextElement();
            String items = (String)hParametres.get(parametre);
            if ( items.length() > 0 ) {
	            JComboBox jc = new JComboBox(Ja.getVector(items));
	            // un item peut être de la forme "(X) label, (Y) label"
	            // la valeur mémorisé sera alors seulement X
            	jc.setSelectedItem(Ja.getItem(Ja.getVector(items), (String)jac.getProperty(paragraphe + "." + parametre)));
	            jc.setFont(Ja.getFont());
	            jc.setForeground((Color.blue).darker());
	            this.addLabelCombo(new JLabel(parametre), jc, gridbag, textPane);	            
            	hParametres.put(parametre, jc);
            } else {
	            JTextField jt = new JTextField(jac.getProperty(paragraphe + "." + parametre), 15);
	            jt.setFont(Ja.getFont());
	            jt.setForeground((Color.blue).darker());
	            this.addLabelText(new JLabel(parametre), jt, gridbag, textPane);	            
            	hParametres.put(parametre, jt);
            } // endif
        } // end for
        getContentPane().add(textPane, BorderLayout.NORTH);
        pack();
        // centrage de la fenêtre
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;
        this.setBounds(
            Integer.parseInt(jac.getProperty(nomClasse + ".x", "" + x)),
            Integer.parseInt(jac.getProperty(nomClasse + ".y", "" + y)),
            Integer.parseInt(jac.getProperty(nomClasse + ".sx", "" + w)),
            h
        );
        this.setVisible(true);
    } // end add()

    private void addLabelText(JLabel label, JTextField text, GridBagLayout gridbag, Container container) {
      GridBagConstraints c = new GridBagConstraints();
      c.insets = new Insets(2, 2, 2, 2);
      c.anchor = GridBagConstraints.EAST;
      c.gridwidth = GridBagConstraints.RELATIVE;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0.0;
      gridbag.setConstraints(label, c);
      container.add(label);
      c.gridwidth = GridBagConstraints.REMAINDER;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1.0;
      gridbag.setConstraints(text, c);
      container.add(text);
    } // end addLabelText

    private void addLabelCombo(JLabel label, JComboBox text, GridBagLayout gridbag, Container container) {
      GridBagConstraints c = new GridBagConstraints();
      c.insets = new Insets(2, 2, 2, 2);
      c.anchor = GridBagConstraints.EAST;
      c.gridwidth = GridBagConstraints.RELATIVE;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0.0;
      gridbag.setConstraints(label, c);
      container.add(label);
      c.gridwidth = GridBagConstraints.REMAINDER;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1.0;
      gridbag.setConstraints(text, c);
      container.add(text);
    } // end addLabelText

    /**
     * Création de l'IHM (Interface Homme Machine)
     */
    private void initComponents() {
        String methode = "initComponents";

        // MENUS
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("Fichier");

        menuPosition = new JMenuItem("Mémoriser la position et la taille de la fenétre");
        menuPosition.addActionListener(this);
        menuFile.add(menuPosition);

        menuFile.add(new JSeparator() );

        menuFermer = new JMenuItem("Fermer");
        menuFermer.addActionListener(this);
        menuFile.add(menuFermer);

        menuBar.add(menuFile);
        setJMenuBar(menuBar);

        // BOUTONS
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonOK = new JButton("OK");
        buttonOK.addActionListener(this);
        buttons.add(buttonOK);
        buttonCancel = new JButton("Annuler");
        buttonCancel.addActionListener(this);
        buttons.add(buttonCancel);

        getContentPane().add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(buttonOK);

        // gestion de la demande d'arrét
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    } // end initComponents

    /**
     * gestion des ACTIONS
     */
    public void actionPerformed(ActionEvent e) {
        Object objet = e.getSource();
        if ( objet == menuPosition ) {
            savePosition();
            return;
        }
        if ( objet == menuFermer) {
            sauveQuiPeut();
        }
        if ( objet == buttonOK ) {
            // chargement des paramétres
            for (Enumeration ee = hParametres.keys(); ee.hasMoreElements(); ) {
                String parametre = (String)ee.nextElement();
                if ( hParametres.get(parametre).getClass().getName().indexOf("JTextField") != -1 ) {
	                JTextField jt = (JTextField)hParametres.get(parametre);
	                jac.setProperty(paragraphe + "." + parametre, jt.getText());
                } // endif
                if ( hParametres.get(parametre).getClass().getName().indexOf("JComboBox") != -1 ) {
	                JComboBox jc = (JComboBox)hParametres.get(parametre);
	                String item = Ja.getItem((String)jc.getSelectedItem());
	                jac.setProperty(paragraphe + "." + parametre, item);
                } // endif
            } // end for
            jac.setProperty(nomClasse + ".return", "OK");
            jac.save();
            dispose();
            return;
        }
        if ( objet == buttonCancel ) {
            sauveQuiPeut();
            return;
        }
    } // end actionPerformed

    /**
     * demande de sauvegarde de la position du séparateur
     */
    public void savePosition() {
        jac.savePosition(this, nomClasse);
    } // endif

    /**
     * terminaison : demande de sauvegarde des modifs
     */
    public void sauveQuiPeut() {
        jac.setProperty(nomClasse + ".return", "Cancel");
        jac.save();
        dispose();
    } // end sauveQuiPeut()

} // end class