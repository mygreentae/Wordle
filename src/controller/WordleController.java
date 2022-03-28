package controller;
import model.WordleModel;

/**
 * What links the view to the model. This class contains methods that
 * updates the model on what the user guesses. Ultimately this is what the user
 * interacts with.
 * 
 * @author Leighanna Pipatanangkura
 * 
 * @see WordleModel
 */
public class WordleController {
	
	private WordleModel model;
	private static final int MAX_NUM_GUESSES = 6;
	private int currGuess;
	
	public WordleController (WordleModel model) {
		this.model = model;
		this.currGuess = 0;
	} 
	
	/**
	 * Returns true if the game is over, and false if the game is not.
	 * @return
	 * 		a boolean representing if the game is over or not.
	 */
	public boolean isGameOver() {
		if (currGuess == 0) {
			return false;
		}
		else if (currGuess == MAX_NUM_GUESSES) {
			return true;
		}
		else if (model.getProgress()[currGuess-1].getIsCorrect() == true) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns if the user won or not.
	 * @return
	 * 	True of the user won, otherwise false.
	 */
	public boolean isWin() {
		return model.getProgress()[currGuess-1].getIsCorrect();
	}
	
	/**
	 * Returns the answer to win the game which was determined by
	 * the model.
	 * @return
	 * 		the hidden word.
	 */
	public String getAnswer() {
		return this.model.getAnswer();
	}
	
	/**
	 * Updates the model on what the user's guesses. Additionally
	 * updates the progress the user has made.
	 * @param guess
	 * 		the user's guess passed in as an array.
	 * @throws IllegalArgumentException raises this exception if
	 * 		the user guesses a word that is not equal to 5
	 */
	public void makeGuess(String guess) {
		try {
			model.makeGuess(currGuess, guess.toUpperCase().strip());
			currGuess++;
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

}
