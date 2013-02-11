package atelier;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;

/**
 * @author  : Philippe.BILLEROT@laposte.net
 * @version : 1.0 du 23 juillet 2000
 */
public class JaParamManager extends JDialog implements ActionListener {
private final static String nomClasse = "JaParamManager";
private JaProperties    jac = null;
private JaFrame         jaf = null;
// MENU
private JMenuItem   menuPosition;
private JMenuItem   menuClose;
// BARRE BOUTONS
private JButton buttonSave;
private JButton buttonClose;
// TABLE
//private JaTableModel tableModel = null;
private DefaultTableModel tableModel = null;
private JTable table = null;
private Vector vparam = new Vector(); // tableau de vecteurs 1 / ligne
private Vector vcolonne = new Vector();
private String cle;
private String titreA; 
private String titreB; 
// BARRE BOUTONS DE COMMANDE
private JButton buttonInsert;
private JButton buttonAjout;
private JButton buttonDelete;

    /**
     * Contruction de la classe
     */
    public JaParamManager(
    		JaFrame jaf,   // pointeur sur la frame de l'atelier
    		String titre,  // titre de la fenétre
    		String cle,    // cle dans le fichier propriété cle.*.*
    		String titreA, // titre colonne A
    		String titreB  // titre colonne B
    		) {
    	super(jaf.getFrame(), titre, true);
    	this.jaf = jaf;
    	this.jac = jaf.getJaProperties();
    	this.cle = cle;
    	this.titreA = titreA;
    	this.titreB = titreB;
    	load(cle);
    	initComponents();
    	pack();
    	jac.getPosition(this, nomClasse);
    	saveEnabled(false);
    	buttonInsert.setEnabled(false);
    	buttonDelete.setEnabled(false);
    	table.getColumnModel().getColumn(1).setPreferredWidth(
    			(int)this.getBounds().getWidth() - table.getColumnModel().getColumn(0).getPreferredWidth() - 11);
    	setVisible(true);
    } // end init

    /**
     *  Chargement des clé dans un vector dans un 1er temps
     *  cle.1.var = nom paramétre
     *  cle.1.val = valeur du paramétre
     *  cle.2.var = nom paramétre
     *  cle.2.val = valeur du paramétre
     */
    private void load(String cle) {
        this.cle = cle;
        for (int i = 1; ; i++) {
        	String pkey = cle + "." + i + "." + JaFrame.CLEVAR;
        	String vkey = cle + "." + i + "." + JaFrame.CLEVAL;
        	if ( jac.getProperty(pkey) == null ) break;
        	Vector v = new Vector();
            v.addElement(jac.getProperty(pkey));                    
            v.addElement(jac.getProperty(vkey));                    
            vparam.addElement(v);                    
        } // end for
        
    } // end add()

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
        
        // création de la table
		//tableModel = new JaTableModel(vparam);
		vcolonne.addElement(titreA);
		vcolonne.addElement(titreB);		
		tableModel = new DefaultTableModel(vparam, vcolonne);
		table = new JTable(tableModel);		     
		table.getSelectionModel().addListSelectionListener(new JaListSelectionListener());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(true);
		table.getTableHeader().setBackground(Color.yellow);
		table.setDefaultRenderer(table.getColumnClass(0)
			, new JaTableCellRenderer(table.getCellRenderer(0, 0)));
		table.getColumnModel().getColumn(0).setCellEditor(new JaTableCellEditor());
		table.getColumnModel().getColumn(1).setCellEditor(new JaTableCellEditor());
		table.getColumnModel().getColumn(0).setPreferredWidth(
			Integer.parseInt(jac.getProperty(this.getTitle() + ".divider", "300")));
        JScrollPane tablePane = new JScrollPane(table);

        // barre d'outils de commande
        JToolBar toolbar2 = new JToolBar();
        toolbar2.setFloatable(false);

        buttonInsert = new JButton("Insérer", Ja.getIcon("/images/new.gif"));
        buttonInsert.setToolTipText("pour insérer une ligne au dessus");
        buttonInsert.addActionListener(this);
        toolbar2.add(buttonInsert);

        buttonAjout = new JButton("Ajouter", Ja.getIcon("/images/listeAjout.gif"));
        buttonAjout.setToolTipText("pour ajouter une ligne à la fin");
        buttonAjout.addActionListener(this);
        toolbar2.add(buttonAjout);

        buttonDelete = new JButton("Enlever", Ja.getIcon("/images/del.gif"));
        buttonDelete.setToolTipText("pour enlever la ligne");
        buttonDelete.addActionListener(this);
        toolbar2.add(buttonDelete);

        getContentPane().add(toolbar, BorderLayout.NORTH);
        getContentPane().add(tablePane, BorderLayout.CENTER);
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
     * terminaison : demande de sauvegarde des modifs
     */
    public void sauveQuiPeut() {
        if ( buttonSave.isEnabled() ) {
            int iret = JOptionPane.showConfirmDialog(this,
            "Les paramètres ont été modifiés, voulez-vous les enregistrer ?",
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
        } // endif
        if ( objet == menuClose || objet == buttonClose ) {
            sauveQuiPeut();
        } // endif
        if ( objet == buttonSave ) {
            // on delete avant les clés 
	        for (int i = 1; ; i++) {
	        	String pkey = cle + "." + i + "." + JaFrame.CLEVAR;
	        	String vkey = cle + "." + i + "." + JaFrame.CLEVAL;
	        	if ( jac.getProperty(pkey) == null ) break;
	        	jac.remove(pkey);
	        	jac.remove(vkey);
	        } // end for
	        for (int i = 1; i < tableModel.getRowCount() + 1; i++ ) {
	        	jac.setProperty(cle + "." + i + "." + JaFrame.CLEVAR,	(String)tableModel.getValueAt(i-1, 0));
	        	jac.setProperty(cle + "." + i + "." + JaFrame.CLEVAL,	(String)tableModel.getValueAt(i-1, 1));
	        } // end for	        	 
            jac.save();
            saveEnabled(false);
        } // endif
        if ( objet == buttonInsert ) {
        	Vector v = new Vector();
        	v.addElement("?");
        	v.addElement("?");
        	tableModel.insertRow(table.getSelectedRow(), v);
            saveEnabled(true);
            buttonsEnabled();
            return;
        } // endif
        if ( objet == buttonAjout ) {
        	Vector v = new Vector();
        	v.addElement("?");
        	v.addElement("?");
        	tableModel.insertRow(table.getRowCount(), v);
            saveEnabled(true);
            buttonsEnabled();
            return;
        } // endif
        if ( objet == buttonDelete ) {
        	tableModel.removeRow(table.getSelectedRow());
            saveEnabled(true);
            buttonsEnabled();
            return;
        } // endif
    } // end actionPerformed

    /**
     * enabled disabled du bouton et menu Sauvegarder
     */
    private void saveEnabled(boolean b) {
        buttonSave.setEnabled(b);
    } // endif

	public void buttonsEnabled() {
		if ( table.getSelectedRow() == -1 || table.getSelectedColumn() == -1 ) {
	        buttonInsert.setEnabled(false);
	        buttonDelete.setEnabled(false);
		} else {
	        buttonInsert.setEnabled(true);
	        buttonDelete.setEnabled(true);
		} // endif			
	} // end buttonsEnabled

    /**
     * demande de sauvegarde de la position du séparateur
     */
    public void savePosition() {
        jac.setProperty(this.getTitle() + ".divider", 
        	"" + table.getColumnModel().getColumn(0).getPreferredWidth());
        jac.savePosition(this, nomClasse);
    } // endif
	
	class JaListSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			//jaf.log("First:Last", "" + e.getFirstIndex() + ":" + e.getLastIndex());			
			buttonsEnabled();
		} // end		
	} // end class JaTableModelListener
			
	class JaTableCellRenderer implements TableCellRenderer {
		private final Font font = Ja.getFont();
		private TableCellRenderer oldRenderer = null;
		
		public JaTableCellRenderer (TableCellRenderer oldRenderer) {
			this.oldRenderer = oldRenderer;
		} // end
		public Component getTableCellRendererComponent(
			JTable table, Object value, boolean isSelected, boolean hasFocus,
	        int row, int column) {
	        	Component cell = oldRenderer.getTableCellRendererComponent
	        		(table, value, isSelected, hasFocus, row, column );
	
	        	cell.setFont(font);
				cell.setForeground(Color.blue.darker());        	
				return cell;            	
	        } // end  	
	} // end class 

	class JaTableCellEditor extends DefaultCellEditor implements TableCellEditor, CellEditorListener {
		private final Font font = Ja.getFont();
		JTextField cell = new JTextField();		
	
	    public JaTableCellEditor() {
	    	super(new JTextField());
			this.clickCountToStart = 2;
			this.addCellEditorListener(this);
	    } // end constructeur
	    
		public Component getTableCellEditorComponent(
			JTable table, Object value, boolean isSelected,	int row, int column) {
				cell.setText((String)value);
				cell.setFont(font);
				cell.setForeground(Color.red.darker());        	
			return cell;            	
        } // end  
        public Object getCellEditorValue() {
        	return cell.getText();
        } // end
		public void editingStopped(ChangeEvent e) {
			saveEnabled(true);
		} // end
		public void editingCanceled(ChangeEvent e) {
		} // end
	} // end class 

} // end class
