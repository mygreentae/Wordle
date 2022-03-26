package view;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import controller.WordleController;
import model.WordleModel;
import utilities.Guess;
import utilities.INDEX_RESULT;

@SuppressWarnings("deprecation")
public class WordleTextView implements Observer {
	
	public void gameLoop(WordleController controller) {
		System.out.println("Hello! Welcome to Wordle!");
		Scanner in  = new Scanner (System.in);
		boolean play = true;
		
		while (play) {
			playGame(controller, in);
			System.out.println("Good GameL The word was " + controller.getAnswer());
			System.out.println("Play again? yes/no");
			
			String playAgain = in.nextLine().toString();
			if (!playAgain.toLowerCase().equals("yes")) {
				play = false;
			}
		}
		in.close();
		System.out.println("See you next time");
	}
	
	private void playGame(WordleController controller, Scanner in) {
		while (!controller.isGameOver()) {
			try {
				System.out.println("Enter a guess: ");
				String guess = in.nextLine().toString();
				controller.makeGuess(guess);
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void update(Observable model, Object arg1) {
		WordleModel game = (WordleModel) model;
		updateProgress(game);
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
}
