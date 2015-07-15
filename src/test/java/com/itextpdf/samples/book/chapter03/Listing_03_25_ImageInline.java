package com.itextpdf.samples.book.chapter03;

import com.itextpdf.basics.image.Image;
import com.itextpdf.basics.image.ImageFactory;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.samples.GenericTest;
import org.junit.Ignore;

import java.io.FileOutputStream;

@Ignore("Wait for the image refactoring. Now to get image height and width we need to create PdfImageXObject.")
public class Listing_03_25_ImageInline extends GenericTest {

    public static final String DEST = "./target/test/resources/Listing_03_25_ImageInline.pdf";

    public static final String RESOURCE = "src/test/resources/img/loa.jpg";

    public static void main(String[] args) throws Exception {
        new Listing_03_25_ImageInline().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        //Initialize writer
        FileOutputStream fos = new FileOutputStream(dest);
        PdfWriter writer = new PdfWriter(fos);

        //Initialize document and add page
        PdfDocument pdfDoc = new PdfDocument(writer);
        Rectangle postcard = new Rectangle(283, 416);

        Image image = ImageFactory.getImage(RESOURCE);
        new PdfCanvas(pdfDoc.getLastPage()).addImage(image, (postcard.getWidth() - image.getWidth()) / 2, (postcard.getHeight() - image.getHeight()) / 2, true);

        //Close document
        pdfDoc.close();
    }
}
