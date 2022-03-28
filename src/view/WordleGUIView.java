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

	private static int CUR_COL = -1;
	private static int CUR_GUESS = 0;
	
	private WordleController controller;
	
	private static final String[][] keys = {
			{"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
			{"A", "S", "D", "F", "G", "H", "J", "K", "L"},
			{"Z", "X", "C", "V", "B", "N", "M"},
	};
	
	Tile[][] tilesArray;
	KeyBoardTile[] keyboardTiles;
	
	@Override
	public void start(Stage stage) throws IOException {
		WordleModel model = new WordleModel();
		controller = new WordleController(model);
		model.addObserver(this);
		
		BorderPane border = new BorderPane();
		
		tilesArray = new Tile[NUM_GUESSES][NUM_LETTERS];
		keyboardTiles = new KeyBoardTile[26];
		GridPane tiles = addTiles(controller, model, tilesArray);
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


	private GridPane addTiles(WordleController controller, WordleModel model, Tile[][] tilesArray) {
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
	
	private void keyPress(Tile[][] tiles, Stage stage, WordleController controller) {
		
		EventHandler<KeyEvent> keyPressEvent = new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (controller.isGameOver()) {
        			Platform.runLater(() -> {
        				ButtonType button = ButtonType.FINISH;
        				if (controller.isWin()) {
        					Alert dialog = new Alert(AlertType.INFORMATION, "Horay! The word was: " + controller.getAnswer(), button);
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
        					Alert dialog = new Alert(AlertType.INFORMATION, "Horay! The word was: " + controller.getAnswer(), button);
        					dialog.show();
        				}
        				else {
        					Alert dialog = new Alert(AlertType.INFORMATION, "Game Over! The word was: " + controller.getAnswer(), button);
        					dialog.show();
        				}        		        
        		    });
        			
        			return;
        		}
			}
		};
		
		stage.addEventHandler(KeyEvent.KEY_PRESSED, keyPressEvent);
	}
	
	private void letterPress(String letter, Tile currentTile) {
		wiggleRow(currentTile);
		currentTile.getText().setText(letter);
		currentTile.getBorderRectangle().setStroke(Color.BLACK);
	}
	
	private void enterPress (Tile[][] tiles, WordleController controller) {
		 String guess = getWord(tiles, controller);
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
	
	private String getWord(Tile[][] tiles, WordleController controller) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < NUM_LETTERS; i++) {
			sb.append(tiles[CUR_GUESS][i].getText().getText());
		}
		return sb.toString();
	}
	
	private void backSpacePress(Tile curTile) {
		curTile.getText().setText("");
		curTile.getBorderRectangle().setStroke(Color.LIGHTGREY);
	}
	

	@Override
	public void update(Observable model, Object arg1) {
		WordleModel game = (WordleModel) model;
		updateTiles(game);
		updateKeyboard(game);
		
		CUR_GUESS++;
		CUR_COL = -1;
		
		
	}
	
	private void updateTiles(WordleModel game) {
		Guess guess = (Guess) game.getProgress()[CUR_GUESS];
		for (int i = 0; i < NUM_LETTERS; i++) {
			if (guess.getIndices()[i] == INDEX_RESULT.INCORRECT) {
				flipTile(tilesArray[CUR_GUESS][i],i);
				tilesArray[CUR_GUESS][i].borderRectangle.setFill(Color.rgb(119,136,153));
				tilesArray[CUR_GUESS][i].borderRectangle.setStroke(Color.rgb(119,136,153));
				tilesArray[CUR_GUESS][i].getText().setFill(Color.rgb(255, 255, 255));
			}
			
			if (guess.getIndices()[i] == INDEX_RESULT.CORRECT) {
				flipTile(tilesArray[CUR_GUESS][i], i);
				tilesArray[CUR_GUESS][i].borderRectangle.setFill(Color.rgb(106,170,100));
				tilesArray[CUR_GUESS][i].borderRectangle.setStroke(Color.rgb(106,170,100));
				tilesArray[CUR_GUESS][i].getText().setFill(Color.rgb(255, 255, 255));
			}
			
			if (guess.getIndices()[i] == INDEX_RESULT.CORRECT_WRONG_INDEX) {
				flipTile(tilesArray[CUR_GUESS][i], i);
				tilesArray[CUR_GUESS][i].borderRectangle.setFill(Color.rgb(189,183,107));
				tilesArray[CUR_GUESS][i].borderRectangle.setStroke(Color.rgb(189,183,107));
				tilesArray[CUR_GUESS][i].getText().setFill(Color.rgb(255, 255, 255));
			}
		}
	}
	
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
	
	private void wiggleRow(Tile tile) {
		ScaleTransition scale = new ScaleTransition(Duration.millis(20), tile);
		
		scale.setToX(1.08);
		scale.setToY(1.08);
		scale.setCycleCount(2);
		scale.setAutoReverse(true);
        
		SequentialTransition animation = new SequentialTransition (scale);
		animation.play();
	}

	
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
		
		private Rectangle getBorderRectangle() {
			return borderRectangle;
		}
		
		private Text getText() {
			return text;
		}
	}
	
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
		
		private Rectangle getBorderRectangle() {
			return borderRectangle;
		}
		
		private Text getText() {
			return text;
		}
	}
}
