import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    // Declare boolean variables to track key events and a char variable to store the last pressed key
    public boolean generate, guessing, enter, backspace, title;
    public char last;

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Get the key code of the pressed key
        int code = e.getKeyCode();

        // Check specific key codes and set corresponding boolean variables to true
        if (code == KeyEvent.VK_ENTER) {
            enter = true; // Set enter to true when Enter key is pressed
        }
        if (code == KeyEvent.VK_SPACE) {
            generate = true; // Set generate to true when Space key is pressed
            last = '0'; // Set last to '0'
        } 
        if (code == KeyEvent.VK_BACK_SPACE) {
            backspace = true; // Set backspace to true when Backspace key is pressed
        }
        if (code >= KeyEvent.VK_A && code <= KeyEvent.VK_Z) {
            guessing = true; // Set guessing to true when an alphabetical key is pressed
            // Store the lowercase representation of the pressed key in the 'last' variable
            last = KeyEvent.getKeyText(code).toLowerCase().charAt(0);
        }
        if (code == KeyEvent.VK_ESCAPE) {
            title = true; // Set title to true when Escape key is pressed
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
       
    }
}
