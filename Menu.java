import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

/**
 * Stores functions and information for displaying a menu.
 */
public class Menu extends JComponent
{
    private int gap; // gap between cursor and frame/menu items
    
    private int fontSize; // font size of text on the menu
    private int selectedIndex; // the index of the selected item
    
    private double width, height; // dimensions of menu
                                
    private String[] menuItems; // the list of items on the menu
    
    private Rectangle2D.Double frame, cursor; // parts of menu
    
    private boolean visible; // if the menu is visible or not
    
    /**
     * Creates a menu at the center of the specified battle field and cursor on the first of the specified items.
     * Note: String[] might need an empty string at the end to make box go around all options.
     */
    public Menu(String[] items, int txtSize)
    {
        menuItems = items;
        fontSize = txtSize;
        
        gap = (int)(0.75*fontSize);
        
        width = 5*fontSize;
        height = (menuItems.length - 1)*(fontSize + gap);
        
        visible = false;
        selectedIndex = 0;
    }
    
    /**
     * Moves the menu cursor up.
     */
    public void moveCursorUp()
    {
        boolean cont = true;
        selectedIndex--;
        while(cont)
        {
            if (selectedIndex < 0) // is selectedIndex is out of bounds
                selectedIndex = menuItems.length - 1;
            if (menuItems[selectedIndex].equals(""))
                selectedIndex--;
            else
                cont = false;
        }
     
        double xCoord = Math.round(frame.getX() + gap) - 5;
        double yCoord = Math.round(frame.getY() + gap*(2*selectedIndex + 1)) - 5;
        cursor = new Rectangle2D.Double(xCoord, yCoord, width - 2*gap, fontSize + (gap - 5));
    }
    
    /**
     * Moves the menu cursor down.
     */
    public void moveCursorDown()
    {
        boolean cont = true;
        selectedIndex++;
        while(cont)
        {
            if (selectedIndex > menuItems.length - 1) // if selected index is out of bounds
                selectedIndex = 0;
            if (menuItems[selectedIndex].equals(""))
                selectedIndex++;
            else
                cont = false;
        }
            
        double xCoord = Math.round(frame.getX() + gap) - 5;
        double yCoord = Math.round(frame.getY() + gap*(2*selectedIndex + 1)) - 5;
        cursor = new Rectangle2D.Double(xCoord, yCoord, width - 2*gap, fontSize + (gap - 5));
    }
    
    /**
     * Sets the width to the specified value.
     */
    public void setMenuWidth(double newVal)
    {
        width = newVal;
    }
    
    /**
     * Sets the height to the specified value.
     */
    public void setMenuHeight(double newVal)
    {
        height = newVal;
    }
    
    /**
     * Returns the index of the selected item.
     */
    public int getSelectedIndex()
    {
        return selectedIndex;
    }
    
    /**
     * Returns the string of the selected item.
     */
    public String getSelectedAction()
    {
        return menuItems[selectedIndex];
    }
    
    /**
     * Shows the menu.
     */
    public void showMenu()
    {
        visible = true;
    }
    
    /**
     * Hides the menu.
     */
    public void hideMenu()
    {
        visible = false;
        resetCursor();
    }
    
    /**
     * Returns whether the menu is hidden or not.
     */
    public boolean isHidden()
    {
        return !visible;
    }
    
    /**
     * Resets the cursor position.
     */
    public void resetCursor()
    {
        while (selectedIndex > 0)
            moveCursorUp();
    }
    
    /**
     * Paints the menu onto the canvas.
     */
    public void paintComponent(int x, int y, Graphics g)
    {
        if (visible)
        {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.red);
            g2.fillRoundRect(x - gap, y - gap,  Math.round((float)width) + 2*gap, Math.round((float)height) + 2*gap, gap, gap);
                             
            frame = new Rectangle2D.Double(x, y, width, height);
            
            double xCoord = Math.round(frame.getX() + gap) - 5;
            double yCoord = Math.round(frame.getY() + gap*(2*selectedIndex + 1)) - 5;
            cursor = new Rectangle2D.Double(xCoord, yCoord, width - 2*gap, fontSize + (gap - 5));
            
            g2.setColor(Color.yellow);
            g2.fill(frame);
            
            g2.setFont(new Font("", Font.PLAIN, fontSize));
            g2.setColor(Color.black);
            for (int i = 0; i < menuItems.length; i++)
            {
                g2.drawString(menuItems[i], Math.round(frame.getX() + gap), Math.round(frame.getY() + gap*(2*i + 2)));
            }
            g2.draw(cursor);
        }
    }
    
} // END CLASS