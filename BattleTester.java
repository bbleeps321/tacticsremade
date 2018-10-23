import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * From where the program is run.
 * Contains the "main" thread.
 * Controls the GUI and rest of program.
 */
public class BattleTester extends JPanel implements KeyListener, ActionListener
{
    private static final int GAP = 15;
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final int DELAY = 33;
    
    private Battle battle; // the current battle
    
    /**
     * Creates the content pane.
     */
    public BattleTester()
    {
        super(new BorderLayout());
        
        battle = new Battle("Tester Level");
        
        setBorder(BorderFactory.createEmptyBorder(GAP,GAP,GAP,GAP));
        setBackground(Color.black);
        add(battle, BorderLayout.CENTER);
        
        battle.addKeyListener(this);
        battle.requestFocusInWindow();
        this.setFocusable(true);
        addKeyListener(this);
        
        //Set up timer to drive animation events.
        Timer timer = new Timer(DELAY, this);
        timer.setInitialDelay(DELAY);
        timer.start(); 
    }
    
    /**
     * Main thread. From where the program is run.
     */
    public static void main(String[] args)
    {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                createAndShowGUI();
            }
        });
    }
    
    /**
     * Creates and displays the GUI.
     */
    private static void createAndShowGUI()
    {
        //Create and set up the window.
        JFrame frame = new JFrame("Battle Tester");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        //Create and set up the content pane.
        BattleTester contentPane = new BattleTester();
        contentPane.setOpaque(true);
        frame.setContentPane(contentPane);
        frame.setPreferredSize(new Dimension(WIDTH + 2*GAP, HEIGHT + 2*GAP));
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Fired when a key is pressed.
     */
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_UP)
        {
            battle.moveCursorUp();
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
        {
            battle.moveCursorDown();
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            battle.moveCursorLeft();
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            battle.moveCursorRight();
        }
        else if (e.getKeyCode() == KeyEvent.VK_Z)
        {
            battle.select();
        }
        else if (e.getKeyCode() == KeyEvent.VK_X)
        {
            battle.cancel();
        }
        else if (e.getKeyCode() == KeyEvent.VK_S)
        {
            battle.moreInfo();
        }
        else if (e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            battle.startMenu();
        }
    }
    
    /**
     * Called when an actionEvent is fired.
     */
    public void actionPerformed(ActionEvent e)
    {
        updateUI();
    }
    
    /**Required by KeyListener interface.**/
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

} // END CLASS