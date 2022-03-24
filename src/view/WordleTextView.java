package view;

import java.util.Observable;
import java.util.Observer;

import model.WordleModel;

@SuppressWarnings("deprecation")
public class WordleTextView implements Observer {
	
	@Override
	public void update(Observable model, Object arg1) {
		System.out.println("HELLO");
			//WordleModel wordleModel = (WordleModel) model;
			System.out.println("HEREEE");
		
	}
}
