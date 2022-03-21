package model;

import utilities.Guess;
import utilities.INDEX_RESULT;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.Set;

/// change the type of guessed words!!
@SuppressWarnings("deprecation")
public class WordleModel extends Observable {
	
	private static final String FILENAME = "dictionary.txt";
	private static final int WORD_LENGTH = 5;
	private String answer;
	private int currGuess;
	
	private List<String> dictionary;
	private INDEX_RESULT[] guessedCharacters;
	private Set<Guess> guessedWords;
	

	public WordleModel() throws IOException { 
		this.answer = "";
		this.guessedCharacters = new INDEX_RESULT[26];
		this.currGuess = 0;
		
		this.guessedWords = new HashSet<Guess>();
		
		createDictionary();
		setAnswer();
	}

	public void makeGuess(int guessNumber, String guess) {
		try{
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
				guessedWords.add(new Guess(guess, indices, numCorrectIndices==5));
				this.currGuess++;
			}
		} 
		catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}

	public String getAnswer() {
		return this.answer;
	}

	public INDEX_RESULT[] getGuessedCharacters() {
		return guessedCharacters;
	}
	
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
