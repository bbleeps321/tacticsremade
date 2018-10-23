import java.awt.*;
import javax.swing.*;
import java.io.*;

/**
 * From where the program is run.
 * Contains the "main" thread.
 * Controls the GUI and rest of program.
 */
public class Tactics extends JPanel
{
    private BattleField field; // where the battles are fought
    private Battle battle; // the current battle
    
    /**
     * Creates the content pane.
     */
    public Tactics()
    {
        super(new BorderLayout());
        
        JPanel whole = new JPanel(new BorderLayout());
        whole.add(field);
        
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        
        add(whole, BorderLayout.CENTER);
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
        JFrame frame = new JFrame("Tactics");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         frame.setResizable(false);
        
        //Create and set up the content pane.
        JPanel contentPane = new Tactics();
        contentPane.setOpaque(true);
        frame.setContentPane(contentPane);
//         frame.setPreferredSize(new Dimension(field.getWidth(), field.getHeight()));
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
} // END CLASS