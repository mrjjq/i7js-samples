/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part2.chapter07;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLineAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfSquareAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfStampAnnotation;
import com.itextpdf.test.annotations.type.SampleTest;
import com.lowagie.database.DatabaseConnection;
import com.lowagie.database.HsqldbConnection;
import com.lowagie.filmfestival.Movie;
import com.lowagie.filmfestival.PojoFactory;
import com.lowagie.filmfestival.Screening;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

import org.junit.experimental.categories.Category;


@Category(SampleTest.class)
public class Listing_07_25_TimetableAnnotations3 extends Listing_07_21_TimetableAnnotations1 {
    public static final String DEST
            = "./target/test/resources/book/part2/chapter07/Listing_07_25_TimetableAnnotations3.pdf";

    public static final String MOVIE_TEMPLATES = "./src/test/resources/book/part1/chapter03/cmp_Listing_03_29_MovieTemplates.pdf";

    /**
     * Path to IMDB.
     */
    public static final String IMDB = "http://imdb.com/title/tt%s/";

    public static void main(String args[]) throws IOException, SQLException {
        Listing_07_25_TimetableAnnotations3 application = new Listing_07_25_TimetableAnnotations3();
        application.beforeManipulatePdf();
        application.manipulatePdf(DEST);
        application.afterManipulatePdf();
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        // Listing_03_29_MovieTemplates.main(arguments);
        // Create a database connection
        DatabaseConnection connection = new HsqldbConnection("filmfestival");
        locations = PojoFactory.getLocations(connection);
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(MOVIE_TEMPLATES), new PdfWriter(DEST));
        int page = 1;
        Rectangle rect;
        float top;
        PdfAnnotation annotation;
        Movie movie;
        for (Date day : PojoFactory.getDays(connection)) {
            for (Screening screening : PojoFactory.getScreenings(connection, day)) {
                rect = getPosition(screening);
                movie = screening.getMovie();
                // Annotation for press previews
                if (screening.isPress()) {
                    annotation = new PdfStampAnnotation(rect)
                            .setStampName(new PdfName("NotForPublicRelease"))
                            .setContents("Press only")
                            .setColor(Color.BLACK.getColorValue())
                            .setFlags(PdfAnnotation.Print);
                }
                // Annotation for screenings that are sold out
                else if (isSoldOut(screening)) {
                    // TODO No getPageSizeWithRotation
                    top = pdfDoc.getPage(page).getPageSize().getTop();
                    float[] line = new float[]{top - rect.getTop(), rect.getRight(),
                            top - rect.getBottom(), rect.getLeft()};
                    PdfDictionary borderStyleDict = new PdfDictionary();
                    borderStyleDict.put(PdfName.W, new PdfNumber(5));
                    borderStyleDict.put(PdfName.S, PdfName.B);
                    annotation = new PdfLineAnnotation(rect, line).
                            setContents("SOLD OUT")
                            .setTitle(new PdfString(movie.getMovieTitle()))
                            .setColor(Color.GREEN.getColorValue())
                            .setFlags(PdfAnnotation.Print)
                            .setBorderStyle(borderStyleDict);
                }
                // Annotation for screenings with tickets available
                else {
                    PdfArray borderArray = new PdfArray();
                    borderArray.add(new PdfNumber(0));
                    borderArray.add(new PdfNumber(0));
                    borderArray.add(new PdfNumber(2));
                    // TODO No PdfDashPattern
                    annotation = new PdfSquareAnnotation(rect)
                            .setContents("Tickets available")
                            .setTitle(new PdfString(movie.getMovieTitle()))
                            .setColor(Color.BLUE.getColorValue())
                            .setFlags(PdfAnnotation.Print)
                            .setBorder(borderArray);
                }
                // TODO in itext5 we add stamp/line annotations taking page rotation into account
                pdfDoc.getPage(page).addAnnotation(annotation);
            }
            page++;
        }
        // Close the document
        pdfDoc.close();
        // Close the database connection
        connection.close();
    }

    /**
     * Checks if the screening has been sold out.
     *
     * @param screening a Screening POJO
     * @return true if the screening has been sold out.
     */
    public boolean isSoldOut(Screening screening) {
        if (screening.getMovie().getMovieTitle().startsWith("L"))
            return true;
        return false;
    }
}
