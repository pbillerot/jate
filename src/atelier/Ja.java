package atelier;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.text.*;
import java.sql.*;
import javax.swing.*;
import java.awt.*;

/**
 *  Un ensemble de fonctions trés utiles d'ordre générale
 *  @author  : Philippe.BILLEROT@laposte.net
 *  @version : 1.0 du 16/04/2000
 */
public class Ja {
private static final String nomClasse = "Ja";

	public static Font getFont()
	{
		Font font = new Font(Font.MONOSPACED, Font.BOLD, 14);
		return font;
	}
	public static Font getFontBouton() {
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, 11);
		return font;
	}
	
    /**
     * chargement de parametres
     */
    public static Properties loadProperties(String fichier, Object objClass) {
        String methode = "loadProperties";
        Properties  properties = null;
        try {
            // lecture du fichier paramétre.properties
            properties = new Properties();
            properties.load(objClass.getClass().getClassLoader().getResourceAsStream(fichier));
        } catch (Exception e) {
            debug(24, nomClasse, methode, e);
        } // end try
        return properties;
    } // fin loadProperties()

    /**
     * enregistrement des parametres
     */
    public static void saveProperties(Properties properties, String fichier) {
        String methode = "saveProperties";
        try {
            properties.store(new FileOutputStream(fichier), null);
        } catch (Exception e) {
            debug(40, nomClasse, methode, e);
        } // end try
    } // fin saveProperties()

    /**
     * Chargement d'un JTextArea à partir d'une URL
     * @param textArea
     * @param url
     */
    public static void loadTextArea(JTextArea textArea, URL url, String fileEncoding)
    {
      textArea.setText("");
      try {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), fileEncoding));
        StringBuffer str = new StringBuffer();
        String ligne;
        while ((ligne = in.readLine()) != null)
        {
          str.append(ligne + "\n");
        }
        textArea.setText(str.toString());
        in.close();
        str = null;
      } catch (Exception e) {
        debug(stackTraceToString(e));
      }
    }
    /**
     * chargement d'un text JTextArea à partir d'un fichier
     */
    public static void loadTextArea(JTextArea textArea, String fichier, JaFrame jaf) {
        String methode = "loadTextArea";
        textArea.setText("");
        try {
            BufferedReader in = new BufferedReader(new FileReader( fichier ));
//            BufferedReader in = new BufferedReader(new InputStreamReader(
//            		new URL(fichier.indexOf(":")==-1 ? "file:" + fichier : fichier).openStream()
//            		, System.getProperty("file.encoding")));
            StringBuffer str = new StringBuffer();
            String ligne;
            while ( (ligne = in.readLine()) != null ) {
                str.append(ligne + "\n");
            } // endwhile
            in.close();
            textArea.setText(str.toString());
            str = null;
        } catch (Exception e) {
            debug(52, nomClasse, methode, e);
            jaf.log(e);
        } // end try
    } // fin loadTextArea

    /**
     * enregistrement d'un text JTextArea dans un fichier
     */
    public static void saveTextArea(JTextArea textArea, String fichier, JaFrame jaf) {
        String methode = "saveTextArea";
        // transformation des simples lf ou cr en crlf
        String text = textArea.getText();
        StringBuffer buf = new StringBuffer();
        int len = textArea.getDocument().getLength();
        char pre = ' ';
        for(int i=0; i < len; i++ ) {
            char car = text.charAt(i);
            switch ( car ) {
                case '\n':
                    if ( pre != '\r' ) buf.append('\r');
                default:
                    pre = car;
                    buf.append(car);
            } // end switch
        } // end for
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream( fichier ));
            out.print(buf.toString());
            out.close();
            text = null;
            buf = null;
        } catch (Exception e) {
            debug(95, nomClasse, methode, e);
            jaf.log(e);
        } // end try
    } // fin saveTextArea

    /**
     * obtenir une connexion é une base de données ODBC
     */
    public static Connection getConnectionJdbc(String nomODBC) {
        String methode = "getConnectionJdbc";
        // connection é la base de données
        // nom user et mot de passe é blanc par défaut
        Connection conn = null;
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            conn = DriverManager.getConnection("jdbc:odbc:" + nomODBC, "", "");
        } catch (Exception e) {
            debug(80, nomClasse, methode, e);
        } // end try
        return conn;
    } // fin getConnectionJdbc

    /**
     * ordre SQL
     */
    public static ResultSet getselectSQL(Connection conn, String ordreSQL) {
        Statement           stmt = null;
        ResultSet           rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(ordreSQL);
            stmt.close();
        } catch (Exception e ) {
            debug(154, nomClasse, ordreSQL, e);
            return null;
        } // end catch
        return rs;
    } //

    /**
     * Ajout de zéro devant une chaéne de caractéres
     */
    public static String prefixeZero(String str, int longueurStrSortie) {
        String  buf = "";
        for (int i=0; i < (longueurStrSortie - str.length()); i++ ) {
            buf = buf + "0";
        } // end for
        buf = buf + str;
        return buf;
    } // fin for

    /**
     * Encodage du HTML
     */
    public static String encodeHTML(String str) {
        String res = "";
        if ( str == null ) return res;
        for ( int i = 0; i < str.length(); i++ ) {
            char c = str.charAt(i);
            switch ( c ) {
                case '<': res += "&lt;"; break;
                case '>': res += "&gt;"; break;
                case '&': res += "&amp;"; break;
                default: res += c;
            } // end switch
        } // end for
        return res;
    } // end encodeHTML

    /**
     * Encodage des paramétres é une URL par exemple
     */
    //public static String encodeURL(String str) {
    //    return URLEncoder.encode(str);
    //} // end encodeHTML

    /**
     *  retourne les n 1er caractéres de gauche de la chaéne
     */
    public static String left(String pstr, int plen) {
        String str;
        str = pstr;
        if ( pstr == null ) str = "";
        if ( pstr.equals("null") ) str = "";
        if ( str.length() > plen ) {
            str = str.substring(0, plen);
        } // end-if
        return str.trim();
    } // end left

    /**
     *  retourne les n 1er caractéres de gauche de la chaéne
     */
    public static String left(String pstr) {
        String str;
        if ( pstr == null ) {
            str = "";
        } else {
            str = pstr.trim();
        } // endif
        return str;
    } // end left

    /**
     *  retourne une chaéne é partir d'une position de caractére sur une longueur donnée
     *  (la position commence é 1)
     */
    public static String mid(String pstr, int pos, int plen) {
        String str;
        str = pstr;
        if ( pos == 0 ) pos = 1;
        if ( pstr == null ) str = "";
        if ( str.length() + 1 > pos ) {
            str = left(pstr.substring(pos-1), plen);
        } // end-if
        return str;
    } // end left

    /**
     *  retourne une chaéne é partir d'une position de caractére
     *  (la position commence é 1)
     */
    public static String mid(String pstr, int pos) {
        String str;
        str = pstr;
        if ( pos == 0 ) pos = 1;
        if ( pstr == null ) str = "";
        if ( str.length() + 1 > pos ) {
            str = left(pstr.substring(pos-1));
        } // end-if
        return str;
    } // end left

    /**
     *  retourne les n caractéres de droite de la chaéne
     */
    public static String right(String pstr, int plen) {
        String str = "";
        str = pstr;
        if ( pstr == null ) str = "";
        if ( pstr.equals("null") ) str = "";
        if ( str.length() > plen ) {
            str = str.substring(str.length() - plen);
        } // end-if
        return str;
    } // end right

    /**
     *  remplacer un string par un autre
     */
    public static String remplace(String source, String ancien, String nouveau) {
        StringBuffer buf = new StringBuffer();
        String str = source;
        int pos = str.indexOf(ancien);
        while ( pos != -1 ) {
            buf.append(str.substring(0, pos));
            buf.append(nouveau);
            pos = pos + ancien.length();
            if ( pos < str.length() ) {
                str = str.substring(pos);
            } else {
                str = "";
                break;
            } // endif
            pos = str.indexOf(ancien);
        } // end while
        buf.append(str);
        return buf.toString();
    } // endif

    /**
     *  retourne la chaéne remplit avec le caractére
     */
    public static String fill(char ch, int len) {
        StringBuffer str = new StringBuffer(len);
        for (int i=0; i < len; i++ ) str.append(ch);
        return str.toString();
    } // end right

    /**
     *  transforme la chaéne dans le format donné
     *  ("12", "ZZ99") -> bb12
     *  ("12", "9999") -> 0012
     *  ("12", "prix : 9999") -> prix : 0012
     */
    public static String format(String num, String pattern) {
    int             lenNombre, lenPattern, i, j;
    StringBuffer    buf = new StringBuffer();
    String          nombre;

        nombre = left(num);

        lenNombre = nombre.length();
        lenPattern = pattern.length();

        for (i = 0, j = 0; i < lenPattern; i++) {
            if ( i < (lenPattern - lenNombre) ) {
                switch ( pattern.charAt(i) ) {
                    case 'Z':
                        buf.append(" ");
                        break;
                    case '9':
                        buf.append("0");
                        break;
                    default:
                        buf.append(pattern.substring(i, i + 1));
                        break;
                } // end switch
            } else {
                buf.append(nombre.substring(j, j + 1));
                j++;
            } // endif
        } // endfor
        return buf.toString();
    } // end format()

    /*
     *  retourne la date du systéme sous la forme : 25/02/2000 18:32
     */
    public static String getDateMaj() {
        Calendar calend = Calendar.getInstance();
        return format("" + calend.get(Calendar.DAY_OF_MONTH), "99") + "/" + format("" + (1 + calend.get(Calendar.MONTH)), "99") + "/" +  format("" + calend.get(Calendar.YEAR), "9999") + " " + format("" + calend.get(Calendar.HOUR_OF_DAY), "99") + ":" + format("" + calend.get(Calendar.MINUTE), "99") + ":" + format("" + calend.get(Calendar.SECOND), "99");
    } // end getDateMaj()

    /*
     *  retourne une chaéne formatée d'un nombre décimal
     */
    public static String decimalNumber(double nombre, String pattern ) {
        NumberFormat nf = NumberFormat.getInstance();
        ((DecimalFormat)nf).applyPattern(pattern);
        return nf.format(nombre);
    } // end decimalNumber()

    /**
     * Affichage du message de debug
     * sur la console java
     */
    public static void debug(String mess) {
        System.out.println(getDateMaj() + " " + mess);
    } // end debug()
    public static void debug(int ligne, String nomClasse, String methode, String mess, Exception e) {
        debug(ligne, nomClasse, methode + ": " + mess, e );
    } // end debug()
    public static void debug(int ligne, String nomClasse, String methode, StringBuffer mess, Exception e) {
        debug(ligne, nomClasse, methode + ": " + mess.toString(), e );
    } // end debug()
    public static void debug(int ligne, String nomClasse, StringBuffer mess, Exception e) {
        debug(ligne, nomClasse, mess.toString(), e );
    } // end debug()
    public static void debug(int ligne, String nomClasse, Exception e) {
        debug(nomClasse + "(" + ligne + ") " + e.getMessage());
    } // end debug()
    public static void debug(String name, String value) {
        debug(name + ": " + value);
    } // end debug()
    public static void debug(int ligne, String classe) {
        debug(classe + " ligne " + ligne);
    } // end debug()
    public static void debug(int ligne) {
        debug("ligne " + ligne);
    } // end debug()
    public static void debug(int ligne, String nomClasse, String mess, Exception e) {
        debug(nomClasse + "(" + ligne + ") " + mess + " [" + e.getMessage() + "]" );
    } // end debug()

    /**
     * Display an error message in a dialog box.
     * @param message The message to display
     */

    public static void showError(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title,
                                      JOptionPane.ERROR_MESSAGE);
    } // end showError

    /**
     * Display a sample message in a dialog box.
     * @param message The message to display
     */

    public static void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

      public static void setCursorOnWait(Component comp, boolean on) {
        if (on)
          comp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        else
          comp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Used to 'beep' the user.
     */

    public static void beep() {
        Toolkit.getDefaultToolkit().beep();
    }

    /**
     * This methods is used to determine screen's dimensions.
     * @return A <code>Dimension</code> object containing screen's resolution
     */
    public static Dimension getScreenDimension() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * A very nice trick is to center windows on screen, this method
     * helps you to to that.
     * @param compo The <code>Component</code> to center
     */
    public static void centerComponent(Component compo) {
        compo.setLocation(new Point((getScreenDimension().width - compo.getSize().width) / 2,
                          (getScreenDimension().height - compo.getSize().height) / 2));
    }

    /**
     * A very nice trick is to center dialog with their parent.
     * @param parent The parent <code>Component</code>
     * @param child The <code>Component</code> to center
       */
    public static void centerComponentChild(Component parent, Component child) {
        Rectangle par = parent.getBounds();
        Rectangle chi = child.getBounds();
        child.setLocation(new Point(par.x + (par.width - chi.width) / 2,
                                    par.y + (par.height - chi.height) / 2));
    }

    public static Image getImage(String picture) {
        return Toolkit.getDefaultToolkit().getImage(Ja.class.getResource(picture));
    }

    public static Icon getIcon(String picture) {
        return new ImageIcon(Toolkit.getDefaultToolkit().getImage(Ja.class.getResource(picture)));
    }


    /**
     *    pour trier la DefaultListModel donnée en paramétre
     *    ne marche pas, je ne comprends pas.
     */
    public static void sortList2(DefaultListModel listModel) {
        Set set = new TreeSet();
        for(Enumeration ee = listModel.elements(); ee.hasMoreElements(); ) {
            String cle = (String)ee.nextElement();
            set.add(cle);
        } // endif
        listModel.clear();
        for(Iterator it = set.iterator(); it.hasNext(); ) {
            String cle = (String)it.next();
            listModel.addElement(cle);
        } // endif
        set = null;
    } // end sortDefaultListModel

    /**
     *    pour trier la DefaultListModel donnée en paramétre
     */
    public static void sortList(DefaultListModel listModel) {
        int iMax = listModel.size();
        String[] stringSorted = new String[iMax];
        int i = 0;
        for(Enumeration ee = listModel.elements(); ee.hasMoreElements(); ) {
            stringSorted[i++] = (String)ee.nextElement();
        } // endif
        Arrays.sort(stringSorted, String.CASE_INSENSITIVE_ORDER);
        listModel.clear();
        for( i=0; i < iMax; i++) {
            listModel.addElement(stringSorted[i]);
            stringSorted[i] = null;
        } // endif
        stringSorted = null;
    } // end sortDefaultListModel

    /**
     * pour retourner un vector é parttir d'une liste d'item séparés par une 
     * virgule
     */
    public static Vector getVector(String listeVirgule) {
    	Vector v = new Vector();
    	StringTokenizer token = new StringTokenizer(listeVirgule, ",");
    	while ( token.hasMoreElements() ) {
    		String item = (String)token.nextElement();
    		if ( Ja.left(item).equalsIgnoreCase("_") ) {
    			v.addElement("");
    		} else {
    			v.addElement(Ja.left(item));
    		} // endif
    	} // end while    	
    	return v;
    } // end
    
	/**
	 * Method getItem.
	 * (H) CRCP --> H
	 * @param value
	 * @return String
	 */
    public static String getItem(String item) {
        if ( item.startsWith("(") ) {
        	return item.substring(1, item.indexOf(")"));
        } // endif
        return item;
    } // end getItem
	/**
	 * Method getItem.
	 * @param v le vecteur de tous les items complets
	 * @param item X ou label
	 * @return String l'item complet correspondant é la lettre X --> "(X) label "
	 */
    public static String getItem(Vector v, String item) {
    	Enumeration enumeration = v.elements();
    	while (enumeration.hasMoreElements()) {
			String element = (String) enumeration.nextElement();
			if ( element.startsWith("(" + item + ")") ) {
				return element;
			} // endif  
			if ( element.equalsIgnoreCase(item) ) {
				return element;
			} // endif
    	} // endwhile
        return "";
    } // end getItem

	public static String stackTraceToString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.close();
		String s0 = Ja.remplace(sw.toString(), "\r\n", " - ");
		String s1 = Ja.remplace(s0, "\r", " ");
		String s2 = Ja.remplace(s1, "\n", " ");
		String s3 = Ja.remplace(s2, "\t", " ");
		String s4 = Ja.remplace(s3, "  ", " ");
		String s5 = Ja.remplace(s4, "  ", " ");
		return s5;
	} // end stackTraceToString
	
    public static String stringToHex(String base)
    {
    	StringBuffer buffer = new StringBuffer();
    	int intValue;
    	for(int x = 0; x < base.length(); x++)
    	{
    		int cursor = 0;
    		intValue = base.charAt(x);
    		String binaryChar = new String(Integer.toBinaryString(base.charAt(x)));
    		for(int i = 0; i < binaryChar.length(); i++)
    		{
    			if(binaryChar.charAt(i) == '1')
    			{
    				cursor += 1;
    			}
    		}
    		if((cursor % 2) > 0)
    		{
    			intValue += 128;
    		}
    		buffer.append(Integer.toHexString(intValue) + " ");
    	}
    	return buffer.toString();
    }
} // end Console

