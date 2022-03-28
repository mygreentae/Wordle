
package view;
import java.io.IOException;
import javafx.application.Application;

/**
 * This initializes the wordle game. It takes in one command line argument
 * where:
 * 	-text: launches the text based UI
 * 	-gui or no command line argument: 
 * 
 * @author Leighanna Pipatanangkura
 */
@SuppressWarnings("deprecation")
public class Wordle {
   
	public static void main(String[] args) throws IOException {
		if (args.length == 0 || args[0].equals("-gui")) {
			Application.launch(WordleGUIView.class, args);
		}
    	else if (args[0].equals("-text")) {
			WordleTextView view = new WordleTextView();
    		view.gameLoop();
    	}
    } 
}
