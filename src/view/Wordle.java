package view;


import javafx.application.Application;

import java.io.IOException;

import controller.WordleController;
import model.WordleModel;

@SuppressWarnings("deprecation")
public class Wordle {
   
	public static void main(String[] args) throws IOException {
    	WordleModel model = new WordleModel();
    	WordleController controller = new WordleController(model);
    	
    	if (args[0].equals("-gui")) {// TODO Write while loop
    		WordleGUIView view = new WordleGUIView();
    		model.addObserver(view);
    		Application.launch(WordleGUIView.class, args);
    	}
    	else if (args[0].equals("-text")) {
			WordleTextView view = new WordleTextView();
    		model.addObserver(view);
    		controller.makeGuess("eager");
    	}
    	
    }
	// TODO Write gameLoop :D
    
}
