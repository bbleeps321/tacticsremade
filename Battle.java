import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;

/**
 * Stores and controls battle sequences, sides, etc.
 */
public class Battle extends JComponent implements ActionListener
{
    //constants
    private static final String LEVEL_DIRECTORY = "Levels/"; // directory of levels
    private static final String FILE_TYPE = ".dat"; // file type
    private static final String NEWLINE = "\n"; // the new line character
    private static final String CURRENT_FILE = LEVEL_DIRECTORY + "Current Level" + FILE_TYPE; // the file where all saved changes are made
    private static final String CURRENT_LEVEL_DIRECTORY = "Units/Current Level/";
    
    private static final String ATTACK_ACTION = "Attack"; // actions displayed on menus
    private static final String ITEM_ACTION = "Item";
    private static final String WAIT_ACTION = "Wait";
    private static final String CANCEL_ACTION = "Cancel";
    private static final String SAVE_ACTION = "Save";
    private static final String END_TURN_ACTION = "End";
    
    private static final int FONT_SIZE = (int)(BattleField.GRID_SIZE*0.4); // the font size of the text
    private static final int MAX_HIT = 100; // maximum hit percentage
    private static final int GAP = 15; // gap between objects
    private static final int WINDOW_WIDTH = 11; // width of window in grid units (not including the border)
    private static final int WINDOW_HEIGHT = 11; // height of window in grid units (not including the border
    private static final int DELAY = 500; // delay between each "move" of the AI
    
    //instance fields
    private ArrayList<ArrayList<Unit>> side; // each team's units, divided
    private Unit selected; // the currently selected unit
    private Unit target; // the unit decided to be the target
    
    private BattleField field; // the battle field where the battle occurs
    
    private int originX, originY; // the the grid coordinates of the origin in terms of the upper left corner of the battle field.
    private int turn; // the number of turns so far
    private int currentSide; // the current side
    private int state; // the current state of the program
                       // 0 = no unit selected
                       // 1 = unit selected, not yet moved
                       // 2 = unit moved, no action decided (but menu shown)
                       // 3 = attack action selected, no weapon selected
                       // 4 = attack action and weapon selected, no target selected
                       // 5 = target selected, statistics displayed, battle not confirmed
                       // 6 = battle confirmed an "in progress"
                       // 7 = item menu displayed
    
    private Cursor cursor; // the cursor used to select units
    
    private Menu actionMenu; // the menu displaying the possible actions of a unit
    private Menu itemMenu; // the menu displaying the items and weapons of a unit
    private Menu startMenu; // the menu displaying save and end turn action
    
    private StatBox statBox; // displays a unit's information
    private BattleBox battleBox; // displays the stats of the attacker and defender before a battle
    
    private File battleFile; // the file the battle is based on
    private String fileName; // the name of the file
    
    private boolean hasCountered; // if the defender has counter-attacked
    
    private Rectangle2D.Double r = new Rectangle2D.Double(-50,-50,40,40);
    
    private javax.swing.Timer timer; // timer that regulates the speed at which the AI moves
    
    //constructors
    /**
     * PRIMARY CONSTRUCTOR.
     * Creates a battle using the specified string file name.
     */
    public Battle(String aFileName)
    {
        fileName = aFileName;
        
        side = new ArrayList<ArrayList<Unit>>();

        battleFile = new File(LEVEL_DIRECTORY + fileName + FILE_TYPE);
        
        readBattleFile();
        
        String[] actions = {ATTACK_ACTION, ITEM_ACTION, WAIT_ACTION, ""};
        actionMenu = new Menu(actions, FONT_SIZE);
        
        statBox = new StatBox(FONT_SIZE, (side.get(0)).get(0)); // create with arbitrary unit
        battleBox = new BattleBox(FONT_SIZE, (side.get(0)).get(0), (side.get(0)).get(0), this);
        
        String[] itemsAndWeapons = {}; // dummy array
        itemMenu = new Menu(itemsAndWeapons, FONT_SIZE);
        
        String[] startMenuOptions = {SAVE_ACTION, END_TURN_ACTION, ""};
        startMenu = new Menu(startMenuOptions, FONT_SIZE);
        
        int x = WINDOW_WIDTH/2;
        int y = WINDOW_HEIGHT/2;
        cursor = new Cursor(x, y, field);
        state = 0;
        
        currentSide = 0;
        turn = 1;
        
        //Set up timer to drive animation events.
        timer = new javax.swing.Timer(DELAY, this);
        timer.setInitialDelay(DELAY);
    }
    
    //methods
    /**
     * Called when an actionEvent is fired.
     */
    public void actionPerformed(ActionEvent e)
    {
        if (currentSide != 0) // not player side
        {
            
        }
    }
    
    /**
     * Creates a side with no units in it.
     */
    public void createEmptySide()
    {
        side.add(new ArrayList<Unit>());
    }
    
    /**
     * Adds the specified unit to the specified side.
     */
    public void add(Unit newUnit)
    {
        (side.get(newUnit.getSide())).add(newUnit);
        field.fill(newUnit.getLoc(), newUnit.getSide());
    }
    
    /**
     * Adds the specified side.
     */
    public void add(ArrayList<Unit> newSide)
    {
        side.add(newSide);
    }
    
    /**
     * Returns the cursor.
     */
    public Cursor getFieldCursor()
    {
        return cursor;
    }
    
    /**
     * Moves the cursor up.
     */
    public void moveCursorUp()
    {
        if (!statBox.isHidden()) // more info has been shown but menu is still hidden
        {
            statBox.moveCursorUp();
        }
        else if (!startMenu.isHidden()) // start menu has been shown
        {
            startMenu.moveCursorUp();
        }
        else if (!actionMenu.isHidden())// menu has been shown
        {
            actionMenu.moveCursorUp();
        }
        else if (state == 3 || state == 7) // if the itemMenu has been shown
        {
            itemMenu.moveCursorUp();
        }
        else if ((state == 0 || state == 1 || state == 4)) // if the menu
        {
            if (cursor.moveUp())
                originY++;
        }
        
    }
    
    /**
     * Moves the cursor down.
     */
    public void moveCursorDown()
    {
        if (!statBox.isHidden()) // more info has been shown
        {
            statBox.moveCursorDown();
        }
        else if (!startMenu.isHidden()) // start menu has been shown
        {
            startMenu.moveCursorDown();
        }
        else if (!actionMenu.isHidden())// menu has been shown
        {
            actionMenu.moveCursorDown();
        }
        else if (state == 3 || state == 7) // if the itemMenu has been shown
        {
            itemMenu.moveCursorDown();
        }
        else if ((state == 0 || state == 1 || state == 4)) // if the menu
        {
            if (cursor.moveDown())
                originY--;
        }
    }
    
    /**
     * Moves the cursor left.
     */
    public void moveCursorLeft()
    {
        if (!statBox.isHidden()) // more info has been shown
        {
            statBox.switchPanel();
        }
        else if ((state == 0 || state == 1 || state == 4) && startMenu.isHidden()) // if the menu and start menu has not been shown
        {
            if (cursor.moveLeft())
            {
                originX++;
            }
        }
        
    }
    
    /**
     * Moves the cursor right.
     */
    public void moveCursorRight()
    {
        if (!statBox.isHidden()) // more info has been shown but menu is still hidden
        {
            statBox.switchPanel();
        }
        else if ((state == 0 || state == 1 || state == 4) && startMenu.isHidden()) // if the menu and start menu has not been shown
        {
            if (cursor.moveRight())
                originX--;
        }
        
    }

    /**
     * Selects what the cursor is over (if anything).
     */
    public void select()
    {
        if (statBox.isHidden()) // more info about a unit has not been displayed
        {
            if (!startMenu.isHidden()) // start menu has been shown
            {
                if ((startMenu.getSelectedAction()).equals(SAVE_ACTION))
                {
                    saveBattleFile();
                    startMenu.hideMenu();
                }
                else if ((startMenu.getSelectedAction()).equals(END_TURN_ACTION))
                {
                    endTurnOfCurrentSide();
                    startMenu.hideMenu();
                }
            }
            else if (state == 0) // when no unit has been selected. Selects target
            {
                selectUnit();
            }
            else if (state == 1) // when a unit has been selected but not moved. Moves unit
            {
                moveUnit();
            }
            else if (state == 2) // when unit moved, action menu shown, no action decided. Decides an action
            {
                String selectedAction = actionMenu.getSelectedAction();
                
                if (selectedAction.equals(ATTACK_ACTION)) // attack action
                {
                    showWpnSelection();
                }
                else if (selectedAction.equals(ITEM_ACTION)) // item action
                {
                    String[] unitWeapons = selected.getWpnsAsString();
                    String[] unitItems = selected.getItemsAsString();
                    String[] itemsAndWeapons = {unitWeapons[0], unitWeapons[1], unitWeapons[2], unitWeapons[3], "", 
                                                unitItems[0], unitItems[1], unitItems[2], unitItems[3], CANCEL_ACTION};
                    itemMenu = new Menu(itemsAndWeapons, FONT_SIZE);
                    
                    itemMenu.setMenuWidth(10*FONT_SIZE);
                    itemMenu.showMenu();
                    
                    state = 7;
                }
                else // wait action
                {
                    state = 0; // reset state
                    selected.setEnabled(false);
                    
                    if (turnOfCurrentSideOver())
                        endTurnOfCurrentSide();
                }
                
                actionMenu.hideMenu();
            }
            else if (state == 3) // when attack option selected, no weapon selected. Selects weapon to attack with
            {
                if (itemMenu.getSelectedIndex() < 4) // a weapon is selected, show wpn range
                {
                    showWpnRng();
                }
                else
                {
                    cancel();
                }
            }
            else if (state == 4) // when attack option and weapon selected, no target selected. Selects target to attack
            {
                selectTarget();
            }
            else if (state == 5) // when target selected, statistics displayed, battle not confirmed. Confirms and simulates battle (attacker attacking defender
            {
                simulate(selected, target, true, false); // human unit gains exp, NOT counter attacking
                
                if (!target.isAlive()) // if target is dead
                    field.empty(target.getLoc());
                
                state++;
                
                battleBox.hideBox();
            }
            else if (state == 6) // battle confirmed and "in progress" (defender counter-attacking) Ends battle
            {
                int dist = Math.abs(target.getX() - selected.getX()) + Math.abs(target.getY() - selected.getY());
                NumberSet atkRng = target.getWpnAt(0).getAtkRng();
                boolean inRange = false;
                for (int i = 0; i < atkRng.size(); i++)
                    if (dist == atkRng.get(i))
                        inRange = true;
                        
                if (target.isAlive() && inRange) // target is still alive after the attack and attacker is in target's attack range
                    simulate(target, selected, false, true); // AI unit does not gain exp, IS counter attacking
                if (!selected.isAlive()) // attacker is dead
                    field.empty(selected.getLoc());
                    
                selected.setEnabled(false);
                
                if (turnOfCurrentSideOver())
                    endTurnOfCurrentSide();
                    
                state = 0;
            }
            else if (state == 7) // when item menu has been displayed. Selects a weapon to equip or an item to use
            {
                int selectedIndex = itemMenu.getSelectedIndex();
                if (selectedIndex < 4) // weapon was selected
                {
                    setPrimaryWpn(selectedIndex);
                }
                else if (selectedIndex < 9) // item was selected
                {
                    useItem(selectedIndex - 5);
                }
                else
                {
                    cancel();
                }
            }
        }
    }
    
    /**
     * Cancels the previous action, going back a "step".
     */
    public void cancel()
    {
        if (!statBox.isHidden()) // when more info about a unit has been displayed. Hides more info
        {
            statBox.hideBox();
        }
        else if (!startMenu.isHidden()) // start menu has been shown. Hides the start menu
        {
            startMenu.hideMenu();
        }
        else if (state == 1) // when unit selected but not moved. Deselects units
        {
            selected.setVisibleRange(false); // hide movement range
            
            state--;
        }
        else if (state == 2) // when unit moved, action menu shown, no action decided. "Unmoves" unit
        {
            selected.setVisibleRange(true); // show the movement range
            
            actionMenu.hideMenu(); // hide menu
            
            selected.setLoc(selected.getOldLoc()); // move unit back
//             selected.setY(selected.getOldY());
            
            updateField(selected);
            state--;
        }
        else if (state == 3) // when attack option selected, no weapon selected. Undoes attack action selected
        {
            itemMenu.hideMenu();
            actionMenu.showMenu();
            state--;
        }
        else if (state == 4) // when attack option and weapon selected, no target selected. Undoes weapon selection
        {
            String[] unitWeapons = selected.getWpnsAsString();
            String[] itemsAndWeapons = {unitWeapons[0], unitWeapons[1], unitWeapons[2], unitWeapons[3], CANCEL_ACTION, ""};
            itemMenu = new Menu(itemsAndWeapons, FONT_SIZE);
            
            itemMenu.setMenuWidth(10*FONT_SIZE);
            itemMenu.showMenu();
            
            state--;
            selected.setAtkRngVisible(false);
        }
        else if (state == 5) // when target selected, statistics displayed, battle not confirmed. Undoes target selection
        {
            selected.setAtkRngVisible(true);
            battleBox.hideBox();
            state--;
        }
        else if (state == 8) // when item option selected. Undoes item action selection
        {
            itemMenu.hideMenu();
            actionMenu.showMenu();
            state = 2;
        }
    }
    
    /**
     * Displays the exapnded information of what the cursor is over.
     */
    public void moreInfo()
    {
        if (!statBox.isHidden() && statBox.getDisplayed() == 1) // more info about a unit has been displayed and the items are currently visible
        {
            statBox.showSelectedItemInfo();
        }
        else if (state != 2 && startMenu.isHidden() && statBox.isHidden()) // action  and start menus have not been shown, more info has not been displayed
        {
            if (!field.isEmpty(cursor.getLoc()))
            {
                for (int i = 0; i < side.size(); i++)
                {
                    ArrayList<Unit> theSide = side.get(i);
                    for (int j = 0; j < theSide.size(); j++)
                        if (cursor.getLoc().equals((theSide.get(j)).getLoc()) && (theSide.get(j)).isAlive()) // if there is a live unit at the cursor's coordinates
                        {
                            statBox.setUnit(theSide.get(j));
                            statBox.showBox();
                        }
                }
            }
        }
    }
    
    /**
     * Displays the start menu.
     */
    public void startMenu()
    {
        if (startMenu.isHidden())
            startMenu.showMenu();
        else
            startMenu.hideMenu();
    }
    
    /**
     * Selects a unit.
     */
    private void selectUnit()
    {
        if (!field.isEmpty(cursor.getLoc())) // if something is there
        {
            ArrayList<Unit> theSide = side.get(currentSide);
            for (int j = 0; j < theSide.size(); j++)
            {
                if (cursor.getLoc().equals((theSide.get(j)).getLoc()) && (theSide.get(j)).isEnabled() && (theSide.get(j)).isAlive()) // enabled and live 
                {                                                                                                                    // unit at cursor's 
                    selected = theSide.get(j);                                                                                       // coordinates
                    selected.setVisibleRange(true);                                                                                             
                    state++;
                }
            }
        }
        else
        {
            startMenu.showMenu();
        }
    }
    
    /**
     * Moves selected unit and displays action menu afterwards.
     */
    private void moveUnit()
    {
        if (selected.rangeVisible()) // if the unit's range is displayed
        {
            if (selected.isInMoveRange(cursor.getLoc()) && field.isEmpty(cursor.getLoc())) // location is within movement range and empty
            {
                selected.setLoc(cursor.getLoc());
                selected.setVisibleRange(false);
                
                updateField(selected);
                
                actionMenu.showMenu();
                state++;
            }
        }
    }
    
    /**
     * Displays menu for selecting a weapon to attack with.
     */
    private void showWpnSelection()
    {
        String[] unitWeapons = selected.getWpnsAsString();
        String[] itemsAndWeapons = {unitWeapons[0], unitWeapons[1], unitWeapons[2], unitWeapons[3], CANCEL_ACTION, ""};
        itemMenu = new Menu(itemsAndWeapons, FONT_SIZE);
        
        itemMenu.setMenuWidth(10*FONT_SIZE);
        itemMenu.showMenu();
        
        state++;
    }
    
    /**
     * Shows selected weapon's attack range.
     */
    private void showWpnRng()
    {
        selected.setPrimaryWpn(itemMenu.getSelectedIndex());
        itemMenu.hideMenu();
        selected.setAtkRngVisible(true);
        state++;
    }
    
    /**
     * Selects a target to attack and displays battlebox afterwards.
     */
    private void selectTarget()
    {
        Unit tempTarget = null; // default to null
        if (!field.isEmpty(cursor.getLoc()))
        {
            for (int i = 0; i < side.size(); i++) // check each side
            {
                ArrayList<Unit> theSide = side.get(i);
                for (int j = 0; j < theSide.size(); j++) // check each unit on each side
                {
                    if (cursor.getLoc().equals((theSide.get(j)).getLoc()) && (theSide.get(j)).isAlive()) // live unit at the cursor's coordinates
                    {
                        tempTarget = theSide.get(j);
                        if (selected.getSide() == tempTarget.getSide()) // if attacker and target are on the same side
                            tempTarget = null;
                    }
                }
            }
        }
        if (selected.isInAtkRange(cursor.getLoc()) && tempTarget != null) // there is a unit targeted and it is in attack range
        {
            target = tempTarget;
            selected.setAtkRngVisible(false);
            
            battleBox.setUnits(selected, target);
            battleBox.showBox();
        
            state++;
        }
    }
    
    /**
     * Sets the unit's primary weapon.
     */
    private void setPrimaryWpn(int index)
    {
        selected.setPrimaryWpn(index);
                    
        // update menu
        String[] unitWeapons = selected.getWpnsAsString();
        String[] unitItems = selected.getItemsAsString();
        String[] itemsAndWeapons = {unitWeapons[0], unitWeapons[1], unitWeapons[2], unitWeapons[3], "", 
                                    unitItems[0], unitItems[1], unitItems[2], unitItems[3], CANCEL_ACTION};
        itemMenu = new Menu(itemsAndWeapons, FONT_SIZE);
        
        itemMenu.setMenuWidth(2*FONT_SIZE);
        itemMenu.showMenu();
    }
    
    /**
     * Uses selected item.
     */
    private void useItem(int index)
    {
        selected.useItem(index);
        
        itemMenu.hideMenu();
        
        selected.setEnabled(false);
        
        if (turnOfCurrentSideOver())
            endTurnOfCurrentSide();
            
        state = 0;
    }
    
    /**
     * Simulates the battle using the specified attacker and defender.
     */
    public void simulate(Unit atk, Unit def, boolean withExp, boolean counter)
    {
        Random rand = new Random();
        
        int randNum = rand.nextInt(MAX_HIT);
        if (randNum < getHitChanceOf(atk, def, counter)) // hit
        {
            def.setHP(def.getHP() - getDamageOf(atk, def, counter));
            
            if (withExp)
                if (def.isAlive()) // did not kill
                    atk.setExp(atk.getExp() + getExpGainOf(atk, def));
                else
                    atk.setExp(atk.getExp() + getExpGainOf(atk, def) + 15);
        }
        else // miss
        {
            if (withExp)
                atk.setExp(atk.getExp() + 1);
        }
    }
    
    /**
     * Returns the hit chance (out of 100) of the specified attacker to the specified defender.
     */
    public int getHitChanceOf(Unit atk, Unit def, boolean counter)
    {
        // attacker values
        int sklAtk = atk.getSkl();
        int wpnWt = atk.getWpnAt(0).getWt();
        NumberSet atkWpnRng = atk.getWpnAt(0).getAtkRng();
        
        // defender values
        int spdDef = def.getSpd();
        NumberSet defWpnRng = def.getWpnAt(0).getAtkRng();
        
        int hit = MAX_HIT + 2*sklAtk - wpnWt - spdDef;
        
        if (hit < 0) // if hit value is invalid
            hit = 0;
        else if (hit > MAX_HIT)
            hit = 100;
            
        if ((field.terrainAt(def.getLoc())).equals(BattleField.FOREST_SYMBOL)) // if defender is in a forest
            hit -= 15;
        
        String atkAdv = atk.getWpnAt(0).getAdvantage();
        String defWpnKind = def.getWpnAt(0).getKind();
        if (atkAdv.equals(defWpnKind))
            hit += 10;
            
        String atkWkn = atk.getWpnAt(0).getWeakness();
        if (atkWkn.equals(defWpnKind))
            hit -= 10;
        
        if (counter)
        {
            if (!atkWpnRng.overlaps(defWpnRng))
                hit = 0;
        }
        
        return hit;
    }
    
    /**
     * Returns the damage done from the specified attacker to the specified defender.
     */
    public int getDamageOf(Unit atk, Unit def, boolean counter)
    {
        // attacker values
        int strAtk = atk.getStr();
        int wpnMt = atk.getWpnAt(0).getMt();
        NumberSet atkWpnRng = atk.getWpnAt(0).getAtkRng();
        
        // defender values
        int defDef = def.getDef();
        NumberSet defWpnRng = def.getWpnAt(0).getAtkRng();
        
        int dmg = strAtk + wpnMt - defDef;
        if (dmg < 0) // if damage is negative
            dmg = 0;
        
        if (counter)
        {
//             boolean inAtkRng = false; // if the unit's weapon range won't reach that far (for counter attacks)
//             for (int i = 0; i < atkWpnRng.length; i++)
//                 for (int j = 0; j < defWpnRng.length; j++)
//                     if (atkWpnRng[i] == defWpnRng[j])
//                         inAtkRng = true;
            if (!atkWpnRng.overlaps(defWpnRng))
                dmg = 0;
        }
            
        return dmg;
    }
    
    /**
     * Returns the exp the attacker gains from fighting the defender.
     */
    private int getExpGainOf(Unit atk, Unit def)
    {
        // attacker values
        int lvlAtk = atk.getLevel();
        int wpnWt = atk.getWpnAt(0).getWt();
        
        // defender values
        int lvlDef = def.getLevel();
        
        return 10 - (lvlAtk - lvlDef) + wpnWt;
    }
    
    /**
     * Returns whether or not the current team has no more enabled units.
     */
    private boolean turnOfCurrentSideOver()
    {
        boolean over = true;
        
        for (int i = 0; i < (side.get(currentSide)).size(); i++)
            if (((side.get(currentSide)).get(i)).isEnabled() && ((side.get(currentSide)).get(i)).isAlive())
                over = false;
                
        if (getVictorSide() != -1)
        {
            state = -1; // nothing more can be done
            over = true;
        }
        
        return over;
    }
    
    /**
     * Ends the turn of the current side.
     * Makes all units "enabled."
     * Switched current side to next one.
     */
    private void endTurnOfCurrentSide()
    {
        for (int i = 0; i < (side.get(currentSide)).size(); i++)
            ((side.get(currentSide)).get(i)).setEnabled(true);
        
        switchSide();
        state = 0;
        
        if (currentSide != 0) // not player side
        {
            executeAI(currentSide);
            timer.start();
        }
    }
    
    /**
     * Returns if the specified side has any survivors or not.
     */
    private boolean hasSurvivors(int aSide)
    {
        boolean survivors = false; // assume opposite
        
        ArrayList<Unit> theSide = side.get(aSide);
        for (int i = 0; i < theSide.size(); i++) // for each unit
            if (theSide.get(i).isAlive()) // if a unit is found to be alive
                survivors = true;
                
        return survivors;
    }
    
    /**
     * Returns the side that won the battle.
     * Returns -1 if the battle is not finished yet.
     */
    public int getVictorSide()
    {
        int winningSide = -1;
        for (int i = 0; i < side.size(); i++) // for each side
        {
            if (hasSurvivors(i)) // the side is alive
            {
                boolean othersDead = true;
                for (int j = 0; j < side.size(); j++)
                    if (hasSurvivors(j) && j != i)
                        othersDead = false;
                        
                if (othersDead) // if all other sides are dead
                    winningSide = i;
            }
        }
        return winningSide;
    }
    /**
     * Updates the field about the location of units.
     */
    private void updateField(Unit unit)
    {
        field.empty(unit.getOldLoc());
        field.fill(unit.getLoc(), unit.getSide());
    }
    
    /**
     * Switches the current side to the next one.
     * Increments turn count by one if all sides have gone once.
     */
    private void switchSide()
    {
        currentSide++;
        
        if (getVictorSide() == -1) // battle not over
        {
            boolean done = false;
            do
            {
                if (currentSide >= side.size())
                {
                    currentSide = 0;
                    turn++;
                }
                if (hasSurvivors(currentSide))
                {
                    done = true;
                }
                else
                {
                    currentSide++;
                }
            }
            while (!done);
        }
    }
    
    /**
     * Acts all the units on the side found at the specified index.
     */
    private void executeAI(int index)
    {
        // find the AI side
        ArrayList<Unit> theSide = side.get(index);
        
        // find the enemy side(s)
        ArrayList<ArrayList<Unit>> enemySides = new ArrayList<ArrayList<Unit>>();
        for (int i = 0; i < side.size(); i++)
            if (i != index)
                enemySides.add(side.get(i));
                
        // make each unit act
        for (Unit aiUnit : theSide) // for each unit on the side
        {
            selected = aiUnit;
            Unit theTarget = AI.getTarget(aiUnit, enemySides);
            if (theTarget != null)
            {
                int dist = AI.optimalAtkDist(aiUnit, theTarget);
                if (dist < 0) // no optimal distance
                    dist = 1;
                moveToClosestLocation(aiUnit, theTarget, AI.optimalAtkDist(aiUnit, theTarget)); // move the AI unit to the location closest to its target that is empty
            }
        }
    }
    
    /**
     * Moves the specified unit to an empty grid as close to the specified distance from the target
     * as possible while still limited to its movement range.
     * Should only be called by the executeAI method.
     */
    private void moveToClosestLocation(Unit theUnit, Unit theTarget, int dist)
    {
        // default the locations
        Location targLoc = theTarget.getLoc();
        Location locAbove = null;
        Location locBelow = null;
        Location locLeft = null;
        Location locRight = null;
        
        // initialize locations to valid ones if possible
        if (field.inBounds(targLoc.x() - 1, targLoc.y())) // try location to the left if the method that called it did not do the same
            locLeft = nearestEmptyLocation(theUnit, targLoc.x() - dist, targLoc.y(), "left");
        if (field.inBounds(targLoc.x() + 1, targLoc.y())) // try location to the right if the method that called it did not do the same
            locRight = nearestEmptyLocation(theUnit, targLoc.x() + dist, targLoc.y(), "right");
        if (field.inBounds(targLoc.x(), targLoc.y() - 1)) // try location above if the method that called it did not do the same
            locAbove = nearestEmptyLocation(theUnit, targLoc.x(), targLoc.y() - dist, "above");
        if (field.inBounds(targLoc.x(), targLoc.y() + 1)) // try location below if the method that called it did not do the same
            locBelow = nearestEmptyLocation(theUnit, targLoc.x(), targLoc.y() + dist, "below");
            
        Location loc = locAbove;
        if (BattleField.distanceBetween(targLoc, loc) > BattleField.distanceBetween(targLoc, locBelow))
            loc = locBelow;
        if (BattleField.distanceBetween(targLoc, loc) > BattleField.distanceBetween(targLoc, locLeft))
            loc = locLeft;
        if (BattleField.distanceBetween(targLoc, loc) > BattleField.distanceBetween(targLoc, locRight))
            loc = locRight;
            
        if (!loc.equals(new Location(-1,-1))) // valid coordinates are returned
        {
            theUnit.setLoc(loc);
            updateField(theUnit);
        }
    }
    
    /**
     * Returns the empty location nearest the specified one while still limited to the unit's movement range.
     * Returns a location of (-1,-1) if there is no empty location that is in the unit's movement range.
     */
    private Location nearestEmptyLocation(Unit theUnit, int x, int y, String dir)
    {
        Location targLoc = new Location(x,y);
        if (field.isEmpty(targLoc) && theUnit.isInMoveRange(targLoc))
        {
            return targLoc;
        }
        else if (theUnit.isInMoveRange(targLoc))
        {
            // default the locations
            Location locAbove = new Location(-1,-1);
            Location locBelow = new Location(-1,-1);
            Location locLeft = new Location(-1,-1);
            Location locRight = new Location(-1,-1);
            
            // initialize locations to valid ones if possible
            if (field.inBounds(x - 1, y) && !dir.equals("right")) // try location to the left if the method that called it did not do the same
                locLeft = nearestEmptyLocation(theUnit, x - 1, y, "left");
            if (field.inBounds(x + 1, y) && !dir.equals("left")) // try location to the right if the method that called it did not do the same
                locRight = nearestEmptyLocation(theUnit, x + 1, y, "right");
            if (field.inBounds(x, y - 1) && !dir.equals("below")) // try location above if the method that called it did not do the same
                locAbove = nearestEmptyLocation(theUnit, x, y - 1, "above");
            if (field.inBounds(x, y + 1) && !dir.equals("above")) // try location below if the method that called it did not do the same
                locBelow = nearestEmptyLocation(theUnit, x, y + 1, "below");
                
            Location loc = locAbove;
            if (BattleField.distanceBetween(targLoc, loc) > BattleField.distanceBetween(targLoc, locBelow))
                loc = locBelow;
            if (BattleField.distanceBetween(targLoc, loc) > BattleField.distanceBetween(targLoc, locLeft))
                loc = locLeft;
            if (BattleField.distanceBetween(targLoc, loc) > BattleField.distanceBetween(targLoc, locRight))
                loc = locRight;
                
            return loc; // returns the nearest empty location
        }
        else
        {
            return new Location(-1,-1);
        }
    }
    
    /**
     * Returns an arraylist of locations that the specified unit could possible attack from using the specified weapon.
     */
    private ArrayList<Location> getAtkLoc(Unit theUnit, int wpnIndex, Location targ)
    {
        ArrayList<Location> loc = new ArrayList<Location>();
        
        return loc;
    }
    
    /**
     * Returns the battlefield the battle is being fought on.
     */
    public BattleField getField()
    {
        return field;
    }
    
    /**
     * Reads the battle file and set fields accordingly.
     */
    private void readBattleFile()
    {
        try
        {
            Scanner in = new Scanner(new FileInputStream(battleFile));
            
            field = new BattleField(nextItemInFile(in));
            
            String[][] unitInfo = readWholeFile(in);
            
            for (int i = 0; i < unitInfo.length; i++)
            {
                int unitSide = Integer.parseInt(unitInfo[i][0]);
                String unitName = unitInfo[i][1];
                int unitX = Integer.parseInt(unitInfo[i][2]);
                int unitY = Integer.parseInt(unitInfo[i][3]);
                
                if (unitName.startsWith("*"))
                    unitName = fileName + "/" + unitName.substring(1, unitName.length());
                    
                if (unitSide + 1 != side.size()) // if the side the unit is on has not been created yet
                    side.add(new ArrayList<Unit>());
                    
                (side.get(unitSide)).add(new Unit(unitX, unitY, unitName, unitSide, field));
                field.fill(new Location(unitX, unitY), unitSide);
            }
        }
        catch (IOException e) {}
    }
    
    /**
     * Reads the file using the specified scanner and returns each seperate token in a string[][].
     */
    private String[][] readWholeFile(Scanner in)
    {
        final int tokenPerLine = 4;
        ArrayList<String[]> tokens = new ArrayList<String[]>();
        
        for (int i = 0; in.hasNextLine(); i++)
        {
            tokens.add(new String[tokenPerLine]); // add new line to arraylist
            for (int j = 0; j < tokenPerLine; j++)
            {
                if (in.hasNext())
                    tokens.get(i)[j] = in.next();
            }
        }
        
        // copy arraylist into 2d string array
        String[][] tokensReturn = new String[tokens.size()][tokenPerLine];
        for (int i = 0; i < tokens.size(); i++)
        {
            for (int j = 0; j < tokenPerLine; j++)
            {
                tokensReturn[i][j] = tokens.get(i)[j];
            }
        }
        
        return tokensReturn;
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
     * Saves all the data into the CURRENT_FILE file.
     * Does not change any specific level data from missions.
     * Saves the status of units on side "0" to original files.
     * Saves status of all other units into file in a folder
     */
    private void saveBattleFile()
    {
        // save unit files
        for (int i = 0; i < side.size(); i++) // for each side
        {
            ArrayList<Unit> theSide = side.get(i);
            if (i == 0) // side "0"
                for (int j = 0; j < theSide.size(); j++) // for each unit
                    theSide.get(j).saveUnitFile(); // save the changes to original file
            else // all other sides
                 for (int j = 0; j < theSide.size(); j++) // for each unit
                    theSide.get(j).saveUnitFile(new File(CURRENT_LEVEL_DIRECTORY + theSide.get(j).getUnitFileString() + FILE_TYPE)); // save the changes to file in current file 
        }
        // save battle files
        try
        {
            PrintStream out = new PrintStream(new File(CURRENT_FILE));
            
            out.print(
            "Battlefield: " + field.getFieldFile() + NEWLINE +
            NEWLINE);
            
            for (int i = 0; i < side.size(); i++) // for each side
            {
                ArrayList<Unit> theSide = side.get(i);
                for (int j = 0; j < theSide.size(); j++) // for each unit
                {
                    Unit theUnit = theSide.get(j);
                    out.print(theUnit.getSide() + " " + theUnit.getUnitFileString() + " " + theUnit.getX() + " " + theUnit.getY());
                    if ((i != side.size() - 1) || (j != theSide.size() - 1))
                        out.print(NEWLINE);
                }
            }
        }
        catch (IOException e) {}
    }
    
    /**
     * Paints the battle onto the canvas.
     */
    public void paintComponent(Graphics g)
    {
        if (getVictorSide() == -1) // battle not over
        {
            g.translate(originX*BattleField.GRID_SIZE, originY*BattleField.GRID_SIZE);
            
            int x = (int)(((WINDOW_WIDTH - 4)/2) - originX); // grid coordinates for center of window
            int y = (int)(((WINDOW_HEIGHT - 5)/2) - originY);
            
            int xCoord = Math.round((float)BattleField.getXOf(new Location(x,y))); // actual coordinates
            int yCoord = Math.round((float)BattleField.getYOf(new Location(x,y)));
            
            field.paintComponent(g); // paint field
            
            for (int i = 0; i < side.size(); i++) // paint all sides except current one
                if (i != currentSide)
                    for (int j = 0; j < (side.get(i)).size(); j++)
                        ((side.get(i)).get(j)).paintComponent(g);
            for (int i = 0; i < (side.get(currentSide)).size(); i++) // paint current side
                ((side.get(currentSide)).get(i)).paintComponent(g);
                
            cursor.paintComponent(g); // paint cursor
    
            actionMenu.paintComponent(xCoord, yCoord, g); // paint action menu
            
            statBox.paintComponent(xCoord, yCoord, g); // paint stat box
            
            battleBox.paintComponent(xCoord - 2*BattleField.GRID_SIZE, yCoord, g); // paint battle box
            
            itemMenu.paintComponent(xCoord, yCoord, g); // paint item menu
            
            startMenu.paintComponent(xCoord, yCoord, g); // paint start menu
            
            if (state == 6) // battle in progress
            {
                Animation anim = new Animation();
                anim.paintComponent(xCoord - 2*BattleField.GRID_SIZE, yCoord, FONT_SIZE, selected, target, g);
            }
            
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.white);
            if (r != null)
                g2.fill(r);
        }
    }
    
} // END CLASS