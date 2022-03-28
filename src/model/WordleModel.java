package model;

import utilities.Guess;
import utilities.INDEX_RESULT;
import java.util.Observable;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 * This class represents the model of Wordle. It stores each of the user's guesses,
 * and keeps track of which characters have been correctly guessed,
 * incorrectly guessed and which crackers are correctly guessed but in
 * the incorrect positions.
 * 
 * @author Leighanna Pipatanangkura
 */
@SuppressWarnings("deprecation")
public class WordleModel extends Observable {
	
	private static final String FILENAME = "Dictionary.txt";
	private static final int WORD_LENGTH = 5;
	private static final int NUM_GUESSES = 6;
	
	private String answer;
	
	private List<String> dictionary;
	private INDEX_RESULT[] guessedCharacters;
	private Guess[] guessedWords;
	

	public WordleModel() throws IOException { 
		this.answer = "";
		this.guessedCharacters = new INDEX_RESULT[26];
		this.guessedWords = new Guess[NUM_GUESSES];
		
		createDictionary();
		setAnswer();
	}

	/**
	 * Updates the guessed characters array with whether or not at the index
	 * the character appears in the alphabet. If the character has not been guessed 
	 * yet, the corresponding index will be null.
	 * @param guess
	 * 		The user's guess passed in as a string.
	 * @return guessNumber 
	 * 		the guess number we are on.
	 */
	public void makeGuess(int guessNumber, String guess) {
			if (guess.length() != WORD_LENGTH) {
				throw new IllegalArgumentException("The length of the guess must be 5.");
			}
			else if (inDictionary(guess) == false) {
				throw new IllegalArgumentException("This word is not found in the dictionary.");
			}
			else {
				
				INDEX_RESULT[] indices = new INDEX_RESULT[WORD_LENGTH];
				int numCorrectIndices = 0;

				for (int i = 0; i < guess.length(); i++) {
					if (guess.charAt(i) == answer.charAt(i)) {
						guessedCharacters[(guess.charAt(i) - 'A')] = INDEX_RESULT.CORRECT;
						indices[i] = INDEX_RESULT.CORRECT;
						numCorrectIndices++;
					}
					else if (answer.indexOf(guess.charAt(i)) > -1 && guess.charAt(i) != answer.charAt(i)) {
						guessedCharacters[(guess.charAt(i) - 'A')] = INDEX_RESULT.CORRECT_WRONG_INDEX;
						indices[i] = INDEX_RESULT.CORRECT_WRONG_INDEX;
					}
					else if (answer.indexOf(guess.charAt(i)) < 0 && guess.charAt(i) != answer.charAt(i)) {
						guessedCharacters[(guess.charAt(i) - 'A')] = INDEX_RESULT.INCORRECT;
						indices[i] = INDEX_RESULT.INCORRECT;
					}
				}
				guessedWords[guessNumber] = new Guess(guess, indices, numCorrectIndices==WORD_LENGTH);
				setChanged();
				notifyObservers();
			}
	}

	/**
	 * Returns the answer to win the game.
	 * @return
	 * 		the hidden word.
	 */
	public String getAnswer() {
		return this.answer;
	}

	/**
	 * Returns an array of characters that corresponds to what the user has guess and have not guessed.
	 * Where null will represent what the user has not guessed, and the actual character for what the user 
	 * has already guessed all corresponding to the indices they appear in the alphabet.
	 * @return
	 * 	the character array that represents the letters the user 
	 */
	public INDEX_RESULT[] getGuessedCharacters() {
		return guessedCharacters;
	}
	
	/**
	 * Returns the user's progress.
	 * @return
	 * 		a array of guesses that represents the progress the user has made
	 * 		through out the game.
	 */
	public Guess[] getProgress() {
		return guessedWords;
	}
	
	/**
	 * At the beginning of the game, this sets the word that needs to be
	 * guessed.
	 */
	private void setAnswer() {
		/** Randomly picks a word from the dictionary. */
		int randomIndex = new Random().nextInt(this.dictionary.size());
		this.answer = this.dictionary.get(randomIndex).toUpperCase();
	}

	/**
	 * Returns true of the user's guess exists in the dictionary. and false
	 * if the guess does not.
	 * @param guess
	 * 		the user's guess passed in as a string.
	 * @return
	 * 		A boolean to see if the dictionary contains 
	 * 		the user's guess.
	 */
	private boolean inDictionary(String guess) {
		for (String word : dictionary) {
			if (word.toUpperCase().equals(guess)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Creates the dictionary that this game is going to use. The dictionary
	 * contains all of the words that is available for the user to guess
	 * and the hidden word.
	 * @throws IOException
	 * 		thrown if the "Dictionary.txt" is not found.
	 */
	private void createDictionary() throws IOException {
		this.dictionary = Files.readAllLines(Paths.get(FILENAME));
	}
}
