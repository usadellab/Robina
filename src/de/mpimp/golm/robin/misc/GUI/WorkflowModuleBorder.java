
package de.mpimp.golm.robin.misc.GUI;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.border.AbstractBorder;

public class WorkflowModuleBorder extends AbstractBorder {
    
    Image top_center, top_left, top_right;
    Image left_center, right_center;
    Image bottom_center, bottom_left, bottom_right, bottom_arrow;
    Insets insets;
    
    private int style;
    
    public static final int SIMPLE_GREY = 1;
    public static final int BLUE_SHADOW = 2;
    public static final int SIMPLE_GREY_NOARROW = 3;
    public static final int SIMPLE_BLUE_NOARROW = 4;
    
    public WorkflowModuleBorder(Image top_left, Image top_center, Image top_right,
        Image left_center, Image right_center,
        Image bottom_left, Image bottom_center, Image bottom_right) {
            
        this.top_left = top_left;
        this.top_center = top_center;
        this.top_right = top_right;
        this.left_center = left_center;
        this.right_center = right_center;
        this.bottom_left = bottom_left;
        this.bottom_center = bottom_center;
        this.bottom_right = bottom_right;
    }
    
    public WorkflowModuleBorder(int style) {
        
        this.style = style;
        
        if (style == SIMPLE_GREY) {
            this.top_left = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/upper_left.png")).getImage();
            this.top_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/upper.png")).getImage();
            this.top_right = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/upper_right.png")).getImage();
            this.left_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/left_center.png")).getImage();
            this.right_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/right_center.png")).getImage();
            this.bottom_left = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bottom_left.png")).getImage();
            this.bottom_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bottom_center.png")).getImage();
            this.bottom_right = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bottom_right.png")).getImage(); 
            this.bottom_arrow = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bottom_arrow.png")).getImage(); 
            
        } else if (style == BLUE_SHADOW) {
            this.top_left = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bs_upper_left.png")).getImage();
            this.top_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bs_upper.png")).getImage();
            this.top_right = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bs_upper_right.png")).getImage();
            this.left_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bs_left_center.png")).getImage();
            this.right_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bs_right_center.png")).getImage();
            this.bottom_left = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bs_bottom_left.png")).getImage();
            this.bottom_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bs_bottom_center.png")).getImage();
            this.bottom_right = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bs_bottom_right.png")).getImage(); 
            this.bottom_arrow = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bs_bottom_arrow.png")).getImage();
        
        } else if (style == SIMPLE_GREY_NOARROW) {
            this.top_left = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/upper_left.png")).getImage();
            this.top_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/upper.png")).getImage();
            this.top_right = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/upper_right.png")).getImage();
            this.left_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/left_center.png")).getImage();
            this.right_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/right_center.png")).getImage();
            this.bottom_left = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/grey_bottom_left.png")).getImage();
            this.bottom_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/grey_bottom_center.png")).getImage();
            this.bottom_right = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/grey_bottom_right.png")).getImage(); 
            
        } else if (style == SIMPLE_BLUE_NOARROW) {
            this.top_left = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bs_upper_left.png")).getImage();
            this.top_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bs_upper.png")).getImage();
            this.top_right = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bs_upper_right.png")).getImage();
            this.left_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bs_left_center.png")).getImage();
            this.right_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/bs_right_center.png")).getImage(); 
            this.bottom_left = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/blue_bottom_left.png")).getImage();
            this.bottom_center = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/blue_bottom_center.png")).getImage();
            this.bottom_right = new ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/blue_bottom_right.png")).getImage();
        } else {
            return;
        }
    }
    
    public void setInsets(Insets insets) {
        this.insets = insets;
    }
    
    public Insets getBorderInsets(Component c) {
        if(insets != null) {
            return insets;
        } else {
            return new Insets(top_center.getHeight(null),left_center.getWidth(null),
                bottom_center.getHeight(null), right_center.getWidth(null));
        }
    }
    
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.white);
        g.fillRect(x,y,width,height);

        Graphics2D g2 = (Graphics2D)g;
        
        int tlw = top_left.getWidth(null);
        int tlh = top_left.getHeight(null);
        int tcw = top_center.getWidth(null);
        int tch = top_center.getHeight(null);
        int trw = top_right.getWidth(null);
        int trh = top_right.getHeight(null);
        
        int lcw = left_center.getWidth(null);
        int lch = left_center.getHeight(null);
        int rcw = right_center.getWidth(null);
        int rch = right_center.getHeight(null);
        
        int blw = bottom_left.getWidth(null);
        int blh = bottom_left.getHeight(null);
        int bcw = bottom_center.getWidth(null);
        int bch = bottom_center.getHeight(null);
        int brw = bottom_right.getWidth(null);
        int brh = bottom_right.getHeight(null);        
        
        int baw = 0, bah = 0;
        if (! ( style == SIMPLE_BLUE_NOARROW || style == SIMPLE_GREY_NOARROW ) ) {
            baw = bottom_arrow.getWidth(null);
            bah = bottom_arrow.getHeight(null);
        }
        
        fillTexture(g2, top_left, x,
                y, tlw, tlh);
        fillTexture(g2, top_center, x + tlw,
                y, width - tlw - trw, tch);
        fillTexture(g2, top_right, x + width - trw, y, trw, trh);

        fillTexture(g2, left_center, x, y + tlh, lcw, height - tlh - blh);
        fillTexture(g2, right_center, x + width - rcw, y + trh, rcw, height - trh - brh);

        fillTexture(g2, bottom_left, x, y + height - blh, blw, blh);
        fillTexture(g2, bottom_center, x + blw, y + height - bch, width - blw - brw, bch);
        fillTexture(g2, bottom_right, x + width - brw, y + height - brh, brw, brh);
        if (! ( style == SIMPLE_BLUE_NOARROW || style == SIMPLE_GREY_NOARROW ) ) {
            fillTexture(g2, bottom_arrow, (x+(width/2)-(baw/2)), y+height-bah, baw, bah);
        }
    }
    
    public void fillTexture(Graphics2D g2, Image img, int x, int y, int w, int h) {
        BufferedImage buff = createBufferedImage(img);
        Rectangle anchor = new Rectangle(x,y,
            img.getWidth(null),img.getHeight(null));
        TexturePaint paint = new TexturePaint(buff,anchor);
        g2.setPaint(paint);
        g2.fillRect(x,y,w,h);
    }

    public BufferedImage createBufferedImage(Image img) {
        BufferedImage buff = new BufferedImage(img.getWidth(null), 
            img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics gfx = buff.createGraphics();
        gfx.drawImage(img, 0, 0, null);
        gfx.dispose();
        return buff;
    }
}

