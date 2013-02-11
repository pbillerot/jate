package atelier;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;

import javax.swing.*;
import javax.swing.text.*;


import java.util.*;
import java.sql.*;

/**
 * @author  : Philippe.BILLEROT@laposte.net
 * @version : 1.0 du 2 aout 2000
 */
public class JaResultPane extends JPanel implements ActionListener, Runnable, ItemListener, MouseListener, KeyListener {
private final static String nomClasse = "JaResultPane";
public final static int COMMANDE_INCONNUE = 0;
public final static int COMMANDE_SQL = 1;
public final static int COMMANDE_DOS = 2;
public final static int COMMANDE_SOCKET = 3;
public final static int COMMANDE_MAIL = 4;
public final static int COMMANDE_EDIT = 5;
public final static int COMMANDE_PLAY = 6;
public final static int COMMANDE_GETHTTP = 7;
private JaFrame         jaf = null;
private JaProperties    jac;
private Thread          thread = null;
private String          objetCommande = "";
private String          texteCommande = "";
private String          ligneCommande = "";
private String          titreCommande = "";
private String          bufferCommande = "";
private int             typeCommande = COMMANDE_INCONNUE;
private String          fichier = "";
private Connection      connODBC = null;
private QueueListener   queueListener = null;
private LinkedList      queue = null;
// MENU POPUP
private JPopupMenu  menuPopup;
private JMenuItem   menuPrint;
private JMenuItem   menuExecuter;
private JMenuItem   menuSaveAs;
private JMenuItem   menuCut;
private JMenuItem   menuCopy;
private JMenuItem   menuPaste;
private JMenuItem   menuDel;
// BARRE BOUTONS
private JButton         buttonPrint;
private JButton         buttonCut;
private JButton         buttonCopy;
private JButton         buttonPaste;
private JButton         buttonDel;
private JCheckBox       checkRetourLigne;
//private JRadioButton    radioDos;
//private JRadioButton    radioSql;
// EDITEUR
private JTextArea       textArea;
private JScrollPane     scrollPane;
private JViewport       viewPort;
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
// PLAYER
//JaPlayerFrame jaPlayerFrame = null;

    /**
     * Contruction de la classe
     */
    public JaResultPane(JaFrame jaf) {
        super(new BorderLayout());
        this.jaf = jaf;
        this.jac = jaf.getJaProperties();
        queue = new LinkedList();

        initComponents();
        queueListener = new QueueListener();
        new javax.swing.Timer(100, queueListener).start();
    } // end init

    public void saveAs(String fichier) {
        this.fichier = fichier;
        Ja.saveTextArea(textArea, fichier, jaf);
    }

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

        menuDel = new JMenuItem("tout effacer (F12)");
        menuDel.addActionListener(this);
        menuPopup.add(menuDel);

        menuPopup.add(new JSeparator() );

        menuExecuter = new JMenuItem("exécuter (F5)");
        menuExecuter.addActionListener(this);
        menuPopup.add(menuExecuter);

        menuPopup.add(new JSeparator() );

        //if ( ! jac.getProperty("path.acrobat", "").equals("")) {
            menuPrint = new JMenuItem("Imprimer le fichier");
            menuPrint.addActionListener(this);
            menuPopup.add(menuPrint);
        //} // endif

        menuSaveAs = new JMenuItem("Enregistrer le texte sous...");
        menuSaveAs.addActionListener(this);
        menuPopup.add(menuSaveAs);

        // barre d'outils
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.addMouseListener(this);

        //if ( ! jac.getProperty("print.titre", "").equals("") || ! jac.getProperty("path.acrobat", "").equals("")) {
            buttonPrint = new JButton(Ja.getIcon("/images/print.gif"));
            buttonPrint.setToolTipText(jac.getProperty("print.titre", ""));
            buttonPrint.addActionListener(this);
            toolbar.add(buttonPrint);
            toolbar.add(new JToolBar.Separator());
        //} // endif

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

        buttonDel = new JButton(Ja.getIcon("/images/del.gif"));
        buttonDel.setToolTipText("tout effacer (F12)");
        buttonDel.addActionListener(this);
        toolbar.add(buttonDel);

        toolbar.add(new JToolBar.Separator());

        checkRetourLigne = new JCheckBox("retour ligne", false);
        checkRetourLigne.setFont(Ja.getFontBouton());
        checkRetourLigne.addItemListener(this);
        toolbar.add(checkRetourLigne);

//        toolbar.add(new JToolBar.Separator());
//
//        ButtonGroup groupRadio = new ButtonGroup();
//
//        radioDos = new JRadioButton("dos");
//        radioDos.setActionCommand("dos");
//        radioDos.setSelected(true);
//        radioDos.addActionListener(this);
//        radioDos.setFont(Ja.getFontBouton());
//        groupRadio.add(radioDos);
//        toolbar.add(radioDos);
//
//        radioSql = new JRadioButton("sql");
//        radioSql.setActionCommand("sql");
//        radioSql.addActionListener(this);
//        radioSql.setFont(Ja.getFontBouton());
//        groupRadio.add(radioSql);
//        toolbar.add(radioSql);

        this.add(toolbar, BorderLayout.NORTH);

        // création de la zone EDITEUR
        textArea = new JTextArea();
        textArea.addMouseListener(this);
        textArea.addKeyListener(this);
        textArea.setFont(Ja.getFont());
        textArea.setForeground((Color.magenta).darker());
        Keymap k = textArea.getKeymap();
        JTextComponent.loadKeymap(k, defaultBindings, textArea.getActions());

        JScrollPane scrollPane = new JScrollPane();
        JViewport viewPort = scrollPane.getViewport();
        viewPort.add(textArea);
        this.add(scrollPane, BorderLayout.CENTER);

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
        if ( objet == buttonCut || objet == menuCut ) {
            textArea.cut();
            textArea.requestFocus();
            return;
        }
        if ( objet == buttonCopy || objet == menuCopy ) {
            textArea.copy();
            textArea.requestFocus();
            return;
        }
        if ( objet == buttonPaste || objet == menuPaste ) {
            textArea.paste();
            textArea.requestFocus();
            return;
        }
        if ( objet == buttonDel || objet == menuDel ) {
            textArea.setText("");
            textArea.requestFocus();
            return;
        }
        if ( objet == menuExecuter ) {
            // exécution de la ligne de commande courante
            try {
                int pos = textArea.getCaretPosition();
                int lig = textArea.getLineOfOffset(pos);
                int start = textArea.getLineStartOffset(lig);
                int end = textArea.getLineEndOffset(lig);
                String text = textArea.getText(start, end - start);
                if ( ! text.trim().equals("") ) executer(nomClasse, "", "Commande", text.trim());
            } catch (Exception ex ) {
            	jaf.log(ex); 
            } // end try
            textArea.requestFocus();
            return;
        }
        if ( objet == buttonPrint || objet == menuPrint ) {
            try {
                File temp = File.createTempFile(nomClasse, ".bat");
                temp.deleteOnExit();
                saveAs(temp.getAbsolutePath());
                //if ( ! jac.getProperty("path.acrobat", "").equals("") ) {
                    new JaPrintToPdf(temp.getAbsolutePath(), jaf);
                //} // endif
                temp = null;
            } catch (Exception ex ) {
            	jaf.log(ex); 
            } // end try
            textArea.requestFocus();
            return;
        } // endif
        if ( objet == menuSaveAs ) {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(fichier));
            chooser.setDialogTitle("Enregistrer sous...");
            chooser.setCurrentDirectory(new File("."));
            int iret = chooser.showSaveDialog(this);
            if ( iret == JFileChooser.APPROVE_OPTION ) {
                fichier = chooser.getSelectedFile().getAbsolutePath();
                saveAs(fichier);
            } // endif
            textArea.requestFocus();
        }
        textArea.requestFocus();
    } // end actionPerformed

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
     * terminaison : demande de sauvegarde des modifs
     */
    public void sauveQuiPeut() {
        try {
            if ( connODBC != null ) connODBC.close();
            thread = null;
        } catch (Exception e ) {}
    } // end sauveQuiPeut()


    /**
     * EXECUTION DE LA COMMANDE EDIT
     * p1: nom du fichier é éditer
     */
    private void interpreteEdit() {
        try {
            String p1 = null;
            StringTokenizer st = new StringTokenizer(ligneCommande, ";}");
            if (st.hasMoreTokens()) st.nextToken();
            if (st.hasMoreTokens()) p1 = st.nextToken();

            //fireJaPaneEventNewEdit(p1);
            //fireJaPaneEventTitle(p1);
            new JaTextFrame(jaf, p1);

        } catch ( Exception e ) {
        	jaf.log(e); 
        } // end try
    }

    /**
     * EXECUTION DE LA COMMANDE SOCKET
     */
    private void interpreteSocket() {
        try {
            String p1 = null, p2 = null, p3 = null;
            StringTokenizer st = new StringTokenizer(ligneCommande, ";}");
            if (st.hasMoreTokens()) st.nextToken();
            if (st.hasMoreTokens()) p1 = st.nextToken();
            if (st.hasMoreTokens()) p2 = st.nextToken();
            if (st.hasMoreTokens()) p3 = st.nextToken();
            if ( ligneCommande.indexOf("{$ecouteSocket;") != -1 ) {
                new JaSocketServeur(jaf, Integer.parseInt(p1));
            } // endif
            if ( ligneCommande.indexOf("{$socket;") != -1 ) {
                new JaSocketClient(jaf, p1, Integer.parseInt(p2), p3);
            } // endif
        } catch ( Exception e ) {
        	jaf.log(e); 
        } // end try
    }

    /**
     * EXECUTION DU SCRIPT SHELL
     */
    private void interpreteShellProcessBuilder() {
    	try {
    		ProcessBuilder pb = new ProcessBuilder(jac.getParam("Jate", "shell") );
    		Process process = pb.start();
            BufferedWriter outCommand = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            outCommand.write(ligneCommande);
            outCommand.flush();

            pb = pb.redirectErrorStream(true); // on mélange les sorties du processus
    		InputStream is = process.getInputStream(); 
    		InputStreamReader isr = new InputStreamReader(is);
    		BufferedReader br = new BufferedReader(isr);
    		String ligne; 
    		while (( ligne = br.readLine()) != null) { 
    			// ligne contient une ligne de sortie normale ou d'erreur
                jaf.log(ligne);
    		}
    	} catch ( Exception e ) {
    		jaf.log(e); 
    	} // end try
    }
    
    /**
     * EXECUTION DU SCRIPT SHELL
     */
    private void interpreteShell() {
    	try {
    		boolean isLinux = System.getProperty("os.name").equalsIgnoreCase("linux");
    		Process process = null;
    		BufferedReader in = null;
    		BufferedReader er = null;
    		jaf.log("# " + System.getProperty("user.name") + " " + Ja.getDateMaj() + "> " + ligneCommande);        	
    		if ( isLinux ) {
    			if ( ligneCommande.endsWith(".sh")) {
    				File fileCmd = new File(ligneCommande);
    				fileCmd.setExecutable(true);
    				process = Runtime.getRuntime().exec(fileCmd.getAbsolutePath());        		
    			} else {
    				process = Runtime.getRuntime().exec("/bin/sh");
    				BufferedWriter outCommand = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    				outCommand.write(ligneCommande + "\n");            
    				outCommand.write("exit\n");            
    				outCommand.flush();
    			}
        		in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        		er = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    		} else {
    			// windows
    			if ( ligneCommande.endsWith(".cmd")) {
    				process = Runtime.getRuntime().exec(ligneCommande);        		
    			} else {
    				process = Runtime.getRuntime().exec("cmd");
    				BufferedWriter outCommand = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    				outCommand.write(ligneCommande + "\n");
    				outCommand.write("exit" + "\n");
    				outCommand.flush();
    			}
        		in = new BufferedReader(new InputStreamReader(process.getInputStream(), "CP850"));
        		er = new BufferedReader(new InputStreamReader(process.getErrorStream(), "CP850"));
    		} // endif
    		new JaDosOutput(jaf, in);
    		new JaDosOutput(jaf, er);
    		process.waitFor();
    	} catch ( Exception e ) {
    		jaf.log(e); 
    	} // end try
    }
    
    /**
     * EXECUTION D'UNE URL
     */
    private void interpreteUrl() {
    	try {
    		if( Desktop.isDesktopSupported()) {
    			if( Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)){
    				Desktop dt = Desktop.getDesktop();
    				dt.browse(new URI(ligneCommande));
    			}
    		} // endif
    	} catch ( Exception e ) {
    		jaf.log(e); 
    	} // end try
    }
    
    /**
     * EXECUTION DU SCRIPT SHELL
     */
    private void interpreteShellOld() {
        try {
            File tempFile = File.createTempFile("Jate", "." + jac.getParam("Jate", "shell"));
            tempFile.setExecutable(true);
            tempFile.deleteOnExit();
            BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
            if (this.ligneCommande.indexOf(" %1") != -1)
              out.write(this.texteCommande);
            else {
              out.write(Ja.mid(this.ligneCommande, 5));
            }
            out.close();

            String commande = tempFile.getPath();
            jaf.log("\nStart shell: " + commande);
            Process process = Runtime.getRuntime().exec(commande);
            //String encoding = System.getProperty("os.name").toLowerCase().indexOf("windows") != -1 
            //      ? "CP850" : System.getProperty("file.encoding"); // fonctionne bien pour les commandes dos mais pour les autrs  commandes
            String encoding = System.getProperty("os.name").toLowerCase().indexOf("windows") != -1 
            		? System.getProperty("file.encoding") : System.getProperty("file.encoding");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), encoding));
            BufferedReader er = new BufferedReader(new InputStreamReader(process.getErrorStream(), encoding));
            new JaDosOutput(jaf, in);
            new JaDosOutput(jaf, er);
            process.waitFor();
            jaf.log("End shell: " + commande);
        } catch ( Exception e ) {
        	jaf.log(e); 
        } // end try
    }

    /**
     * EXECUTION DU SCRIPT SQL
     */
    private void interpreteSQL() {
        // lecture du script
        // suppression des commentaires et des retours chariot
        StringBuffer sql = new StringBuffer();
        int lenBuf = texteCommande.length();
        char car;
        boolean bComLigne = false;
        boolean bComMultiLigne = false;
        boolean blanc = true;
        for (int i=0; i < lenBuf; i++ ) {
            car = texteCommande.charAt(i);
            switch (car) {
                case '/':
                    i++;
                    car = texteCommande.charAt(i);
                    if ( car == '/' ) bComLigne = true;
                    if ( car == '*' ) bComMultiLigne = true;
                    break;
                case '*':
                    if ( bComMultiLigne ) {
                        i++;
                        car = texteCommande.charAt(i);
                        if ( car == '/' )bComMultiLigne = false;
                    } else {
                        if ( ! bComLigne ) {
                            sql.append(car);
                            blanc = false;
                        } // endif
                    } // endif
                    break;
                case '\r':
                case '\n':
                    bComLigne = false;
                    car = ' ';
                case ' ':
                    if ( blanc ) break;
                    sql.append(car);
                    blanc = true;
                    break;
                case '\t':
                    if ( blanc ) break;
                    sql.append(' ');
                    blanc = true;
                    break;
                case ';':
                    if ( bComMultiLigne ) break;
                    if ( bComLigne ) break;
                    executeSQL(sql.toString());
                    sql = null;
                    sql = new StringBuffer();
                    break;
                default:
                    if ( bComMultiLigne ) break;
                    if ( bComLigne ) break;
                    sql.append(car);
                    blanc = false;
            } // end switch
        } // endfor
        if ( sql.length() != 0 ) executeSQL(sql.toString());
    }

    /**
     * EXECUTION D'UN ORDRE SELECT
     */
    private Connection ouvertureBaseODBC() throws Exception{
        if ( connODBC == null ) {
            log("Ouverture de " + jac.getProperty("odbc.base") + " en cours...");
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            connODBC = DriverManager.getConnection("jdbc:odbc:"
              + jac.getProperty("odbc.base", "JATE"), jac.getProperty("odbc.user", ""), jac.getProperty("odbc.mdp", ""));
        } // endif
        return connODBC;
    } // end ouvertureBaseODBC()

    /**
     * EXECUTION D'UN ORDRE SELECT
     */
    private Connection ouvertureBaseODBC(String sql) {
      try {
        if ( connODBC != null ) {
          connODBC.close();
          connODBC = null;
        } // endif
        StringTokenizer token = new StringTokenizer(sql, " ,;");
        String nomBase = null;
        String user = null;
        String mdp = null;
        if (token.hasMoreElements()) token.nextElement();
        if (token.hasMoreElements()) nomBase = (String)token.nextElement();
        if (token.hasMoreElements()) user = (String)token.nextElement();
        if (token.hasMoreElements()) mdp = (String)token.nextElement();
        if (nomBase == null) return null;
        log("Ouverture de " + nomBase + " en cours...");
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        connODBC = DriverManager.getConnection("jdbc:odbc:" + nomBase, user, mdp);
      } catch (Exception e ) {
      	jaf.log(e); 
        return null;
      } // end catch
      return connODBC;
    } // end ouvertureBaseODBC()

    /**
     * EXECUTION D'UN ORDRE SQL
     */
    private void executeSQL(String sql) {
        String SQL = sql.trim();
        if ( SQL.equals("") ) return;
        if ( SQL.toUpperCase().startsWith("SELECT") ) {
            selectSQL(SQL);
            return;
        } // endif
        if ( SQL.toUpperCase().startsWith("CONNECT") ) {
            ouvertureBaseODBC(SQL);
        } else {
            updateSQL(SQL);
        } // endif
    } // end selectSQL

    /**
     * EXECUTION D'UN ORDRE SELECT
     */
    private void selectSQL(String sql) {
        String methode = "selectSQL";
        try {
            Statement stmt = ouvertureBaseODBC().createStatement();
            log(sql);
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData md = rs.getMetaData();
            int nCols = md.getColumnCount();
            // impression des entétes
            StringBuffer ligne = new StringBuffer();
            ligne.append("     ");
            for (int i= 1; i < nCols + 1; i++ ) {
                int isize = md.getColumnDisplaySize(i);
                isize = (isize > 64) ? 64 : isize;
                ligne.append(Ja.left(md.getColumnName(i) + Ja.fill(' ', isize), isize) + " ");
            } // endfor
            log(ligne.toString());
            ligne = null;
            ligne = new StringBuffer();
            ligne.append("-----");
            for (int i= 1; i < nCols + 1; i++ ) {
                int isize = md.getColumnDisplaySize(i);
                isize = (isize > 64) ? 64 : isize;
                ligne.append(Ja.left(Ja.fill('-', isize), isize) + "-");
            } // endfor
            log(ligne.toString());
            ligne = null;
            // boucle d'affichage
            int qLigne = 1;
            while ( rs.next() ) {
                ligne = new StringBuffer();
                ligne.append(Ja.right(Ja.fill(' ', 4) + qLigne++, 4) + " ");
                for (int i= 1; i < nCols + 1; i++ ) {
                    String temp = Ja.left(rs.getString(i));
                    int isize = md.getColumnDisplaySize(i);
                    isize = (isize > 64) ? 64 : isize;
                    if ( temp == "" ) {
                        ligne.append(Ja.fill(' ', isize) + " ");
                    } else {
                        ligne.append(Ja.left(temp + Ja.fill(' ', isize), isize) + " ");
                    } // endif
                    temp = null;
                } // endfor
                log(ligne.toString());
                ligne = null;
            } // end while
            log("commande SQL exécutée avec succés");
            stmt.close();
        } catch (Exception e ) {
        	jaf.log(e); 
        } // end catch
    } // end selectSQL

    /**
     * EXECUTION D'UN ORDRE DE MAJ
     */
    private void updateSQL(String sql) {
        try {
            Statement stmt = ouvertureBaseODBC().createStatement();
            log(sql);
            int iret = stmt.executeUpdate(sql);
            if ( iret > 0 ) {
                log("" + iret + " colonnes mises é jour");
            } // endif
            log("commande SQL exécutée avec succés");
            stmt.close();
        } catch (Exception e ) {
        	jaf.log(e); 
        } // end catch
    } // end updateSQL()

    /**
     *  Démarrage du THREAD
     */
    public void run() {
        Thread      myThread = Thread.currentThread();

        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        try {
            while (thread == myThread ) {
                switch ( typeCommande ) {
                    case COMMANDE_SQL:
                    interpreteSQL();
                    break;
                    case COMMANDE_DOS:
                    	if ( ligneCommande.startsWith("http://") ) {
                            interpreteUrl();
                    	} else {
                            interpreteShell();
                    	} // endif
                    break;
                    case COMMANDE_SOCKET:
                    interpreteSocket();
                    break;
                    case COMMANDE_EDIT:
                    interpreteEdit();
                    break;
                } // end switch
                thread = null;
            } // end while
        } catch (Exception e) {
        	jaf.log(e); 
            thread = null;
        } // end try
    } // end run

    /**
     * demande d'interprétation du script
     */
    public void executer(String pobjetCommande, String ptexteCommande, String ptitreCommande, String pligneCommande) {
        typeCommande = COMMANDE_DOS;

        objetCommande = pobjetCommande;
        titreCommande = ptitreCommande;
        ligneCommande = pligneCommande;
        texteCommande = ptexteCommande;

        /*
        jaf.log("objetCommande=" + objetCommande);
        jaf.log("titreCommande=" + titreCommande);
        jaf.log("ligneCommande=" + ligneCommande);
        jaf.log("texteCommande=" + texteCommande);
        */

        if ( ligneCommande.indexOf("{$repertoireAtelier}") != -1 ) {
            ligneCommande = Ja.remplace(ligneCommande, "{$repertoireAtelier}", System.getProperty("user.dir"));
        } // endif
        if ( ligneCommande.indexOf("{$fichier}") != -1 ) {
            ligneCommande = Ja.remplace(ligneCommande, "{$fichier}", pobjetCommande);
        } // endif
        // lecture des paramétres du script {paramétre 1} {paramétre 2}
        // un paramétre peut aussi étre de la forme {nom param, val1, val2, val3}
        // dans ce cas il faudra présenter une listeBox
        JaInputDialog jai = null;
        int deb = ligneCommande.indexOf("{");
        if ( ligneCommande.charAt(deb + 1) == '$' ) {
            // il faut sauter la commande de type $ecouteSocket;
            deb = ligneCommande.indexOf("{", deb + 1);
        } // endif
        Hashtable hparam = new Hashtable(); // pour éviter de demander 2 fois le méme paramétre
        while ( deb != -1 ) {
            int fin = ligneCommande.indexOf("}", deb + 1);
            if ( fin == -1 ) break;
            String param = Ja.mid(ligneCommande, deb + 2, fin - deb - 1);
            if ( hparam.get(param) == null ) {
                if ( jai == null ) jai = new JaInputDialog(jaf, objetCommande, objetCommande);
                int virgule = param.indexOf(",");
                if ( virgule != -1 ) {
                    jai.add(param.substring(0, virgule), Ja.left(param.substring(virgule + 1)));
                } else {
                    jai.add(param);
                } // endif
                hparam.put(param, "");
            } // endif
            deb = ligneCommande.indexOf("{", deb + 1);
        } // endwhile
        hparam = null;
        if ( jai != null ) {
            jai.showInputDialog();
            if ( jac.getProperty("JaInputDialog.return").equals("OK") ) {
                // remplacement des paramétres par la valeur saisie
                String ordreResult = ligneCommande;
                deb = ligneCommande.indexOf("{");
                if ( ligneCommande.charAt(deb + 1) == '$' ) {
                    // il faut sauter la commande de type $ecouteSocket;
                    deb = ligneCommande.indexOf("{", deb + 1);
                } // endif
                while ( deb != -1 ) {
                    int fin = ligneCommande.indexOf("}", deb + 1);
                    if ( fin == -1 ) break;
                    String param = Ja.mid(ligneCommande, deb + 2, fin - deb - 1);
                    int virgule = param.indexOf(",");
                    if ( virgule != -1 ) {
                        ordreResult = Ja.remplace(ordreResult, "{" + param + "}",
                            jac.getProperty(objetCommande + "." + param.substring(0, virgule)));
                    } else {
                        ordreResult = Ja.remplace(ordreResult, "{" + param + "}",
                            jac.getProperty(objetCommande + "." + param));
                    } // endif
                    deb = ligneCommande.indexOf("{", deb + 1);
                } // endwhile
                ligneCommande = ordreResult;
            } else {
                return;
            } // endif
        } // endif
        if ( ligneCommande.indexOf("{$ecouteSocket;") != -1 ) {
            typeCommande = COMMANDE_SOCKET;
        } // endif
        if ( ligneCommande.indexOf("{$socket;") != -1 ) {
            typeCommande = COMMANDE_SOCKET;
        } // endif
        if ( ligneCommande.indexOf("{$mail;") != -1 ) {
            typeCommande = COMMANDE_MAIL;
        } // endif
        if ( ligneCommande.indexOf("{$play;") != -1 ) {
            typeCommande = COMMANDE_PLAY;
        } // endif
        if ( ligneCommande.indexOf("{$getHttp;") != -1 ) {
            typeCommande = COMMANDE_GETHTTP;
        } // endif
        if ( ligneCommande.indexOf("{$edit;") != -1 ) {
            // est exécuté de suite dans le méme thread
            interpreteEdit();
            return;
        } // endif

        try {
            this.thread = new Thread(this, this.titreCommande);
            this.thread.start();
        } catch ( Exception e ) {
        	jaf.log(e); 
            thread = null;
        } // end try
    } // fin executer()

    public void log(String mess) {
        queue.add(mess);
    } // end log()
    public void log(String var, String val) {
        log(var + ": " + val);
    } // end
    public void log(int ligne, String classe, Exception e) {
        log(classe + "_" + ligne, e.getMessage());
    } // end

    // Ecouteur de la queue
    protected class QueueListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            while ( queue.size() > 0 ) {
                String str = (String)queue.removeFirst();
                textArea.append(str + "\n");
                try {
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                    textArea.requestFocus();
                } catch (Exception ex ) {
                	jaf.log(ex); 
                } // end try
                str = null;
            } // end while
        } // end actionPerformed

    } // end class QueueListener

    /**
     * Ecouteur du clavier
     */
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        if ( e.getKeyCode() == KeyEvent.VK_F12 ) {
            // effacement du texte
            buttonDel.doClick();
        } // endif
        if ( e.getKeyCode() == KeyEvent.VK_F5 ) {
            // exécution de la ligne de commande courante
            menuExecuter.doClick();
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
            	jaf.log(ex); 
            } // end try
        } // endif
        if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
            try {
                int pos = textArea.getCaretPosition();
                int lig = textArea.getLineOfOffset(pos);
                int start = textArea.getLineStartOffset(lig);
                int end = textArea.getLineEndOffset(lig);
                String text = textArea.getText(start, end - start);
                // retour é la ligne suivante méme colonne que la ligne précédente
                // recherche du 1er caractère sur la ligne courante
                StringBuffer buf = new StringBuffer();
                buf.append("\n");
                for (int i=0, len=text.length(); i < len && text.charAt(i) == ' '; i++ ) {
                    buf.append(" ");
                } // endfor
                textArea.insert(buf.toString(), pos);
                buf = null;
                e.consume();
            } catch (Exception ex ) {
            	jaf.log(ex); 
            } // end try
        } // endif
    } // end keyPressed

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
    protected void fireJaPaneEventSetTexte(String mess) {
        JaPaneEvent e = new JaPaneEvent(this, mess);
        for (int i=0, n = listeners.size(); i < n; i++ ) {
            JaPaneListener listener = (JaPaneListener)listeners.elementAt(i);
            listener.jaPaneEventSetTexte(e);
        } // end for
    }
} // end class
