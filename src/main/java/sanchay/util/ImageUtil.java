/*
 * ImageUtil.java
 *
 * Created on June 19, 2006, 11:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sanchay.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author anil
 */
public class ImageUtil{ 
    
    public static int IMAGE_READERS = 0;
    public static int IMAGE_READER_MIMES = 1;
    public static int IMAGE_WRITERS = 2;
    public static int IMAGE_WRITER_MIMES = 3;
    
    private static JLabel imgObserver = new JLabel(); 

    public static Image createGhostImage(Image img){ 
        BufferedImage ghost = new BufferedImage(img.getWidth(imgObserver) 
                , img.getHeight(imgObserver), BufferedImage.TYPE_INT_ARGB_PRE); 
        Graphics2D g2 = ghost.createGraphics(); 
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.5f)); 
        g2.drawImage(img, 0, 0, ghost.getWidth(), ghost.getHeight(), imgObserver); 
        g2.dispose(); 
        return ghost; 
    } 

    public static String[] getImageReaderList(int type)
    {
        if(type == IMAGE_READERS)
        {
            String readerNames[] = ImageIO.getReaderFormatNames();
            return readerNames;
        }
        else if(type == IMAGE_READER_MIMES)
        {
            String readerMimes[] = ImageIO.getReaderMIMETypes();
            return readerMimes;
        }
        else if(type == IMAGE_WRITERS)
        {
            String writerNames[] = ImageIO.getWriterFormatNames();
            return writerNames;
        }
        else if(type == IMAGE_WRITER_MIMES)
        {
            String writerMimes[] = ImageIO.getWriterMIMETypes();
            return writerMimes;
        }
        
        return null;
   }
    
//    protected static ImageIcon createImageIcon(String path) {
//        java.net.URL imgURL = Utils.class.getResource(path);
//        if (imgURL != null) {
//            return new ImageIcon(imgURL);
//        } else {
//            System.err.println("Couldn't find file: " + path);
//            return null;
//        }
//    }
}