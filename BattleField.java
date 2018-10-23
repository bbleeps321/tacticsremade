import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

/**
 * Stores terrain and location information.
 * Stores images of terrain.
 */
public class BattleField extends JPanel implements ImageObserver
{
    public static int GRID_SIZE = 40; // the size of a single grid square
    
    private static final String FIELD_DIRECTORY = "Battlefields/"; // directory of battle fields
    private static final String FILE_TYPE = ".dat";
    
    private static final String TERRAIN_IMAGE_HEADING = "images/terrain/"; // directory of terrain images
    private static final String GRASS_URL = TERRAIN_IMAGE_HEADING + "grass.gif"; // the url of the terrain images
    private static final String FOREST_URL = TERRAIN_IMAGE_HEADING + "forest.gif";
    private static final String OCEAN_URL = TERRAIN_IMAGE_HEADING + "ocean.gif";
    private static final String FORTRESS_URL = TERRAIN_IMAGE_HEADING + "fortress.gif";
    private static final String HOUSE_URL = TERRAIN_IMAGE_HEADING + "house.gif";
    private static final String MOUNTAIN_URL = TERRAIN_IMAGE_HEADING + "mountain.gif";
    private static final String PEAK_URL = TERRAIN_IMAGE_HEADING + "peak.gif";
    private static final String THRONE_URL = TERRAIN_IMAGE_HEADING + "throne.gif";
    
    public static final String PLAIN_SYMBOL = "G"; // symbols in file that represent a certain terrain type; g for grass
    public static final String FOREST_SYMBOL = "T"; // t for tree
    public static final String FORT_SYMBOL = "F"; // f for fort
    public static final String OCEAN_SYMBOL = "O"; // o for ocean
    public static final String HOUSE_SYMBOL = "H"; // h for house
    public static final String MOUNTAIN_SYMBOL = "M"; // m for mountain
    public static final String PEAK_SYMBOL = "P"; // p for peak
    public static final String CHAIR_SYMBOL = "C"; // c for chair
    
    private static final int NO_DELAY = 1; // the values of delay that the terrain type gives to movement
    private static final int FOREST_DELAY = 2;
    private static final int OCEAN_DELAY = 100; // basically, impassable
    private static final int MOUNTAIN_DELAY = 3;
    private static final int PEAK_DELAY = 4;
    
    private Rectangle2D.Double[][] field; // the array of grids in the field
    private boolean[][] empty; // the array of grids representing if that part is empty or not
    private int[][] contains; // the array of grids representing the side of the unit contained there (-1 = no unit)
    private String[][] terrain; // the array of grids representing the terrain type there
    
    private int width, height; // the dimensions of the field
    
    private File fieldFile; // the file containing the battle field information
    private String fileName; // the file name, without any directories
    
    /**
     * Creates a battlefield with the specified dimensions.
     */
    public BattleField(int awidth, int aheight)
    {
        width = awidth;
        height = aheight;
        
        field = new Rectangle2D.Double[width][height];
        empty = new boolean[width][height];
        contains = new int[width][height];
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                field[i][j] = new Rectangle2D.Double(GRID_SIZE*i, GRID_SIZE*j, GRID_SIZE, GRID_SIZE);
                empty[i][j] = true;
                contains[i][j] = -1;
            }
        }
    }
    
    /**
     * PRIMARY CONSTRUCTOR.
     * Creates a battle field based on the specified String url.
     */
    public BattleField(String aFileName)
    {
        fileName = aFileName;
        fieldFile = new File(FIELD_DIRECTORY + fileName + FILE_TYPE);
        
        readFieldFile();
    }
    
    /**
     * Returns the frame's x-coordinate that corresponds to the specified grid coordinates.
     * Returns -1 if coordinate not on grid.
     */
    public static double getXOf(Location loc)
    {
        return loc.x()*GRID_SIZE;
    }
    
    /**
     * Returns the frame's y-coordinate that corresponds to the specified grid coordinates.
     * Returns -1 if coordinate not on grid.
     */
    public static double getYOf(Location loc)
    {
        return loc.y()*GRID_SIZE;
    }
    
    /**
     * Returns the frame's x-coordinate that corresponds to the specified grid coordinates.
     * Returns -1 if coordinate not on grid.
     */
    public static double getXOf(int x, int y)
    {
        return x*GRID_SIZE;
    }
    
    /**
     * Returns the frame's y-coordinate that corresponds to the specified grid coordinates.
     * Returns -1 if coordinate not on grid.
     */
    public static double getYOf(int x, int y)
    {
        return y*GRID_SIZE;
    }
    
    /**
     * Returns the total number of grid squares in the width.
     */
    public int getTotalWidth()
    {
        return width;
    }
    
    /**
     * Returns the total number of grid squares in the height.
     */
    public int getTotalHeight()
    {
        return height;
    }
    
    /**
     * Returns the actual width of the field.
     */
    public double getActualWidth()
    {
        return width*GRID_SIZE;
    }
    
    /**
     * Returns the actual width of the field.
     */
    public double getActualHeight()
    {
        return height*GRID_SIZE;
    }
    
    /**
     * Sets the size of each size of each grid.
     */
    public void setGridSize(int newVal)
    {
        GRID_SIZE = newVal;
    }
    
    /**
     * Fills the specified space.
     */
    public void fill(Location loc, int side)
    {
        empty[loc.x()][loc.y()] = false;
        contains[loc.x()][loc.y()] = side;
    }
    
    /**
     * Empties the specified space.
     */
    public void empty(Location loc)
    {
        empty[loc.x()][loc.y()] = true;
        contains[loc.x()][loc.y()] = -1;
    }
    
    /**
     * Returns the itself as a panel.
     */
    public JPanel getPanel()
    {
        return this;
    }
    
    /**
     * Returns whether or not the specified grid coordinates contain
     */
    public boolean isEmpty(Location loc)
    {
        return empty[loc.x()][loc.y()];
    }
    
    /**
     * Returns the movement delay value at the specified coordinates.
     */
    public int delayAt(Location loc, int side)
    {
        return delayAt(loc.x(), loc.y(), side);
    }
    
    /**
     * Returns the movement delay value at the specified coordinates.
     */
    public int delayAt(int x, int y, int side)
    {
        try
        {
            String terrain = terrainAt(x,y);
            int delay;
            if (terrain.equals(OCEAN_SYMBOL))
                delay =  OCEAN_DELAY;
            else if (terrain.equals(FOREST_SYMBOL))
                delay = FOREST_DELAY;
            else if (terrain.equals(MOUNTAIN_SYMBOL))
                delay = MOUNTAIN_DELAY;
            else if (terrain.equals(PEAK_SYMBOL))
                delay = PEAK_DELAY;
            else
                delay = NO_DELAY;
                
            if (side != contains[x][y] && contains[x][y] != -1) // if the location has an enemy on it
                delay = 100; // impassable
                
            return delay;
        }
        catch (ArrayIndexOutOfBoundsException e) // coordinate not on battle field
        {
            return 100; // impassable
        }
    }
            
    /**
     * Returns the terrain type at the specified coordinates.
     */
    public String terrainAt(Location loc)
    {
        return terrain[loc.x()][loc.y()];
    }
    
    /**
     * Returns the terrain type at the specified coordinates.
     */
    public String terrainAt(int x, int y)
    {
        return terrain[x][y];
    }
    
    /**
     * Returns the field file as a string without any preceding directories.
     */
    public String getFieldFile()
    {
        return fileName; 
    }
    
    /**
     * Returns the distance between the two locations.
     */
    public static int distanceBetween(int x1, int y1, int x2, int y2)
    {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }
    
    /**
     * Returns the distance between the two locations.
     */
    public static int distanceBetween(Location first, Location second)
    {
        return distanceBetween(first.x(), first.y(), second.x(), second.y());
    }
    
    /**
     * Reads the battle field file.
     */
    private void readFieldFile()
    {
        try
        {
            Scanner in = new Scanner(new FileInputStream(fieldFile));
            
            width = nextIntInFile(in);
            height = nextIntInFile(in);
            
            field = new Rectangle2D.Double[width][height];
            empty = new boolean[width][height];
            contains = new int[width][height];
            
            for (int i = 0; i < width; i++)
            {
                for (int j = 0; j < height; j++)
                {
                    field[i][j] = new Rectangle2D.Double(GRID_SIZE*i, GRID_SIZE*j, GRID_SIZE, GRID_SIZE);
                    empty[i][j] = true;
                    contains[i][j] = -1;
                }
            }
            
            terrain = readWholeFile(in);
        }
        catch (IOException e) {}
    }
    
    /**
     * Reads the file using the specified scanner and returns each seperate token in a string[][].
     */
    private String[][] readWholeFile(Scanner in)
    {
        String[][] tokens = new String[width][height];
        
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                tokens[j][i] = in.next();
            }
        }
        
        return tokens;
    }
    
    /**
     * Returns if the coordinates are valid or not (in bounds).
     */
    public boolean inBounds(int x, int y)
    {
        return (0 <= x) && (x < width) && (0 <= y) && (y < height);
    }
    
    /**
     * Returns if the coordinates are valid or not (in bounds).
     */
    public boolean inBounds(Location loc)
    {
        return inBounds(loc.x(), loc.y());
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
     * Paints the battlefield onto the canvas.
     */
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        
        Image plainImg = Toolkit.getDefaultToolkit().getImage(GRASS_URL);
        Image forestImg = Toolkit.getDefaultToolkit().getImage(FOREST_URL);
        Image fortImg = Toolkit.getDefaultToolkit().getImage(FORTRESS_URL);
        Image oceanImg = Toolkit.getDefaultToolkit().getImage(OCEAN_URL);
        Image houseImg = Toolkit.getDefaultToolkit().getImage(HOUSE_URL);
        Image mountainImg = Toolkit.getDefaultToolkit().getImage(MOUNTAIN_URL);
        Image peakImg = Toolkit.getDefaultToolkit().getImage(PEAK_URL);
        Image chairImg = Toolkit.getDefaultToolkit().getImage(THRONE_URL);
        
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                if (terrain[i][j].equals(PLAIN_SYMBOL))
                    g2.drawImage(plainImg, Math.round((float)field[i][j].getX()), Math.round((float)field[i][j].getY()), GRID_SIZE, GRID_SIZE, this);
                else if (terrain[i][j].equals(FOREST_SYMBOL))
                    g2.drawImage(forestImg, Math.round((float)field[i][j].getX()), Math.round((float)field[i][j].getY()), GRID_SIZE, GRID_SIZE, this);
                else if (terrain[i][j].equals(OCEAN_SYMBOL))
                    g2.drawImage(oceanImg, Math.round((float)field[i][j].getX()), Math.round((float)field[i][j].getY()), GRID_SIZE, GRID_SIZE, this);
                else if (terrain[i][j].equals(FORT_SYMBOL))
                    g2.drawImage(fortImg, Math.round((float)field[i][j].getX()), Math.round((float)field[i][j].getY()), GRID_SIZE, GRID_SIZE, this);
                else if (terrain[i][j].equals(HOUSE_SYMBOL))
                    g2.drawImage(houseImg, Math.round((float)field[i][j].getX()), Math.round((float)field[i][j].getY()), GRID_SIZE, GRID_SIZE, this);
                else if (terrain[i][j].equals(MOUNTAIN_SYMBOL))
                    g2.drawImage(mountainImg, Math.round((float)field[i][j].getX()), Math.round((float)field[i][j].getY()), GRID_SIZE, GRID_SIZE, this);
                else if (terrain[i][j].equals(PEAK_SYMBOL))
                    g2.drawImage(peakImg, Math.round((float)field[i][j].getX()), Math.round((float)field[i][j].getY()), GRID_SIZE, GRID_SIZE, this);
                else if (terrain[i][j].equals(CHAIR_SYMBOL))
                    g2.drawImage(chairImg, Math.round((float)field[i][j].getX()), Math.round((float)field[i][j].getY()), GRID_SIZE, GRID_SIZE, this);
                    
                g2.drawRect(Math.round((float)field[i][j].getX()), Math.round((float)field[i][j].getY()), GRID_SIZE, GRID_SIZE);
            }
        }
 
    }
    
} // END CLASS