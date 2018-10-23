import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

/**
 * Generic unit.
 * Stores stats, weapon, image, etc, of unit.
 * Contains methods dictating movement.
 * Superclass of all other unit classes.
 */
public class Unit extends JComponent
{
    private static final String NEWLINE = "\n";
    
    public static final int WPN_SLOTS = 4; // the maximum number of weapons a unit can hold
    public static final int ITEM_SLOTS = 4; // the maximum number of items a unit can hold
    private static final int MAX_EXP = 100; // 100 points per level
    
    private static final Color[] SIDE_COLOR = {Color.blue, Color.red, Color.green, Color.yellow};
    
    private static final String UNIT_FILE_HEADING = "units/"; // the directory in which the unit files can be found
    private static final String UNIT_IMAGE_HEADING = "images/units/"; // the directory in which the unit picture can be found
    private static final String FILE_FORMAT = ".dat"; // the format of the file
    private static final String IMAGE_FORMAT = ".gif"; // the format of the image
    
    private ArrayList<String> validWpns; // all weapons the unit can wield
        
    private String weakness; // the kind of weapons the unit is weak against
    private String className; // the class name of the unit
    
    private int lvl; // the level of the unit
    private int hpMax; // the max hp of the unit
    private int hp; // the hp of the unit
    private int str, def, skl, spd; // the individual stats of the unit
    private int rng; // the movement range of the unit
    private int exp; // the exp the unit has (0 <= exp <= 99)
    private int side; // the side the unit is on
    
    private Location loc; // the grid coordinates of the unit
    private Location oldLoc; // the coordinates the unit was at before moving
    
    private Weapon[] wpns; // the weapon the unit wields (index 0 should be primary)
    private Item[] items; // the items the unit holds
    
    private File unitFile; // the file the unit is based on
    private String fileName; // the name of the file, without any directories
    
    private boolean promoted; // if the unit is promoted
    private boolean rngVisible; // if the unit's movement range is visible on the map
    private boolean atkRngVisible; // if the unit's weapon range is visible on the map
    private boolean enabled; // if the unit can be moved during the turn or not
    
    private Image unitImage; // the image of the unit displayed on the map
    
    private BattleField field; // the battlefield the unit is on
    
    /**
     * PRIMARY CONSTRUCTOR.
     * Creates a unit with properties read from the specified String url.
     */
    public Unit(int ax, int ay, String aFileName, int aSide, BattleField aField)
    {
        loc = new Location(ax,ay);
        side = aSide;
        
        String[] directories = aFileName.split("/");
        fileName = directories[directories.length - 1];
        unitImage = Toolkit.getDefaultToolkit().getImage(UNIT_IMAGE_HEADING + this.getName() + IMAGE_FORMAT);
        
        field = aField;
        
        items = new Item[ITEM_SLOTS];
        wpns = new Weapon[WPN_SLOTS];
        
        unitFile = new File(UNIT_FILE_HEADING + aFileName + FILE_FORMAT);
        readUnitFile();
        
        rngVisible = false;
        enabled = true;
    }
    
    /**
     * Returns the x coordinate of the unit.
     */
    public int getX()
    {
        return loc.x();
    }
    
    /**
     * Returns the y coordinate of the unit.
     */
    public int getY()
    {
        return loc.y();
    }
    
    /**
     * Returns the location of the unit.
     */
    public Location getLoc()
    {
        return loc;
    }
    
    /**
     * Returns the old x coordinate of the unit.
     */
    public int getOldX()
    {
        return oldLoc.x();
    }
    
    /**
     * Returns the old y coordinate of the unit.
     */
    public int getOldY()
    {
        return oldLoc.y();
    }
    
    /**
     * Returns the old location of the unit.
     */
    public Location getOldLoc()
    {
        return oldLoc;
    }
    
    /**
     * Returns the name of the unit.
     */
    public String getName()
    {
        String[] nameParts = fileName.split("_");
        return nameParts[0]; // return "first" name only
    }
    
    /**
     * Returns the hp of the unit.
     */
    public int getHP()
    {
        return hp;
    }
    
    /**
     * Returns the strength of the unit.
     */
    public int getStr()
    {
        return str;
    }
    
    /**
     * Returns the defense of the unit.
     */
    public int getDef()
    {
        return def;
    }
    
    /**
     * Returns the skill of the unit.
     */
    public int getSkl()
    {
        return skl;
    }
    
    /**
     * Returns the speed of the unit.
     */
    public int getSpd()
    {
        return spd;
    }
    
    /**
     * Returns the movement range of the unit.
     */
    public int getMoveRng()
    {
        return rng;
    }
    
    /**
     * Returns the weapon of the unit.
     */
    public Weapon[] getWpns()
    {
        return wpns;
    }
    
    /**
     * Returns the weapon weakness of the unit.
     */
    public String getWeakness()
    {
        return weakness;
    }
    
    /**
     * Returns the level of the unit.
     */
    public int getLevel()
    {
        return lvl;
    }
    
    /**
     * Returns the side the unit is on.
     */
    public int getSide()
    {
        return side;
    }
    
    /**
     * Returns the unit's exp.
     */
    public int getExp()
    {
        return exp;
    }

    /**
     * Returns if the unit is promoted or not.
     */
    public boolean isPromoted()
    {
        return promoted;
    }
    
    /**
     * Returns if the unit is "enabled" or not.
     */
    public boolean isEnabled()
    {
        return enabled;
    }
    
    /**
     * Returns all the items the unit is holding.
     */
    public Item[] getItems()
    {
        return items;
    }
    
    /**
     * Returns the items the unit is holding as a string[].
     */
    public String[] getItemsAsString()
    {
        String[] s = new String[ITEM_SLOTS];
        for (int i = 0; i < ITEM_SLOTS; i++)
        {
            s[i] = items[i].getName();
        }
        return s;
    }
    
    /**
     * Returns the weapons the unit is holding as a string[].
     */
    public String[] getWpnsAsString()
    {
        String[] s = new String[WPN_SLOTS];
        for (int i = 0; i < WPN_SLOTS; i++)
        {
            s[i] = wpns[i].getName();
        }
        return s;
    }
    
    /**
     * Returns the weapon at the specified index.
     */
    public Weapon getWpnAt(int index)
    {
        return wpns[index];
    }
    
    /**
     * Returns the item at the specified index.
     */
    public Item getItemAt(int index)
    {
        return items[index];
    }
    
    /**
     * Sets the unit's "enabled" to the specified value.
     */
    public void setEnabled(boolean newVal)
    {
        enabled = newVal;
    }
    
    /**
     * Sets the x coordinate of the unit.
     */
    public void setX(int newVal)
    {
        oldLoc = new Location(loc);
        loc = new Location(newVal, loc.y());
    }
    
    /**
     * Sets the y coordinate of the unit.
     */
    public void setY(int newVal)
    {
        oldLoc = new Location(loc);
        loc = new Location(loc.x(), newVal);
    }
    
    /**
     * Sets the location of the unit.
     */
    public void setLoc(Location aLoc)
    {
        oldLoc = new Location(loc);
        loc = aLoc;
    }
    
    /**
     * Sets the hp of the unit.
     */
    public void setHP(int newVal)
    {
        if (newVal < 0)
            newVal = 0;
        else if (newVal > hpMax)
            newVal = hpMax;
        
        hp = newVal;
    }
    
    /**
     * Sets the strength of the unit.
     */
    public void setStr(int newVal)
    {
        str = newVal;
    }
    
    /**
     * Sets the defense of the unit.
     */
    public void setDef(int newVal)
    {
        def = newVal;
    }
    
    /**
     * Sets the skill of the unit.
     */
    public void setSkl(int newVal)
    {
        skl = newVal;
    }
    
    /**
     * Sets the speed of the unit.
     */
    public void setSpd(int newVal)
    {
        spd = newVal;
    }
    
    /**
     * Sets the movement range of the unit.
     */
    public void setMoveRng(int newVal)
    {
        rng = newVal;
    }
    
    /**
     * Sets the weapon of the unit at the specified index.
     */
    public void setWpnAt(int index, Weapon newWpn)
    {
        wpns[index] = newWpn;
    }
    
    /**
     * Sets the unit's weapon weakness to the specified weapon kind.
     */
    public void setWeakness(String aWpnKind)
    {
        weakness = aWpnKind;
    }
    
    /**
     * Sets the unit's wieldable weapon kinds to the specified String ArrayList of weapon kinds.
     */
    public void setWieldable(ArrayList<String> newVal)
    {
        validWpns = newVal;
    }
    
    /**
     * Sets the unit's exp to the specified value.
     */
    public void setExp(int newVal)
    {
        exp = newVal;
        if (exp >= MAX_EXP)
        {
            levelUp();
            exp -= MAX_EXP;
        }
    }
    
    /**
     * Sets the primary weapon of the unit to the one at the specified index.
     * All other weapons are shifted "down" and index.
     */
    public void setPrimaryWpn(int index)
    {
        ArrayList<Weapon> tempWpns = new ArrayList<Weapon>();
        
        for (int i = 0; i < wpns.length; i++)
        {
            tempWpns.add(wpns[i]);
        }
        
        Weapon theWpn = tempWpns.set(index, new Weapon("DUMMY"));
        tempWpns.add(0, theWpn); // add weapon to beginning
        tempWpns.trimToSize();
        
        for (int j = 0; j < wpns.length; j++)
            if (!((tempWpns.get(j)).getName()).equals(""))
                wpns[j] = tempWpns.get(j);
            else
                break;
    }
    /**
     * Returns whether or not the specified weapon is a weapon that the unit can wield.
     */
    public boolean isValidWpn(Weapon aWpn)
    {
        boolean isValid = false;
        
        for (int i = 0; i < validWpns.size(); i++)
        {
            if (aWpn.getKind().equals((String)validWpns.get(i)))
            {
                isValid = true;
            }
        }
        return isValid;
    }
    
    /**
     * Returns whether or not the unit is alive (has any hp left).
     */
    public boolean isAlive()
    {
        return hp > 0;
    }
    
    /**
     * Returns whether or not the specified grid coordinates are in the unit's movement range.
     */
    public boolean isInMoveRange(int x2, int y2)
    {
        boolean inRange = false;
        ArrayList<Rectangle2D.Double> grids = createMovementRange(loc.x(), loc.y(), 0);
        for (Rectangle2D.Double r : grids)
            if (x2*BattleField.GRID_SIZE == r.getX() && y2*BattleField.GRID_SIZE == r.getY())
                inRange = true;
                
        return inRange;
    }
    
    /**
     * Returns whether or not the specified grid coordinates are in the unit's movement range.
     */
    public boolean isInMoveRange(Location loc2)
    {
        return isInMoveRange(loc2.x(), loc2.y());
    }
    
    /**
     * Returns whether or not the specified grid coordinates are in the unit's attack range.
     */
    public boolean isInAtkRange(int x2, int y2)
    {
        return isInAtkRange(loc.x(), loc.y(), x2, y2);
    }
    
    /**
     * Returns whether or not the specified grid coordinates are in the unit's attack range.
     */
    public boolean isInAtkRange(Location loc2)
    {
        return isInAtkRange(loc.x(), loc.y(), loc2.x(), loc2.y());
    }
    
    /**
     * Returns whether or not the specified grid coordinates are in the unit's attack range from the specified location.
     */
    public boolean isInAtkRange(int x1, int y1, int x2, int y2)
    {
        int count = Math.abs(x2 - x1) + Math.abs(y2 - y1);
        NumberSet wpnRng = wpns[0].getAtkRng(); // get attack range of primary weapon
        boolean inRange = false;
        for (int i = 0; i < wpnRng.size(); i++)
            if(count == wpnRng.get(i))
                inRange = true;
                
        return inRange;
    }
    
    /**
     * Returns whether or not the specified grid coordinates are in the unit's farthest possible attack range, even after movement, during one turn.
     */
    public boolean isInThreatRange(int x2, int y2)
    {
        boolean inRange = false;
        ArrayList<Rectangle2D.Double> grids = createMovementRange(loc.x(), loc.y(), 0);
        for (Rectangle2D.Double r : grids)
        {
            int tempX = Math.round((float)(r.getX()/BattleField.GRID_SIZE)); // get grid coordinates at possible movement location
            int tempY = Math.round((float)(r.getY()/BattleField.GRID_SIZE));
            if (isInAtkRange(tempX, tempY, x2, y2))
                inRange = true;
        }
        return inRange;
    }
    
    /**
     * Returns whether or not the specified grid coordinates are in the unit's farthest possible attack range, even after movement, during one turn.
     */
    public boolean isInThreatRange(Location loc2)
    {
        return isInThreatRange(loc2.x(), loc2.y());
    }
    
    /**
     * Uses the selected item.
     */
    public void useItem(int index)
    {
        if (items[index].getUsesLeft() > 0)
        {
            String affected = items[index].getAffectedStat();
            
            if (affected.equals(Item.HP_UP_TYPE))
            {
                hp += items[index].getChangeValue();
                if (hp > hpMax)
                    hp = hpMax;
            }
            else if (affected.equals(Item.STR_UP_TYPE))
                str += items[index].getChangeValue();
            else if (affected.equals(Item.DEF_UP_TYPE))
                def += items[index].getChangeValue();
            else if (affected.equals(Item.SKL_UP_TYPE))
                skl += items[index].getChangeValue();
            else if (affected.equals(Item.SPD_UP_TYPE))
                spd += items[index].getChangeValue();
            else if (affected.equals(Item.RNG_UP_TYPE))
                rng += items[index].getChangeValue();

            items[index].useOnce();
            
            if (items[index].getUsesLeft() <= 0) // remove item is completely used up
            {
                ArrayList<Item> itemsTemp = new ArrayList<Item>();
                
                for (int i = 0; i < ITEM_SLOTS; i++) // shift all items below the used up one on the list upwards
                {
                    itemsTemp.add(items[i]);
                    items[i] = null;
                }
                
                itemsTemp.remove(index);
                itemsTemp.trimToSize();
                
                for (int i = 0; i < itemsTemp.size(); i++)
                    items[i] = itemsTemp.get(i);
                for (int i = 0; i < ITEM_SLOTS; i++)
                    if (items[i] == null)
                        items[i] = new Item("DUMMY");
            }
        }
    }
    
    /**
     * Raises level by one and randomly raises stats.
     */
    public void levelUp()
    {
        Random rand = new Random();
        
        int last = rand.nextInt(100);
        if (last == 0) // 1/5 chance of increasing the stat
        {
            hp++;
            hpMax++;
        }
        last = rand.nextInt(100);
        if (last == 0) // 1/5 chance of increasing the stat
            str++;
        last = rand.nextInt(100);
        if (last == 0) // 1/5 chance of increasing the stat
            def++;
        last = rand.nextInt(100);
        if (last == 0) // 1/5 chance of increasing the stat
            skl++;
        last = rand.nextInt(100);
        if (last == 0) // 1/5 chance of increasing the stat
            spd++;
        
        lvl++;
    }
    
    /**
     * Sets the unit's movement range to the specified boolean.
     */
    public void setVisibleRange(boolean newVal)
    {
        rngVisible = newVal;
    }
    
    /**
     * Returns whether the unit's movement range is visible or not.
     */
    public boolean rangeVisible()
    {
        return rngVisible;
    }
    
    /**
     * Sets the attack range to the specified value.
     */
    public void setAtkRngVisible(boolean newVal)
    {
        atkRngVisible = newVal;
    }
    
    /**
     * Returns whether the unit's attack range is visible or not.
     */
    public boolean atkRngVisible()
    {
        return atkRngVisible;
    }
    
    /**
     * Returns the unit file as a string.
     */
    public String getUnitFileString()
    {
        return fileName;
    }
    
    /**
     * Returns the unit file.
     */
    public File getUnitFile()
    {
        return unitFile;
    }
    
    /**
     * Reads unit's properties from the specified file.
     * Sets the stats, name, weapon, etc to those found in the file.
     */
    private void readUnitFile()
    {
        try
        {
            Scanner in = new Scanner(unitFile);
            
            validWpns = new ArrayList<String>();
            
            lvl = nextIntInFile(in);
            exp = nextIntInFile(in);
            hp = nextIntInFile(in);
            hpMax = hp;
            str = nextIntInFile(in);
            def = nextIntInFile(in);
            skl = nextIntInFile(in);
            spd = nextIntInFile(in);
            rng = nextIntInFile(in);
            for (int i = 0; i < WPN_SLOTS; i++) // read weapon(s)
            {
                wpns[i] = new Weapon(nextItemInFile(in));
            }
            for (int i = 0; i < ITEM_SLOTS; i++) // read item(s)
            {
                items[i] = new Item(nextItemInFile(in), nextIntInFile(in));
            }
            String[] allValidWpn = nextItemInFile(in).split(" ");
            for (int i = 0; i < allValidWpn.length; i++)
                validWpns.add(allValidWpn[i]);
        }
        catch (IOException e) { System.out.println(e);}
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
        String s;
        do
        {
            s = in.nextLine();
        }
        while (s.equals(""));

        String[] sArray = s.split(":");
        return sArray[1].trim();
    }
    
    /**
     * Saves the unit's properties into a data file.
     */
    public void saveUnitFile(File file)
    {
        try
        {
            PrintStream out = new PrintStream(new FileOutputStream(file));
            
            String[] wpnString = new String[WPN_SLOTS];
            String[] itmString = new String[ITEM_SLOTS];
            
            for (int i = 0; i < wpnString.length; i++) 
                if (wpns[i].getName().equals("") || wpns[i] == null)
                    wpnString[i] = "DUMMY";
                else
                    wpnString[i] = wpns[i].getName();
            for (int i = 0; i < itmString.length; i++) 
                if (items[i].getName().equals("") || items[i] == null)
                    itmString[i] = "DUMMY";
                else
                    itmString[i] = items[i].getName();
                    
            out.print(
            "Level: " + lvl + NEWLINE +
            "Exp: " + exp + NEWLINE +
            "HP: " + hp + NEWLINE +
            "Strength: " + str + NEWLINE +
            "Defense: " + def + NEWLINE +
            "Skill: " + skl + NEWLINE +
            "Speed: " + spd + NEWLINE +
            "Movement Range: " + rng + NEWLINE +
            "Weapon1: " + wpnString[0] + NEWLINE +
            "Weapon2: " + wpnString[1] + NEWLINE +
            "Weapon3: " + wpnString[2] + NEWLINE +
            "Weapon4: " + wpnString[3] + NEWLINE +
            "Item1: " + itmString[0] + NEWLINE +
            "Item2: " + itmString[1] + NEWLINE +
            "Item3: " + itmString[2] + NEWLINE +
            "Item4: " + itmString[3] + NEWLINE +
            "Wieldable: ");
            
            for (int i = 0; i < validWpns.size(); i++)
                out.print(validWpns.get(i));
        }
        catch (IOException e) {}
    }
    
    /**
     * Saves the unit's properties into a data file.
     */
    public void saveUnitFile()
    {
        try
        {
            PrintStream out = new PrintStream(new FileOutputStream(unitFile));
            
            String[] wpnString = new String[WPN_SLOTS];
            String[] itmString = new String[ITEM_SLOTS];
            
            for (int i = 0; i < wpnString.length; i++) 
                if (wpns[i].getName().equals("") || wpns[i] == null)
                    wpnString[i] = "DUMMY";
                else
                    wpnString[i] = wpns[i].getName();
            for (int i = 0; i < itmString.length; i++) 
                if (items[i].getName().equals("") || items[i] == null)
                    itmString[i] = "DUMMY";
                else
                    itmString[i] = items[i].getName();
                    
            out.print(
            "Level: " + lvl + NEWLINE +
            "Exp: " + exp + NEWLINE +
            "HP: " + hp + NEWLINE +
            "Strength: " + str + NEWLINE +
            "Defense: " + def + NEWLINE +
            "Skill: " + skl + NEWLINE +
            "Speed: " + spd + NEWLINE +
            "Movement Range: " + rng + NEWLINE +
            "Weapon1: " + wpnString[0] + NEWLINE +
            "Weapon2: " + wpnString[1] + NEWLINE +
            "Weapon3: " + wpnString[2] + NEWLINE +
            "Weapon4: " + wpnString[3] + NEWLINE +
            "Item1: " + itmString[0] + NEWLINE +
            "Item2: " + itmString[1] + NEWLINE +
            "Item3: " + itmString[2] + NEWLINE +
            "Item4: " + itmString[3] + NEWLINE +
            "Wieldable: ");
            
            for (int i = 0; i < validWpns.size(); i++)
                out.print(validWpns.get(i));
        }
        catch (IOException e) {}
    }
    
    /**
     * Paints the unit onto the canvas.
     */
    public void paintComponent(Graphics g)
    {
        if (hp > 0) // paints if unit has hp left
        {
            Graphics2D g2 = (Graphics2D) g;
            
            double canvasX = BattleField.getXOf(loc);
            double canvasY = BattleField.getYOf(loc);
            
            g2.drawImage(unitImage, Math.round((float)canvasX) + 1, Math.round((float)canvasY) + 1, BattleField.GRID_SIZE - 2, BattleField.GRID_SIZE - 2, this);
            
            Rectangle2D.Double r = new Rectangle2D.Double(Math.round((float)canvasX) + 1, Math.round((float)canvasY) + 1, BattleField.GRID_SIZE - 2, BattleField.GRID_SIZE - 2);
            if (enabled)
                g2.setColor(SIDE_COLOR[side]);
            else
                g2.setColor(Color.gray);
            g2.draw(r);
            
            r = new Rectangle2D.Double(Math.round((float)canvasX) + 2, Math.round((float)canvasY) + 2, BattleField.GRID_SIZE - 4, BattleField.GRID_SIZE - 4);
            g2.draw(r);
            
            if (rngVisible)
            {
                g2.setColor(Color.white);
                ArrayList<Rectangle2D.Double> grids = createMovementRange(loc.x(), loc.y(), 0); // step count is initially zero
                for (Rectangle2D.Double rect : grids)
                    g2.draw(rect);
            }
            if (atkRngVisible)
                paintAttackRange(g);
        }
    }
    
    /**
     * Creates and returns the unit's movement range in the form of rectangles.
     */
    private ArrayList<Rectangle2D.Double> createMovementRange(int lastX, int lastY, int count)
    {
        ArrayList<Rectangle2D.Double> grids = new ArrayList<Rectangle2D.Double>();
        
        // right of old coordinate
        double xCoord = BattleField.getXOf(lastX + 1, lastY);
        double yCoord = BattleField.getYOf(lastX + 1, lastY);
        Rectangle2D.Double r = new Rectangle2D.Double(xCoord, yCoord, BattleField.GRID_SIZE, BattleField.GRID_SIZE);
        
        if (count + field.delayAt(lastX + 1, lastY, side) <= rng)
        {
            grids.add(r);
            ArrayList<Rectangle2D.Double> temp = createMovementRange(lastX + 1, lastY, count + field.delayAt(lastX + 1, lastY, side));
        
            for (int i = 0; i < temp.size(); i++)
                if (!grids.contains(temp.get(i))) // to avoid redundancy
                    grids.add(temp.get(i));
        }
            
        // below old coordinate
        xCoord = BattleField.getXOf(lastX, lastY + 1);
        yCoord = BattleField.getYOf(lastX, lastY + 1);
        r = new Rectangle2D.Double(xCoord, yCoord, BattleField.GRID_SIZE, BattleField.GRID_SIZE);

        if (count + field.delayAt(lastX, lastY + 1, side) <= rng)
        {
            grids.add(r);
            ArrayList<Rectangle2D.Double> temp = createMovementRange(lastX, lastY + 1, count + field.delayAt(lastX, lastY + 1, side));
        
            for (int i = 0; i < temp.size(); i++)
                if (!grids.contains(temp.get(i))) // to avoid redundancy
                    grids.add(temp.get(i));
        }
        
        // left of old coordinate
        xCoord = BattleField.getXOf(lastX - 1, lastY);
        yCoord = BattleField.getYOf(lastX - 1, lastY);
        r = new Rectangle2D.Double(xCoord, yCoord, BattleField.GRID_SIZE, BattleField.GRID_SIZE);

        if (count + field.delayAt(lastX - 1, lastY, side) <= rng)
        {
            grids.add(r);
            ArrayList<Rectangle2D.Double> temp = createMovementRange(lastX - 1, lastY, count + field.delayAt(lastX - 1, lastY, side));
        
            for (int i = 0; i < temp.size(); i++)
                if (!grids.contains(temp.get(i))) // to avoid redundancy
                    grids.add(temp.get(i));
        }
        
        // above old coordinate
        xCoord = BattleField.getXOf(lastX, lastY - 1);
        yCoord = BattleField.getYOf(lastX, lastY - 1);
        r = new Rectangle2D.Double(xCoord, yCoord, BattleField.GRID_SIZE, BattleField.GRID_SIZE);

        if (count + field.delayAt(lastX, lastY - 1, side) <= rng)
        {
            grids.add(r);
            ArrayList<Rectangle2D.Double> temp = createMovementRange(lastX, lastY - 1, count + field.delayAt(lastX, lastY - 1, side));
        
            for (int i = 0; i < temp.size(); i++)
                if (!grids.contains(temp.get(i))) // to avoid redundancy
                    grids.add(temp.get(i));
        }
        return grids;
    }
    
    /**
     * Paints the unit's attack range.
     */
    private void paintAttackRange(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        
        NumberSet wpnRng = wpns[0].getAtkRng(); // get attack range of primary weapon
        for (int a = 0; a < wpnRng.size(); a++)
        {
            double xCoord, yCoord;
            Line2D.Double line;
            g2.setColor((Color.red).darker());
            
            for (int i = 0; i <= wpnRng.get(a); i++)
            {
                xCoord = BattleField.getXOf(new Location(loc.x() - i, loc.y() - (wpnRng.get(a) - i)));
                yCoord = BattleField.getYOf(new Location(loc.x() - i, loc.y() - (wpnRng.get(a) - i)));
                
                // draw x in center
                drawX(xCoord, yCoord, g2);
                
                // left of upper left
                line = new Line2D.Double(xCoord + 1, yCoord + 1, xCoord + 1, yCoord + BattleField.GRID_SIZE - 2);
                g2.draw(line);
                
                // right of upper right
                xCoord = BattleField.getXOf(new Location(loc.x() + i + 1, loc.y() - (wpnRng.get(a) - i)));
                yCoord = BattleField.getYOf(new Location(loc.x() + i + 1, loc.y() - (wpnRng.get(a) - i)));
                line = new Line2D.Double(xCoord - 1, yCoord - 1, xCoord - 1, yCoord + BattleField.GRID_SIZE - 2);
                g2.draw(line);
                
                // draw x in center
                drawX(xCoord - BattleField.GRID_SIZE, yCoord, g2);
                
                // left of lower left
                xCoord = BattleField.getXOf(new Location(loc.x() - i, loc.y() + (wpnRng.get(a) - i)));
                yCoord = BattleField.getYOf(new Location(loc.x() - i, loc.y() + (wpnRng.get(a) - i)));
                line = new Line2D.Double(xCoord + 1, yCoord + 1, xCoord + 1, yCoord + BattleField.GRID_SIZE - 2);
                g2.draw(line);
                
                // draw x in center
                drawX(xCoord, yCoord, g2);
                
                // right of lower right
                xCoord = BattleField.getXOf(new Location(loc.x() + i + 1, loc.y() + (wpnRng.get(a) - i)));
                yCoord = BattleField.getYOf(new Location(loc.x() + i + 1, loc.y() + (wpnRng.get(a) - i)));
                line = new Line2D.Double(xCoord - 1, yCoord - 1, xCoord - 1, yCoord + BattleField.GRID_SIZE - 2);
                g2.draw(line);
                
                // draw x in center
                drawX(xCoord - BattleField.GRID_SIZE, yCoord, g2);
            }
            for (int j = 0; j <= wpnRng.get(a); j++)
            {
                // top of upper right
                xCoord = BattleField.getXOf(new Location(loc.x() - (wpnRng.get(a) - j), loc.y() - j));
                yCoord = BattleField.getYOf(new Location(loc.x() - (wpnRng.get(a) - j), loc.y() - j));
                line = new Line2D.Double(xCoord + 1, yCoord + 1, xCoord + BattleField.GRID_SIZE - 2, yCoord + 1);
                g2.draw(line);
                
                // top of upper right
                xCoord = BattleField.getXOf(new Location(loc.x() - (wpnRng.get(a) - j), loc.y() + j + 1));
                yCoord = BattleField.getYOf(new Location(loc.x() - (wpnRng.get(a) - j), loc.y() + j + 1));
                line = new Line2D.Double(xCoord + 1, yCoord + 1, xCoord + BattleField.GRID_SIZE - 2, yCoord + 1);
                g2.draw(line);
                
                // bottom of lower left
                xCoord = BattleField.getXOf(new Location(loc.x() + (wpnRng.get(a) - j), loc.y() - j));
                yCoord = BattleField.getYOf(new Location(loc.x() + (wpnRng.get(a) - j), loc.y() - j));
                line = new Line2D.Double(xCoord - 1, yCoord - 1, xCoord + BattleField.GRID_SIZE - 2, yCoord - 1);
                g2.draw(line);
                
                // bottom of lower right
                xCoord = BattleField.getXOf(new Location(loc.x() + (wpnRng.get(a) - j), loc.y() + j + 1));
                yCoord = BattleField.getYOf(new Location(loc.x() + (wpnRng.get(a) - j), loc.y() + j + 1));
                line = new Line2D.Double(xCoord - 1, yCoord - 1, xCoord + BattleField.GRID_SIZE - 2, yCoord - 1);
                g2.draw(line);
            }
        }
    }
    
    /**
     * Draws an "X" using a Graphics2D object and the specified coordinates.
     */
    private void drawX(double xCoord, double yCoord, Graphics2D g2)
    {
        Line2D.Double line;
        
        // part of x in center
        line = new Line2D.Double(xCoord + 1, yCoord + 1, xCoord + BattleField.GRID_SIZE - 2, yCoord + BattleField.GRID_SIZE - 2);
        g2.draw(line);
        
        // other part of x in center
        line = new Line2D.Double(xCoord + 1, yCoord + (BattleField.GRID_SIZE - 2), xCoord + BattleField.GRID_SIZE - 2, yCoord + 1);
        g2.draw(line);
    }

} // END CLASS