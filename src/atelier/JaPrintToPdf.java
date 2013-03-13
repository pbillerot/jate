package atelier;
/**
 * @(#)JaPrintToPdf.java
 *
 * @author  philippe.billerot@ca-cmds.fr
 *
 * Conversion d'un fichier Texte en fichier pdf
 *
 */

import java.awt.Color;
import java.io.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

public class JaPrintToPdf {
    private final String nomClasse = "JaPrintToPdf";
    private JaFrame      jaf = null;
    private JaProperties jac = null;

    public JaPrintToPdf(String fichier, JaFrame pjaf) {
        this.jaf = pjaf;
        this.jac = jaf.getJaProperties();

        // creation du document
        Document document = new Document(PageSize.A4, 20, 20, 40, 10);
        try {
            // création des polices
            Font titre = new Font(Font.HELVETICA, 14, Font.NORMAL, Color.red);
            Font corps = new Font(Font.COURIER, 8, Font.BOLD, (Color.blue).darker());

            // creation du writer
            File tempFile = File.createTempFile("Jate", ".pdf");
            //tempFile.setWritable(true);
            tempFile.deleteOnExit();

            PdfWriter.getInstance(document, new FileOutputStream(tempFile));
            // meta information
            document.addAuthor(nomClasse);
            document.addTitle(fichier);
            //
            HeaderFooter header = new HeaderFooter(new Phrase(fichier, titre), false);
            header.setAlignment(Element.ALIGN_RIGHT);
            document.setHeader(header);
            HeaderFooter footer = new HeaderFooter(new Phrase("page "), true);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.setFooter(footer);

            // ouverture du document
            document.open();

            String encoding = Ja.getEncoding(fichier);
            BufferedReader in = new BufferedReader(
            		new InputStreamReader(new FileInputStream(fichier), encoding));
            String str;
            while ((str = in.readLine()) != null ) {
                document.add(new Paragraph(str + "\n", corps));
            } // end while
            in.close();
            document.close();
            // lancement de Pdf Reader
            String commande = jac.getParam("Jate", "pdfreader") + " " + tempFile.getPath();
            jaf.log(commande);
            Process process = Runtime.getRuntime().exec(commande);
            BufferedReader inProcess = new BufferedReader(new InputStreamReader(process.getInputStream()) );
            BufferedReader erProcess = new BufferedReader(new InputStreamReader(process.getErrorStream()) );
            new JaDosOutput(jaf, inProcess);
            new JaDosOutput(jaf, erProcess);
            inProcess = null;
            erProcess = null;
        } catch(Exception e) {
            jaf.log(e);
        }
    } // end constructeur

} // end class
