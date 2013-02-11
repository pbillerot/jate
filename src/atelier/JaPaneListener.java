package atelier;
import java.util.*;

/**
 * Interface entre la frame principale et les panneaux de cette frame
 * @author  : Philippe.BILLEROT@laposte.net
 * @version : 1.0 du 12 novembre 2000
 */
public interface JaPaneListener extends EventListener {

    public void jaPaneEventTitle(JaPaneEvent e);
    public void jaPaneEventEdit(JaPaneEvent e, boolean dansNouvelleFenetre);
    public void jaPaneEventSetTexte(JaPaneEvent e);
    public void jaPaneEventRemove(JaPaneEvent e);
    public void jaPaneEventAction(JaPaneEvent e);

} // end JaTextPaneListener
