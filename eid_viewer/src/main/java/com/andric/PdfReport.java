package com.andric;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;


public class PdfReport
{
    java.awt.Image photo;
    EidInfo info;

    public PdfReport(EidInfo info, java.awt.Image photo)
    {
        this.info = info;
        this.photo = photo;
    }


    public void write(final String filename) throws IOException, DocumentException
    {
        SerbianScript.Script script = SerbianScript.resolveTargetScript(info.getNameFull());

        Document document = new Document();
        document.setPageSize(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();

        // Write image: Read from byte stream, as otherwise photo gets wash out
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        ImageIO.write((BufferedImage) photo, "jpeg", bas);
        byte[] data = bas.toByteArray();
        Image image = Image.getInstance(data);
        image.setAbsolutePosition(60, 572);
        image.setBorder(Image.BOX);
        image.setBorderWidth(1f);
        image.scaleAbsolute(119, 158);
        writer.getDirectContent().addImage(image);

        // Write info
        PdfContentByte cb = writer.getDirectContent();

        drawRulers(cb, 2f, 782, 747);
        drawRulers(cb, 1.5f, 554, 529, 304, 279);

        String fontPath = getClass().getResource("/DejaVuSans.ttf").toString();
        BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        cb.beginText();

        cb.setFontAndSize(bf, 15);
        writeText(cb, normalize("ČITAČ ELEKTRONSKE LIČNE KARTE: ŠTAMPA PODATAKA", script), 62, 760);

        cb.setFontAndSize(bf, 11);
        writeLabel(cb, normalize("Podaci o građaninu", script), 537);
        writeLabel(cb, normalize("Podaci o dokumentu", script), 288);

        cb.setFontAndSize(bf, 10);
        writeLine(cb, normalize("Prezime:", script), normalize(info.getSurname(), script), 513);
        writeLine(cb, normalize("Ime:", script), normalize(info.getGivenName(), script), 489);
        writeLine(cb, normalize("Ime jednog roditelja:", script), normalize(info.getParentGivenName(), script), 463);
        writeLine(cb, normalize("Datum rođenja:", script), normalize(info.getDateOfBirth(), script), 440);
        writeLabel(cb, normalize("Mesto rođenja, \n", script), 415);
        writeLabel(cb, normalize("opština i država:", script), 403);
        writeLine(cb, "", normalize(safe(info.getPlaceOfBirthFull()).replace("\n", ", "), script), 409);
        writeLabel(cb, normalize("Prebivalište: ", script), 380);
//        writeLabel(cb, normalize("adresa stana:", script), 368);
        writePlace(cb, info, script);
        writeLine(cb, normalize("JMBG:", script), normalize(info.getPersonalNumber(), script), 340);
        writeLine(cb, normalize("Pol:", script), normalize(info.getSex(), script), 316);

        writeLine(cb, normalize("Dokument izdaje:", script), normalize(info.getIssuingAuthority(), script), 262);
        writeLine(cb, normalize("Broj dokumenta:", script), normalize(info.getDocRegNo(), script), 238);
        writeLine(cb, normalize("Datum izdavanja:", script), normalize(info.getIssuingDate(), script), 215);
        writeLine(cb, normalize("Važi do:", script), normalize(info.getExpiryDate(), script), 190);

        cb.endText();

        document.close();
    }

    private void writePlace(PdfContentByte cb, EidInfo info, SerbianScript.Script script) throws DocumentException, IOException {

        String[] place = safe(info.getPlaceFull("/ %s", "%s. sprat", "stan %s")).split("\n");

        if (place.length > 1) {
            for(int i=2; i<place.length; i++)
                place[1] += ", " + place[i];

            writeLine(cb, "", normalize(place[0], script), 380);
            writeLine(cb, "", normalize(place[1], script), 368);
        }
        else {
            writeLine(cb, "", normalize(place[0], script), 374);
        }
    }

    private String normalize(String value, SerbianScript.Script script) {
        return SerbianScript.normalize(safe(value), script);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void drawRulerLine(PdfContentByte cb, int height)
    {
        cb.moveTo(59, height);
        cb.lineTo(536, height);
        cb.stroke();
    }

    private void drawRulers(PdfContentByte cb, float weight, int... heights)
    {
        cb.setLineWidth(weight);
        for (int height : heights)
            drawRulerLine(cb, height);
    }


    private void writeText(PdfContentByte cb, String text, int x, int y) throws DocumentException, IOException
    {
        cb.setTextMatrix(x, y);
        cb.showText(text);
    }

    private void writeLabel(PdfContentByte cb, String text, int height) throws DocumentException, IOException
    {
        writeText(cb, text, 68, height);
    }

    private void writeLine(PdfContentByte cb, String label, String text, int height) throws DocumentException, IOException
    {
        writeLabel(cb, label, height);
        writeText(cb, text, 200, height);
    }

}