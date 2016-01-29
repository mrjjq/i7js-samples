/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

/**
 * Example written by Bruno Lowagie in answer to:
 * http://thread.gmane.org/gmane.comp.java.lib.itext.general/65892
 *
 * Some text displayed using a Small Caps font.
 */
package com.itextpdf.samples.sandbox.fonts;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.annotations.type.SampleTest;
import com.itextpdf.model.Document;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.samples.GenericTest;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.experimental.categories.Category;

@Category(SampleTest.class)
public class SmallCapsExample extends GenericTest {
    public static final String DEST = "./target/test/resources/sandbox/fonts/small_caps_example.pdf";
    public static final String FONT = "./src/test/resources/sandbox/fonts/Delicious-SmallCaps.otf";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new SmallCapsExample().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(DEST)));
        Document doc = new Document(pdfDoc);
        PdfFont font = PdfFontFactory.createFont(FONT, "Identity-H", true);
        Paragraph p = new Paragraph("This is some text displayed using a Small Caps font.")
                .setFont(font)
                .setFontSize(12);
        doc.add(p);
        doc.close();
    }
}
