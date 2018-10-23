import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

/**
 * Stores and controls the animation processes.
 */
public class Animation extends JComponent
{
    private static final String ANIM_DIRECTORY = "Animations/"; // directory of animations
    private static final String IMAGE_TYPE = ".gif"; // format of images
    private static final int DELAY = 33;
    
    /**
     * Paints the animation onto the canvas at the specified coordinates.
     */
    public void paintComponent(int xCoord, int yCoord, int fontSize, Unit right, Unit left, Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        
        final int GAP = (int)(0.75*fontSize);
        final int WIDTH = (int)(19.5*fontSize);
        final int HEIGHT = (int)(5*(fontSize + GAP));
        
        // paint border
        g2.setColor(Color.red);
        g2.fillRoundRect(xCoord - GAP, yCoord - GAP, WIDTH + 2*GAP, HEIGHT + 2*GAP, GAP, GAP);
        
        // paint background
        g2.setColor(Color.white);
        g2.fillRect(xCoord, yCoord, WIDTH, HEIGHT);
        
        // paint image on right
        Image pic = Toolkit.getDefaultToolkit().getImage(ANIM_DIRECTORY + right.getName() + IMAGE_TYPE);
        int x = xCoord + WIDTH - (GAP + pic.getWidth(this));
        int y = yCoord + HEIGHT - (GAP + pic.getHeight(this));
        g2.drawImage(pic, x, y, this);
        
        // paint image on left
        pic = Toolkit.getDefaultToolkit().getImage(ANIM_DIRECTORY + left.getName() + IMAGE_TYPE);
        x = xCoord + GAP;
        y = yCoord + HEIGHT - (GAP + pic.getHeight(this));
        g2.drawImage(pic, x, y, this);
    }

} // END CLASS