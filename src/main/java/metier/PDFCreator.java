package metier;


import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import domain.Contact;
import domain.FormulaireConsent;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

public class PDFCreator {


    private static final Logger LOG = Logger.getLogger(PDFCreator.class.getName());

    public static void create(Path imagePath) throws Exception {
        String outputFileName = "/Users/janedoe/Simple.pdf";
        // Create a document and add a page to it
        PDDocument document = new PDDocument();
        PDPage page1 = new PDPage(PDRectangle.A4);
        // PDRectangle.LETTER and others are also possible
        PDRectangle rect = page1.getMediaBox();
        // rect can be used to get the page width and height
        document.addPage(page1);

        // Create a new font object selecting one of the PDF base fonts
        PDFont fontPlain = PDType1Font.HELVETICA;
        PDFont fontBold = PDType1Font.HELVETICA_BOLD;
        PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
        PDFont fontMono = PDType1Font.COURIER;

        // Start a new content stream which will hold the content that's about to be created
        PDPageContentStream cos = new PDPageContentStream(document, page1);

        int line = 0;

        // Define a text content stream using the selected font, move the cursor and draw some text
        cos.beginText();
        cos.setFont(fontBold, 16);
        cos.setNonStrokingColor(Color.BLUE);
        cos.newLineAtOffset(50, rect.getHeight() - 30*(++line));
        cos.showText("Déclaration de consentement pour l’utilisation des données");
        cos.endText();

        cos.beginText();
        cos.setFont(fontBold, 16);
        cos.setNonStrokingColor(Color.BLUE);
        cos.newLineAtOffset(50, rect.getHeight() - 15*(++line));
        cos.showText("santé et des échantillons à des fins de recherche");
        cos.endText();

        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(100, rect.getHeight() - 50*(++line));
        cos.showText("J’accepte que mes données de santé et mes échantillons biologiques collectés durant les");
        cos.endText();

        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(100, rect.getHeight() - 1*(++line));
        cos.showText(" soins (consultations ambulatoires et hospitalisations) soient utilisés à des fins de recherche");
        cos.endText();



        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(100, rect.getHeight() - 50*(++line));
        cos.showText("Hello World");
        cos.endText();

        cos.beginText();
        cos.setFont(fontItalic, 12);
        cos.newLineAtOffset(100, rect.getHeight() - 50*(++line));
        cos.showText("Italic");
        cos.endText();

        cos.beginText();
        cos.setFont(fontBold, 12);
        cos.newLineAtOffset(100, rect.getHeight() - 50*(++line));
        cos.showText("Bold");
        cos.endText();

        cos.beginText();
        cos.setFont(fontMono, 12);
        cos.setNonStrokingColor(Color.BLUE);
        cos.newLineAtOffset(100, rect.getHeight() - 50*(++line));
        cos.showText("Monospaced blue");
        cos.endText();

        // Make sure that the content stream is closed:
        cos.close();
//
//        PDPage page2 = new PDPage(PDRectangle.A4);
//        document.addPage(page2);
//        cos = new PDPageContentStream(document, page2);
//
//        // draw a red box in the lower left hand corner
//        cos.setNonStrokingColor(Color.RED);
//        cos.addRect(10, 10, 100, 100);
//        cos.fill();
//
//        // add two lines of different widths
//        cos.setLineWidth(1);
//        cos.moveTo(200, 250);
//        cos.lineTo(400, 250);
//        cos.closeAndStroke();
//        cos.setLineWidth(5);
//        cos.moveTo(200, 300);
//        cos.lineTo(400, 300);
//        cos.closeAndStroke();
//
//        // close the content stream for page 2
//        cos.close();

        PDPage page3 = new PDPage(PDRectangle.A4);
        document.addPage(page3);
        cos = new PDPageContentStream(document, page3);

        // add an image
        try {
            PDImageXObject ximage = PDImageXObject.createFromFile(imagePath.toString(), document);
            float scale = 700F / ximage.getWidth();
            float w = ximage.getWidth() * scale;
            float h = ximage.getHeight() * scale;
            float x_pos = page3.getCropBox().getWidth();
            float y_pos = page3.getCropBox().getHeight();
            float x_adjusted = (x_pos - h) / 2;
            float y_adjusted = (y_pos - w) / 2;
            Matrix mt = new Matrix(0f, -1f, 1f, 0f, page3.getCropBox().getLowerLeftX() / 2, (page3.getCropBox().getUpperRightY()));
            cos.transform(mt);
            cos.drawImage(ximage, x_adjusted, y_adjusted, w, h);

        } catch (IOException ioex) {
            System.out.println("No image for you");
        }
        cos.close();

        PDPage page4 = new PDPage(PDRectangle.A4);
        document.addPage(page4);
        cos = new PDPageContentStream(document, page4);
        cos.close();
        // Save the results and ensure that the document is properly closed:
        document.save(outputFileName);
        document.close();
    }
}
