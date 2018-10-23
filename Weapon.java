import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.text.*;

/**
 * Stores the weapon properties (mt, wt, strengths, weaknesses, etc.).
 */
public class Weapon extends JComponent
{
    private static final String WEAPON_FILE_HEADING = "weapons/"; // the directory in which the weapon files can be found
    private static final String EXTENSION = ".dat"; // file type
    
    public static final String SWORD = "Sword"; // kind of weapon
    public static final String LANCE = "Lance";
    public static final String AXE = "Axe";
    public static final String BOW = "Bow";
    public static final String SPELL = "Spell";
    
    public static final String IRON = "Iron"; // level of weapon
    public static final String STEEL = "Steel";
    public static final String SILVER = "Silver";
    
    public static final String FIRE = "Fire"; // fire spells
    public static final String ELFIRE = "Elfire";
    public static final String REXFLAME = "Rexflame";
    
    public static final String WIND = "Wind"; // wind spells
    public static final String ELWIND = "Elwind";
    public static final String REXCALIBUR = "Rexcalibur";
    
    public static final String THUNDER = "Thunder"; // wind spells
    public static final String ELTHUNDER = "Elthunder";
    public static final String REXBOLT = "Rexbolt";
    
    private static final String DUMMY = "DUMMY"; // a substitute for an empty space 
    
    private File wpnFile; // the data file storing the weapon parameters
    
    private String level; // how "advanced" the weapon is (iron, steel, silver, etc).
    private String kind; // the kind of weapon (sword, lance, etc)
    private String name; // the entire name that is displayed
    
    private int mt; // the might (strength) of the weapon
    private int wt; // the weight of the weapon
    private NumberSet rng; // the range of the weapon
    
    private boolean hidden; // if the weapon stats are hidden or not
    
    /**
     * PRIMARY CONSTRUCTOR.
     * Creates a weapon with properties read from the String url.
     */
    public Weapon(String url)
    {
        name = url;
        wpnFile = new File(WEAPON_FILE_HEADING + url + EXTENSION);
        readWeaponFile();
        hidden = true;
    }
    
    /**
     * Creates a weapon based on the specified weapon.
     * Creates a copy.
     */
    public Weapon(Weapon wpn)
    {
        mt = wpn.getMt();
        wt = wpn.getWt();
        rng = wpn.getAtkRng();
        level = wpn.getLevel();
        kind = wpn.getKind();
        wpnFile = wpn.getFile();
    }
    
    /**
     * Returns the might of the weapon.
     */
    public int getMt()
    {
        return mt;
    }
    
    /**
     * Returns the weight of the weapon.
     */
    public int getWt()
    {
        return wt;
    }
    
    /**
     * Returns the attack range of the weapon.
     */
    public NumberSet getAtkRng()
    {
        return rng;
    }
    
    /**
     * Returns the weapon level.
     */
    public String getLevel()
    {
        return level;
    }
    
    /**
     * Returns the weapon kind.
     */
    public String getKind()
    {
        return kind;
    }
    
    /**
     * Returns the name of the weapon.
     */
    public String getName()
    {
        return name;
    }
        
    /**
     * Returns the file the weapon is based on.
     */
    public File getFile()
    {
        return wpnFile;
    }
    
    /**
     * Reads the weapon's stats from the file.
     * Sets the mt, wt, and rng variables to those found in the file.
     */
    private void readWeaponFile()
    {
        try
        {
            Scanner in = new Scanner(new FileInputStream(wpnFile));
            
            level = nextItemInFile(in);
            kind = nextItemInFile(in);
            mt = nextIntInFile(in);
            wt = nextIntInFile(in);
            rng = new NumberSet();
            while (in.hasNextLine())
                rng.add(nextIntInFile(in));
                
            if (level.equalsIgnoreCase(DUMMY)) // weapon file as a substitute
            {
                level = "";
                kind = "";
                name = "";
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
     * Hides the weapon display.
     */
    public void hideWpn()
    {
        hidden = true;
    }
    
    /**
     * Shows the weapon display.
     */
    public void showWpn()
    {
        hidden = false;
    }
    
    /**
     * Returns if the weapon is hidden or not.
     */
    public boolean isHidden()
    {
        return hidden;
    }
    
    /**
     * Returns the weapon type this weapon has an advantage over.
     */
    public String getAdvantage()
    {
        String s = "";
        if (kind.equals(SWORD))
            s = AXE;
        else if (kind.equals(LANCE))
            s = SWORD;
        else if (kind.equals(AXE))
            s = LANCE;
        return s;
    }
    
    /**
     * Returns the weapon type this weapon has a weakness against.
     */
    public String getWeakness()
    {
        String s = "";
        if (kind.equals(SWORD))
            s = LANCE;
        else if (kind.equals(LANCE))
            s = AXE;
        else if (kind.equals(AXE))
            s = SWORD;
        return s;
    }
    
    /**
     * Paints the weapon's stat display onto the canvas.
     */
    public void paintComponent(int x, int y, Graphics g)
    {
        if (!hidden)
        {
            final int FONT_SIZE = 20;
            final int GAP = 15;
            final int WIDTH = 150;
            final Font FONT = new Font("", Font.PLAIN, FONT_SIZE);
            
            Graphics2D g2 = (Graphics2D) g;
            String[] s = {"Mt: " + mt, "Wt: " + wt, "Rng:"};
            for (int h = 0; h < rng.size(); h++)
            {
                s[2] = s[2] + " " + rng.get(h);
                if (!(h == rng.size() - 1)) // not last one
                    s[2] = s[2] + " -";
            }
            
            g2.setColor(Color.red);
            g2.fillRoundRect(x, y, WIDTH, (s.length)*(FONT_SIZE + GAP), GAP, GAP);
            
            g2.setColor(Color.orange);
            g2.setFont(FONT);
            
            for (int i = 0; i < s.length; i++)
                g2.drawString(s[i], Math.round(x + GAP), Math.round(y + GAP*(2*i + 2)));
        }
    }
    
} // END CLASS