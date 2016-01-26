package com.itextpdf.samples.book.part2.chapter07;


import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfReader;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.canvas.PdfCanvas;
import com.itextpdf.model.Canvas;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.Link;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.experimental.categories.Category;

@Category(SampleTest.class)
public class Listing_07_13_EventsAndActions extends GenericTest {
    public static final String DEST = "./target/test/resources/book/part2/chapter07/Listing_07_13_EventAndActions.pdf";
    public static final String RESOURCE = "./src/test/resources/book/part2/chapter07/print_page.js";

    public static final String MOVIE_TEMPLATES = "./src/test/resources/book/part1/chapter03/cmp_Listing_03_29_MovieTemplates.pdf";

    protected String[] arguments;

    public static void main(String args[]) throws IOException, SQLException {
        Listing_07_13_EventsAndActions application = new Listing_07_13_EventsAndActions();
        application.arguments = args;
        application.manipulatePdf(DEST);
    }

    protected static String readFileToString(String path) throws IOException {
        File file = new File(path);
        byte[] jsBytes = new byte[(int) file.length()];
        FileInputStream f = new FileInputStream(file);
        f.read(jsBytes);
        return new String(jsBytes);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        // Listing_03_29_MovieTemplates.main(arguments);
        // Create a reader
        PdfReader reader = new PdfReader(MOVIE_TEMPLATES);
        // Create a writer
        PdfWriter writer = new PdfWriter(DEST);
        // Create a pdf document
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        int n = pdfDoc.getNumberOfPages();
        // Add some javascript
        pdfDoc.getCatalog().setOpenAction(PdfAction.createJavaScript(readFileToString(RESOURCE)));
        // Create a Chunk with a chained action
        PdfCanvas canvas;
        PdfAction action = PdfAction.createJavaScript("app.alert('Think before you print!');");
        action.next(PdfAction.createJavaScript("printCurrentPage(this.pageNum);"));
        action.next(PdfAction.createURI("http://www.panda.org/savepaper/"));
        Link link = new Link("print this page", action);
        Paragraph paragraph = new Paragraph(link);
        // Add this Paragraph to every page
        for (int i = 1; i <= n; i++) {
            canvas = new PdfCanvas(pdfDoc.getPage(i));
            new Canvas(canvas, pdfDoc, pdfDoc.getPage(i).getPageSize())
                    .showTextAligned(paragraph, 816, 18, i,
                            Property.TextAlignment.RIGHT, Property.VerticalAlignment.MIDDLE, 0);
        }
        // Close the pdfDocument
        pdfDoc.close();
    }
}
