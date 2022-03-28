

package view;

import java.util.Observable;
import java.util.Observer;

import controller.WordleController;
import model.WordleModel;
import utilities.Guess;
import utilities.INDEX_RESULT;

import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * This uses JavaFX to create a GUI for the game Wordle which is supposed to be
 * more or less a clone of the original game play and visuals. However, it allows 
 * you to play infinitely unlike the original. 
 * 
 * @author Leighanna Pipatanangkura
 * 
 * @see WordleController
 * @see WordleModel
 */
@SuppressWarnings("deprecation")
public class WordleGUIView extends Application implements Observer{

	/* Constants for grid of letters */
	private static final int GRID_GAP = 10;

	/* Constants for letters in grid */
	private static final int LETTER_FONT_SIZE = 30;
	private static final int LETTER_SQUARE_SIZE = 60;

	private static final int NUM_GUESSES = 6;
	private static final int NUM_LETTERS = 5;
	
	/* Constants for letter summary of letters */
	private static final int SUMMARY_FONT = 15;
	private static final int SUMMARY_SQUARE_SIZE = 45;

	/* Constants to keep track of game progress */
	private static int CUR_COL = -1;
	private static int CUR_GUESS = 0;
	
	private WordleController controller;
	
	// keyboard layout
	private static final String[][] keys = {
			{"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
			{"A", "S", "D", "F", "G", "H", "J", "K", "L"},
			{"Z", "X", "C", "V", "B", "N", "M"},
	};
	
	Tile[][] tilesArray;
	KeyBoardTile[] keyboardTiles;
	
	/**
	 * This is the main entry point for all JavaFX applications. This 
	 * method is called after the main (init) function is called, and when
	 * the system is ready to begin running.
	 */
	@Override
	public void start(Stage stage) throws IOException {
		WordleModel model = new WordleModel();
		controller = new WordleController(model);
		model.addObserver(this);
		
		BorderPane border = new BorderPane();
		
		tilesArray = new Tile[NUM_GUESSES][NUM_LETTERS];
		keyboardTiles = new KeyBoardTile[26];
		GridPane tiles = addTiles(tilesArray);
		GridPane letters = letters(keyboardTiles);
		
		border.setCenter(tiles);
		border.setBottom(letters);
		
		Scene scene = new Scene(border);
		//scene.setFill(Color.rgb(248, 240, 227));
		scene.setFill(Color.rgb(250, 249, 246));
        stage.setScene(scene);
        stage.setTitle("Wordle!");
        stage.show();
        
        keyPress(tilesArray, stage, controller);        
	}

	/**
	 * Creates the game tiles that the player will see. These also update
	 * when the user enters a word. These tiles change color:
	 * 		Yellow: Correct Letter, but Wrong Index;
	 * 		Red: Incorrect Letter;
	 * 		Green: Correct Letter;
	 * These tiles also are animated too
	 * 
	 * @param tilesArray are the collection of tiles to be laid out. They are
	 * 		in this array so it is easier to update and animate once
	 * 		the user has interacted with it.
	 * @return
	 * 		the GridPane that displays all the tiles is returned to
	 * 		be displayed.
	 */
	private GridPane addTiles(Tile[][] tilesArray) {
		GridPane letterGrid = new GridPane();
		letterGrid.setHgap(GRID_GAP);
	    letterGrid.setVgap(GRID_GAP);
	    letterGrid.setPadding(new Insets(50, 100, 50, 100));
	    letterGrid.setAlignment(Pos.CENTER);
		
		for (int i= 0; i < NUM_GUESSES; i++) {
			for (int j = 0; j < NUM_LETTERS; j++) {
				Tile tile = new Tile();
				letterGrid.add(tile, j, i);
				tilesArray[i][j] = tile;
			}
		}
		return letterGrid;
		}
	
	/**
	 * Creates the letters to be displayed in the keyboard below the word tiles.
	 * Like the word tiles, these change color corresponding to the 
	 * correctness of the letter entered.
	 * 
	 * @param keyboardTiles are the collection of the letter tiles (to form the keyboard).
	 * 		They are in this array so it is easier to update and animate once the user has
	 * 		Interacted with the interface.
	 * @return 
	 * 		The GridPane that displays all the tiles is returned to be displayed.
	 */
	private GridPane letters(StackPane[] keyboardTiles) {
		GridPane letters = new GridPane();
		
		letters.setHgap(GRID_GAP);
	    letters.setVgap(GRID_GAP);
		letters.setPadding(new Insets(50, 100, 10, 100));
		letters.setAlignment(Pos.CENTER);
		
		int curLetter = 0;
		for (int i = 0; i < keys.length; i++) {
			GridPane letterRow = new GridPane();
			letterRow.setHgap(GRID_GAP); 
			for (int j = 0; j < keys[i].length; j++) {
				
				KeyBoardTile keyboardTile = new KeyBoardTile();
				keyboardTile.getText().setText(keys[i][j]);
		        
				
				letterRow.add(keyboardTile, j, 1);
				keyboardTiles[curLetter++] = keyboardTile;
			}
			letterRow.setAlignment(Pos.CENTER);
			letters.add(letterRow, 1, i);
		}
		
		return letters;
	}
	
	/**
	 * This handles the events for the game. This includes the all the key presses:
	 * 	1) ENTER: When the user presses enter, the word is submitted and evaluated.
	 * 		Error messages will be displayed if the word is not long enough or if the
	 * 		word does not appear in the dictionary. It also checks is the game is
	 * 		over or not so the correct answer can be displayed.
	 * 	2) BACKSPACE: when the user presses back space. deletes the previous word.
	 * 	3) Any alphabetical key press: the ultimately put together to form the user's
	 * 		guesses.
	 * 	The tiles are also animated as they are typed in.
	 * 
	 * @param tiles
	 * 		These are updated as the user enters letters in
	 * @param stage
	 * 		This is passed in so we can update the tiles on the screen
	 * @param controller
	 * 		This is passed in so we can tell if the user won or not and if the game is over.
	 */
	private void keyPress(Tile[][] tiles, Stage stage, WordleController controller) {
		
		EventHandler<KeyEvent> keyPressEvent = new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (controller.isGameOver()) {
        			Platform.runLater(() -> {
        				ButtonType button = ButtonType.FINISH;
        				if (controller.isWin()) {
        					Alert dialog = new Alert(AlertType.INFORMATION, "Hooray! The word was: " + controller.getAnswer(), button);
        					dialog.show();
        				}
        				else {
        					Alert dialog = new Alert(AlertType.INFORMATION, "Game Over! The word was: " + controller.getAnswer(), button);
        					dialog.show();
        				}        		        
        		    });
        			
        			return;
        		}
				else if (event.getCode().isLetterKey()) {
                	if (CUR_COL+1 < NUM_LETTERS) {
            			CUR_COL++;
            			String letter = event.getCode().getName().toUpperCase();
                        letterPress(letter, tiles[CUR_GUESS][CUR_COL]);
            		}
                }
                
				else if (event.getCode().equals(KeyCode.ENTER)) {
                	enterPress(tiles, controller);
                }
                
				else if (event.getCode().equals(KeyCode.BACK_SPACE)) {
                	if (CUR_COL > -1) {
                		backSpacePress(tiles[CUR_GUESS][CUR_COL]);
                	}
                	
                	if (CUR_COL > -1 && CUR_COL < NUM_LETTERS) {
            			CUR_COL--;
                	}
                }
				if (controller.isGameOver()) {
        			Platform.runLater(() -> {
        				ButtonType button = ButtonType.FINISH;
        				if (controller.isWin()) {
        					Alert dialog = new Alert(AlertType.INFORMATION, 
        							"Horay! The word was: " + controller.getAnswer(), button);
        					dialog.show();
        				}
        				else {
        					Alert dialog = new Alert(AlertType.INFORMATION, 
        							"Game Over! The word was: " + controller.getAnswer(), button);
        					dialog.show();
        				}        		        
        		    });
        			
        			return;
        		}
			}
		};
		
		stage.addEventHandler(KeyEvent.KEY_PRESSED, keyPressEvent);
	}
	
	/**
	 * This handles the animation and updates the letters being typed by the user.
	 * As each key is pressed, the tile is wiggled and the tile is darkend.
	 * 
	 * @param letter
	 * 		This is the letter being typed in which will be updated onto the tile.
	 * @param currentTile
	 * 		This is the current tile we are on that will be updated with a letter and
	 * 		animated.
	 */
	private void letterPress(String letter, Tile currentTile) {
		wiggleRow(currentTile);
		currentTile.getText().setText(letter);
		currentTile.getBorderRectangle().setStroke(Color.BLACK);
	}
	
	/**
	 * This handles when the ENTER key is pressed. It ultimately sends the 
	 * guess to the controller to be evaluated.  An alert will pop up if the user
	 * guesses:
	 * 	1) a word that is not in the dictionary, and
	 * 	2) a word that is too short (less than 4 letters)
	 * 
	 * @param tiles
	 * 		The tiles are passed in so we know what the user's guess is.
	 * @param controller
	 * 		the controller is passed in so we can send the guess to be evaluated.
	 */
	private void enterPress (Tile[][] tiles, WordleController controller) {
		 String guess = getWord(tiles);
		 try {
			 controller.makeGuess(guess);
		 }
		 catch (IllegalArgumentException e) {
		    Platform.runLater(() -> {
		        Alert dialog = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.OK);
		        dialog.show();
		    });
		 }
	}
	
	/**
	 * The method retrieves the current guess and returns it so we can send it
	 * to the controller to be evaluated.
	 * 
	 * @param tiles
	 * 		These are the tiles we are currently dealing with.
	 * @return
	 * 		Returns the user's guess in a string format.
	 */
	private String getWord(Tile[][] tiles) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < NUM_LETTERS; i++) {
			sb.append(tiles[CUR_GUESS][i].getText().getText());
		}
		return sb.toString();
	}
	
	/**
	 * This handles when a backspace is pressed. The previous tile gets blanked out
	 * and formatting is wiped.
	 * 
	 * @param curTile
	 * 		This is the tile to be wiped.
	 */
	private void backSpacePress(Tile curTile) {
		curTile.getText().setText("");
		curTile.getBorderRectangle().setStroke(Color.LIGHTGREY);
	}
	

	/**
	 * This updates the view. So the tiles animated, and the keyboard is 
	 * updated to reflect the correctness of each letter guessed.
	 */
	@Override
	public void update(Observable model, Object arg1) {
		WordleModel game = (WordleModel) model;
		updateTiles(game);
		updateKeyboard(game);
		
		CUR_GUESS++;
		CUR_COL = -1;
	}
	
	/**
	 * This updates the tiles after the letters in the guess has been
	 * validated. The tiles are flipped to reveal the color that reflects
	 * their correctness.
	 * @param game
	 * 		This is the WordleModel which is passed in to check the
	 * 		correctness of the guess.
	 */
	private void updateTiles(WordleModel game) {
		Guess guess = (Guess) game.getProgress()[CUR_GUESS];
		for (int i = 0; i < NUM_LETTERS; i++) {
			if (guess.getIndices()[i] == INDEX_RESULT.INCORRECT) {
				// animation
				flipTile(tilesArray[CUR_GUESS][i],i);
				// update color
				tilesArray[CUR_GUESS][i].borderRectangle.setFill(Color.rgb(119,136,153));
				tilesArray[CUR_GUESS][i].borderRectangle.setStroke(Color.rgb(119,136,153));
				tilesArray[CUR_GUESS][i].getText().setFill(Color.rgb(255, 255, 255));
			}
			
			if (guess.getIndices()[i] == INDEX_RESULT.CORRECT) {
				// animation
				flipTile(tilesArray[CUR_GUESS][i], i);
				// update color
				tilesArray[CUR_GUESS][i].borderRectangle.setFill(Color.rgb(106,170,100));
				tilesArray[CUR_GUESS][i].borderRectangle.setStroke(Color.rgb(106,170,100));
				tilesArray[CUR_GUESS][i].getText().setFill(Color.rgb(255, 255, 255));
			}
			
			if (guess.getIndices()[i] == INDEX_RESULT.CORRECT_WRONG_INDEX) {
				// animation
				flipTile(tilesArray[CUR_GUESS][i], i);
				// update color
				tilesArray[CUR_GUESS][i].borderRectangle.setFill(Color.rgb(189,183,107));
				tilesArray[CUR_GUESS][i].borderRectangle.setStroke(Color.rgb(189,183,107));
				tilesArray[CUR_GUESS][i].getText().setFill(Color.rgb(255, 255, 255));
			}
		}
	}
	
	/**
	 * Updates the keyboard under the tiles to reflect the correctness of what the player
	 * has already guessed.
	 * 
	 * @param game
	 * 		This is the WordleModel which is passed in to check the correctness of the guess.
	 */
	private void updateKeyboard(WordleModel game) {
		for (int i = 0; i < 26; i ++) {
			int letterIdx = (int) keyboardTiles[i].getText().getText().toUpperCase().charAt(0) - 'A';
			if (game.getGuessedCharacters()[letterIdx] == INDEX_RESULT.INCORRECT) {
				keyboardTiles[i].getBorderRectangle().setFill(Color.rgb(119,136,153));
				keyboardTiles[i].getBorderRectangle().setStroke(Color.rgb(119,136,153));
				keyboardTiles[i].getText().setFill(Color.rgb(255, 255, 255));
			}
			if (game.getGuessedCharacters()[letterIdx] == INDEX_RESULT.CORRECT) {
				keyboardTiles[i].getBorderRectangle().setFill(Color.rgb(106,170,100));
				keyboardTiles[i].getBorderRectangle().setStroke(Color.rgb(106,170,100));
				keyboardTiles[i].getText().setFill(Color.rgb(255, 255, 255));
			}
			if (game.getGuessedCharacters()[letterIdx] == INDEX_RESULT.CORRECT_WRONG_INDEX) {
				keyboardTiles[i].getBorderRectangle().setFill(Color.rgb(189,183,107));
				keyboardTiles[i].getBorderRectangle().setStroke(Color.rgb(189,183,107));
				keyboardTiles[i].getText().setFill(Color.rgb(255, 255, 255));
			}
		}
	}
	
	/**
	 * Implements the animation to flip the tile over when the player has made
	 * a guess.
	 * 
	 * @param tile
	 * 		The tile to be animated.
	 * @param col
	 * 		This is used to calculate the delay of animation between each
	 * 		tile.
	 */
	private void flipTile(Tile tile, int col) {
		int delay = col * 200;
		int speed = 700;
		RotateTransition rotator = new RotateTransition(Duration.millis(speed), tile);
		rotator.setAxis(Rotate.X_AXIS);
        rotator.setFromAngle(0);
        rotator.setToAngle(360);
        
		SequentialTransition animation = new SequentialTransition(
				new PauseTransition(Duration.millis(delay)), rotator);
		animation.play();
	}
	
	/**
	 * Implements the animation to wiggle the tile.
	 * 
	 * @param tile
	 * 		The tile to be wiggled.
	 */
	private void wiggleRow(Tile tile) {
		ScaleTransition scale = new ScaleTransition(Duration.millis(20), tile);
		
		scale.setToX(1.08);
		scale.setToY(1.08);
		scale.setCycleCount(2);
		scale.setAutoReverse(true);
        
		SequentialTransition animation = new SequentialTransition (scale);
		animation.play();
	}

	/**
	 * This class implements each of the the tiles in the keyboard. This initializes
	 * their color, size, and other overall appearances the player will see
	 * when the game is started.
	 * @author Leighanna Pipatanangkura
	 *
	 */
	private class KeyBoardTile extends StackPane {
		private Text text = new Text();
		private Rectangle borderRectangle;
		
		private KeyBoardTile() {
			borderRectangle = new Rectangle();
			
			borderRectangle.setWidth(SUMMARY_SQUARE_SIZE); 
			borderRectangle.setHeight(SUMMARY_SQUARE_SIZE+5);
			borderRectangle.setStroke(Color.WHITE);
			borderRectangle.setFill(Color.LIGHTGRAY);
			borderRectangle.setArcHeight(10.0d);
			borderRectangle.setArcWidth(10.0d);
	        
	        text.setFont(new Font(SUMMARY_FONT));
			text.setStyle("-fx-font-weight: bold");
			
			setAlignment(Pos.CENTER);
			getChildren().addAll(borderRectangle, text);
		}
		
		/**
		 * Returns the Rectangle surrounding the letter
		 * @return
		 * 		The rectangle that borders the letter
		 */
		private Rectangle getBorderRectangle() {
			return borderRectangle;
		}
		
		/**
		 * Returns the Text object that is placed in the middle
		 * of the rectangle
		 * @return
		 * 		The text object that appears in the center of the
		 * 		rectangle
		 */
		private Text getText() {
			return text;
		}
	}
	/**
	 * This class represents the Tiles that the user interacts with. This
	 * initializes the tiles' color, size, and other overall appearances the player
	 * will see when the game is started.
	 * @author lee
	 *
	 */
	private class Tile extends StackPane {
		private Text text = new Text();
		private Rectangle borderRectangle;
		
		private Tile() {
			borderRectangle = new Rectangle();
			borderRectangle.setFill(null);
			borderRectangle.setStroke(Color.rgb(216,216,216));
			borderRectangle.setWidth(LETTER_SQUARE_SIZE); 
			borderRectangle.setHeight(LETTER_SQUARE_SIZE);
			borderRectangle.setArcHeight(10.0d);
	        borderRectangle.setArcWidth(10.0d);
			
			text.setFont(new Font(LETTER_FONT_SIZE));
			text.setStyle("-fx-font-weight: bold");
			
			setAlignment(Pos.CENTER);
			getChildren().addAll(borderRectangle, text);
		}
		
		/**
		 * Returns the Rectangle surrounding the letter
		 * @return
		 * 		The rectangle that borders the letter
		 */
		private Rectangle getBorderRectangle() {
			return borderRectangle;
		}
		
		/**
		 * Returns the Text object that is placed in the middle
		 * of the rectangle
		 * @return
		 * 		The text object that appears in the center of the
		 * 		rectangle
		 */
		private Text getText() {
			return text;
		}
	}
}
