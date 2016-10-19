package ab.text.editor;

import java.awt.*;
import java.awt.print.*;
import static java.awt.print.Printable.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.*;

/*
  The print class was unapologetically snatched from some other code
  on the internet. It was hard to find the source because it's been
  copied so many times.
*/
public class Print implements Printable {

    PrintView printView;
    DefaultStyledDocument doc;
    StyleContext context;

    JTextArea stage;

    public Print(JTextArea jta) {
        stage = jta;
    }

    public void doPrinting() {
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(Print.this);
        if (pj.printDialog()) {
            try {
                pj.print();
            } catch (PrinterException pe) {
                System.out.println(pe.getMessage());
            }
        }
    }

    public int print(Graphics pg, PageFormat pageFormat,
            int pageIndex) throws PrinterException {
        pg.translate((int) pageFormat.getImageableX(),
                (int) pageFormat.getImageableY());
        int wPage = (int) pageFormat.getImageableWidth();
        int hPage = (int) pageFormat.getImageableHeight();
        pg.setClip(0, 0, wPage, hPage);

        if (printView == null) {
            BasicTextUI btui = (BasicTextUI) stage.getUI();
            View root = btui.getRootView(stage);
            printView = new PrintView(
                    doc.getDefaultRootElement(),
                    root, wPage, hPage);
        }

        boolean bContinue = printView.paintPage(pg,
                hPage, pageIndex);
        System.gc();

        if (bContinue) {
            return PAGE_EXISTS;
        } else {
            printView = null;
            return NO_SUCH_PAGE;
        }
    }

    class PrintView extends BoxView {

        protected int m_firstOnPage = 0;
        protected int m_lastOnPage = 0;
        protected int m_pageIndex = 0;

        public PrintView(Element elem, View root, int w, int h) {
            super(elem, Y_AXIS);
            setParent(root);
            setSize(w, h);
            layout(w, h);
        }

        public boolean paintPage(Graphics g, int hPage,
                int pageIndex) {
            if (pageIndex > m_pageIndex) {
                m_firstOnPage = m_lastOnPage + 1;
                if (m_firstOnPage >= getViewCount()) {
                    return false;
                }
                m_pageIndex = pageIndex;
            }
            int yMin = getOffset(Y_AXIS, m_firstOnPage);
            int yMax = yMin + hPage;
            Rectangle rc = new Rectangle();

            for (int k = m_firstOnPage; k < getViewCount(); k++) {
                rc.x = getOffset(X_AXIS, k);
                rc.y = getOffset(Y_AXIS, k);
                rc.width = getSpan(X_AXIS, k);
                rc.height = getSpan(Y_AXIS, k);
                if (rc.y + rc.height > yMax) {
                    break;
                }
                m_lastOnPage = k;
                rc.y -= yMin;
                paintChild(g, rc, k);
            }
            return true;
        }
    }

}
