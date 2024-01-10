import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;

// The main class named "Main"
public class Main {
    // Thread for the game
    Thread gameThread;

    // The main method
    public static void main(String[] args) {
        // Create a JFrame named "window"
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation on exit
        window.setResizable(false); // Make the window not resizable
        window.setTitle("Yourdle"); // Set the title of the window
        window.setSize(1536, 864); // Set the initial size of the window
        window.setResizable(false); // Make the window not resizable
        window.setExtendedState(JFrame.MAXIMIZED_BOTH); // Set the window to maximize on start

        // Create a GamePanel named "gamePanel"
        GamePanel gamePanel = new GamePanel();

        // Add the gamePanel to the window
        window.add(gamePanel);

        window.setLocationRelativeTo(null); // Center the window on the screen
        window.setVisible(true); // Set the window to be visible

        // Start the game thread in the gamePanel
        gamePanel.startGameThread();

        window.pack(); // Adjust the size of the window based on its contents
    }
}
