package view;

import java.io.IOException;

import javafx.application.Application;

@SuppressWarnings("deprecation")
public class Wordle {
   
	public static void main(String[] args) throws IOException {
    	
    	
    	if (args.length == 0 || args[0] == "-gui"){
    		Application.launch(WordleGUIView.class, args);
    	}
    	else if (args[0].equals("-text")) {
			WordleTextView view = new WordleTextView();
    		view.gameLoop();
    	}
    } 
}
