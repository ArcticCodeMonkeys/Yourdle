import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// Class representing the main game panel
public class GamePanel extends JPanel implements Runnable {
	// Declaration of key and mouse handlers
	KeyHandler keyH = new KeyHandler();
	mouseHandler mouseH = new mouseHandler();

	// Thread for the game loop
	Thread gameThread;

	// Game parameters
	int FPS = 60;
	int length = 5;
	int chances = 6;
	int sw = 1536;
	int sh = 864;
	int typed = -1;
	int attempt = 0;
	int dur = 0;
	String gameState = "Title Screen";
	String word;
	boolean wordReady = false;

	// Arrays to store letters and their validity
	char letters[][] = new char[30][18];
	String validity[][] = new String[30][18];

	// Array to store random strings
	String randString[] = generateRandomString(1000);

	// List of words
	ArrayList<String> words = new ArrayList<String>();

	// Constructor for the GamePanel
	public GamePanel() {
		// Setting up the JPanel
		this.setPreferredSize(new Dimension(sw, sh));
		this.setBackground(Color.WHITE);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.addMouseListener(mouseH);
		this.setFocusable(true);
	}

	// Method to start the game thread
	public void startGameThread() {
		// Start the game thread

		// Load words from a file
		try (BufferedReader reader = new BufferedReader(new FileReader("words_alpha.txt"))) {
			String line;

			while ((line = reader.readLine()) != null) {
				words.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Create a new thread and start it
		gameThread = new Thread(this);
		gameThread.start();
	}

	// Method to sort words based on length
	public ArrayList<String> sort(int length) {
		ArrayList<String> sizedWords = new ArrayList<String>();
		for (int i = 0; i < words.size(); i++) {
			String check = words.get(i);
			if (check.length() == length) {
				sizedWords.add(check);
			}
		}
		return sizedWords;
	}

	// Main game loop
	public void run() {
		// Main game loop
		int startingTime = (int) (System.nanoTime() / 1000000000);
		String line = "";
		int infoCount = 0;

		while (gameThread != null) {
			// Update and repaint game components

			// Get drawing intervals and timer
			double drawInterval = 1000000000 / FPS;
			double nextDrawTime = System.nanoTime() + drawInterval;
			long currentTime = System.nanoTime();

			// Update player position and information
			update();

			// Repaint the panel
			repaint();

			try {
				// Time drawing intervals
				double remainingTime = nextDrawTime - System.nanoTime();

				remainingTime = remainingTime / 1000000;

				if (remainingTime < 0) {
					remainingTime = 0;
				}
				// Sleep to control the frame rate
				Thread.sleep((long) remainingTime);
				nextDrawTime += drawInterval;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Method to generate an array of random strings
	public static String[] generateRandomString(int length) {
		Random random = new Random();
		StringBuilder stringBuilder = new StringBuilder(length);
		String[] rands = new String[20];

		// Generate 20 random strings
		for (int k = 0; k < 20; k++) {
			for (int i = 0; i < length; i++) {
				char randomChar = (char) ('A' + random.nextInt(26)); // Assuming uppercase letters only
				stringBuilder.append(randomChar);
			}
			rands[k] = stringBuilder.toString();
			stringBuilder = new StringBuilder(length);
		}
		return rands;
	}

	// Method to get a random word of a specified length
	public String getWord(int length) {
		Random rand = new Random();
		ArrayList<String> output = sort(length);
		int num = rand.nextInt(0, output.size());
		return output.get(num);
	}

	// Method to check the correctness of the guessed word
	public boolean checkWord() {
		boolean valid = true;
		for (int i = 0; i < length; i++) {
			if (letters[attempt][i] == word.charAt(i)) {
				validity[attempt][i] = "Correct";
			} else if (word.contains(String.valueOf(letters[attempt][i]))) {
				validity[attempt][i] = "Close";
				valid = false;
			} else {
				validity[attempt][i] = "Wrong";
				valid = false;
			}
		}
		return valid;
	}

	// Method to check if the guessed word is valid
	public boolean validWord() {
		String guess = "";
		for (int i = 0; i < length; i++) {
			guess = guess + letters[attempt][i];
		}
		return words.contains(guess);
	}

	// Method to update game state and components
	public void update() {
		// Get the mouse position
		Point m = MouseInfo.getPointerInfo().getLocation();

		Rectangle2D b1 = new Rectangle2D.Double((sw / 2 - 200), (sh / 2 + 50), 400, 100);
		Rectangle2D b2 = new Rectangle2D.Double((sw / 2 - 200), (sh / 2 + 200), 400, 100);

		// Title screen logic
		if (gameState == "Title Screen") {
			// Adjust word length if button 1 is clicked
			if (mouseH.mouseClicked && b1.contains(m)) {
				SwingUtilities.invokeLater(() -> {
					length = getLength();
				});
				mouseH.mouseClicked = false;
			}
			// Adjust number of guesses if button 2 is clicked
			if (mouseH.mouseClicked && b2.contains(m)) {
				SwingUtilities.invokeLater(() -> {
					chances = getChances();
				});
				mouseH.mouseClicked = false;
			}

			// Start the game if the generate key is pressed
			if (keyH.generate) {
				gameState = "Playing";
				typed = -1;
				attempt = 0;
				keyH.last = '\0';
				word = getWord(length);

				// Reset arrays for letters and validity
				for (int j = 0; j < length; j++) {
					for (int l = 0; l < chances; l++) {
						validity[l][j] = null;
						letters[l][j] = '\0';
					}
				}
				keyH.generate = false;
				System.out.println(word);
			}
		}
		// Game playing logic
		else if (gameState == "Playing") {
			// Return to the title screen if escape key is pressed
			if (keyH.title) {
				gameState = "Title Screen";
				keyH.title = false;
			}

			// Handle key presses for guessing
			if (keyH.guessing) {
				if ((typed < (length - 1))) {
					typed++;
					letters[attempt][typed] = keyH.last;
					System.out.println(keyH.last);

					keyH.guessing = false;
				} else {
					keyH.guessing = false;
				}
			}

			// Handle backspace key press
			if (keyH.backspace) {
				if (typed >= 0) {
					letters[attempt][typed] = '\0';
					typed--;
				}
				keyH.backspace = false;
			}

			// Handle enter key press
			if (keyH.enter) {
				if ((typed == (length - 1))) {
					if (validWord()) {
						if (checkWord()) {
							gameState = "Win";
						} else {
							typed = -1;
							attempt++;
							if (attempt == chances) {
								gameState = "Game Over";
							}
						}
						System.out.println(gameState);
					} else {
						JOptionPane.showMessageDialog(null, "That's not a valid word!");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Your word isn't long enough!");
				}
				keyH.enter = false;
			}
		}
		// Game over or win logic
		else if (gameState == "Game Over" || gameState == "Win") {
			// Start a new game if generate key is pressed
			if (keyH.generate) {
				gameState = "Playing";
				typed = -1;
				attempt = 0;
				keyH.last = '\0';
				word = getWord(length);

				// Reset arrays for letters and validity
				for (int j = 0; j < length; j++) {
					for (int l = 0; l < chances; l++) {
						validity[l][j] = null;
						letters[l][j] = '\0';
					}
				}
				keyH.generate = false;
				System.out.println(word);
			}
			// Return to title screen if title key is pressed
			if (keyH.title) {
				gameState = "Title Screen";
				keyH.title = false;
			}
		}
	}

	// Method to get the number of chances from user input
	private static int getChances() {
		String input = JOptionPane.showInputDialog(null, "How many guesses would you like? (1-30)");

		try {
			int userInput = Integer.parseInt(input);

			// Validate input within the specified range (1-30)
			if (userInput >= 1 && userInput <= 30) {
				return userInput;
			} else {
				JOptionPane.showMessageDialog(null, "Please enter a valid number between 1 and 30.");
				return getChances(); // Recursively ask for input until valid input is provided
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Please enter a valid integer.");
			return getChances(); // Recursively ask for input until valid input is provided
		}
	}

	// Method to get the word length from user input
	private static int getLength() {
		String input = JOptionPane.showInputDialog(null, "How long would you like your word to be? (1-18)");

		try {
			int userInput = Integer.parseInt(input);

			// Validate input within the specified range (1-18)
			if (userInput >= 1 && userInput <= 18) {
				return userInput;
			} else {
				JOptionPane.showMessageDialog(null, "Please enter a valid number between 1 and 18.");
				return getLength(); // Recursively ask for input until valid input is provided
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Please enter a valid integer.");
			return getLength(); // Recursively ask for input until valid input is provided
		}
	}

	// Method to draw game components on the panel
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// Drawing fonts and setting up strokes
		Font font1 = new Font("SANS_SERIF", Font.BOLD, 16);
		g2.setFont(font1);
		FontMetrics fm1 = g2.getFontMetrics();
		Font font2 = new Font("SANS_SERIF", Font.BOLD, 30);
		g2.setFont(font2);
		FontMetrics fm2 = g2.getFontMetrics();
		Font font3 = new Font("SANS_SERIF", Font.BOLD, 78);
		g2.setFont(font3);
		FontMetrics fm3 = g2.getFontMetrics();

		Stroke defaultStroke = g2.getStroke();
		Stroke thicker = new BasicStroke(3.0f);
		int tileSize = (Math.min(1000 / length, 800 / chances));

		// Drawing game components based on the game state
		if (gameState == "Playing") {
			// Drawing the letter grid for each attempt
			for (int i = 0; i < chances; i++) {
				for (int k = 0; k < length; k++) {
					// Coloring based on validity
					if (validity[i][k] == "Correct") {
						g2.setColor(Color.GREEN);
					} else if (validity[i][k] == "Close") {
						g2.setColor(Color.YELLOW);
					} else if (validity[i][k] == "Wrong") {
						g2.setColor(Color.RED);
					} else {
						g2.setColor(Color.WHITE);
					}
					// Drawing filled rectangles for each letter
					g2.fillRect(((sw - (length * tileSize)) / 2) + (k * tileSize),
							((sh - (chances * tileSize)) / 2) + (i * tileSize), tileSize, tileSize);

					// Drawing borders for each rectangle
					g2.setColor(Color.BLACK);
					g2.drawRect(((sw - (length * tileSize)) / 2) + (k * tileSize),
							((sh - (chances * tileSize)) / 2) + (i * tileSize), tileSize, tileSize);

					// Drawing letters inside the rectangles
					Font dynamic = new Font("SANS_SERIF", Font.BOLD, (36 - length));
					g2.setFont(dynamic);
					FontMetrics d = g2.getFontMetrics();
					g2.drawString(letters[i][k] + "",
							((sw - (length * tileSize)) / 2) + (k * tileSize)
									- (d.stringWidth(letters[i][k] + "") / 2 - (tileSize / 2)),
							((sh - (chances * tileSize)) / 2) + (i * tileSize) + (tileSize / 2) + (d.getHeight() / 2));
					g2.setFont(font1);
					g2.drawString("Press Escape to Change Settings", 20, 20);
				}
			}
		} else if (gameState == "Title Screen") {
			// Drawing the title screen components
			g2.setColor(Color.DARK_GRAY);
			g2.fillRect(-4000, -4000, 8000, 8000);

			// Drawing title screen animation
			g2.setFont(font3);
			g2.setColor(Color.RED);
			g2.drawString(randString[0], dur, fm3.getHeight() / 2 + 20);
			g2.setColor(Color.ORANGE);
			g2.drawString(randString[1], dur, fm3.getHeight() / 2 * 3 + 20);
			g2.setColor(Color.YELLOW);
			g2.drawString(randString[2], dur, fm3.getHeight() / 2 * 5 + 20);
			g2.setColor(Color.GREEN);
			g2.drawString(randString[3], dur, fm3.getHeight() / 2 * 7 + 20);
			g2.setColor(Color.CYAN);
			g2.drawString(randString[4], dur, fm3.getHeight() / 2 * 9 + 20);
			g2.setColor(Color.BLUE);
			g2.drawString(randString[5], dur, fm3.getHeight() / 2 * 11 + 20);
			g2.setColor(Color.MAGENTA);
			g2.drawString(randString[6], dur, fm3.getHeight() / 2 * 13 + 20);
			g2.setColor(Color.PINK);
			g2.drawString(randString[7], dur, fm3.getHeight() / 2 * 15 + 20);
			g2.setColor(Color.WHITE);
			g2.drawString(randString[8], dur, fm3.getHeight() / 2 * 17 + 20);

			// Decrease the x-coordinate for animation
			dur--;

			// Drawing buttons with rounded rectangles
			g2.setColor(Color.WHITE);
			g2.fillRoundRect((sw / 2 - 200), (sh / 2 + 50), 400, 100, 10, 10);
			g2.fillRoundRect((sw / 2 - 200), (sh / 2 + 200), 400, 100, 10, 10);
			g2.fillRoundRect(sw / 2 - 200, sh / 2 - 150, 400, 100, 10, 10);
			g2.fillRoundRect((sw - fm1.stringWidth("Press Space to Start!")) / 2 - 10, (sh - fm1.getHeight()) / 2,
					fm1.stringWidth("Press Space to Start!") + 20, fm1.getHeight() / 2 + fm1.getAscent(), 10, 10);
			g2.setStroke(thicker);

			// Drawing borders for rounded rectangles
			g2.setColor(Color.BLACK);
			g2.drawRoundRect((sw / 2 - 200), (sh / 2 + 50), 400, 100, 10, 10);
			g2.drawRoundRect((sw / 2 - 200), (sh / 2 + 200), 400, 100, 10, 10);
			g2.drawRoundRect(sw / 2 - 200, sh / 2 - 150, 400, 100, 10, 10);
			g2.drawRoundRect((sw - fm1.stringWidth("Press Space to Start!")) / 2 - 10, (sh - fm1.getHeight()) / 2,
					fm1.stringWidth("Press Space to Start!") + 20, fm1.getHeight() / 2 + fm1.getAscent(), 10, 10);
			g2.setStroke(defaultStroke);

			// Drawing additional text and logo
			g2.setColor(Color.MAGENTA);
			g2.drawString("YOUR", (sw / 2 - fm3.stringWidth("YOURDLE") / 2),
					(sh - fm3.getHeight()) / 2 + fm3.getAscent() - 100);
			g2.setColor(Color.BLUE);
			g2.drawString("DLE", (sw / 2) - fm3.stringWidth("YOURDLE") / 2 + fm3.stringWidth("YOUR"),
					(sh - fm3.getHeight()) / 2 + fm3.getAscent() - 100);

			g2.setFont(font1);
			g2.setColor(Color.MAGENTA);
			g2.drawString("Press Space to Start!", (sw - fm1.stringWidth("Press Space to Start!")) / 2,
					(sh - fm1.getHeight()) / 2 + fm1.getAscent());
			g2.drawString("Adjust Word Length", (sw - fm1.stringWidth("Adjust Word Length")) / 2,
					(sh - fm1.getHeight()) / 2 + fm1.getAscent() + 100);
			g2.drawString("Adjust Number of Guesses", (sw - fm1.stringWidth("Adjust Number of Guesses")) / 2,
					(sh - fm1.getHeight()) / 2 + fm1.getAscent() + 250);
		} else if (gameState == "Game Over") {
			// Drawing game over screen components
			g2.setColor(Color.BLACK);
			g2.fillRect(-1000, -1000, 4000, 4000);
			g2.setColor(Color.RED);
			g2.setFont(font3);
			g2.drawString("Game Over!", (sw - fm3.stringWidth("Game Over!")) / 2,
					(sh - fm3.getHeight()) / 2 + fm3.getAscent() + -100);
			g2.setColor(Color.WHITE);
			g2.setFont(font2);
			g2.drawString("The Word Was: " + word, (sw - fm2.stringWidth("The Word Was: " + word)) / 2,
					(sh - fm2.getHeight()) / 2 + fm3.getAscent() + 100);
			g2.setFont(font1);
			g2.drawString("Press Space to Try Again", (sw - fm1.stringWidth("Press Space to Try Again")) / 2,
					(sh - fm1.getHeight()) / 2 + fm1.getAscent() + 200);

		} else if (gameState == "Win") {
			// Drawing win screen components
			g2.setColor(Color.WHITE);
			g2.fillRect(-1000, -1000, 4000, 4000);
			g2.setColor(Color.GREEN);
			g2.setFont(font3);
			g2.drawString("You Win!", (sw - fm3.stringWidth("You Win!")) / 2,
					(sh - fm3.getHeight()) / 2 + fm3.getAscent() + -100);
			g2.setColor(Color.BLACK);
			g2.setFont(font2);
			g2.drawString("The Word Was: " + word, (sw - fm2.stringWidth("The Word Was: " + word)) / 2,
					(sh - fm2.getHeight()) / 2 + fm3.getAscent() + 100);
			g2.setFont(font1);
			g2.drawString("Press Space to Play Again", (sw - fm1.stringWidth("Press Space to Play Again")) / 2,
					(sh - fm1.getHeight()) / 2 + fm1.getAscent() + 200);
			g2.drawString("Press Escape to Change Settings",
					(sw - fm1.stringWidth("Press Escape to Change Settings")) / 2,
					(sh - fm1.getHeight()) / 2 + fm1.getAscent() + 250);
		}

		// Release resources
		g2.dispose();
	}
}
