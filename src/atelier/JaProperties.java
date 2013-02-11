package atelier;
import java.io.*;
import java.util.*;
import javax.swing.*;

import java.awt.*;

/**
 * @author  : Philippe.BILLEROT@laposte.net
 * @version : 1.0 du 16/04/2000
 */
public class JaProperties extends Properties implements Serializable {
    private static final String    nomClasse = "JaProperties";
    private String fichier;

    /**
     * chargement de parametres
     */
    public JaProperties(String fichier, Object objClass) {
        super();
        this.fichier = fichier;
        try {
            load(objClass.getClass().getClassLoader().getResourceAsStream(fichier));
        } catch (Exception e) {
            Ja.debug(24, nomClasse, fichier, e);
        } // end try
    }
    
    public String getParam(String groupe, String cle, String defaut) {
        for (int i = 1; ; i++ ) {
            String key = this.getProperty(groupe + "." + i + "." + JaFrame.CLEVAR, "");
            if ( key.equals("") ) {
                break;
            } // endif
            if ( key.equalsIgnoreCase(cle)) {
                return this.getProperty(groupe + "." + i + "." + JaFrame.CLEVAL, "");
            } // endif
        } // end for
        return defaut;
    }
    public String getParam(String groupe, String cle) {
        return this.getParam(groupe, cle, "");
    }
    public String getNomFichier() {
    	return fichier;
    } // endif
    public void setNomFichier(String fichier) {
    	this.fichier = fichier;
    }
    public JaProperties(String fichier) {
        this.fichier = fichier;
        load(fichier);
    }
    public JaProperties() {
        this.fichier = "";
    }

    public void load(String fichier) {
        this.fichier = fichier;
        try {
            this.clear();
            File file = new File(fichier);
            if (file.exists()) load(new FileInputStream(fichier));
            file = null;
        } catch (Exception e) {
            Ja.debug(37, nomClasse, fichier, e);
        } // end try
    }

    public void reload() {
        try {
            this.clear();
            load(new FileInputStream(fichier));
        } catch (Exception e) {
            Ja.debug(40, nomClasse, fichier, e);
        } // end try
    }

    /**
     * enregistrement des parametres
     */
    public void save() {
        String methode = "save";
        try {
            store(new FileOutputStream(fichier), null);
        } catch (Exception e) {
            Ja.debug(40, nomClasse, methode, e);
        } // end try
    }

    public void getPosition(Component frame, String id) {
        frame.setBounds(
            Integer.parseInt(getProperty(id + ".x", "150")),
            Integer.parseInt(getProperty(id + ".y", "150")),
            Integer.parseInt(getProperty(id + ".sx", "450")),
            Integer.parseInt(getProperty(id + ".sy", "250"))
            );
    }

    public void savePosition(Component frame, String id) {
        setProperty(id + ".x", "" + frame.getX());
        setProperty(id + ".y", "" + frame.getY());
        setProperty(id + ".sx", "" + frame.getWidth());
        setProperty(id + ".sy", "" + frame.getHeight());
        save();
    }

    public String[][] toTable() {
        String[][] data = new String[this.size()][2];
        int i=0;
        for (Enumeration e = this.keys(); e.hasMoreElements(); i++) {
            String cle = (String)e.nextElement();
            data[i][0] = cle;
            data[i][1] = (String)this.get(cle);
        } // endfor
        return data;
    }

    public String[] keysToString() {
        String[] data = new String[this.size()];
        int i=0;
        for (Enumeration e = this.keys(); e.hasMoreElements(); i++) {
            String cle = (String)e.nextElement();
            data[i] = cle;
        } // endfor
        return data;
    }
    public String[] elementsToString() {
        String[] data = new String[this.size()];
        int i=0;
        for (Enumeration e = this.keys(); e.hasMoreElements(); i++) {
            String cle = (String)e.nextElement();
            data[i] = (String)this.get(cle);
        } // endfor
        return data;
    }

    public void keysToList(DefaultListModel list) {
        list.clear();
        int iMax = this.size();
        String[] stringSorted = new String[iMax];
        int i = 0;
        for (Enumeration e = this.keys(); e.hasMoreElements(); ) {
            stringSorted[i++] = (String)e.nextElement();
        } // endfor
        Arrays.sort(stringSorted, String.CASE_INSENSITIVE_ORDER);
        for( i=0; i < iMax; i++) {
            list.addElement(stringSorted[i]);
            stringSorted[i] = null;
        } // endif
        stringSorted = null;
    }
    public void elementsToList(DefaultListModel list) {
        list.clear();
        int iMax = this.size();
        String[] stringSorted = new String[iMax];
        int i = 0;
        for (Enumeration e = this.keys(); e.hasMoreElements(); ) {
            stringSorted[i++] = (String)e.nextElement();
        } // endfor
        Arrays.sort(stringSorted, String.CASE_INSENSITIVE_ORDER);
        for( i=0; i < iMax; i++) {
            list.addElement(Ja.left((String)this.getProperty(stringSorted[i])));
            stringSorted[i] = null;
        } // endif
        stringSorted = null;
    }

    public void saveTable(String[][] data) {
        int imax = data.length;
        this.clear();
        for (int i=0; i < imax; i++ ) {
            this.put(data[i][0], data[i][1]);
        } // endfor
        this.save();
    }
    public void saveFromListes(JList lkeys, JList lelements) {
        int imax = lkeys.getModel().getSize();
        this.clear();
        for (int i=0; i < imax; i++ ) {
            lkeys.setSelectedIndex(i);
            lelements.setSelectedIndex(i);
            this.put(lkeys.getSelectedValue(), lelements.getSelectedValue());
        } // endfor
        this.save();
    }
    public void saveFromListe(JList lkeys) {
        int imax = lkeys.getModel().getSize();
        this.clear();
        for (int i=0; i < imax; i++ ) {
            lkeys.setSelectedIndex(i);
            this.put(lkeys.getSelectedValue(), "");
        } // endfor
        this.save();
    }
    public void saveFromListe(String groupe, JList lkeys) {
    	int imax = lkeys.getModel().getSize();

    	for (int i = 0; ; i++) {
    		String key = getProperty(groupe + "." + i, "");
    		if (key.equals("")) {
    			break;
    		}
    		remove(groupe + "." + i);
    	}
    	for (int i = 0; i < imax; i++) {
    		lkeys.setSelectedIndex(i);
    		put(groupe + "." + i, lkeys.getSelectedValue());
    	}
    	save();
    }

    public void rename(String newNameFile) {
        fichier = newNameFile;
    }

    public void setSave(String key, String value) {
    	load(fichier);
    	put(key, value);
    	save();
    }
    public void groupeToList(String groupe, DefaultListModel list) {
    	list.clear();

    	int iMax = 0;
    	for (; ; iMax++) {
    		String valeur = getProperty(groupe + "." + iMax, "");
    		if (valeur.equals("")) {
    			break;
    		}
    	}
    	String[] stringSorted = new String[iMax];

    	int i = 0;
    	for (i = 0; i < iMax; i++) {
    		String valeur = getProperty(groupe + "." + i, "");
    		stringSorted[i] = valeur;
    		remove(groupe + "." + i);
    	}

    	Arrays.sort(stringSorted, String.CASE_INSENSITIVE_ORDER);
    	for (i = 0; i < iMax; i++) {
    		list.addElement(stringSorted[i]);
    		put(groupe + "." + i, stringSorted[i]);
    		stringSorted[i] = null;
    	}
    	stringSorted = (String[])null;
    }    
} // end Console

