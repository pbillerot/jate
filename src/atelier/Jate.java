package atelier;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.util.*;

/**
 * @author  : Philippe.BILLEROT@gmail.com
 */
public class Jate extends JFrame implements ActionListener, JaPaneListener, JaFrame {
	private final static String nomClasse = "Jate";
	private final static String nomProperties = "jate.properties";
	private final static String VERSION = "Jate 13.3.13";
private String  fichier = "";
private final static String labelApropos = "Jate - Atelier de Maintenance"
	+ "\n" + Jate.VERSION + " (c) philippe.billerot@gmail.com";
private JaFrame jaf = this;
private JaProperties jac = null;
private Hashtable contexte = new Hashtable();
// MENU
private JMenuItem   menuPosition;
private JMenuItem   menuProperties;
private JMenuItem   menuOutils;
private JMenuItem   menuActions;
private JMenuItem   menuBarreOutils;
private JMenuItem   menuExtensions;
private JMenuItem   menuFermer;
private JMenuItem   menuApropos;
private JMenuItem   menuImages;
private JMenuItem   menuHelpJate;
private JMenuItem   menuHelp;
private JMenuItem   menuHelpIcon;
// découpage en panneau
private JSplitPane  splitPaneV;
private JSplitPane  splitPaneH;

JToolBar outilsPane;

// PANNEAU DE GAUCHE
private JTabbedPane tabbedListPane;
private JaListPane  listPane;

// PANNEAU DE DROITE
private JTabbedPane tabbedTextPane;
// PANNEAU INFERIEUR
private JaResultPane resultPane;

    public Jate() {
    	try
    	{
    		this.jac = new JaProperties(Jate.nomProperties);
    		if (this.jac.size() == 0)
    		{
    			// Chargement du properties fourni dans le jate.jar
    			this.jac.load(this.getClass().getClassLoader().getResourceAsStream(Jate.nomProperties));
    			jac.setNomFichier(Jate.nomProperties);
    		}
    		initComponents();
    		pack();
    		initPosition();
    		// Réglage de line.separator pour ne gérer que \n dans les JtextArea
    		System.setProperty("line.separator", "\n");

    		//this.log(Jate.labelApropos);
    		//this.log("Hello " + System.getProperty("user.name") + " !");
    		this.log("Identification: " + System.getProperty("user.name") 
    				+ " - " + System.getProperty("os.name")
    				+ " - " + Jate.VERSION);
    		this.log(" Configuration: " + System.getProperty("user.dir") + System.getProperty("file.separator") + Jate.nomProperties);
    	} catch (Exception e) {
    		log(e);
    	}
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
        resultPane.log(Ja.stackTraceToString(e));
    }
    public void log(String message) {
        resultPane.log(message);
    }
    public void log(String var, String val) {
        resultPane.log(var, val);
    }
    public void log(int lig, String var, Exception e) {
        resultPane.log(lig, var, e);
    }

    private void initPosition() {
        jac.getPosition(this, "Jate");
        splitPaneH.setDividerLocation(Integer.parseInt(jac.getProperty("Jate.dividerH", "200")));
        splitPaneV.setDividerLocation(Integer.parseInt(jac.getProperty("Jate.dividerV", "100")));
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

        menuPosition = new JMenuItem("Mémoriser la position et la taille de la fenêtre");
        menuPosition.addActionListener(this);
        menuFile.add(menuPosition);

        menuFile.add(new JSeparator() );

        menuProperties = new JMenuItem("Paramétrer l'atelier...");
        menuProperties.addActionListener(this);
        menuFile.add(menuProperties);

        menuOutils = new JMenuItem("Paramétrer le menu Outils...");
        menuOutils.addActionListener(this);
        menuFile.add(menuOutils);
        
        this.menuBarreOutils = new JMenuItem("Paramétrer la barre d'outils...");
        this.menuBarreOutils.addActionListener(this);
        menuFile.add(this.menuBarreOutils);
        
        menuActions = new JMenuItem("Paramétrer les Actions...");
        menuActions.addActionListener(this);
        menuFile.add(menuActions);

        menuExtensions = new JMenuItem("Paramétrer les Extensions...");
        menuExtensions.addActionListener(this);
        menuFile.add(menuExtensions);

        menuFile.add(new JSeparator() );

        menuFermer = new JMenuItem("Fermer");
        menuFermer.addActionListener(this);
        menuFile.add(menuFermer);

        menuBar.add(menuFile);

        // menu OUTILS
        JMenu menuOutils = null;
        if ( ! jac.getParam("Jate","outils.titre", "").equals("") ) {
            menuOutils = new JMenu(jac.getParam("Jate","outils.titre"));
            for (int i = 1; ; i++ ) {
                String titre = jac.getProperty("outils." + i + "." + JaFrame.CLEVAR, "");
                if ( ! titre.equals("") ) {
                    if ( titre.startsWith("-") ) {
                        menuOutils.add(new JSeparator() );
                    } else {
                        JMenuItem menu = new JMenuItem(titre);
                        menu.setActionCommand(jac.getProperty("outils." + i + "." + JaFrame.CLEVAL, ""));
                        menu.addActionListener(this);
                        menuOutils.add(menu);
                    } // endif
                } else {
                    break;
                } // endif
            } // end for
            menuBar.add(menuOutils);
        } // endif

        // menu AIDE
        JMenu menuAide = new JMenu("?");

        this.menuHelpJate = new JMenuItem("Aide sur Jate...");
        this.menuHelpJate.addActionListener(this);
        menuAide.add(this.menuHelpJate);

//        menuImages = new JMenuItem("Images des menus et boutons");
//        menuImages.addActionListener(this);
//        menuAide.add(menuImages);
//        menuBar.add(menuAide);

        menuApropos = new JMenuItem("A propos...");
        menuApropos.addActionListener(this);
        menuAide.add(menuApropos);
        menuBar.add(menuAide);

        setJMenuBar(menuBar);
        
        // barre des boutons
        if (!this.jac.getProperty("barre.1.var", "").equals("")) {
        	this.outilsPane = new JToolBar();
        	this.outilsPane.setFloatable(false);
        	for (int i = 1; ; i++) {
        		String titre = this.jac.getProperty("barre." + i + "." + "var", "");
        		if (titre.equals("")) break;
        		if (titre.startsWith("-")) {
        			this.outilsPane.add(new JToolBar.Separator());
        		} else {
        			StringTokenizer token = new StringTokenizer(titre, "|");
        			String label = token.hasMoreElements() ? (String)token.nextElement() : "";
        			String icon = token.hasMoreElements() ? (String)token.nextElement() : "";
        			JButton button;
        			if (icon.length() > 0)
        				button = new JButton(label, Ja.getIcon(icon));
        			else {
        				button = new JButton(label);
        			}
        			button.setActionCommand(this.jac.getProperty("barre." + i + "." + "val", ""));
        			button.addActionListener(this);
        			button.setFont(Ja.getFontBouton());
        			this.outilsPane.add(button);         		}
        	}
        }

        // PANNEAU LISTE
        listPane = new JaListPane(this, jac);
        listPane.addJaPaneListener(this);

        tabbedListPane = new JTabbedPane();
        tabbedListPane.addTab("", Ja.getIcon("/images/liste.gif"), listPane);

        // PANNEAU EDIT
        JPanel editPane = new JPanel(new BorderLayout());

        JaEditPane textPane = new JaEditPane(this);
        textPane.addJaPaneListener(this);
        tabbedTextPane = new JTabbedPane();
        tabbedTextPane.addTab("", Ja.getIcon("/images/document.gif"), textPane);

        editPane.add(tabbedTextPane, BorderLayout.CENTER);

        // PANNEAU SUPERIEUR
        splitPaneH = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   tabbedListPane, editPane);
        splitPaneH.setOneTouchExpandable(true);

        // PANNEAU INFERIEUR
        resultPane = new JaResultPane(this);
        resultPane.addJaPaneListener(this);

        // séparation haut bas
        splitPaneV = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                   splitPaneH, resultPane);
        splitPaneV.setOneTouchExpandable(true);

        // disposition des PANELS
        if (this.outilsPane != null) getContentPane().add(this.outilsPane, "North");
        getContentPane().add(splitPaneV, BorderLayout.CENTER);
        setTitre();
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
        listPane.sauveQuiPeut();
        int q = tabbedTextPane.getTabCount();
        for (int i=0; i < q; i++ ) {
            JaEditPane textPane = (JaEditPane)tabbedTextPane.getComponentAt(i);
            textPane.sauveQuiPeut();
        } // endfor
        System.exit(0);
    } // end sauveQuiPeut()

    public void setTitre() {
        this.setTitle(" " + jac.getParam("Jate","atelier.titre", "Jate"));
    } // end setTitre

    /**
     * gestion des ACTIONS
     */
    public void actionPerformed(ActionEvent e) {
        Object objet = e.getSource();
        if ( objet == menuPosition ) {
            jac.setProperty("Jate.dividerH", "" + splitPaneH.getDividerLocation());
            jac.setProperty("Jate.dividerV", "" + splitPaneV.getDividerLocation());
            jac.savePosition(this, "Jate");
            return;
        }
        if ( objet == menuProperties) {
            JaParamManager dialog = new JaParamManager(this, 
                	"Jate - Propriétés", "Jate", "parametre", "valeur");
            return;
        }
        if ( objet == menuOutils) {
            JaParamManager dialog = new JaParamManager(this, 
            	"Jate - Outils", "outils", "titre du menu", "commande");
            return;
        }
        if (objet == this.menuBarreOutils)
        {
          new JaParamManager(this, 
            "Jate - Barre d'outils", "barre", "libellé du bouton", "commande");
          return;
        }
        if ( objet == menuActions) {
            JaParamManager dialog = new JaParamManager(this, 
            	"Jate - Actions", "action", "titre du menu", "commande");
            return;
        }
        if ( objet == menuExtensions) {
            JaParamManager dialog = new JaParamManager(this, 
                	"Jate - Extensions", "extension", "nom de l'extension", "commande à lancer");
            // ne pas effacer pour garder le mode d'appel de JaParamDialog
            //JaParamDialog dialog = new JaParamDialog(this, "Jate - Extensions");
            //dialog.add("extension.*");
            //dialog.showDialog(); bug ici
            return;
        }
        if ( objet == menuFermer ) {
            sauveQuiPeut();
            return;
        }
        if ( objet == menuApropos ) {
            JOptionPane.showMessageDialog(this,
            labelApropos,
            "A propos...", JOptionPane.INFORMATION_MESSAGE);
            return;
        } // endif
        if (objet == this.menuHelpJate) {
        	//this.resultPane.executer("Aide sur Jate", "", "", "{$edit;/doc/readme.txt}");
        	this.resultPane.executer("Aide sur Jate", "", "", "http://philippe.billerot.free.fr/wikijate");
        	return;
        }
        if ( objet == menuHelp ) {
            resultPane.executer(jac.getProperty("aide.titre"), "", "", jac.getProperty("aide.cmd"));
            return;
        }

        if ( objet == menuImages) {
            JaImageViewer dialog = new JaImageViewer(this);
            return;
        }
        // traitement du menu OUTILS
        resultPane.executer(((AbstractButton)objet).getText(), "", "", ((AbstractButton)objet).getActionCommand());

    } // end actionPerformed

    /**
     * évènement de JaPaneListener
     */
    public void jaPaneEventTitle(JaPaneEvent e) {
        Object objet = e.getSource();
        String text = e.getMessage();
        if ( objet == listPane ) {
            tabbedListPane.setTitleAt(tabbedListPane.getSelectedIndex(), text);
        } else {
            tabbedTextPane.setTitleAt(tabbedTextPane.getSelectedIndex(), text);
        } // endif
    }
    public void jaPaneEventEdit(JaPaneEvent e, boolean dansNouvelleFenetre) {
        // evt from listPane suite dclic sur l'un des fichiers de la liste
        // evt from resultPane suite $edit
        if ( tabbedTextPane.getTabCount() == 0 || dansNouvelleFenetre) {
            JaEditPane textPane = new JaEditPane(this);
            textPane.addJaPaneListener(this);
            tabbedTextPane.addTab("", Ja.getIcon("/images/document.gif"), textPane);
            tabbedTextPane.setSelectedComponent(textPane);
            textPane.load(e.getMessage());
        } else {
            JaEditPane textPane = (JaEditPane)tabbedTextPane.getSelectedComponent();
            textPane.load(e.getMessage());
        } // endif
        //jaf.log("jaPaneEventEdit(" + e.getMessage() + ", " + dansNouvelleFenetre + ")");
    }
    public void jaPaneEventSetTexte(JaPaneEvent e) {
        if ( tabbedTextPane.getTabCount() != 0 ) {
            JaEditPane textPane = (JaEditPane)tabbedTextPane.getSelectedComponent();
            textPane.setTexte(e.getMessage());
        } // endif
    }
    public void jaPaneEventRemove(JaPaneEvent e) {
        JaEditPane textPane = (JaEditPane)tabbedTextPane.getSelectedComponent();
        textPane.removeJaPaneListener(this);
        tabbedTextPane.remove(textPane);
    }
    public void jaPaneEventAction(JaPaneEvent e) {
        resultPane.executer(e.getObjetCommande(), e.getTexteCommande(), e.getTitreCommande(), e.getLigneCommande());
    } // end
    
    /**
     * point d'entree de l'application
     */
    public static void main(String[] args) {
        Jate frame = null;
        try {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // pb avec les icones ci-dessus
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) { }
        frame = new Jate();
        frame.setVisible(true);
    } // end main
} // end class
