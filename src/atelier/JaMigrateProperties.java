package atelier;
/**
 * Migration des propriétés de l'atelier dans la nouvelle version
 * &lt; 1.5 ==&gt; 1.6
 * @author philippe.billerot@laposte.net
 * @version 1.O
 * 
 */
public class JaMigrateProperties {
	JaProperties jac = null;
	JaFrame jaf = null;
	
	public JaMigrateProperties(JaFrame jaf) {
		this.jaf = jaf;
		jac = jaf.getJaProperties();
		migrateTo16();
	} // endif
	
	private void migrateTo16() {
		if ( jac.getProperty("version.16", "").length() > 0 ) return;
		// outils.1.titre = valTitre
		// outils.1.cmd = valCmd
		// outils.1.var = valTitre
		// outils.1.val = valCmd
		for (int i = 1; ; i++) {
			String cleVar = "outils." + i + ".titre";
			String cleVal = "outils." + i + ".cmd";
			if ( jac.getProperty(cleVar, "").length() > 0 ) {
				jac.setProperty("outils." + i + ".var", jac.getProperty(cleVar));
				jac.setProperty("outils." + i + ".val", jac.getProperty(cleVal));
				jac.remove(cleVar);
				jac.remove(cleVal);
			} else {
				break;
			} // endif
		} // end for
		// actions.1.titre = valTitre
		// actions.1.cmd = valCmd
		// actions.1.var = valTitre
		// actions.1.val = valCmd
		for (int i = 1; ; i++) {
			String cleVar = "action." + i + ".titre";
			String cleVal = "action." + i + ".cmd";
			if ( jac.getProperty(cleVar, "").length() > 0 ) {
				jac.setProperty("action." + i + ".var", jac.getProperty(cleVar));
				jac.setProperty("action." + i + ".val", jac.getProperty(cleVal));
				jac.remove(cleVar);
				jac.remove(cleVal);
			} else {
				break;
			} // endif
		} // end for
		Ja.debug("migration du fichier " + jac.getNomFichier() + " en version 16");
		jac.setProperty("version.16", "ok");
		jac.save();		
	} // end migrateTo16
}
