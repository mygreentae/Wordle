package view;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import controller.WordleController;
import model.WordleModel;
import utilities.Guess;
import utilities.INDEX_RESULT;

/**
 * The text based UI. This class contains methods to display what
 * the user would see to interact with the controller. ultimately,
 * this is what the user sees. This uses the Observer/Observable
 * design patter with the MVC design architecture.
 * 
 * @author Leighanna Pipatanangkura
 *
 * @see WordleController
 * @see WordleModel
 */
@SuppressWarnings("deprecation")
public class WordleTextView implements Observer {
	
	public void gameLoop() throws IOException {
		System.out.println("Hello! Welcome to Wordle!");
		Scanner in  = new Scanner (System.in);
		boolean play = true;
		
		// main game loop that keeps the game going
		while (play) {
			WordleController controller = createGame();
			playGame(controller, in);
			System.out.println("Good Game the word was: " + controller.getAnswer());
			System.out.println("Play again? yes/no");
			
			String playAgain = in.nextLine().toString();
			if (!playAgain.toLowerCase().equals("yes")) {
				play = false;
			}
		}
		in.close();
		System.out.println("See you next time.");
	}
	
	/**
	 * Runs the game loop and directs the data from the user to the controller.
	 * @param controller
	 * 		handles the guesses the user enters and keeps track of their 
	 * 		progress.
	 * @param scanner
	 * 		continuously seeks for the user input when needed.
	 */
	private void playGame(WordleController controller, Scanner in) {
		
		while (controller.isGameOver()==false) {
			System.out.println("Enter a guess: ");
			String guess = in.nextLine().toString();
			try {
				controller.makeGuess(guess);
			}
			catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Updates the progress that the player will see. 
	 */
	@Override
	public void update(Observable model, Object arg1) {
		WordleModel game = (WordleModel) model;
		updateProgress(game);
		updateLetterSummary(game);
	}
	
	private void updateProgress(WordleModel game) {
		Guess[] progress = game.getProgress();
		for (Guess guess : progress) {
			if (guess == null) {
				System.out.println("_ _ _ _ _");
			} 
			else {
				String word = guess.getGuess();
				for (int i = 0; i< word.length(); i++) {
					if (guess.getIndices()[i] == INDEX_RESULT.INCORRECT) {
						System.out.print("_");
					}
					else if (guess.getIndices()[i] == INDEX_RESULT.CORRECT) {
						System.out.print(Character.toUpperCase(word.charAt(i)));
					}
					else if (guess.getIndices()[i] == INDEX_RESULT.CORRECT_WRONG_INDEX) {
						System.out.print(Character.toLowerCase(word.charAt(i)));
					}
					System.out.print(" ");
				}
				System.out.println();
			}
		}
		System.out.println();
	}
	
	/**
	 * This method will:
	 * 	1) Print out all the characters the user has not guessed
	 * 	2) Print out all the characters the user incorrectly guessed
	 * 	3) Print out all the characters the user correctly guessed
	 * 	4) Print out all the characters the user correctly guessed but are in the wrong place
	 * @param game
	 */
	private void updateLetterSummary (WordleModel game) {
		StringBuilder unguessed = new StringBuilder();
		StringBuilder incorrect = new StringBuilder();
		StringBuilder correct = new StringBuilder();
		StringBuilder correctWrong = new StringBuilder();
		
		for (int i = 0; i < 26; i++) {
			if (game.getGuessedCharacters()[i] == null) {
				unguessed.append((char) ((int) 'A' + i));
				unguessed.append(", ");
			}
			else if (game.getGuessedCharacters()[i] == INDEX_RESULT.INCORRECT) {
				incorrect.append((char) ((int) 'A' + i));
				incorrect.append(", ");
			}
			else if (game.getGuessedCharacters()[i] == INDEX_RESULT.CORRECT) {
				correct.append((char) ((int) 'A' + i));
				correct.append(", ");
			}
			else if (game.getGuessedCharacters()[i] == INDEX_RESULT.CORRECT_WRONG_INDEX) {
				correctWrong.append((char) ((int) 'A' + i));
				correctWrong.append(", ");
			}
		}
		System.out.println("Unguessed [" + unguessed.toString().replaceAll(", $", "") + "]");
		System.out.println("Incorrect [" + incorrect.toString().replaceAll(", $", "") + "]");
		System.out.println("Correct [" + correct.toString().replaceAll(", $", "") + "]");
		System.out.println("Correct Letter, Wrong Index[" + correctWrong.toString().replaceAll(", $", "") + "]");
		System.out.println();
	}
	
	/**
	 * Initializes the controller and model when the game is started.
	 * @return
	 * 		the controller of the game which is what the user
	 * 		interacts with.
	 * @throws IOException
	 * 		throws exception if the controller/model cannot find the
	 * 		dictionary.
	 */
	private  WordleController createGame() throws IOException {
		WordleModel model = new WordleModel();
		WordleController controller = new WordleController(model);
		model.addObserver(this);
		return controller;
	}
}
