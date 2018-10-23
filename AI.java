import java.util.*;

/**
 * Controls the processes by which the computer controls units.
 */
public class AI
{
    /**
     * Returns the enemy unit that the AI has decided to target using the specified parent unit, and ArrayList of enemy sides.
     * Highest score is the most optimal unit.
     * Returns null if the target with the greatest score is not in the unit's movement range.
     */
    public static Unit getTarget(Unit parentUnit, ArrayList<ArrayList<Unit>> enemies)
    {
        Unit myUnit = parentUnit; // initialize parent unit
        
        ArrayList<Unit> myEnemy = new ArrayList<Unit>(); // initialize enemies, regardless of sides
        for (int i = 0; i < enemies.size(); i++)
            for (int j = 0; j < enemies.get(i).size(); j++)
                myEnemy.add(enemies.get(i).get(j));
                
        int[] myScore = new int[myEnemy.size()]; // the score assigned to each enemy unit based on the success rate of the parent unit over each
                                                 // initialized to zero (by default)
            
        myScore = evaluateForDistance(myUnit, myEnemy, myScore); // evaluate for distance
        myScore = evaluateForCounter(myUnit, myEnemy, myScore); // evaluate for ability to counter
        myScore = evaluateForHP(myEnemy, myScore); // evaluate for hp remaining
        myScore = evaluateForAlive(myEnemy, myScore); // evaluate for still being alive
        
        // find index of unit with highest score
        int greatestScoreIndex = 0; // assume greatest score to be that of first unit
        for (int i = 1; i < myScore.length; i++) // for all scores
            if (myScore[i] > myScore[greatestScoreIndex]) // if score of new value is greater than old record
                greatestScoreIndex = i;
                
        if (myScore[greatestScoreIndex] < 1000) // target not in movement range
            return null;
        else
            return myEnemy.get(greatestScoreIndex); // return the target with highest score
    }
    
    /**
     * Returns the optimal distance of the first unit to attack the other.
     * Returns -1 if there is no optimal distance (target can counter attack regardless).
     */
    public static int optimalAtkDist(Unit myUnit, Unit myEnemy)
    {
        for (int i = 0; i < Unit.WPN_SLOTS; i++)
        {
            NumberSet myWpnRng = myUnit.getWpnAt(i).getAtkRng();
            NumberSet enemyWpnRng = myEnemy.getWpnAt(0).getAtkRng(); // attack range of enemy's primary weapon
            for (int a = 0; a < myWpnRng.size(); a++) // check if range of own weapon is same as enemy's
                if (!enemyWpnRng.contains(myWpnRng.get(a)) && myWpnRng.get(a) != 0) // ranges are not the same
                    return myWpnRng.get(a); // return the range
        }
        return -1;
    }
    
    /**
     * Returns the index of the best weapon to attack with.
     * Returns -1 if there is no optimal weapon (target can counter attack each weapon)
     */
    public static int indexOfOptimalWpn(Unit myUnit, Unit myEnemy)
    {
        for (int i = 0; i < Unit.WPN_SLOTS; i++)
        {
            NumberSet myWpnRng = myUnit.getWpnAt(i).getAtkRng();
            NumberSet enemyWpnRng = myEnemy.getWpnAt(0).getAtkRng(); // attack range of enemy's primary weapon
            for (int a = 0; a < myWpnRng.size(); a++) // check if range of own weapon is same as enemy's
                if (!enemyWpnRng.contains(myWpnRng.get(a)) && myWpnRng.get(a) != 0) // ranges are not the same and are valid
                    return i; // return the index of the weapon
        }
        return -1;
    }
    
    /**
     * Evaluates the possible targets based on their distance from the parent unit.
     * Should only be called by getTarget, after the parent unit, enemies, and scores have been (re)set.
     * Returns the updated scores.
     */
    private static int[] evaluateForDistance(Unit myUnit, ArrayList<Unit> myEnemy, int[] myScore)
    {
        for (int i = 0; i < myScore.length; i++) // split scores into seperate tiers based on whether they are in the parent unit's attack range or not.
            if (myUnit.isInThreatRange(myEnemy.get(i).getLoc())) // the unit is can be attacked this turn
                myScore[i] += 1000; // split into seperate tier
                
        return myScore; // return updated scores
    }
    
    /**
     * Evaluates the possible targets based on whether or not they can counter attack the parent unit.
     * Should only be called by getTarget, after the parent unit, enemies, and scores have been (re)set.
     * Returns the updated scores.
     */
    private static int[] evaluateForCounter(Unit myUnit, ArrayList<Unit> myEnemy, int[] myScore)
    {
        for (int i = 0; i < Unit.WPN_SLOTS; i++) // for every weapon the unit has
        {
            NumberSet myWpnRng = myUnit.getWpnAt(i).getAtkRng();
            for (int j = 0; j < myEnemy.size(); j++) // for every enemy unit
            {
                NumberSet enemyWpnRng = myEnemy.get(j).getWpnAt(0).getAtkRng(); // attack range of enemy's primary weapon
                for (int a = 0; a < myWpnRng.size(); a++) // check if range of own weapon is same as enemy's
                    if (!enemyWpnRng.contains(myWpnRng.get(a))) // ranges do NOT overlap
                        myScore[j] += 500; // split into seperate tier
            }
        }
        return myScore; // return updated scores
    }
    
    /**
     * Evaluates the possible targets based on how much hp they have.
     * Should only be called by getTarget, after the parent unit, enemies, and scores have been (re)set.
     * Returns the updated scores.
     */
    private static int[] evaluateForHP(ArrayList<Unit> myEnemy, int[] myScore)
    {
        int maxBonus = myEnemy.size() - 1; // initialize the bonus score given to the unit ranking the highest in its tier (based on number of candidates)
        
        // create arraylist to contain units in increasing hp order
        ArrayList<Unit> rank = new ArrayList<Unit>();
        
        rank.add(myEnemy.get(0)); // assume first unit in list has highest
        
        for (int i = 1; i < myEnemy.size(); i++) // for every enemy
        {
            Unit theUnit = myEnemy.get(i);
            for (int j = 0; j < rank.size(); j++) // for every unit already ranked
            {
                if (theUnit.getHP() > rank.get(j).getHP()) // if the unit has more hp
                {
                    rank.add(j + 1, theUnit); // place it in the index NEXT to the one it was compared with
                    break;
                }
            }
        }
        
        // update scores
        for (int i = 0; i < rank.size(); i++)
            myScore[myEnemy.indexOf(rank.get(i))] += maxBonus - 1;
            
        return myScore; // return updated scores
    }
    
    /**
     * Evaluates the possible targets based on whether they are alive or not.
     * If dead, set their score to -1;
     * Should only be called by getTarget, after the parent unit, enemies, and scores have been (re)set and at the END of the method.
     * Returns the updated scores.
     */
    private static int[] evaluateForAlive(ArrayList<Unit> myEnemy, int[] myScore)
    {
        for (int i = 0; i < myEnemy.size(); i++) // for every possible target
            if (!myEnemy.get(i).isAlive()) // enemy is dead
                myScore[i] = -1; // set its score to -1
                
        return myScore; // return updated scores
    }
    
    /**
     * Returns the distance between the parent unit and the specified unit in grid units.
     */
    private static int distanceFrom(Unit thisUnit, Unit thatUnit)
    {
        return Math.abs(thatUnit.getX() - thisUnit.getX()) + Math.abs(thatUnit.getY() - thisUnit.getY());
    }
    
} // END CLASS