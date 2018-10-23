import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Stores where the cursor/selector is on the map.
 */
public class Cursor extends JComponent
{
    private static final double SIZE = BattleField.GRID_SIZE; // the size of the cursor
    
    private Rectangle2D.Double cursor; // the rectangle representing the cursor
    
    private Location loc; // the grid coordinates of the cursor
    
    private BattleField field; // the battlefield the cursor is on
    
    /**
     * Creates a cursor at the specified grid coordinates on the specified battlefield.
     */
    public Cursor(int ax, int ay, BattleField aField)
    {
        loc = new Location(ax, ay);
        field = aField;
        
        double xCoord = BattleField.getXOf(loc);
        double yCoord = BattleField.getYOf(loc);
        
        cursor = new Rectangle2D.Double(xCoord, yCoord, SIZE, SIZE);
    }
    
    /**
     * Creates a cursor at the specified grid coordinates on the specified battlefield.
     */
    public Cursor(Location aLoc, BattleField aField)
    {
        loc = aLoc;
        field = aField;
        
        double xCoord = BattleField.getXOf(loc);
        double yCoord = BattleField.getYOf(loc);
        
        cursor = new Rectangle2D.Double(xCoord, yCoord, SIZE, SIZE);
    }
    
    /**
     * Creates a cursor at the center of the specified battlefield.
     */
    public Cursor(BattleField aField)
    {
        field = aField;
        
        int x = (int)(field.getTotalWidth()/2.0);
        int y = (int)(field.getTotalHeight()/2.0);
        loc = new Location(x,y);
        
        double xCoord = BattleField.getXOf(loc);
        double yCoord = BattleField.getYOf(loc);
        
        cursor = new Rectangle2D.Double(xCoord, yCoord, SIZE, SIZE);
    }
    
    /**
     * Moves the cursor up one grid unit.
     * Returns if the cursor actually moved.
     */
    public boolean moveUp()
    {
        if (inBoundsUp())
        {
            loc = new Location(loc.x(), loc.y() - 1);
            double xCoord = BattleField.getXOf(loc);
            double yCoord = BattleField.getYOf(loc);
            
            cursor = new Rectangle2D.Double(xCoord, yCoord, SIZE, SIZE);
            return true;
        }
        else
            return false;
    }
    
    /**
     * Moves the cursor down one grid unit.
     * Returns if the cursor actually moved.
     */
    public boolean moveDown()
    {
        if (inBoundsDown())
        {
            loc = new Location(loc.x(), loc.y() + 1);
            double xCoord = BattleField.getXOf(loc);
            double yCoord = BattleField.getYOf(loc);
            
            cursor = new Rectangle2D.Double(xCoord, yCoord, SIZE, SIZE);
            return true;
        }
        else
            return false;
    }
    
    /**
     * Moves the cursor left one grid unit.
     * Returns if the cursor actually moved.
     */
    public boolean moveLeft()
    {
        if (inBoundsLeft())
        {
            loc = new Location(loc.x() - 1, loc.y());
            double xCoord = BattleField.getXOf(loc);
            double yCoord = BattleField.getYOf(loc);
            
            cursor = new Rectangle2D.Double(xCoord, yCoord, SIZE, SIZE);
            return true;
        }
        else
            return false;
    }
    
    /**
     * Moves the cursor right one grid unit.
     * Returns if the cursor actually moved.
     */
    public boolean moveRight()
    {
        if (inBoundsRight())
        {
            loc = new Location(loc.x() + 1, loc.y());
            double xCoord = BattleField.getXOf(loc);
            double yCoord = BattleField.getYOf(loc);
            
            cursor = new Rectangle2D.Double(xCoord, yCoord, SIZE, SIZE);
            return true;
        }
        else
            return false;
    }
    
    /**
     * Moves the cursor to the specified coordinates.
     */
    public void moveTo(int ax, int ay)
    {
        loc = new Location(ax,ay);
    }
    
    /**
     * Returns the grid x coordinate of the grid currently selected by the cursor.
     */
    public int getX()
    {
        return loc.x();
    }
    
    /**
     * Returns the grid y coordinate of the grid currently selected by the cursor.
     */
    public int getY()
    {
        return loc.y();
    }
    
    /**
     * Returns the location of the cursor.
     */
    public Location getLoc()
    {
        return loc;
    }
    
    /**
     * Returns whether or not moving up would leave the cursor in bounds.
     */
    private boolean inBoundsUp()
    {
        return (getY() - 1 >= 0);
    }
        
    /**
     * Returns whether or not moving down would leave the cursor in bounds.
     */
    private boolean inBoundsDown()
    {
        return (getY() + 1 <= field.getTotalHeight() - 1);
    }
    
    /**
     * Returns whether or not moving left would leave the cursor in bounds.
     */
    private boolean inBoundsLeft()
    {
        return (getX() - 1 >= 0);
    }
    
    /**
     * Returns whether or not moving right would leave the cursor in bounds.
     */
    private boolean inBoundsRight()
    {
        return (getX() + 1 <= field.getTotalWidth() - 1);
    }
    
    /**
     * Paints the cursor onto the canvas.
     */
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setColor(Color.yellow);
        g2.draw(cursor);
    }
    
} // END CLASS