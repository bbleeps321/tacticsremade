import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

/**
 * Stores properties of items (potions, stat-builders, etc.).
 */
public class Item extends JComponent
{
    private static final String ITEM_FILE_HEADING = "items/"; // the directory in which the item files can be found
    private static final String EXTENSION = ".dat"; // file type
    
    public static final String HP_UP_TYPE = "HP"; // the types of items
    public static final String STR_UP_TYPE = "Strength";
    public static final String DEF_UP_TYPE = "Defense";
    public static final String SKL_UP_TYPE = "Skill";
    public static final String SPD_UP_TYPE = "Speed";
    public static final String RNG_UP_TYPE = "Range";
    
    public static final String POTION = "Potion"; // healing items
    public static final String ELIXER = "Elixer";
    
    private static final String DUMMY = "DUMMY"; // substitute for empty space
    
    private int myUses; // how many times the item can be used
    
    private File itemFile; // the file containing the item's properties
    
    private String myName; // the name of the item
    private String myType; // the type of the item
    private String myDescription; // the description of the item
    
    private int bonusVal; // the value added/subtracted from the stat
    
    private boolean hidden; // if the weapon stats are hidden or not
    
    /**
     * PRIMARY CONSTRUCTOR.
     * Creates a item with properties read from the String url.
     */
    public Item(String url)
    {
        itemFile = new File(ITEM_FILE_HEADING + url + EXTENSION);
        readItemFile();
        hidden = true;
    }
    
    /**
     * PRIMARY CONSTRUCTOR.
     * Creates a item with properties read from the String url and specified number of uses.
     */
    public Item(String url, int usesLeft)
    {
        itemFile = new File(ITEM_FILE_HEADING + url + EXTENSION);
        readItemFile();
        myUses = usesLeft;
        hidden = true;
    }
    
    /**
     * Returns the name of the item.
     */
    public String getName()
    {
        return myName;
    }
    
    /**
     * Returns what stat is affected by the item.
     */
    public String getAffectedStat()
    {
        return myType;
    }
    
    /**
     * Returns the value by which the affected stat is increased.
     */
    public int getChangeValue()
    {
        return bonusVal;
    }
    
    /**
     * Returns the number of uses remaining on the item.
     */
    public int getUsesLeft()
    {
        return myUses;
    }
    
    /**
     * Decreases uses left by one.
     */
    public void useOnce()
    {
        myUses--;
    }
    
    /**
     * Reads the weapon's stats from the file.
     * Sets the name, type, uses, and bonusVal variables to those found in the file.
     */
    private void readItemFile()
    {
        try
        {
            Scanner in = new Scanner(new FileInputStream(itemFile));
            
            myName = nextItemInFile(in);
            myType = nextItemInFile(in);
            bonusVal = nextIntInFile(in);
            myUses = nextIntInFile(in);
            in.nextLine(); // for some reason...
            myDescription = nextItemInFile(in);
            
            if (myName.equals(DUMMY)) // item is a substitute
            {
                myName = "";
                myType = "";
                myUses = 0;
            }
        }
        catch (FileNotFoundException e) {}
    }
    
    /**
     * Returns the next integer in the file with the specified scanner.
     */
    private int nextIntInFile(Scanner in)
    {
        while (!in.hasNextInt())
        {
            in.next();
        }
        return in.nextInt();
    }
    
    /**
     * Returns the next item/weapon in the file with the specified scanner.
     */
    private String nextItemInFile(Scanner in)
    {
        String s = in.nextLine();
        String[] sArray = s.split(":");
        return sArray[1].trim();
    }
    
    /**
     * Hides the item display.
     */
    public void hideItem()
    {
        hidden = true;
    }
    
    /**
     * Shows the item display.
     */
    public void showItem()
    {
        hidden = false;
    }
    
    /**
     * Returns if the item is hidden or not.
     */
    public boolean isHidden()
    {
        return hidden;
    }

    /**
     * Paints the item's stat display onto the canvas.
     */
    public void paintComponent(int x, int y, Graphics g)
    {
        if (!hidden)
        {
            final int FONT_SIZE = 20;
            final int GAP = 15;
            final int WIDTH = 250;
            final Font FONT = new Font("", Font.PLAIN, FONT_SIZE);
            
            Graphics2D g2 = (Graphics2D) g;
            String[] s = {"Uses: " + myUses, myDescription};
            
            g2.setColor(Color.red);
            g2.fillRoundRect(x, y, WIDTH, (s.length)*(FONT_SIZE + GAP), GAP, GAP);
            
            g2.setColor(Color.orange);
            g2.setFont(FONT);
            
            for (int i = 0; i < s.length; i++)
                g2.drawString(s[i], Math.round(x + GAP), Math.round(y + GAP*(2*i + 2)));
        }
    }
    
} // END CLASS