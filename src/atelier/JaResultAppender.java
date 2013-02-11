package atelier;

import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * @author billerot
 * Appender qui récupère les événements de log pour les afficher
 * dans la fenêtre du bas de l'atelier
 *
 */
public class JaResultAppender implements Appendable {

	private final JTextComponent textComponent;

	public JaResultAppender(JTextComponent component) {
		this.textComponent = component;
	}

	/**
	 * Insertion d'un chaine dans un document,
	 * en déplacant les scrollbars si neccessaire
	 */
	private void insertString(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// On récupèrel le composant graphique
				JTextComponent comp = JaResultAppender.this.textComponent;
				// On récupère le document associé
				Document doc = comp.getDocument();
				try {
					// On insert le text recu à la fin du document :
					doc.insertString(doc.getLength(), text, null);
					// Et on déplace les scrollbars :
					comp.scrollRectToVisible( comp.modelToView(doc.getLength()));
				} catch (Exception e) {
					throw new RuntimeException(e); // Ne devrait pas arriver
				}
			}
		});
	}

	/*
	 * On implémente les diverses méthodes de l'interface Appendable
	 * en se contentant de renvoyer vers la méthode insertString() :
	 * 
	 */

	public Appendable append(char c) throws IOException {
		insertString(Character.toString(c));
		return this;
	}
	public Appendable append(CharSequence csq) throws IOException {
		insertString(csq.toString());
		return this;
	}
	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		insertString(csq.subSequence(start, end).toString());
		return this;
	}
}

