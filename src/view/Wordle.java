package view;


import javafx.application.Application;

import java.io.IOException;
import java.util.Scanner;

import controller.WordleController;
import model.WordleModel;

@SuppressWarnings("deprecation")
public class Wordle {
   
	public static void main(String[] args) throws IOException {
    	WordleModel model = new WordleModel();
    	WordleController controller = new WordleController(model);
    	
    	if (args[0].equals("-gui")) {
    		WordleGUIView view = new WordleGUIView();
    		model.addObserver(view);
    		Application.launch(WordleGUIView.class, args);
    	}
    	else if (args[0].equals("-text")) {
    		System.out.println("HERE");
    		WordleTextView view = new WordleTextView();
    		model.addObserver(view);
    		controller.makeGuess("eager");
    	}
    	
    }
    
}
