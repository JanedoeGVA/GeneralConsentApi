package metier;


import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import domain.FormulaireConsent;
import org.apache.pdfbox.cos.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.*;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;


public class PDFCreator {


    private static final Logger LOG = Logger.getLogger(PDFCreator.class.getName());

    private static String LOGO_PATH = PDFCreator.class.getResource("/logo_hug.jpg").getFile();

    private static final DateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);

    private static final DateFormat formatterWithHour = new SimpleDateFormat("dd MMMM yyyy 'à' hh:mm:ss", Locale.FRENCH);

    public static void create(Path imagePath, FormulaireConsent formulaireConsent) throws Exception {
        final java.nio.file.Path path = Files.createTempFile("temp_pdf", ".pdf");
        String outputFileName = path.toString();
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

        // Start a new content stream which will hold the content that's about to be created
        PDPageContentStream cos = new PDPageContentStream(document, page1);

        int line = 0;
        final Color lightBlue = new Color(51, 153, 255);
        final int border = 50;
        // Define a text content stream using the selected font, move the cursor and draw some text
        cos.setNonStrokingColor(lightBlue);
        cos.beginText();
        cos.setFont(fontBold, 16);
        cos.newLineAtOffset(border, rect.getHeight() - 45 * (++line));
        cos.showText("Déclaration de consentement pour l’utilisation des données");
        cos.endText();

        cos.beginText();
        cos.setFont(fontBold, 16);
        cos.newLineAtOffset(border, rect.getHeight() - 30 * (++line));
        cos.showText("santé et des échantillons à des fins de recherche");
        cos.endText();

        cos.setNonStrokingColor(Color.black);

        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(border, rect.getHeight() - 35 * (++line));
        cos.showText("J’accepte que mes données de santé et mes échantillons biologiques collectés durant les");
        cos.endText();

        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(border, rect.getHeight() - 30 * (++line));
        cos.showText("soins (consultations ambulatoires et hospitalisations) soient utilisés à des fins de recherche");
        cos.endText();

        cos.beginText();
        cos.setFont(fontPlain, 12);
        float ty = rect.getHeight() - 32 * (++line);
        LOG.log(Level.INFO, "ty :" + ty);
        cos.newLineAtOffset(180, ty);
        cos.showText("Oui");
        cos.endText();

        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(350, ty);
        cos.showText("Non");
        cos.endText();

        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(border, rect.getHeight() - 32 * (++line));
        cos.showText("J’ai compris :");
        cos.endText();

        int tab = border + 30;
        int tabBullet = border + 15;


        //bloc1
        float tyBloc = rect.getHeight() - 33 * (++line);
        float spaceLine = 15;

        cos.beginText();
        cos.setFont(fontBold, 18);
        cos.newLineAtOffset(tabBullet, tyBloc - 3);
        cos.showText("\u2022");
        cos.endText();

        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(tab, tyBloc);
        cos.showText("les explications sur la réutilisation de mes données de santé et échantillons");
        cos.endText();

        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(tab, tyBloc - spaceLine);
        cos.showText("biologiques à des fins de recherche, détaillées dans l’information ci-dessus.");
        cos.endText();

        //bloc2
        tyBloc = rect.getHeight() - 34 * (++line);

        cos.beginText();
        cos.setFont(fontBold, 18);
        cos.newLineAtOffset(tabBullet, tyBloc - 3);
        cos.showText("\u2022");
        cos.endText();

        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(tab, tyBloc);
        cos.showText("que mes données personnelles sont protégées");
        cos.endText();

        //bloc3
        tyBloc = rect.getHeight() - 33 * (++line);

        cos.beginText();
        cos.setFont(fontBold, 18);
        cos.newLineAtOffset(tabBullet, tyBloc - 3);
        cos.showText("\u2022");
        cos.endText();

        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(tab, tyBloc);
        cos.showText("que mes données et échantillons biologiques peuvent être utilisés dans des projets");
        cos.endText();

        cos.beginText();
        ++line;
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(tab, tyBloc - spaceLine);
        cos.showText("de recherche nationaux et internationaux, dans les secteurs public et privé.");
        cos.endText();

        //bloc4
        tyBloc = rect.getHeight() - 31 * (++line);

        cos.beginText();
        cos.setFont(fontBold, 18);
        cos.newLineAtOffset(tabBullet, tyBloc - 3);
        cos.showText("\u2022");
        cos.endText();

        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(tab, tyBloc);
        cos.showText("que les projets peuvent inclure des analyses génétiques sur mes échantillons, à des");
        cos.endText();

        cos.beginText();
        ++line;
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(tab, tyBloc - spaceLine);
        cos.showText("fins de recherche.");
        cos.endText();


        //bloc 5
        tyBloc = rect.getHeight() - 29.5F * (++line);

        cos.beginText();
        cos.setFont(fontBold, 18);
        cos.newLineAtOffset(tabBullet, tyBloc - 3);
        cos.showText("\u2022");
        cos.endText();

        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(tab, tyBloc);
        cos.showText("que je peux être recontacté-e si des résultats pertinents me concernant sont mis en");
        cos.endText();

        cos.beginText();
        ++line;
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(tab, tyBloc - spaceLine);
        cos.showText("évidence.");
        cos.endText();


        //bloc6
        tyBloc = rect.getHeight() - 28.5F * (++line);

        cos.beginText();
        cos.setFont(fontBold, 18);
        cos.newLineAtOffset(tabBullet, tyBloc - 3);
        cos.showText("\u2022");
        cos.endText();

        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(tab, tyBloc);
        cos.showText("que ma décision est libre et n’a pas d’effet sur mon traitement médical.");
        cos.endText();


        //bloc7
        tyBloc = rect.getHeight() - 28.5F * (++line);

        cos.beginText();
        cos.setFont(fontBold, 18);
        cos.newLineAtOffset(tabBullet, tyBloc - 3);
        cos.showText("\u2022");
        cos.endText();

        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(tab, tyBloc);
        cos.showText("que ma décision est valable pour une durée illimitée.");
        cos.endText();


        //bloc8
        tyBloc = rect.getHeight() - 28.5F * (++line);

        cos.beginText();
        cos.setFont(fontBold, 18);
        cos.newLineAtOffset(tabBullet, tyBloc - 3);
        cos.showText("\u2022");
        cos.endText();

        cos.beginText();
        cos.setFont(fontPlain, 12);
        cos.newLineAtOffset(tab, tyBloc);
        cos.showText("que je peux retirer mon consentement à tout moment sans avoir à justifier ma décision.");
        cos.endText();
        line++;

        // Patient
        // title
        final float tyTitle = rect.getHeight() - 28 * (++line);
        cos.beginText();
        cos.setFont(fontBold, 12);
        cos.newLineAtOffset(border, tyTitle);
        cos.showText("Patient :");
        cos.endText();
        cos.setLineWidth(0.5f);
        cos.moveTo(border+ 47, tyTitle-2); // moves "pencil" to a position
        cos.lineTo(border-1, tyTitle-2);     // creates an invisible line to another position
        cos.stroke();
        //nom
        ty = rect.getHeight() - 28.5F * (++line);
        cos.beginText();
        cos.setFont(fontBold, 12);
        cos.newLineAtOffset(border, ty);

        cos.showText("Nom :");
        cos.endText();

        cos.beginText();
        cos.setFont(fontItalic, 12);
        cos.newLineAtOffset(border + 40, ty);

        cos.showText(formulaireConsent.getContact().getNom());
        cos.endText();

        //prenom
        ty = rect.getHeight() - 28.5F * (++line);
        cos.beginText();
        cos.setFont(fontBold, 12);
        cos.newLineAtOffset(border, ty);
        ;
        cos.showText("Prenom :");
        cos.endText();

        cos.beginText();
        cos.setFont(fontItalic, 12);
        cos.newLineAtOffset(border + 55, ty);
        ;
        cos.showText(formulaireConsent.getContact().getPrenom());
        cos.endText();


        //birthday
        ty = rect.getHeight() - 28.5F * (++line);
        cos.beginText();
        cos.setFont(fontBold, 12);
        cos.newLineAtOffset(border, ty);
        cos.showText("Date de naissance :");
        cos.endText();

        cos.beginText();
        cos.setFont(fontItalic, 12);
        cos.newLineAtOffset(border + 115, ty);
        cos.showText(formatter.format(Date.from(Instant.ofEpochSecond(formulaireConsent.getContact().getBirthday()))));
        cos.endText();

        // Representant légal
        if (formulaireConsent.getRepresentant() != null) {
            // title
            final int borderRepresentant = border + 240;
            cos.beginText();
            cos.setFont(fontBold, 12);
            cos.newLineAtOffset(borderRepresentant, tyTitle);
            cos.showText("Representant légal :");
            cos.endText();
            cos.setLineWidth(0.5f);
            cos.moveTo(borderRepresentant+ 115, tyTitle-2); // moves "pencil" to a position
            cos.lineTo(borderRepresentant-1, tyTitle-2);     // creates an invisible line to another position
            cos.stroke();
            //nom
            ty = rect.getHeight() - 28.5F * (line-=2);
            cos.beginText();
            cos.setFont(fontBold, 12);
            cos.newLineAtOffset(borderRepresentant , ty);
            cos.showText("Nom :");
            cos.endText();
            cos.beginText();
            cos.setFont(fontItalic, 12);
            cos.newLineAtOffset(borderRepresentant+ 40, ty);
            cos.showText(formulaireConsent.getRepresentant().getNom());
            cos.endText();

            //prenom
            ty = rect.getHeight() - 28.5F * (line+=1);
            cos.beginText();
            cos.setFont(fontBold, 12);
            cos.newLineAtOffset(borderRepresentant, ty);
            cos.showText("Prenom :");
            cos.endText();
            cos.beginText();
            cos.setFont(fontItalic, 12);
            cos.newLineAtOffset(borderRepresentant+55, ty);
            cos.showText(formulaireConsent.getRepresentant().getPrenom());
            cos.endText();

            //relation
            ty = rect.getHeight() - 28.5F * (++line);
            cos.beginText();
            cos.setFont(fontBold, 12);
            cos.newLineAtOffset(borderRepresentant, ty);
            cos.showText("Relation :");
            cos.endText();
            cos.beginText();
            cos.setFont(fontItalic, 12);
            cos.newLineAtOffset(borderRepresentant + 57, ty);
            cos.showText(formulaireConsent.getRepresentant().getRelation());
            cos.endText();
        }

        ty = rect.getHeight() - 30F * (line+=2);
        cos.beginText();
        cos.setFont(fontItalic, 12);
        cos.newLineAtOffset(border, ty);
        final String dateCreation = formatterWithHour.format(Date.from(Instant.now()));
        cos.showText("Crée le " + dateCreation);
        cos.endText();

        // Checkbox
        PDAcroForm acroForm = new PDAcroForm(document);
        PDCheckBox checkBoxY = new PDCheckBox(acroForm);
        PDCheckBox checkBoxN = new PDCheckBox(acroForm);
        checkBoxY.setPartialName("oui");
        checkBoxN.setPartialName("non");
        //PDAnnotationWidget widget = new PDAnnotationWidget();
        PDAnnotationWidget widgetY = checkBoxY.getWidgets().get(0);
        widgetY.setRectangle(new PDRectangle(150, 680, 10, 10));
        widgetY.setAnnotationFlags(4);
        widgetY.setPage(page1);
        PDAnnotationWidget widgetN = checkBoxN.getWidgets().get(0);
        widgetN.setRectangle(new PDRectangle(320, 680, 10, 10));
        widgetN.setAnnotationFlags(4);
        widgetN.setPage(page1);

        // inspired by annot 92 of file from PDFBOX-563
        // annot 172 has "checkmark" instead, but more more complex, needs ZaDb

        String offNString = "1 g\n"
                + "0 0 9.5 9.5 re\n"
                + "f\n"
                + "0.5 0.5 9.5 9.5 re\n"
                + "s";
        String offDString = "0.75 g\n"
                + "0 0 9.5 9.5 re\n"
                + "f\n"
                + "0.5 0.5 9.5 9.5 re\n"
                + "s";
        String yesNString = "1 g\n"
                + "0 0 9 9.5 re\n"
                + "f\n"
                + "0.5 0.5 9.5 9.5 re\n"
                + "s\n"
                + "q\n"
                + "  1 1 9 9 re\n"
                + "  W\n"
                + "  n\n"
                + "  2 8 m\n"
                + "  8 2 l\n"
                + "  8 8 m\n"
                + "  2 2 l\n"
                + "  s\n"
                + "Q";
        String yesDString = "0.75 g\n"
                + "0 0 9 9.5 re\n"
                + "f\n"
                + "0.5 0.5 9.5 9.5 re\n"
                + "s\n"
                + "q\n"
                + "  1 1 9 9 re\n"
                + "  W\n"
                + "  n\n"
                + "  2 8 m\n"
                + "  8 2 l\n"
                + "  8 8 m\n"
                + "  2 2 l\n"
                + "  s\n"
                + "Q";

        COSDictionary apNDict = new COSDictionary();
        COSStream offNStream = new COSStream();
        offNStream.setItem(COSName.BBOX, new PDRectangle(10, 10));
        offNStream.setItem(COSName.FORMTYPE, COSInteger.ONE);
        offNStream.setItem(COSName.TYPE, COSName.XOBJECT);
        offNStream.setItem(COSName.SUBTYPE, COSName.FORM);
        offNStream.setItem(COSName.MATRIX, new Matrix().toCOSArray());
        offNStream.setItem(COSName.RESOURCES, new COSDictionary());
        OutputStream os = offNStream.createOutputStream();
        os.write(offNString.getBytes());
        os.close();
        apNDict.setItem(COSName.Off, offNStream);

        COSStream yesNStream = new COSStream();
        yesNStream.setItem(COSName.BBOX, new PDRectangle(10, 10));
        yesNStream.setItem(COSName.FORMTYPE, COSInteger.ONE);
        yesNStream.setItem(COSName.TYPE, COSName.XOBJECT);
        yesNStream.setItem(COSName.SUBTYPE, COSName.FORM);
        yesNStream.setItem(COSName.MATRIX, new Matrix().toCOSArray());
        yesNStream.setItem(COSName.RESOURCES, new COSDictionary());
        os = yesNStream.createOutputStream();
        os.write(yesNString.getBytes());
        os.close();
        apNDict.setItem(COSName.getPDFName("Yes"), yesNStream);

        COSDictionary apDDict = new COSDictionary();
        COSStream offDStream = new COSStream();
        offDStream.setItem(COSName.BBOX, new PDRectangle(16, 16));
        offDStream.setItem(COSName.FORMTYPE, COSInteger.ONE);
        offDStream.setItem(COSName.TYPE, COSName.XOBJECT);
        offDStream.setItem(COSName.SUBTYPE, COSName.FORM);
        offDStream.setItem(COSName.MATRIX, new Matrix().toCOSArray());
        offDStream.setItem(COSName.RESOURCES, new COSDictionary());
        os = offDStream.createOutputStream();
        os.write(offDString.getBytes());
        os.close();
        apDDict.setItem(COSName.Off, offDStream);

        COSStream yesDStream = new COSStream();
        yesDStream.setItem(COSName.BBOX, new PDRectangle(16, 16));
        yesDStream.setItem(COSName.FORMTYPE, COSInteger.ONE);
        yesDStream.setItem(COSName.TYPE, COSName.XOBJECT);
        yesDStream.setItem(COSName.SUBTYPE, COSName.FORM);
        yesDStream.setItem(COSName.MATRIX, new Matrix().toCOSArray());
        yesDStream.setItem(COSName.RESOURCES, new COSDictionary());
        os = yesDStream.createOutputStream();
        os.write(yesDString.getBytes());
        os.close();
        apDDict.setItem(COSName.getPDFName("Yes"), yesDStream);
        PDAppearanceDictionary appearance = new PDAppearanceDictionary();
        PDAppearanceEntry appearanceNEntry = new PDAppearanceEntry(apNDict);
        appearance.setNormalAppearance(appearanceNEntry);
        widgetY.setAppearance(appearance);
        widgetN.setAppearance(appearance);
        COSDictionary acdDict = new COSDictionary();
        acdDict.setItem(COSName.CA, new COSString("8")); // 8 is X, 4 is checkmark
        COSArray bcArray = new COSArray();
        bcArray.add(COSInteger.ZERO);
        acdDict.setItem(COSName.BC, bcArray);
        COSArray bgArray = new COSArray();
        bgArray.add(COSInteger.ONE);
        acdDict.setItem(COSName.BG, bgArray);
        PDAppearanceCharacteristicsDictionary acd = new PDAppearanceCharacteristicsDictionary(acdDict);
        widgetY.setAppearanceCharacteristics(acd);
        page1.getAnnotations().add(widgetY);
        widgetN.setAppearanceCharacteristics(acd);
        page1.getAnnotations().add(widgetN);
        checkBoxY.setValue("Yes");
        checkBoxN.setValue("Yes");
        if (formulaireConsent.isHasAcceptedConsent()) {
            checkBoxN.unCheck();
        } else {
            checkBoxY.unCheck();
        }
        acroForm.getFields().add(checkBoxY);
        document.getDocumentCatalog().setAcroForm(acroForm);


        //Logo HUG
        final PDImageXObject ximageLogo = PDImageXObject.createFromFile(LOGO_PATH, document);
        float w = ximageLogo.getWidth() / 5;
        float h = ximageLogo.getHeight() / 5;
        cos.drawImage(ximageLogo, 50, 30, w, h);

        cos.close();

        PDPage page2 = new PDPage(PDRectangle.A4);
        document.addPage(page2);
        cos = new PDPageContentStream(document, page2);

        // add an image
        try {
            PDImageXObject ximage = PDImageXObject.createFromFile(imagePath.toString(), document);
            LOG.log(Level.INFO, "size : " + ximage.getWidth() + " x " + ximage.getHeight());
            final float heightMax = 700f;
            float x_pos = page2.getCropBox().getWidth();
            float y_pos = page2.getCropBox().getHeight();
            //image est a l'envers il faut la retourner
            if (ximage.getWidth()> ximage.getHeight()) {
                LOG.log(Level.INFO, "image a l'envers");
                float scale = heightMax / ximage.getWidth();
                LOG.log(Level.INFO, "scale : " + scale);
                float wPhoto = ximage.getWidth() * scale;
                float hPhoto = ximage.getHeight() * scale;
                LOG.log(Level.INFO, "size after scale: " + wPhoto + " x " + hPhoto);
                float x_adjusted = (x_pos - hPhoto) / 2;
                float y_adjusted = (y_pos - wPhoto) / 2;
                Matrix mt = new Matrix(0f, -1f, 1f, 0f, page2.getCropBox().getLowerLeftX() / 2, (page2.getCropBox().getUpperRightY()));
                cos.transform(mt);
                cos.drawImage(ximage, x_adjusted, y_adjusted, wPhoto, hPhoto);
            } else {
                LOG.log(Level.INFO, "image a l'endroit");
                float scale = 700F / ximage.getHeight();
                LOG.log(Level.INFO, "scale : " + scale);
                float wPhoto = ximage.getWidth() * scale;
                float hPhoto = ximage.getHeight() * scale;
                LOG.log(Level.INFO, "size after scale: " + wPhoto + " x " + hPhoto);
                float x_adjusted = (x_pos - wPhoto) / 2;
                float y_adjusted = (y_pos - hPhoto) / 2;
                Matrix mt = new Matrix(0f, -1f, 1f, 0f, page2.getCropBox().getLowerLeftX() / 2, (page2.getCropBox().getUpperRightY()));
                cos.transform(mt);
                cos.drawImage(ximage, x_adjusted, y_adjusted, wPhoto, hPhoto);
            }


        } catch (IOException ioex) {
            System.out.println("No image for you");
        }
        cos.close();

        // Save the results and ensure that the document is properly closed:
        AccessPermission ap = new AccessPermission();
        ap.setCanModify(false);
        ap.setCanExtractContent(false);
        ap.setCanPrint(false);
        ap.setCanPrintDegraded(false);
        ap.setReadOnly();
        StandardProtectionPolicy spp = new StandardProtectionPolicy(UUID.randomUUID().toString(), "", ap);
        document.protect(spp);
        document.save(outputFileName);
        SMTP.sendFormConsent(path);
        document.close();
    }
}
