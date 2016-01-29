/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part4.chapter16;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.action.PdfTargetDictionary;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.model.Document;
import com.itextpdf.model.element.Cell;
import com.itextpdf.model.element.Image;
import com.itextpdf.model.element.Link;
import com.itextpdf.model.element.List;
import com.itextpdf.model.element.ListItem;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Table;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;
import com.lowagie.database.DatabaseConnection;
import com.lowagie.database.HsqldbConnection;
import com.lowagie.filmfestival.Movie;
import com.lowagie.filmfestival.PojoFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.experimental.categories.Category;

@Ignore
@Category(SampleTest.class)
public class Listing_16_08_KubrickBox extends GenericTest {
    public static final String DEST = "./target/test/resources/book/part4/chapter16/Listing_16_08_KubrickBox.pdf";
    public static final String IMG_BOX = "./src/test/resources/book/part4/chapter16/kubrick_box.jpg";
    public static final String RESOURCE = "./src/test/resources/book/part4/chapter16/posters/%s.jpg";
    public static final float[] WIDTHS = { 1 , 7 };

    public static void main(String args[]) throws Exception {
        new Listing_16_08_KubrickBox().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);
        Image img = new Image(ImageFactory.getImage(IMG_BOX));
        doc.add(img);
        List list = new List();
        list.setSymbolIndent(20);
        PdfTargetDictionary target;
        Link link;
        ListItem item;
        DatabaseConnection connection = new HsqldbConnection("filmfestival");
        Set<Movie> box = new TreeSet<>();
        box.addAll(PojoFactory.getMovies(connection, 1));
        box.addAll(PojoFactory.getMovies(connection, 4));
        connection.close();
        for (Movie movie : box) {
            if (movie.getYear() > 1960) {
                pdfDoc.addFileAttachment(movie.getTitle(),
                        PdfFileSpec.createEmbeddedFileSpec(pdfDoc, createMoviePage(movie), null,
                                String.format("kubrick_%s.pdf", movie.getImdb()), null, null, null, false));
                item = new ListItem(movie.getMovieTitle());
                // TODO Implement 'from box' way to work with TargetDictionaries
                target = new PdfTargetDictionary(PdfName.C);
                target.put(PdfName.N, new PdfString(movie.getTitle()));
                link = new Link(" (see info)",
                        PdfAction.createGoToE(PdfExplicitDestination.createFit(1), false, target));
                item.add(new Paragraph(link));
                list.add(item);
            }
        }
        doc.add(list);
        doc.close();
    }

    public byte[] createMoviePage(Movie movie) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
        Document doc = new Document(pdfDoc);
        Paragraph p = new Paragraph(movie.getMovieTitle())
                .setFont(PdfFontFactory.createFont(FontConstants.HELVETICA, PdfEncodings.WINANSI, false))
                .setFontSize(16);
        doc.add(p);
        doc.add(new Paragraph("\n"));
        Table table = new Table(WIDTHS);
        table.setWidthPercent(100);
        table.addCell(new Image(ImageFactory.getImage(String.format(RESOURCE, movie.getImdb()))).setAutoScale(true));
        Cell cell = new Cell();
        cell.add(new Paragraph("Year: " + movie.getYear()));
        cell.add(new Paragraph("Duration: " + movie.getDuration()));
        table.addCell(cell);
        doc.add(table);
        PdfTargetDictionary target = new PdfTargetDictionary(PdfName.P);
        Link link = new Link("Go to original document",
                PdfAction.createGoToE(PdfExplicitDestination.createFit(1), false, target));
        doc.add(new Paragraph(link));
        doc.close();
        return baos.toByteArray();
    }
}
