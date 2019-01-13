package testers;

import java.util.ArrayList;

import game.words.WordsParser;

public class WordParser_tb {

	public static void main(String[] args) {
		WordsParser parser = new WordsParser("NL");
		ArrayList<String> result = parser.getWords(8);
		for (String word : result) {
			System.out.println(word);
		}
		//parser.addWord("BOEKENtAS");
	}

}
