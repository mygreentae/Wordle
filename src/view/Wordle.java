package view;


import javafx.application.Application;

import java.io.IOException;

import controller.WordleController;
import model.WordleModel;

@SuppressWarnings("deprecation")
public class Wordle {
   
	public static void main(String[] args) throws IOException {
    	
    	if (args[0].equals("-text")) {
			WordleTextView view = new WordleTextView();
    		view.gameLoop();
    	}
    	else {
    		WordleGUIView view = new WordleGUIView();
    		Application.launch(WordleGUIView.class, args);
    	}
    } 
}
