import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * Controls the display of the unit's stats.
 */
public class StatBox extends JComponent
{
    private static final Color[] SIDE_COLOR = {Color.blue, Color.red, Color.green, Color.yellow};
    
    private static final int STAT_NUM = 8; // number of stats to be displayed
    
    private Unit theUnit; // the unit the statbox is assigned to
    
    private boolean hidden; // if the statbox is hidden or not
    
    private double width, height; // the dimensions of the stat box
    
    private Menu itemMenu; // the part of the stat box that displays the items
    
    private int displayed; // which panel is displayed (0 = stats, 1 = items)
    private int gap; // the gap between objects
    private int fontSize; // font size of text
    
    /**
     * Creates a statbox with the specified Unit as the assigned unit.
     */
    public StatBox(int aFontSize, Unit aUnit)
    {
        fontSize = aFontSize;
        theUnit = aUnit;
        
        gap = (int)(0.75*fontSize);
        
        width = 10*fontSize;
        height = STAT_NUM*(fontSize + gap) + gap;
        
        String[] unitWeapons = theUnit.getWpnsAsString();
        String[] unitItems = theUnit.getItemsAsString();
        
        String[] itemsAndWeapons = {unitWeapons[0], unitWeapons[1], unitWeapons[2], unitWeapons[3], "", unitItems[0], unitItems[1], unitItems[2], unitItems[3]};
        itemMenu = new Menu(itemsAndWeapons, fontSize);
        
        itemMenu.showMenu();
        itemMenu.setMenuWidth(width);
        
        hidden = true;
    }
    
    /**
     * Moves the menu cursor down.
     */
    public void moveCursorDown()
    {
        if (displayed == 1)
            itemMenu.moveCursorDown();
    }
    
    /**
     * Moves the menu cursor up.
     */
    public void moveCursorUp()
    {
        if (displayed == 1)
            itemMenu.moveCursorUp();
    }
    
    /**
     * Sets the unit to the specified unit.
     */
    public void setUnit(Unit newUnit)
    {
        theUnit = newUnit;
        
        String[] unitWeapons = theUnit.getWpnsAsString();
        String[] unitItems = theUnit.getItemsAsString();
        String[] itemsAndWeapons = {unitWeapons[0], unitWeapons[1], unitWeapons[2], unitWeapons[3], "", unitItems[0], unitItems[1], unitItems[2], unitItems[3]};
        itemMenu = new Menu(itemsAndWeapons, fontSize);
        
        itemMenu.showMenu();
        itemMenu.setMenuWidth(width);
    }
    
    /**
     * Returns the int representation of which panel is visible.
     */
    public int getDisplayed()
    {
        return displayed;
    }
    
    /**
     * Shows the stat box.
     */
    public void showBox()
    {
        hidden = false;
    }
    
    /**
     * Returns the index of the item that is selected in the menu panel.
     */
    public int getSelectedIndex()
    {
        return itemMenu.getSelectedIndex();
    }
    
    /**
     * Hides the stat box.
     */
    public void hideBox()
    {
        hidden = true;
    }
    
    /**
     * Returns if the box is hidden.
     */
    public boolean isHidden()
    {
        return hidden;
    }
    
    /**
     * Displays the selected item's info.
     */
    public void showSelectedItemInfo()
    {
        int index = itemMenu.getSelectedIndex();

        if (index < 4) // a weapon is selected (one of the first four)
        {
            if (theUnit.getWpnAt(index).isHidden()) // if the wpn info has been hidden
                theUnit.getWpnAt(index).showWpn();
            else
                theUnit.getWpnAt(index).hideWpn();
        }
        else
        {
            if (theUnit.getItemAt(index - 5).isHidden()) // if the item info has been hidden
                theUnit.getItemAt(index - 5).showItem();
            else
                theUnit.getItemAt(index - 5).hideItem();
        }
    }
    
    /**
     * Switches the panel displayed to the opposite.
     */
    public void switchPanel()
    {
        if (displayed == 0)
            displayed = 1;
        else
            displayed = 0;
    }
    
    /**
     * Resets the cursor position.
     */
    public void resetCursor()
    {
        itemMenu.resetCursor();
    }
    
    /**
     * Paints the statbox onto the canvas.
     */
    public void paintComponent(int xCoord, int yCoord, Graphics g)
    {
        if (!hidden)
        {
            if (displayed == 0)
            {
                paintStats(xCoord, yCoord, g);
            }
            else
            {
                itemMenu.paintComponent(xCoord, yCoord, g);
                if (itemMenu.getSelectedIndex() < 4)
                    theUnit.getWpnAt(itemMenu.getSelectedIndex()).paintComponent(xCoord, yCoord, g);
                else if (itemMenu.getSelectedIndex() < 9)
                    theUnit.getItemAt(itemMenu.getSelectedIndex() - 5).paintComponent(xCoord, yCoord, g);
            }
        }
    }
    
    /**
     * Paints the stat screen.
     */
    private void paintStats(int xCoord, int yCoord, Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(Color.green);
        g2.fillRoundRect(xCoord - gap, yCoord - gap, Math.round((float)width) + 2*gap, Math.round((float)height) + 2*gap, gap, gap);
        
        g2.setColor(Color.orange);
        g2.fillRoundRect(xCoord, yCoord, Math.round((float)width), Math.round((float)height), gap, gap);
        
        g2.setFont(new Font("", Font.PLAIN, fontSize));
        String[] labels = {theUnit.getName(), "Level " + theUnit.getLevel(), "Exp: " + theUnit.getExp(), "HP: " + theUnit.getHP(), "Str: " + theUnit.getStr(), 
                                    "Def: " + theUnit.getDef(), "Skl: " + theUnit.getSkl(), "Spd: " + theUnit.getSpd(), "Move: " + theUnit.getMoveRng()};
        
        g2.setColor(SIDE_COLOR[theUnit.getSide()]); // draw name is separate color
        g2.drawString(labels[0], Math.round(xCoord + gap), Math.round(yCoord + gap*2));
        g2.setColor(Color.black);
        for (int i = 1; i < labels.length; i++)
        {
            g2.drawString(labels[i], Math.round(xCoord + gap), Math.round(yCoord + gap*(2*i + 2)));
        }
    }
    
} // END CLASS