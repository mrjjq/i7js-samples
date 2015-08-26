package com.itextpdf.samples.sandbox.tables;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.model.Document;
import com.itextpdf.model.border.SolidBorder;
import com.itextpdf.model.element.Cell;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Table;
import com.itextpdf.model.renderer.CellRenderer;
import com.itextpdf.model.renderer.TableRenderer;
import com.itextpdf.samples.GenericTest;

import java.io.File;
import java.io.FileOutputStream;

class CustomBorder2TableRenderer extends TableRenderer {
    static boolean isBottomToBeDrawn = false;
    static boolean isTopToBeDrawn = true;

    public CustomBorder2TableRenderer(Table modelElement, Table.RowRange rowRange) {
        super(modelElement, rowRange);
    }

    @Override
    public void drawBorder(PdfDocument document, PdfCanvas canvas) {

        // We strongly believe that everything is fine as we believe in itext5 analog example
        CellRenderer[] firstRowRenderers = rows.get(0);
        // yLines
        canvas.moveTo(firstRowRenderers[0].getOccupiedArea().getBBox().getLeft(),
                getOccupiedArea().getBBox().getBottom());
        canvas.lineTo(firstRowRenderers[0].getOccupiedArea().getBBox().getLeft(),
                getOccupiedArea().getBBox().getTop());
        canvas.moveTo(firstRowRenderers[firstRowRenderers.length - 1].getOccupiedArea().getBBox().getRight(),
                getOccupiedArea().getBBox().getBottom());
        canvas.lineTo(firstRowRenderers[firstRowRenderers.length - 1].getOccupiedArea().getBBox().getRight(),
                getOccupiedArea().getBBox().getTop());

        if (isTopToBeDrawn) {
            canvas.moveTo(firstRowRenderers[0].getOccupiedArea().getBBox().getLeft(),
                    firstRowRenderers[0].getOccupiedArea().getBBox().getTop());
            canvas.lineTo(firstRowRenderers[firstRowRenderers.length - 1].getOccupiedArea().getBBox().getRight(),
                    firstRowRenderers[0].getOccupiedArea().getBBox().getTop());
            isTopToBeDrawn = false;
        }
        if (isBottomToBeDrawn) {
            canvas.moveTo(rows.get(rows.size() - 1)[0].getOccupiedArea().getBBox().getLeft(),
                    rows.get(rows.size() - 1)[0].getOccupiedArea().getBBox().getBottom());
            canvas.lineTo(
                    rows.get(rows.size() - 1)[rows.get(rows.size() - 1).length - 1]
                            .getOccupiedArea().getBBox().getRight(),
                    rows.get(rows.size() - 1)[rows.get(rows.size() - 1).length - 1]
                            .getOccupiedArea().getBBox().getBottom());

        }
        canvas.stroke();
        isBottomToBeDrawn = true;
    }

    @Override
    protected CustomBorder2TableRenderer createOverflowRenderer(Table.RowRange rowRange) {
        CustomBorder2TableRenderer overflowRenderer = new CustomBorder2TableRenderer((Table) modelElement, rowRange);
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        overflowRenderer.addAllProperties(getOwnProperties());
        overflowRenderer.isOriginalNonSplitRenderer = false;
        return overflowRenderer;
    }

    @Override
    protected CustomBorder2TableRenderer createSplitRenderer(Table.RowRange rowRange) {
        CustomBorder2TableRenderer splitRenderer = new CustomBorder2TableRenderer((Table) modelElement, rowRange);
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        // TODO childRenderers will be populated twice during the relayout.
        // We should probably clean them before #layout().
        splitRenderer.childRenderers = childRenderers;
        splitRenderer.addAllProperties(getOwnProperties());
        splitRenderer.headerRenderer = headerRenderer;
        splitRenderer.footerRenderer = footerRenderer;
        return splitRenderer;
    }

    @Override
    protected TableRenderer[] split(int row) {
        isBottomToBeDrawn = false;
        CustomBorder2TableRenderer splitRenderer = createSplitRenderer(
                new Table.RowRange(rowRange.getStartRow(), rowRange.getStartRow() + row));
        splitRenderer.rows = rows.subList(0, row);
        CustomBorder2TableRenderer overflowRenderer = createOverflowRenderer(
                new Table.RowRange(rowRange.getStartRow() + row, rowRange.getFinishRow()));
        overflowRenderer.rows = rows.subList(row, rows.size());
        splitRenderer.occupiedArea = occupiedArea;
        return new TableRenderer[]{splitRenderer, overflowRenderer};
    }

    @Override
    public void draw(PdfDocument document, PdfCanvas canvas) {
        super.draw(document, canvas);

    }
}

public class CustomBorder2 extends GenericTest {
    public static final String DEST = "./target/test/resources/sandbox/tables/custom_border2.pdf";
    public static final String TEXT = "This is some long paragraph that will be added over and over " +
            "again to prove a point. It should result in rows that are split and rows that aren't.";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new CustomBorder2().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        FileOutputStream fos = new FileOutputStream(dest);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Table table = new Table(2);
        table.setWidth(500);
        table.setBorder(new SolidBorder(1));
        // TODO Implement setWidthPercentage(float)
        //table.setWidthPercentage(100);
        // TODO Implement setting-for-all-cells-specific-border method
        //table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        // TODO setSplitLate(boolean)
        // table.setSplitLate(false);

        Cell cell = new Cell().add(new Paragraph(TEXT));
        cell.setBorder(null);
        for (int i = 0; i < 60; ) {
            table.addCell(new Cell().add(new Paragraph("Cell " + (++i))).setBorder(null));
            table.addCell(new Cell().add(new Paragraph(TEXT)).setBorder(null));
        }
        table.setNextRenderer(new CustomBorder2TableRenderer(table, new Table.RowRange(0, 59)));
        doc.add(table);

        doc.close();
    }

}
