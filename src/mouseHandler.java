// Import necessary classes for handling mouse events
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class mouseHandler implements MouseListener {
    // Declare boolean variables to track mouse events and integer variables for coordinates
    public boolean mouseClicked;
    public int cx, cy, mx, my;

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    
    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        // This method is called when any mouse button is pressed down

        // Check if the left mouse button is pressed
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseClicked = true; // Set mouseClicked to true
        }

        // Get the x and y coordinates of the mouse when pressed
        cx = e.getX();
        cy = e.getY();
    }

    
    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    // Override the mouseExited method from the MouseListener interface
    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    // Custom method to handle mouse movement
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        
       
    }
}
