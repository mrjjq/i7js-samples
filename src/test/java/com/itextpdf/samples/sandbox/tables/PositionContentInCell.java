package com.itextpdf.samples.sandbox.tables;

import com.itextpdf.basics.image.ImageFactory;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.model.Document;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.Cell;
import com.itextpdf.model.element.Image;
import com.itextpdf.model.element.Table;
import com.itextpdf.model.renderer.CellRenderer;
import com.itextpdf.samples.GenericTest;
import org.junit.Ignore;

import java.io.File;
import java.io.FileOutputStream;

@Ignore
public class PositionContentInCell extends GenericTest {
    public static final String DEST = "./target/test/resources/sandbox/tables/position_content_in_cell.pdf";
    public static final String IMG = "./src/test/resources/sandbox/tables/info.png";

    public enum POSITION {TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT}

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new PositionContentInCell().manipulatePdf(DEST);
    }


    private class ImageAndPositionRenderer extends CellRenderer {
        private Image img;
        private String content;
        private POSITION position;

        public ImageAndPositionRenderer(Cell modelElement, Image img, String content, POSITION position) {
            super(modelElement);
            this.img = img;
            this.content = content;
            this.position = position;
        }

        @Override
        public void draw(PdfDocument document, PdfCanvas canvas) {
            super.draw(document, canvas);
            img.scaleToFit(getOccupiedAreaBBox().getWidth(), getOccupiedAreaBBox().getHeight());

            canvas.addXObject(img.getXObject(),
                    getOccupiedAreaBBox().getX() +
                            (getOccupiedAreaBBox().getWidth()
                                    - img.getWidth() * (float) img.getProperty(Property.HORIZONTAL_SCALING)) / 2,
                    getOccupiedAreaBBox().getY() +
                            (getOccupiedAreaBBox().getHeight()
                                    - img.getHeight() * (float) img.getProperty(Property.VERTICAL_SCALING)) / 2,
                    img.getWidth() * (float) img.getProperty(Property.HORIZONTAL_SCALING));
            canvas.stroke();
            canvas.beginText();
            canvas.setFontAndSize(this.getPropertyAsFont(Property.FONT), this.getPropertyAsFloat(Property.FONT_SIZE));

            float x = 0;
            float y = 0;
            //int alignment = 0;
            switch (position) {
                case TOP_LEFT:
                    x = getOccupiedAreaBBox().getX() + 3;
                    y = getOccupiedAreaBBox().getY() + getOccupiedAreaBBox().getHeight() - 3;
                    // alignment = .LEFT;
                    break;
                case TOP_RIGHT:
                    x = getOccupiedAreaBBox().getX() + getOccupiedAreaBBox().getWidth() - 3;
                    y = getOccupiedAreaBBox().getY() + getOccupiedAreaBBox().getHeight() - 3;
                    // alignment = .RIGHT;
                    break;
                case BOTTOM_LEFT:
                    x = getOccupiedAreaBBox().getX() + 3;
                    y = getOccupiedAreaBBox().getY() + 3;
                    // alignment = .LEFT;
                    break;
                case BOTTOM_RIGHT:
                    x = getOccupiedAreaBBox().getX() + getOccupiedAreaBBox().getWidth() - 3;
                    y = getOccupiedAreaBBox().getY() + 3;
                    // alignment = .RIGHT;
                    break;
            }
            canvas.moveText(x, y);
            // TODO Implement showTextAligned
            //ColumnText.showTextAligned(canvas, alignment, content, x, y, 0);
            canvas.showText(content);
            canvas.endText();
            canvas.stroke();
        }
    }


    @Override
    protected void manipulatePdf(String dest) throws Exception {
        FileOutputStream fos = new FileOutputStream(dest);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);
        Table table = new Table(2);
        Cell cell1 = new Cell();
        Cell cell2 = new Cell();
        Cell cell3 = new Cell();
        Cell cell4 = new Cell();
        // 2. Inside that table, make each cell with specific height:
        cell1.setHeight(50);
        cell2.setHeight(50);
        cell3.setHeight(50);
        cell4.setHeight(50);
        // 3. Each cell has the same background image
        // 4. Add text in front of the image at specific position
        cell1.setNextRenderer(new ImageAndPositionRenderer(cell1,
                new Image(ImageFactory.getImage(IMG)), "Top left", POSITION.TOP_LEFT));
        cell2.setNextRenderer(new ImageAndPositionRenderer(cell2,
                new Image(ImageFactory.getImage(IMG)), "Top right", POSITION.TOP_RIGHT));
        cell3.setNextRenderer(new ImageAndPositionRenderer(cell3,
                new Image(ImageFactory.getImage(IMG)), "Bottom left", POSITION.BOTTOM_LEFT));
        cell4.setNextRenderer(new ImageAndPositionRenderer(cell4,
                new Image(ImageFactory.getImage(IMG)), "Bottom right", POSITION.BOTTOM_RIGHT));
        // Wrap it all up!
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        doc.add(table);

        doc.close();
    }
}
