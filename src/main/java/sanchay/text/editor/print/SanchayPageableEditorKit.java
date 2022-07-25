/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sanchay.text.editor.print;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.geom.AffineTransform;
import javax.swing.event.*;
import javax.swing.text.rtf.RTFEditorKit;
import sanchay.GlobalProperties;


/**
 *
 * @author anil
 */
public class SanchayPageableEditorKit extends RTFEditorKit {

    PageableViewFactory factory = new PageableViewFactory();
    protected int pageWidth = 150;
    protected int pageHeight = 200;
    public static int DRAW_PAGE_INSET = 15;
    protected Insets pageMargins = new Insets(10, 10, 10, 10);

    protected JEditorPane header;
    protected JEditorPane footer;
    protected boolean isValidHF;
    public static int HF_SHIFT=3;
    DocumentListener relayoutListener=new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
            relayout();
        }

        public void removeUpdate(DocumentEvent e) {
            relayout();
        }

        public void changedUpdate(DocumentEvent e) {
            relayout();
        }

        protected void relayout() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JEditorPane parent=null;
                    int lastFooterHeight=0;
                    if (footer !=null && footer.getParent()!=null && footer.getParent() instanceof JEditorPane) {
                        parent=(JEditorPane)footer.getParent();
                        lastFooterHeight=footer.getHeight();
                    }
                    else if (header !=null && header.getParent()!=null && header.getParent() instanceof JEditorPane) {
                        parent=(JEditorPane)header.getParent();
                    }
                    isValidHF = false;
                    validateHF();
                    if (footer!=null && lastFooterHeight!=footer.getHeight()) {
                        int shift=lastFooterHeight-footer.getHeight();
                        footer.setLocation(footer.getX(),footer.getY()+shift);
                    }
                    ( (SectionView) parent.getUI().getRootView(parent).getView(0)).layout(0, Short.MAX_VALUE);
                    parent.repaint();
                }
            });
        }
    };

    /**
     * Constructs kit instance
     */
    public SanchayPageableEditorKit() {
    }

    /**
     * Sets page width
     * @param width int width value in pixels
     */
    public void setPageWidth(int width) {
        pageWidth = width;
        isValidHF=false;
    }

    /**
     * gets page width
     * @return int width in pixels
     */
    public int getPageWidth() {
        return pageWidth;
    }

    /**
     * Sets page height
     * @param height int height in pixels
     */
    public void setPageHeight(int height) {
        pageHeight = height;
    }

    /**
     * Gets page height
     * @return int height in pixels
     */
    public int getPageHeight() {
        return pageHeight;
    }

    /**
     * Sets page margins (distance between page content and page edge.
     * @param margins Insets margins.
     */
    public void setPageMargins(Insets margins) {
        pageMargins = margins;
        isValidHF=false;
    }

    public void setHeader(JEditorPane header) {
        this.header=header;
        header.getDocument().addDocumentListener(relayoutListener);
        isValidHF=false;
    }
    public void setFooter(JEditorPane footer) {
        this.footer=footer;
        footer.getDocument().addDocumentListener(relayoutListener);
        isValidHF=false;
    }
    public JEditorPane getHeader() {
        return header;
    }
    public JEditorPane getFooter() {
        return footer;
    }

    protected void calculateHFSizes() {
        int hfWidth=pageWidth-pageMargins.left-pageMargins.right-2*DRAW_PAGE_INSET;
        int maxHeight=(pageHeight-pageMargins.top-pageMargins.bottom-2*DRAW_PAGE_INSET)/2;

        if (header!=null) {
            header.setSize(hfWidth, pageHeight);
            int hHeight = Math.min(maxHeight, header.getPreferredSize().height);
            header.setSize(hfWidth, hHeight);
        }

        if (footer!=null) {
            footer.setSize(hfWidth, pageHeight);
            int fHeight = Math.min(maxHeight, footer.getPreferredSize().height);
            footer.setSize(hfWidth, fHeight);
        }
    }

    public void validateHF() {
        if (!isValidHF) {
            calculateHFSizes();

            isValidHF=true;
        }
    }
    /**
     * Collateral method to create application GUI
     * @return JFrame
     */
    public JFrame init() {
        JFrame frame = new JFrame(GlobalProperties.getIntlString("Pagination"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JEditorPane editor = new JEditorPane();
        editor.setEditorKit(this);
        this.setHeader(createHeader());
        this.setFooter(createFooter());
        PageFormat pf = new PageFormat();
        pf.setPaper(new Paper());
        final SanchayPaginationPrinter pp = new SanchayPaginationPrinter(pf, editor);
        JScrollPane scroll = new JScrollPane(editor);
        frame.getContentPane().add(scroll);
        JButton bPrint = new JButton(GlobalProperties.getIntlString("Print_to_default_printer"));
        bPrint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                print(editor, pp);
            }
        });
        frame.getContentPane().add(bPrint, BorderLayout.SOUTH);
        frame.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());

        return frame;
    }

    public JEditorPane createHeader() {
        JEditorPane header=new JEditorPane();
        header.setEditorKit(new StyledEditorKit());
        try {
            header.getDocument().insertString(0, GlobalProperties.getIntlString("header"), new SimpleAttributeSet());
        }
        catch (BadLocationException ex) {
            //can't happen
        }

        return header;
    }

    public JEditorPane createFooter() {
        JEditorPane footer=new JEditorPane();
        footer.setEditorKit(new StyledEditorKit());
        try {
            footer.getDocument().insertString(0, GlobalProperties.getIntlString("footer"), new SimpleAttributeSet());
        }
        catch (BadLocationException ex) {
            //can't happen
        }

        return footer;
    }

    /**
     * Prints pages content
     * @param editor JEditorPane pane with content.
     * @param pp PaginationPrinter Printable implementation.
     */
    public void print(JEditorPane editor, SanchayPaginationPrinter pp) {
        PrinterJob pJob = PrinterJob.getPrinterJob();
        //by default paper is letter
        pJob.setPageable(pp);
        try {
            pJob.print();
        }
        catch (PrinterException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SanchayPageableEditorKit().init().setVisible(true);
    }

    /**
     * gets kit view factory.
     * @return ViewFactory
     */
    public ViewFactory getViewFactory() {
        return factory;
    }

    /**
     * The view factory class creates custom views for pagination
     * root view (SectionView class) and paragraph (PageableParagraphView class)
     *
     * @author Stanislav Lapitsky
     * @version 1.0
     */
    class PageableViewFactory implements ViewFactory {
        /**
         * Creates view for specified element.
         * @param elem Element parent element
         * @return View created view instance.
         */
        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                if (kind.equals(AbstractDocument.ContentElementName)) {
                    return new LabelView(elem);
                }
                else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                    return new PageableParagraphView(elem);
                }
                else if (kind.equals(AbstractDocument.SectionElementName)) {
                    return new SectionView(elem, View.Y_AXIS);
                }
                else if (kind.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                }
                else if (kind.equals(StyleConstants.IconElementName)) {
                    return new IconView(elem);
                }
            }
            // default to text display
            return new LabelView(elem);
        }

    }

    /**
     * Root view which perform pagination and paints frame around pages.
     *
     * @author Stanislav Lapitsky
     * @version 1.0
     */
    class SectionView extends BoxView {
        int pageNumber = 0;

        /**
         * Creates view instace
         * @param elem Element
         * @param axis int
         */
        public SectionView(Element elem, int axis) {
            super(elem, axis);
        }

        /**
         * Gets amount of pages
         * @return int
         */
        public int getPageCount() {
            return pageNumber;
        }

        /**
         * Perform layout on the box
         *
         * @param width the width (inside of the insets) >= 0
         * @param height the height (inside of the insets) >= 0
         */
        public void layout(int width, int height) {
            width = pageWidth - 2 * DRAW_PAGE_INSET - pageMargins.left - pageMargins.right;
            this.setInsets( (short) (DRAW_PAGE_INSET + pageMargins.top), (short) (DRAW_PAGE_INSET + pageMargins.left), (short) (DRAW_PAGE_INSET + pageMargins.bottom),
                           (short) (DRAW_PAGE_INSET + pageMargins.right));

            super.layout(width, height);
//            if(width >= 0 && height >= 0)
//                super.layout(width, height);
//            else if(width >= 0)
//                super.layout(width, 0);
//            else if(height >= 0)
//                super.layout(0, height);
        }

        /**
         * Determines the maximum span for this view along an
         * axis.
         *
         * overriddedn
         */
        public float getMaximumSpan(int axis) {
            return getPreferredSpan(axis);
        }

        /**
         * Determines the minimum span for this view along an
         * axis.
         *
         * overriddedn
         */
        public float getMinimumSpan(int axis) {
            return getPreferredSpan(axis);
        }

        /**
         * Determines the preferred span for this view along an
         * axis.
         * overriddedn
         */
        public float getPreferredSpan(int axis) {
            float span = 0;
            if (axis == View.X_AXIS) {
                span = pageWidth;
            }
            else {
                span = pageHeight * getPageCount();
            }
            return span;
        }

        /**
         * Performs layout along Y_AXIS with shifts for pages.
         *
         * @param targetSpan int
         * @param axis int
         * @param offsets int[]
         * @param spans int[]
         */
        protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
            super.layoutMajorAxis(targetSpan, axis, offsets, spans);
            //validate header and footer sizes if necessary
            validateHF();
            addHF();

            int n = offsets.length;
            pageNumber = 0;
            int headerHeight=header!=null ? header.getHeight() +HF_SHIFT:0;
            int footerHeight=footer!=null ? footer.getHeight() +HF_SHIFT:0;
            int totalOffset = headerHeight;
            for (int i = 0; i < n; i++) {
                offsets[i] = totalOffset;
                View v = getView(i);
                if (v instanceof SanchayMultiPageView) {
                    ( (SanchayMultiPageView) v).setBreakSpan(0);
                    ( (SanchayMultiPageView) v).setAdditionalSpace(0);
                }

                if ( (offsets[i] + spans[i]) > (pageNumber * pageHeight - DRAW_PAGE_INSET * 2 - pageMargins.top - pageMargins.bottom-footerHeight)) {
                    if ( (v instanceof SanchayMultiPageView) && (v.getViewCount() > 1)) {
                        SanchayMultiPageView multipageView = (SanchayMultiPageView) v;
                        int space = offsets[i] - (pageNumber - 1) * pageHeight;
                        int breakSpan = (pageNumber * pageHeight - DRAW_PAGE_INSET * 2 - pageMargins.top - pageMargins.bottom-footerHeight) - offsets[i];
                        multipageView.setBreakSpan(breakSpan);
                        multipageView.setPageOffset(space);
                        multipageView.setStartPageNumber(pageNumber);
                        multipageView.setEndPageNumber(pageNumber);
                        int height = (int) getHeight();

                        int width = ( (BoxView) v).getWidth();
                        if (v instanceof PageableParagraphView) {
                            PageableParagraphView parView = (PageableParagraphView) v;
                            parView.layout(width, height);
                        }

                        pageNumber = multipageView.getEndPageNumber();
                        spans[i] += multipageView.getAdditionalSpace();
                    }
                    else {
                        offsets[i] = pageNumber * pageHeight+headerHeight;
                        pageNumber++;
                    }
                }
                totalOffset = (int) Math.min( (long) offsets[i] + (long) spans[i], Integer.MAX_VALUE);
            }
        }

        private void addHF() {
            JTextComponent text = (JTextComponent) getContainer();
            if (text!=null && header != null) {
                if (!isAdded(text,header)) {
                    text.add(header);
                    header.setLocation(DRAW_PAGE_INSET + pageMargins.left, DRAW_PAGE_INSET + pageMargins.top);
                }
                if (footer!= null && !isAdded(text,footer)) {
                    footer.setLocation(DRAW_PAGE_INSET + pageMargins.left, pageHeight - DRAW_PAGE_INSET - pageMargins.bottom - footer.getHeight());
                    text.add(footer);
                }
            }
        }

        protected boolean isAdded(JComponent text, JComponent c) {
            for (int i=0; i<text.getComponentCount(); i++) {
                if (text.getComponent(i)==c) {
                    return true;
                }
            }
            return false;
        }
        public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
            Point location=getClickedHFLocation(x,y);
            if (location!=null) {
                if (location.y % pageHeight < pageHeight / 2) {
                    //header
                    header.setLocation(location);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            header.requestFocus();
                        }
                    });
                }
                else {
                    //footer
                    footer.setLocation(location);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            footer.requestFocus();
                        }
                    });
                }
                return -1;
            }
            else {
                if(x >= 0 && y >= 0)
                    return super.viewToModel(x, y, a, bias);
                else
                    return -1;
            }
        }
        public Point getClickedHFLocation(float x, float y) {
            if (! (x >= DRAW_PAGE_INSET + pageMargins.left
                   && x <= pageWidth - DRAW_PAGE_INSET - pageMargins.right)) {
                return null;
            }

            if(getHeader() == null || getFooter() == null)
                return null;

            int headerHeight=getHeader().getHeight();
            int footerHeight=getFooter().getHeight();
            int headerStartY=DRAW_PAGE_INSET + pageMargins.top;
            int footerStartY=pageHeight - DRAW_PAGE_INSET - pageMargins.bottom - footerHeight;
            int hfWidth=pageWidth-pageMargins.left-pageMargins.right-2*DRAW_PAGE_INSET;
            for (int i=0; i<getPageCount(); i++) {
                if (y<headerStartY) {
                    return null;
                }
                if (y<headerStartY+headerHeight) {
                    return new Point(DRAW_PAGE_INSET + pageMargins.left,headerStartY);
                }
                if (y>footerStartY && y<footerStartY+footerHeight) {
                    return new Point(DRAW_PAGE_INSET + pageMargins.left,footerStartY);
                }
                headerStartY+=pageHeight;
                footerStartY+=pageHeight;
            }
            return null;
        }

        /**
         * Paints view content and page frames.
         * @param g Graphics
         * @param a Shape
         */
        public void paint(Graphics g, Shape a) {
            Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a.getBounds();
            Shape baseClip = g.getClip().getBounds();
            int pageCount = getPageCount();
            Rectangle page = new Rectangle();
            page.x = alloc.x;
            page.y = alloc.y;
            page.height = pageHeight;
            page.width = pageWidth;
            String sC = Integer.toString(pageCount);
            for (int i = 0; i < pageCount; i++) {
                page.y = alloc.y + pageHeight * i;
                paintPageFrame(g, page, (Rectangle) baseClip);
                paintHeader(g, i, page);
                paintFooter(g, i, page);
                g.setColor(Color.blue);
                String sN = Integer.toString(i + 1);
                String pageStr = GlobalProperties.getIntlString("Page:_") + sN;
                pageStr += GlobalProperties.getIntlString("_of_") + sC;
                g.drawString(pageStr,
                             page.x + page.width - 100,
                             page.y + page.height - 3);
            }
            super.paint(g, a);
            g.setColor(Color.gray);
            // Fills background of pages
            int currentWidth = (int) alloc.getWidth();
            int currentHeight = (int) alloc.getHeight();
            int x = page.x + DRAW_PAGE_INSET;
            int y = 0;
            int w = 0;
            int h = 0;
            if (pageWidth < currentWidth) {
                w = currentWidth;
                h = currentHeight;
                g.fillRect(page.x + page.width, alloc.y, w, h);
            }
            if (pageHeight * pageCount < currentHeight) {
                w = currentWidth;
                h = currentHeight;
                g.fillRect(page.x, alloc.y + page.height * pageCount, w, h);
            }
        }

        protected void paintHeader(Graphics g, int pageIndex, Rectangle page) {
            Graphics2D g2d=(Graphics2D)g;
            if (header!=null) {
                AffineTransform old=g2d.getTransform();
                g2d.translate(DRAW_PAGE_INSET+pageMargins.left,DRAW_PAGE_INSET+pageMargins.top+pageIndex*pageHeight);
                boolean isCaretVisible=header.getCaret().isVisible();
                boolean isCaretSelectionVisible=header.getCaret().isSelectionVisible();
                header.getCaret().setVisible(false);
                header.getCaret().setSelectionVisible(false);
                header.paint(g2d);
                header.getCaret().setVisible(isCaretVisible);
                header.getCaret().setSelectionVisible(isCaretSelectionVisible);
                g2d.setColor(Color.lightGray);
                g2d.draw(new Rectangle(-1,-1,header.getWidth()+1,header.getHeight()+1));
                g2d.setTransform(old);
            }
        }

        protected void paintFooter(Graphics g, int pageIndex, Rectangle page) {
            Graphics2D g2d=(Graphics2D)g;
            if (footer!=null) {
                AffineTransform old=g2d.getTransform();
                g2d.translate(DRAW_PAGE_INSET+pageMargins.left,(pageIndex+1)*pageHeight-DRAW_PAGE_INSET-pageMargins.bottom-footer.getHeight());
                boolean isCaretVisible=footer.getCaret().isVisible();
                boolean isCaretSelectionVisible=footer.getCaret().isSelectionVisible();
                footer.getCaret().setVisible(false);
                footer.getCaret().setSelectionVisible(false);
                footer.paint(g2d);
                footer.getCaret().setVisible(isCaretVisible);
                footer.getCaret().setSelectionVisible(isCaretSelectionVisible);
                g2d.setColor(Color.lightGray);
                g2d.draw(new Rectangle(-1,-1,footer.getWidth()+1,footer.getHeight()+1));
                g2d.setTransform(old);
            }
        }
        /**
         * Paints frame for specified page
         * @param g Graphics
         * @param page Shape page rectangle
         * @param container Rectangle
         */
        public void paintPageFrame(Graphics g, Shape page, Rectangle container) {
            Rectangle alloc = (page instanceof Rectangle) ? (Rectangle) page : page.getBounds();
            if (container.intersection(alloc).height <= 0)
                return;
            Color oldColor = g.getColor();

            //borders
            g.setColor(Color.gray);
            g.fillRect(alloc.x, alloc.y, alloc.width, DRAW_PAGE_INSET);
            g.fillRect(alloc.x, alloc.y, DRAW_PAGE_INSET, alloc.height);
            g.fillRect(alloc.x, alloc.y + alloc.height - DRAW_PAGE_INSET, alloc.width, DRAW_PAGE_INSET);
            g.fillRect(alloc.x + alloc.width - DRAW_PAGE_INSET, alloc.y, DRAW_PAGE_INSET, alloc.height);

            //frame
            g.setColor(Color.black);
            g.drawLine(alloc.x + DRAW_PAGE_INSET, alloc.y + DRAW_PAGE_INSET, alloc.x + alloc.width - DRAW_PAGE_INSET, alloc.y + DRAW_PAGE_INSET);
            g.drawLine(alloc.x + DRAW_PAGE_INSET, alloc.y + DRAW_PAGE_INSET, alloc.x + DRAW_PAGE_INSET, alloc.y + alloc.height - DRAW_PAGE_INSET);
            g.drawLine(alloc.x + DRAW_PAGE_INSET, alloc.y + alloc.height - DRAW_PAGE_INSET, alloc.x + alloc.width - DRAW_PAGE_INSET, alloc.y + alloc.height - DRAW_PAGE_INSET);
            g.drawLine(alloc.x + alloc.width - DRAW_PAGE_INSET, alloc.y + DRAW_PAGE_INSET, alloc.x + alloc.width - DRAW_PAGE_INSET, alloc.y + alloc.height - DRAW_PAGE_INSET);

            //shadow
            g.fillRect(alloc.x + alloc.width - DRAW_PAGE_INSET, alloc.y + DRAW_PAGE_INSET + 4, 4, alloc.height - 2 * DRAW_PAGE_INSET);
            g.fillRect(alloc.x + DRAW_PAGE_INSET + 4, alloc.y + alloc.height - DRAW_PAGE_INSET, alloc.width - 2 * DRAW_PAGE_INSET, 4);

            g.setColor(oldColor);
        }


    }

    /**
     * Represents multipage paragraph.
     *
     * @author Stanislav Lapitsky
     * @version 1.0
     */
    class PageableParagraphView extends ParagraphView implements SanchayMultiPageView {
        protected int additionalSpace = 0;
        protected int breakSpan = 0;
        protected int pageOffset = 0;
        protected int startPageNumber = 0;
        protected int endPageNumber = 0;

        public PageableParagraphView(Element elem) {
            super(elem);
        }

        public void layout(int width, int height) {
//            if(width >= 0 && height >= 0)
//                super.layout(width, height);
//            else if(width >= 0)
//                super.layout(width, 0);
//            else if(height >= 0)
//                super.layout(0, height);
            super.layout(width, height);
        }

        protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
            super.layoutMajorAxis(targetSpan, axis, offsets, spans);
            performMultiPageLayout(targetSpan, axis, offsets, spans);
        }

        /**
         * Layout paragraph's content splitting between pages if needed.
         * Calculates shifts and breaks for parent view (SectionView)
         * @param targetSpan int
         * @param axis int
         * @param offsets int[]
         * @param spans int[]
         */
        public void performMultiPageLayout(int targetSpan, int axis, int[] offsets, int[] spans) {
            if (breakSpan == 0)
                return;
            int space = breakSpan;

            additionalSpace = 0;
            endPageNumber = startPageNumber;
            int topInset = this.getTopInset();
            int offs = 0;
            int headerHeight=getHeaderHeight();
            int footerHeight=getFooterHeight();
            for (int i = 0; i < offsets.length; i++) {
                if (offs + spans[i] + topInset > space) {
                    int newOffset = endPageNumber * pageHeight;
                    int addSpace = newOffset - (startPageNumber - 1) * pageHeight - pageOffset - offs - topInset-headerHeight;
                    additionalSpace += addSpace;
                    offs += addSpace;
                    for (int j = i; j < offsets.length; j++) {
                        offsets[j] += addSpace;
                    }
                    endPageNumber++;
                    space = (endPageNumber * pageHeight - 2 * DRAW_PAGE_INSET - pageMargins.top - pageMargins.bottom) - (startPageNumber - 1) * pageHeight - pageOffset-footerHeight;
                }
                offs += spans[i];
            }
        }

        protected int getHeaderHeight() {
            JTextComponent text = (JTextComponent)getContainer();
            if (text!=null && text instanceof JEditorPane && ((JEditorPane)text).getEditorKit() instanceof SanchayPageableEditorKit) {
                SanchayPageableEditorKit kit=(SanchayPageableEditorKit)((JEditorPane)text).getEditorKit();
                if (kit.getHeader()!=null) {
                    return kit.getHeader().getHeight() + SanchayPageableEditorKit.HF_SHIFT;
                }
            }
            return 0;
        }
        protected int getFooterHeight() {
            JTextComponent text = (JTextComponent)getContainer();
            if (text!=null && ((JEditorPane)text).getEditorKit() instanceof SanchayPageableEditorKit) {
                SanchayPageableEditorKit kit=(SanchayPageableEditorKit)((JEditorPane)text).getEditorKit();
                if (kit.getFooter()!=null) {
                    return kit.getFooter().getHeight()+SanchayPageableEditorKit.HF_SHIFT;
                }
            }
            return 0;
        }
        /**
         * Gets view's start page number
         * @return page number
         */
        public int getStartPageNumber() {
            return startPageNumber;
        }

        /**
         * Gets view's end page number
         * @return page number
         */
        public int getEndPageNumber() {
            return endPageNumber;
        }

        /**
         * Gets view's extra space (space between pages)
         * @return extra space
         */
        public int getAdditionalSpace() {
            return additionalSpace;
        }

        /**
         * Gets view's break span
         * @return break span
         */
        public int getBreakSpan() {
            return breakSpan;
        }

        /**
         * Gets view's offsets on the page
         * @return offset
         */
        public int getPageOffset() {
            return pageOffset;
        }

        /**
         * Sets view's start page number
         *
         * @param startPageNumber page number
         */
        public void setStartPageNumber(int startPageNumber) {
            this.startPageNumber = startPageNumber;
        }

        /**
         * Sets view's end page number
         *
         * @param endPageNumber page number
         */
        public void setEndPageNumber(int endPageNumber) {
            this.endPageNumber = endPageNumber;
        }

        /**
         * Sets extra space (space between pages)
         *
         * @param additionalSpace additional space
         */
        public void setAdditionalSpace(int additionalSpace) {
            this.additionalSpace = additionalSpace;
        }

        /**
         * Sets view's break span.
         *
         * @param breakSpan break span
         */
        public void setBreakSpan(int breakSpan) {
            this.breakSpan = breakSpan;
        }

        /**
         * Sets view's offset on the page
         *
         * @param pageOffset offset
         */
        public void setPageOffset(int pageOffset) {
            this.pageOffset = pageOffset;
        }
    }
}
