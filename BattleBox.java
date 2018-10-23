import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * The stat box to be displayed before a fight.
 * Displays the name, hp, dmg, hit, crt, and wpn of the two units assigned to it.
 */
public class BattleBox extends JComponent
{
    private static final int UNIT_COUNT = 2; // number of units represented by box
     
    private static final Color[] SIDE_COLOR = {Color.blue, Color.red, Color.green, Color.yellow};
    
    private static final int STAT_NUM = 5; // number of stats to be displayed
    
    private Unit[] units; // the units the statbox is assigned to
    
    private boolean hidden; // if the statbox is hidden or not
    
    private double width, height; // the dimensions of the stat box
    
    private int displayed; // which panel is displayed (0 = stats, 1 = items)
    private int gap; // the gap between objects
    private int fontSize; // font size of text
    
    private Battle battle; // the battle where the box is displayed
    
    /**
     * Creates a statbox with the specified Units as the assigned unit.
     */
    public BattleBox(int aFontSize, Unit unit1, Unit unit2, Battle aBattle)
    {
        fontSize = aFontSize;
        
        battle = aBattle;
        units = new Unit[2];
        
        units[0] = unit1;
        units[1] = unit2;
        
        gap = (int)(0.75*fontSize);
        
        width = 19.5*fontSize;
        height = STAT_NUM*(fontSize + gap);
        
        hidden = true;
    }
    
    /**
     * Sets both units.
     */
    public void setUnits(Unit unit1, Unit unit2)
    {
        units[0] = unit1;
        units[1] = unit2;
    }
    
    /**
     * Shows the box.
     */
    public void showBox()
    {
        hidden = false;
    }
    
    /**
     * Hides the box.
     */
    public void hideBox()
    {
        hidden = true;
    }
    
    /**
     * Returns if the box is hidden or not.
     */
    public boolean isHidden()
    {
        return hidden;
    }
    
    /**
     * Paints the box onto the canvas at the specified coordinates.
     */
    public void paintComponent(int xCoord, int yCoord, Graphics g)
    {
        if (!hidden)
        {
            Graphics2D g2 = (Graphics2D) g;
            
            g2.setColor(Color.green);
            g2.fillRoundRect(xCoord - gap, yCoord - gap, Math.round((float)width) + 2*gap, Math.round((float)height) + 2*gap, gap, gap);
            
            for (int i = 0; i < UNIT_COUNT; i++)
            {
                boolean counter = (i == 1);
                
                Unit theUnit = units[i];
                Unit otherUnit;
                if (i == 0)
                    otherUnit = units[1];
                else
                    otherUnit = units[0];
                    
                int x = Math.round((float)(width/2)*i) + xCoord;
                g2.setColor(SIDE_COLOR[theUnit.getSide()]);
                
                g2.fillRoundRect(x, yCoord, Math.round((float)(width/2.0)), Math.round((float)height), gap, gap);
                
                g2.setFont(new Font("", Font.PLAIN, fontSize));
                String[] labels = {theUnit.getName(), "HP: " + theUnit.getHP(), "Dmg: " + battle.getDamageOf(theUnit, otherUnit, counter), "Hit: " + battle.getHitChanceOf(theUnit, otherUnit, counter), 
                                        "Wpn: " + theUnit.getWpnAt(0).getName()};
                
                g2.setColor(Color.white);
                
                for (int j = 0; j < labels.length; j++)
                {
                    g2.drawString(labels[j], Math.round(x + gap), Math.round(yCoord + gap*(2*j + 2)));
                }
            }
        }
    }
}