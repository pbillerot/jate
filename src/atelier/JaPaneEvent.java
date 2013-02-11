package atelier;
import java.util.*;

/**
 * @author  : Philippe.BILLEROT@laposte.net
 * @version : 1.0 du 12 novembre 2000
 */
public class JaPaneEvent extends EventObject {
    String message = "";
    String objetCommande = "";
    String texteCommande = "";
    String titreCommande = "";
    String ligneCommande = "";
    public JaPaneEvent(Object objet, String pmessage) {
        super(objet);
        message = pmessage;
    }
    public JaPaneEvent(Object objet, String pobjetCommande, String ptexteCommande, String ptitreCommande, String pligneCommande) {
        super(objet);
        objetCommande = pobjetCommande;
        texteCommande = ptexteCommande;
        titreCommande = ptitreCommande;
        ligneCommande = pligneCommande;
    }
    public String getMessage() {
        return message;
    }
    public String getObjetCommande() {
        return objetCommande;
    }
    public String getTexteCommande() {
        return texteCommande;
    }
    public String getTitreCommande() {
        return titreCommande;
    }
    public String getLigneCommande() {
        return ligneCommande;
    }
} // end TextPaneEvent
