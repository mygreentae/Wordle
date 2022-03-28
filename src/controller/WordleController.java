package controller;

import model.WordleModel;

import utilities.Guess;

public class WordleController {
	
	private WordleModel model;
	private static final int MAX_NUM_GUESSES = 6;
	private int currGuess;
	
	public WordleController (WordleModel model) {
		this.model = model;
		this.currGuess = 0;
	} 
	
	public boolean isGameOver() {
		if (currGuess == 0) {
			return false;
		}
		else if (currGuess > 5) {
			return true;
		}
		else if (model.getProgress()[currGuess-1].getIsCorrect() == true) {
			return true;
		}
		return false;
	}
	
	public boolean isWin() {
		return model.getProgress()[currGuess-1].getIsCorrect();
	}
	
	public String getAnswer() {
		return this.model.getAnswer();
	}
	
	public int getCurGuess() {
		return currGuess;
	}
	
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
