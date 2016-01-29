/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

/**
 * This example was written by Bruno Lowagie in answer to the following question:
 * http://stackoverflow.com/questions/26648462/how-to-delete-attachment-of-pdf-using-itext
 * (This is part two, there's also a part one named AddEmbeddedFile)
 */
package com.itextpdf.samples.sandbox.annotations;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;

import java.io.File;

import org.junit.experimental.categories.Category;

@Category(SampleTest.class)
public class RemoveEmbeddedFiles extends GenericTest {
    public static final String SRC = "./src/test/resources/sandbox/annotations/hello_with_attachment.pdf";
    public static final String DEST = "./target/test/resources/sandbox/annotations/remove_embedded_files.pdf";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new RemoveEmbeddedFiles().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(DEST));
        PdfDictionary root = pdfDoc.getCatalog().getPdfObject();
        PdfDictionary names = root.getAsDictionary(PdfName.Names);
        names.remove(PdfName.EmbeddedFiles);
        pdfDoc.close();
    }
}
